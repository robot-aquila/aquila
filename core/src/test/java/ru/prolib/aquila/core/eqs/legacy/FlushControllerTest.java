package ru.prolib.aquila.core.eqs.legacy;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.eqs.legacy.FlushController;

public class FlushControllerTest {
	private static Runnable EXIT = new Runnable() { @Override public void run() { } };
	private static Runnable STUB = new Runnable() { @Override public void run() { } };
	
	static class QueueStub {
		private final BlockingQueue<Runnable> queue;
		private final FlushController control;
		
		public QueueStub(BlockingQueue<Runnable> queue, FlushController control) {
			this.queue = queue;
			this.control = control;
		}
		
		public void enqueue(Runnable arg) {
			try {
				control.countUp();
				queue.put(arg);
			} catch ( InterruptedException e) {
				control.countDown();
				e.printStackTrace();
			}
		}
		
	}
	
	static class QueueWorker extends Thread {
		private final BlockingQueue<Runnable> queue;
		private final FlushController control;
		
		public QueueWorker(BlockingQueue<Runnable> queue, FlushController control) {
			this.queue = queue;
			this.control = control;
		}
		
		@Override
		public void run() {
			try {
				Runnable cmd = null;
				long max = 0L;
				while ( (cmd = queue.take()) != null ) {
					max = Math.max(max, control.getCounter());
					control.countDown();
					if ( cmd == EXIT ) {
						//System.out.println("Max: " + max);
						break;
					}
					cmd.run();
				}
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}
		
	}
	
	static class StubTaskRunner implements Runnable {
		private final QueueStub q;
		private final int subtasks;
		
		public StubTaskRunner(QueueStub q, int subtasks) {
			this.q = q;
			this.subtasks = subtasks;
		}

		@Override
		public void run() {
			for ( int i = 0; i < subtasks; i ++ ) {
				q.enqueue(STUB);
			}
		}
		
	}
	
	private BlockingQueue<Runnable> queue;
	private FlushController service;

	@Before
	public void setUp() throws Exception {
		queue = new LinkedBlockingQueue<>();
		service = new FlushController();
	}

	@Test
	public void testCase1() throws Exception {
		CountDownLatch go1 = new CountDownLatch(1), go2 = new CountDownLatch(1);
		CountDownLatch finished = new CountDownLatch(3);
		QueueStub q = new QueueStub(queue, service);
		Thread t1 = new Thread() {
			@Override
			public void run() {
				for ( int i = 0; i < 1000; i ++ ) {
					if ( i == 500 ) {
						go1.countDown();
					}
					q.enqueue(STUB);
				}
				finished.countDown();
			}
		};
		Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					if ( ! go1.await(1, TimeUnit.SECONDS) ) {
						return;
					}
				} catch ( InterruptedException e ) {
					e.printStackTrace();
					return;
				}
				for ( int i = 0; i < 1000; i ++ ) {
					if ( i == 500 ) {
						go2.countDown();
					}
					q.enqueue(STUB);
				}
				finished.countDown();
			}
		};
		Thread t3 = new Thread() {
			@Override
			public void run() {
				try {
					if ( ! go2.await(1, TimeUnit.SECONDS) ) {
						return;
					}
				} catch ( InterruptedException e ) {
					e.printStackTrace();
					return;
				}
				for ( int i = 0; i < 1000; i ++ ) {
					q.enqueue(STUB);
				}
				finished.countDown();
			}
		};
		service.start();
		Thread t4 = new Thread(new QueueWorker(queue, service));
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		service.waitForFlushing(1, TimeUnit.SECONDS);
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		q.enqueue(EXIT);
		t1.join(1000L);
		t2.join(1000L);
		t3.join(1000L);
		t4.join(1000L);
		assertFalse(t1.isAlive());
		assertFalse(t2.isAlive());
		assertFalse(t3.isAlive());
		assertFalse(t4.isAlive());
		
		assertEquals(FlushController.DONE, service.getStatus());
		assertEquals(0L, service.getCounter());
	}
	
	@Test
	public void testCase2() throws Exception {
		QueueStub q = new QueueStub(queue, service);
		Thread qt = new Thread(new QueueWorker(queue, service));
		qt.start();
		service.start();
		
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		for ( int i = 0; i < 5000; i ++ ) {
			q.enqueue(new StubTaskRunner(q, rand.nextInt(1, 24)));
		}
		
		service.waitForFlushing(1, TimeUnit.SECONDS);
		
		q.enqueue(EXIT);
		qt.join(1000L);
		assertFalse(qt.isAlive());
		
		assertEquals(FlushController.DONE, service.getStatus());
		assertEquals(0L, service.getCounter());
	}
	
	@Test
	public void testCase3_IfNoneHappenedBetweenStartAndWait() throws Exception {
		QueueStub q = new QueueStub(queue, service);
		Thread qt = new Thread(new QueueWorker(queue, service));
		qt.start();
		service.start();
		
		service.waitForFlushing(1, TimeUnit.SECONDS);

		q.enqueue(EXIT);
		qt.join(1000L);
		assertFalse(qt.isAlive());
		
		assertEquals(FlushController.DONE, service.getStatus());
		assertEquals(0L, service.getCounter());
	}

}

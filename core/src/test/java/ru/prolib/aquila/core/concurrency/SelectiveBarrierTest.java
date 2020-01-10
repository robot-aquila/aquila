package ru.prolib.aquila.core.concurrency;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class SelectiveBarrierTest {
	private SelectiveBarrier service;
	private List<Thread> threads;
	private CountDownLatch started, finished;

	@Before
	public void setUp() throws Exception {
		service = new SelectiveBarrier();
	}
	
	private void createThreads(int count) {
		threads = new ArrayList<>();
		started = new CountDownLatch(1);
		finished = new CountDownLatch(count);
		for ( int i = 0; i < count; i ++ ) {
			Thread thread = new Thread("T#" + i) {
				@Override
				public void run() {
					try {
						assertTrue(started.await(1, TimeUnit.SECONDS));
						service.await(1, TimeUnit.SECONDS);
						finished.countDown();
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
			threads.add(thread);
		}
	}

	@Test
	public void testAllowPassIfAllowedForAll() throws Exception {
		createThreads(25);
		//service.setAllowAll(true);
		started.countDown();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testAllowPassIfAllowedThread() throws Exception {
		createThreads(10);
		service.setAllowedThreads(threads.subList(0, 5));
		service.setAllowAll(false);
		started.countDown();
		
		for ( int i = 0; i < 5; i ++ ) {
			threads.get(i).join(1000L);
			assertFalse("At#" + i, threads.get(i).isAlive());
		}
		assertEquals(5, finished.getCount());
		
		service.setAllowAll(true);
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testAllowNewAllowedThreads() throws Exception {
		createThreads(10);
		service.setAllowedThreads(threads.subList(0, 5));
		service.setAllowAll(false);
		started.countDown();
		
		for ( int i = 0; i < 5; i ++ ) {
			threads.get(i).join(1000L);
			assertFalse("At#" + i, threads.get(i).isAlive());
		}
		assertEquals(5, finished.getCount());
		
		service.setAllowedThreads(threads.subList(3, 9));

		for ( int i = 0; i < 9; i ++ ) {
			threads.get(i).join(1000L);
			assertFalse("At#" + i, threads.get(i).isAlive());
		}
		assertEquals(1, finished.getCount());
		
		service.setAllowAll(true);
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}

}

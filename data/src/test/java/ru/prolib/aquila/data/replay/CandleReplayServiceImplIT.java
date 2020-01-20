package ru.prolib.aquila.data.replay;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;

public class CandleReplayServiceImplIT {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CandleReplayServiceImplIT.class);
	}
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	static class TestIterator implements CloseableIterator<Candle> {
		private final ZTFrame tframe;
		private final AtomicBoolean closed;
		private final Lock lock;
		private final Instant from;
		private Candle last;
		
		TestIterator(ZTFrame tframe, Instant from) {
			this.tframe = tframe;
			this.from = from;
			closed = new AtomicBoolean(false);
			lock = new ReentrantLock();
		}

		@Override
		public void close() throws IOException {
			closed.compareAndSet(false, true);
		}

		@Override
		public boolean next() throws IOException {
			if ( closed.get() ) {
				return false;
			}
			lock.lock();
			try {
				Instant next_t = last == null ? from : last.getEndTime();
				ThreadLocalRandom rnd = ThreadLocalRandom.current();
				long open, close, p_min = 100L, p_max = 201L, v_min = 50L, v_max = 1000L;
				last = new CandleBuilder(tframe)
						.withTime(next_t)
						.withOpenPrice(open = rnd.nextLong(p_min, p_max))
						.withClosePrice(close = rnd.nextLong(p_min, p_max))
						.withHighPrice(Math.max(open, close))
						.withLowPrice(Math.min(open, close))
						.withVolume(rnd.nextLong(v_min, v_max))
						.buildCandle();
				return true;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public Candle item() throws IOException, NoSuchElementException {
			lock.lock();
			try {
				if ( last == null ) {
					throw new NoSuchElementException();
				}
				return last;
			} finally {
				lock.unlock();
			}
		}
		
	}
	
	static class TestStorage implements MDStorage<TFSymbol, Candle> {

		@Override
		public Set<TFSymbol> getKeys() throws DataStorageException {
			throw new UnsupportedOperationException();
		}

		@Override
		public CloseableIterator<Candle> createReader(TFSymbol key) throws DataStorageException {
			throw new UnsupportedOperationException();
		}

		@Override
		public CloseableIterator<Candle> createReaderFrom(TFSymbol key, Instant from) throws DataStorageException {
			return new TestIterator(key.getTimeFrame(), from);
		}

		@Override
		public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, int count)
				throws DataStorageException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public CloseableIterator<Candle> createReader(TFSymbol key, Instant from, Instant to)
				throws DataStorageException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public CloseableIterator<Candle> createReader(TFSymbol key, int count, Instant to) throws DataStorageException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void warmingUpReader(TFSymbol key, int count, Instant to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public CloseableIterator<Candle> createReaderTo(TFSymbol key, Instant to) throws DataStorageException {
			throw new UnsupportedOperationException();
		}
		
	}
	
	static class TestCandleListener0 implements CandleListener {

		@Override
		public void onCandle(Instant time, Symbol symbol, Candle candle) {
			
		}
		
	}
	
	static class TestCandleListener1 implements CandleListener {
		private final CountDownLatch finished;
		
		TestCandleListener1(CountDownLatch finished) {
			this.finished = finished;
		}

		@Override
		public void onCandle(Instant time, Symbol symbol, Candle candle) {
			finished.countDown();
		}

	}
	
	static class TestCandleListener2 extends TestCandleListener1 {
		private final SubscrHandler handler;

		TestCandleListener2(SubscrHandler handler, CountDownLatch finished) {
			super(finished);
			this.handler = handler;
		}
		
		@Override
		public void onCandle(Instant time, Symbol symbol, Candle candle) {
			handler.close();
			super.onCandle(time, symbol, candle);
		}
		
	}
	
	abstract static class TestThreadAbstract extends Thread {
		protected final int iteration_count;
		protected final CountDownLatch start, finished;
		
		protected TestThreadAbstract(String name, int iteration_count, CountDownLatch start, CountDownLatch finished) {
			setName(name);
			this.iteration_count = iteration_count;
			this.start = start;
			this.finished = finished;
		}
		
		@Override
		public void run() {
			try {
				assertTrue(start.await(1, TimeUnit.SECONDS));
				for ( int i = 0; i < iteration_count; i ++ ) {
					iteration(i);
				}
				finished.countDown();
			} catch ( Exception e ) {
				logger.error("Unexpected exception: ", e);
			}
		}
		
		abstract protected void iteration(int iteration_index) throws Exception;

	}
	
	/**
	 * Subscribe and unsubscribe in separate thread.
	 * Unsubscribe after desired number of candles.
	 * Number of iterations is configurable.
	 */
	static class TestThread1 extends TestThreadAbstract {
		protected final CandleReplayService service;
		protected final TFSymbol key;
		protected final int candle_count;
		
		TestThread1(String name,
				int iteration_count,
				CountDownLatch start,
				CountDownLatch finished,
				CandleReplayService service,
				TFSymbol key,
				int candle_count)
		{
			super(name, iteration_count, start, finished);
			this.service = service;
			this.key = key;
			this.candle_count = candle_count;
		}

		@Override
		protected void iteration(int i) throws Exception {
			CountDownLatch d = new CountDownLatch(candle_count);
			SubscrHandler handler = service.subscribe(key, new TestCandleListener1(d));
			assertTrue("At#" + i, d.await(1, TimeUnit.SECONDS));
			handler.close();
		}
		
	}
	
	/**
	 * Subscribe and unsubscribe immediately after subscription.
	 */
	static class TestThread2 extends TestThreadAbstract {
		protected final CandleReplayService service;
		protected final TFSymbol key;
		
		TestThread2(String name, 
				int iteration_count,
				CountDownLatch start,
				CountDownLatch finished,
				CandleReplayService service,
				TFSymbol key)
		{
			super(name, iteration_count, start, finished);
			this.service = service;
			this.key = key;
		}

		@Override
		protected void iteration(int i) throws Exception {
			SubscrHandler handler = service.subscribe(key, new TestCandleListener0());
			handler.close();
		}

	}
	
	/**
	 * Make two subscription in separate thread.
	 * The first listener closes subscription of the second.
	 * The key result is that both listeners receive update because running node operates of copy of listeners.
	 */
	static class TestThread3 extends TestThreadAbstract {
		protected final CandleReplayService service;
		protected final TFSymbol key;
		
		TestThread3(String name,
				int iteration_count,
				CountDownLatch start,
				CountDownLatch finished,
				CandleReplayService service,
				TFSymbol key)
		{
			super(name, iteration_count, start, finished);
			this.service = service;
			this.key = key;
		}

		@Override
		protected void iteration(int i) throws Exception {
			CountDownLatch d = new CountDownLatch(2);
			SubscrHandler handler1 = service.subscribe(key, new TestCandleListener1(d));
			SubscrHandler handler2 = service.subscribe(key, new TestCandleListener2(handler1, d));
			assertTrue("At#" + i, d.await(1, TimeUnit.SECONDS));
			handler2.close();
		}

	}
	
	static class TestScheduler implements Scheduler {
		
		static class Cmd {
			static final Cmd END = new Cmd(null);
			final SPRunnable task;
			
			Cmd(SPRunnable task) {
				this.task = task;
			}
			
		}
		
		static class Worker extends Thread {
			private final BlockingQueue<Cmd> queue;

			Worker(BlockingQueue<Cmd> queue) {
				this.queue = queue;
				setDaemon(true);
				setName("TestScheduler#Worker");
			}
			
			@Override
			public void run() {
				Instant curr_time = Instant.now(); // does not matter
				List<SPRunnable> tasks = new ArrayList<>();
				for ( ;; ) {
					Cmd cmd = queue.poll();
					if ( cmd != null ) {
						if ( cmd == Cmd.END ) {
							break;
						} else {
							tasks.add(cmd.task);
						}
					}
					Iterator<SPRunnable> it = tasks.iterator();
					while ( it.hasNext() ) {
						SPRunnable task = it.next();
						if ( task.getNextExecutionTime(curr_time) == null ) {
							it.remove();
						}
					}
					for ( SPRunnable task : tasks ) {
						try {
							task.run();
						} catch ( Exception|AssertionError e ) {
							logger.error("Unhandled exception: ", e);
						}
					}
				}
			}
			
		}
		
		private final BlockingQueue<Cmd> queue;
		private final AtomicBoolean closed;
		private final Worker worker;
		
		TestScheduler() {
			queue = new LinkedBlockingQueue<>();
			closed = new AtomicBoolean(false);
			worker = new Worker(queue);
			worker.start();
		}

		@Override
		public Instant getCurrentTime() {
			if ( closed.get() ) {
				throw new IllegalStateException();
			}
			return Instant.now(); // does not matter
		}
		
		@Override
		public TaskHandler schedule(SPRunnable task) {
			if ( closed.get() ) {
				throw new IllegalStateException();
			}
			try {
				queue.put(new Cmd(task));
			} catch ( Exception e ) {
				logger.error("Unexpected exception: ", e);
			}
			return null;
		}

		@Override
		public void close() {
			try {
				if ( closed.compareAndSet(false, true) ) {
					queue.put(Cmd.END);
				}
			} catch ( Exception e ) {
				logger.error("Unexpected exception: ", e);
			}
		}

		@Override
		public TaskHandler schedule(Runnable task, Instant time) {
			throw new UnsupportedOperationException();
		}

		@Override
		public TaskHandler schedule(Runnable task, Instant firstTime, long period) {
			throw new UnsupportedOperationException();
		}

		@Override
		public TaskHandler schedule(Runnable task, long delay) {
			throw new UnsupportedOperationException();
		}

		@Override
		public TaskHandler schedule(Runnable task, long delay, long period) {
			throw new UnsupportedOperationException();
		}

		@Override
		public TaskHandler scheduleAtFixedRate(Runnable task, Instant firstTime, long period) {
			throw new UnsupportedOperationException();
		}

		@Override
		public TaskHandler scheduleAtFixedRate(Runnable task, long delay, long period) {
			throw new UnsupportedOperationException();
		}

	}
	
	private TestScheduler scheduler;
	private TestStorage storage;
	private CandleReplayServiceImpl service;
	
	@Before
	public void setUp() throws Exception {
		scheduler = new TestScheduler();
		storage = new TestStorage();
		service = new CandleReplayServiceImpl(scheduler, storage);
	}
	
	@After
	public void tearDown() throws Exception {
		scheduler.close();
	}

	@Test
	public void testMultiThreading_SpecialCases() throws Exception {
		List<TFSymbol> keys = new ArrayList<>();
		keys.add(new TFSymbol(new Symbol("GAZP"), ZTFrame.M10MSK));
		keys.add(new TFSymbol(new Symbol("AAPL"), ZTFrame.M15UTC));
		keys.add(new TFSymbol(new Symbol("MSFT"), ZTFrame.H1));
		List<Thread> threads = new ArrayList<>();
		int competitors = 40, iterations = 1000;
		CountDownLatch start = new CountDownLatch(1), finished = new CountDownLatch(competitors * keys.size());
		for ( int i = 0; i < competitors; i ++ ) {
			for ( TFSymbol key : keys ) {
				threads.add(new TestThread1("T1#" + i, iterations / 5, start, finished, service, key, 5));
				threads.add(new TestThread2("T2#" + i, iterations, start, finished, service, key));
				threads.add(new TestThread3("T3#" + i, iterations, start, finished, service, key));
			}
		}
		for ( Thread thread : threads ) {
			thread.start();
		}
		start.countDown();
		
		assertTrue(finished.await(5, TimeUnit.SECONDS));
		for ( Thread thread : threads ) {
			thread.join(15000L);
			assertTrue(thread.getName() + " expected to be finished", !thread.isAlive());
		}
	}

}

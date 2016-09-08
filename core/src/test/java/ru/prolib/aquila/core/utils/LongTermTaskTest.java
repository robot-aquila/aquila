package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class LongTermTaskTest {
	private IMocksControl control;
	private Runnable runnable1Mock, runnable2Mock;
	private LongTermTask task;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		runnable1Mock = control.createMock(Runnable.class);
		runnable2Mock = control.createMock(Runnable.class);
		task = new LongTermTask(runnable1Mock, "foobar");
	}
	
	@Test
	public void testCtor1() throws Exception {
		task = new LongTermTask(runnable1Mock);
		assertEquals(runnable1Mock, task.getRunnable());
		assertEquals("LongTermTask", task.getName());
	}

	@Test
	public void testCtor2() throws Exception {
		assertEquals(runnable1Mock, task.getRunnable());
		assertEquals("foobar", task.getName());
	}

	@Test
	public void testRun() throws Exception {
		final CountDownLatch start = new CountDownLatch(1),
				finish = new CountDownLatch(1);
		LongTermTask task = new LongTermTask(new Runnable() {
			@Override public void run() {
				try {
					start.await(1, TimeUnit.SECONDS);
					finish.countDown();
				} catch ( InterruptedException e ) { }
			}
		});
		task.run();
		
		assertFalse(finish.await(50, TimeUnit.MILLISECONDS));
		start.countDown();
		assertTrue(finish.await(50, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("foobar[" + runnable1Mock + "]", task.toString());
	}

	@Test
	public void testEquals() throws Exception {
		assertTrue(task.equals(task));
		assertTrue(task.equals(new LongTermTask(runnable1Mock, "foobar")));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
		assertFalse(task.equals(new LongTermTask(runnable2Mock, "foobar")));
		assertFalse(task.equals(new LongTermTask(runnable1Mock, "barfoo")));
	}

}

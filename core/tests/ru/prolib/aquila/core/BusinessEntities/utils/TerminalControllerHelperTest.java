package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalControllerHelper;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalStartSequence;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalStopSequence;

/**
 * 2013-02-11<br>
 * $Id$
 */
public class TerminalControllerHelperTest {
	private static TerminalControllerHelper helper;
	private IMocksControl control;
	private EditableTerminal terminal;
	private CountDownLatch counter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		helper = new TerminalControllerHelper();
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		counter = control.createMock(CountDownLatch.class);
	}
	
	@Test
	public void testCreateStartedSignal() {
		CountDownLatch c = helper.createStartedSignal();
		assertNotNull(c);
		assertEquals(1, c.getCount());
	}
	
	@Test
	public void testCreateThread() throws Exception {
		final CountDownLatch c = new CountDownLatch(1);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				c.countDown();
			}
		};
		Thread t = helper.createThread(runnable);
		t.start();
		assertTrue(c.await(1000, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testCreateStartSequence() throws Exception {
		assertEquals(new TerminalStartSequence(terminal, counter),
				helper.createStartSequence(terminal, counter));
	}
	
	@Test
	public void testCreateStopSequence() throws Exception {
		assertEquals(new TerminalStopSequence(terminal, counter),
				helper.createStopSequence(terminal, counter));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(helper.equals(helper));
		assertTrue(helper.equals(new TerminalControllerHelper()));
		assertFalse(helper.equals(null));
		assertFalse(helper.equals(this));
	}

}

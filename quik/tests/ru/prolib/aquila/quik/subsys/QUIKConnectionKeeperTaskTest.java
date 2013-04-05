package ru.prolib.aquila.quik.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

/**
 * 2013-02-11<br>
 * $Id$
 */
public class QUIKConnectionKeeperTaskTest {
	private IMocksControl control;
	private QUIKConnectionKeeper keeper, keeper2;
	private QUIKConnectionKeeperTask task;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		keeper = control.createMock(QUIKConnectionKeeper.class);
		keeper2 = control.createMock(QUIKConnectionKeeper.class);
		task = new QUIKConnectionKeeperTask(keeper);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(keeper, task.getConnectionKeeper());
	}
	
	@Test
	public void testRun() throws Exception {
		keeper.restoreConnection();
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(task.equals(task));
		assertTrue(task.equals(new QUIKConnectionKeeperTask(keeper)));
		assertFalse(task.equals(new QUIKConnectionKeeperTask(keeper2)));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
	}

}

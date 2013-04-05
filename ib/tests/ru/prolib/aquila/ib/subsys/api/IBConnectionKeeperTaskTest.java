package ru.prolib.aquila.ib.subsys.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

/**
 * 2013-01-15<br>
 * $Id: IBConnectionKeeperTaskTest.java 435 2013-01-15 13:27:19Z whirlwind $
 */
public class IBConnectionKeeperTaskTest {
	private static IMocksControl control;
	private static IBConnectionKeeper kpr1,kpr2;
	private static IBConnectionKeeperTask task; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		kpr1 = control.createMock(IBConnectionKeeper.class);
		kpr2 = control.createMock(IBConnectionKeeper.class);
		task = new IBConnectionKeeperTask(kpr1);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testRun() throws Exception {
		kpr1.restoreConnection();
		control.replay();
		task.run();
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(task.equals(task));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
		assertTrue(task.equals(new IBConnectionKeeperTask(kpr1)));
		assertFalse(task.equals(new IBConnectionKeeperTask(kpr2)));
	}

}

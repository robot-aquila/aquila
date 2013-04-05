package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventAccounts;

/**
 * 2012-11-26<br>
 * $Id: IBEventAccountsTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventAccountsTest {
	private static IMocksControl control;
	private static EventType type,type2;
	private static IBEventAccounts event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new IBEventAccounts(type, "AC1,AC2");
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}

	@Test
	public void testEquals() throws Exception {
		assertTrue(event.equals(new IBEventAccounts(type, "AC1,AC2")));
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
		assertFalse(event.equals(new IBEventAccounts(type, "TEST")));
		assertFalse(event.equals(new IBEventAccounts(type2, "TEST")));
		assertFalse(event.equals(new IBEventAccounts(type2, "AC1,AC2")));
	}

}

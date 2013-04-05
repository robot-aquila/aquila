package ru.prolib.aquila.ib.event;


import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;

/**
 * 2012-11-26<br>
 * $Id: IBEventUpdateAccountTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventUpdateAccountTest {
	private static IMocksControl control;
	private static EventType type;
	private static IBEventUpdateAccount event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		event = new IBEventUpdateAccount(type, "key", "val", "USD", "AC1");
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct2() throws Exception {
		EventType type2 = control.createMock(EventType.class);
		IBEventUpdateAccount event2 = new IBEventUpdateAccount(type2, event);
		assertSame(type2, event2.getType());
		assertEquals("key", event2.getKey());
		assertEquals("val", event2.getValue());
		assertEquals("USD", event2.getCurrency());
		assertEquals("AC1", event2.getAccount());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<String> vKey = new Variant<String>(vType)
			.add("key")
			.add("zzz");
		Variant<String> vVal = new Variant<String>(vKey)
			.add("val")
			.add("xxx");
		Variant<String> vCur = new Variant<String>(vVal)
			.add("USD")
			.add("EUR");
		Variant<String> vAcc = new Variant<String>(vCur)
			.add("AC1")
			.add("AC2");
		int foundCnt = 0;
		IBEventUpdateAccount x = null, found = null;
		do {
			x = new IBEventUpdateAccount(vType.get(), vKey.get(),
					vVal.get(), vCur.get(), vAcc.get());
			if ( event.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( vAcc.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals("key", found.getKey());
		assertEquals("val", found.getValue());
		assertEquals("USD", found.getCurrency());
		assertEquals("AC1", found.getAccount());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("IBUpdAcc AC1:key=val USD", event.toString());
	}

}

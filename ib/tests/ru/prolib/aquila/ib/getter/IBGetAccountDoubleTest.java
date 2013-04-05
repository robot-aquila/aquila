package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.getter.IBGetAccountDouble;

/**
 * 2012-12-02<br>
 * $Id: IBGetAccountDoubleTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetAccountDoubleTest {
	private static IMocksControl control;
	private static EventType type;
	private static IBGetAccountDouble getter,getter2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		getter = new IBGetAccountDouble("foo", "USD");
		getter2 = new IBGetAccountDouble("foo");
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct1() throws Exception {
		assertEquals("foo", getter2.getKey());
		assertNull(getter2.getCurrency());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals("foo", getter.getKey());
		assertEquals("USD", getter.getCurrency());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter.equals(getter));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vKey = new Variant<String>()
			.add("foo")
			.add("gap");
		Variant<String> vCur = new Variant<String>(vKey)
			.add("USD")
			.add("EUR")
			.add(null);
		Variant<?> iterator = vCur;
		int foundCnt = 0;
		IBGetAccountDouble found = null, x = null;
		do {
			x = new IBGetAccountDouble(vKey.get(), vCur.get());
			if ( getter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foo", found.getKey());
		assertEquals("USD", found.getCurrency());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121203, 195457)
			.append("foo")
			.append("USD")
			.toHashCode(), getter.hashCode());
		assertEquals(new HashCodeBuilder(20121203, 195457)
			.append("foo")
			.append((Object) null)
			.toHashCode(), getter2.hashCode());
	}
	
	@Test
	public void testGet() throws Exception {
		Object fix[][] = {
				// event, expected1, expected2
				{ crtEvent("bar", "val", "JPY"), null,	null },
				{ crtEvent("bar", "val", "USD"), null,	null },
				{ crtEvent("bar", "2.0", "USD"), null,	null },
				{ crtEvent("foo", "val", "JPY"), null,	null },
				{ crtEvent("foo", "2.0", "JPY"), null,	2.0d },
				{ crtEvent("foo", "2.0", "USD"), 2.0d,	2.0d },
				{ crtEvent("foo", "123", "USD"), 123d,	123d },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "Mismatch at #" + i;
			Double actual1 = getter.get(fix[i][0]);
			if ( fix[i][1] == null ) {
				assertNull(msg, actual1);
			} else {
				assertEquals(msg, fix[i][1], actual1);
			}
			Double actual2 = getter2.get(fix[i][0]);
			if ( fix[i][2] == null ) {
				assertNull(msg, actual2);
			} else {
				assertEquals(msg, fix[i][2], actual2);
			}
		}
	}

	private IBEventUpdateAccount crtEvent(String key, String val, String cur) {
		return new IBEventUpdateAccount(type, key, val, cur, "N/U");
	}

}

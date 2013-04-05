package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;
import ru.prolib.aquila.ib.getter.IBGetOrderRestQty;

/**
 * 2012-12-18<br>
 * $Id: IBGetOrderRestQtyTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderRestQtyTest {
	private static IMocksControl control;
	private static IBEventOrderStatus event;
	private static IBGetOrderRestQty getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		EventType type = control.createMock(EventType.class);
		event = new IBEventOrderStatus(type, 12345, "StatusString",
				10, 20, 120.55d, 0, 0, 120.56d, 1, "x");
		getter = new IBGetOrderRestQty();
	}

	@Test
	public void testGet_Ok() throws Exception {
		assertEquals(20L, (long) getter.get(event));
	}
	
	@Test
	public void testGet_IfNotAnOrder() throws Exception {
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOrderRestQty()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121219, 12311)
			.toHashCode(), getter.hashCode());
	}


}

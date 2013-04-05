package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * 2012-12-25<br>
 * $Id: IBGetOrderExecVolumeTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetOrderExecVolumeTest {
	private static IMocksControl control;
	private static IBEventOrderStatus event;
	private static IBGetOrderExecVolume getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		EventType type = control.createMock(EventType.class);
		event = new IBEventOrderStatus(type, 12345, "StatusString",
				10, 20, 120.55d, 0, 0, 120.56d, 1, "x");
		getter = new IBGetOrderExecVolume();
	}

	@Test
	public void testGet_Ok() throws Exception {
		assertEquals(1205.5d, (double) getter.get(event), 0.01d);
	}
	
	@Test
	public void testGet_IfNotAnOrder() throws Exception {
		assertNull(getter.get(null));
		assertNull(getter.get(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetOrderExecVolume()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121225, 102041)
			.toHashCode(), getter.hashCode());
	}


}

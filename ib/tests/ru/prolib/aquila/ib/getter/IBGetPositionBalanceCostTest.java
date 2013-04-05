package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;
import ru.prolib.aquila.ib.getter.IBGetPositionBalanceCost;

/**
 * 2012-12-30<br>
 * $Id: IBGetPositionBalanceCostTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetPositionBalanceCostTest {
	private static IBEventUpdatePortfolio event;
	private static IBGetPositionBalanceCost getter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		IMocksControl control = createStrictControl();
		EventType type = control.createMock(EventType.class);
		event = new IBEventUpdatePortfolio(type, new Contract(), -100,
				1.0d, 2.0d, 3.0d, 4.0d, 5.0d, "TEST");
		getter = new IBGetPositionBalanceCost();
	}

	@Test
	public void testGet() throws Exception {
		assertEquals(-300.00d, getter.get(event), 0.001d);
		assertNull(getter.get(this));
		assertNull(getter.get(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetPositionBalanceCost()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121231, 224831)
			.append(IBGetPositionBalanceCost.class)
			.toHashCode(), getter.hashCode());
	}

}

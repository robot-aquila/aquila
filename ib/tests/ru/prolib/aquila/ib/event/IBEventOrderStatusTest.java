package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * 2012-12-11<br>
 * $Id: IBEventOrderStatusTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventOrderStatusTest {
	private static IMocksControl control;
	private static EventType type1,type2;
	private static IBEventOrderStatus event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		event = new IBEventOrderStatus(type1, 123, "Pending", 10, 5, 100.00d,
				8, 0, 101.00d, 1, "x");
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type1)
			.add(type2);
		Variant<Integer> vNum = new Variant<Integer>(vType)
			.add(123)
			.add(987);
		Variant<String> vStat = new Variant<String>(vNum)
			.add("Pending")
			.add("Cancelled");
		Variant<Integer> vFil = new Variant<Integer>(vStat)
			.add(10)
			.add(200);
		Variant<Integer> vRem = new Variant<Integer>(vFil)
			.add(5)
			.add(80);
		Variant<Double> vAvg = new Variant<Double>(vRem)
			.add(100.00d)
			.add(120.00d);
		Variant<Integer> vPerm = new Variant<Integer>(vAvg)
			.add(8)
			.add(12345);
		Variant<Integer> vPar = new Variant<Integer>(vPerm)
			.add(0)
			.add(987);
		Variant<Double> vLast = new Variant<Double>(vPar)
			.add(101.00d)
			.add(200.2d);
		Variant<Integer> vCid = new Variant<Integer>(vLast)
			.add(1)
			.add(2);
		Variant<String> vHeld = new Variant<String>(vCid)
			.add("x")
			.add("y");
		Variant<?> iterator = vHeld;
		int foundCnt = 0;
		IBEventOrderStatus x = null, found = null;
		do {
			x = new IBEventOrderStatus(vType.get(), vNum.get(), vStat.get(),
					vFil.get(), vRem.get(), vAvg.get(),
					vPerm.get(), vPar.get(), vLast.get(), vCid.get(),
					vHeld.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertEquals(123, found.getOrderId());
		assertEquals("Pending", found.getStatus());
		assertEquals(10, found.getFilled());
		assertEquals(5, found.getRemaining());
		assertEquals(100.00d, found.getAvgFillPrice(), 0.01d);
		assertEquals(8, found.getPermId());
		assertEquals(0, found.getParentId());
		assertEquals(101.00d, found.getLastFillPrice(), 0.01d);
		assertEquals(1, found.getClientId());
		assertEquals("x", found.getWhyHeld());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121211, 193057)
			.append(type1)
			.append(123)
			.append("Pending")
			.append(10)
			.append(5)
			.append(100.00d)
			.append(8)
			.append(0)
			.append(101.00d)
			.append(1)
			.append("x")
			.toHashCode();
		assertEquals(hashCode, event.hashCode());
	}

}

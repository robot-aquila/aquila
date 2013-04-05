package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;

import com.ib.client.*;

/**
 * 2012-12-11<br>
 * $Id: IBEventOpenOrderTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventOpenOrderTest {
	private static IMocksControl control;
	private static Contract c1,c2;
	private static EventType type1,type2;
	private static Order o1,o2;
	private static OrderState s1,s2;
	private static IBEventOpenOrder event;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		c1 = new Contract(); c1.m_symbol = "AAPL"; c1.m_conId = 112;
		c2 = new Contract(); c2.m_symbol = "IBKR"; c2.m_conId = 182;
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		o1 = new Order(); o1.m_permId = 100; o1.m_clientId = 1;
		o2 = new Order(); o2.m_permId = 200; o2.m_clientId = 2;
		s1 = control.createMock(OrderState.class);
		s2 = control.createMock(OrderState.class);
		event = new IBEventOpenOrder(type1, 123, c1, o1, s1);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testGetContractId() throws Exception {
		assertEquals(112, event.getContractId());
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(type1, event.getType());
		assertEquals(123, event.getOrderId());
		assertSame(c1, event.getContract());
		assertSame(o1, event.getOrder());
		assertSame(s1, event.getOrderState());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
			.add(type1)
			.add(type2);
		Variant<Integer> vNum = new Variant<Integer>(vType)
			.add(123)
			.add(800);
		Variant<Contract> vCon = new Variant<Contract>(vNum)
			.add(c1)
			.add(c2);
		Variant<Order> vOrd = new Variant<Order>(vCon)
			.add(o1)
			.add(o2);
		Variant<OrderState> vSta = new Variant<OrderState>(vOrd)
			.add(s1)
			.add(s2);
		Variant<?> iterator = vSta;
		int foundCnt = 0;
		IBEventOpenOrder x = null, found = null;
		do {
			x = new IBEventOpenOrder(vType.get(), vNum.get().intValue(),
					vCon.get(), vOrd.get(), vSta.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertEquals(123, found.getOrderId());
		assertSame(c1, found.getContract());
		assertSame(o1, found.getOrder());
		assertSame(s1, found.getOrderState());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121211, 115527)
			.append(type1)
			.append(123)
			.append(c1)
			.append(o1)
			.append(s1)
			.toHashCode();
		assertEquals(hashCode, event.hashCode());
	}

}

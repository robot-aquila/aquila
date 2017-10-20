package ru.prolib.aquila.ib.assembler.cache;

import static org.junit.Assert.*;
import java.lang.reflect.Constructor;
import java.util.Date;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Direction;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.assembler.cache.OrderEntry;

import com.ib.client.*;

public class OrderEntryTest {
	private Contract contract;
	private Order order;
	private OrderState state;
	private OrderEntry entry;

	@Before
	public void setUp() throws Exception {
		contract = new Contract();
		contract.m_conId = 1015;
		order = new Order();
		order.m_permId = 84;
		order.m_orderId = 924;
		state = createState();
		state.m_status = "foo";
		entry = new OrderEntry(125, contract, order, state);
	}
	
	/**
	 * Создать экземпляр состояния заявки.
	 * <p>
	 * Конструктор класса статуса защищенный. Данный метод создает экземпляр
	 * используя рефлекшн API.
	 * <p>
	 * @return новый экземпляр состояния
	 * @throws Exception
	 */
	private OrderState createState() throws Exception {
		Constructor<OrderState> con = OrderState.class.getDeclaredConstructor();
		con.setAccessible(true);
		con.newInstance();
		return con.newInstance();		
	}
	
	@Test
	public void testGetEntryTime() throws Exception {
		entry = new OrderEntry(125, contract, order, state);
		assertEquals(new Date(), entry.getEntryTime());
	}
	
	@Test
	public void testGetContractId() throws Exception {
		contract.m_conId = 829;
		assertEquals(829, entry.getContractId());
	}
	
	@Test
	public void testGetContract() throws Exception {
		assertSame(contract, entry.getContract());
	}
	
	@Test
	public void testGetOrder() throws Exception {
		assertSame(order, entry.getOrder());
	}
	
	@Test
	public void testGetOrderState() throws Exception {
		assertSame(state, entry.getOrderState());
	}
	
	@Test
	public void testGetId() throws Exception {
		assertEquals(125, entry.getId());
	}
	
	@Test
	public void testGetAccount() throws Exception {
		order.m_account = "TEST";
		assertEquals(new Account("TEST"), entry.getAccount());
	}
	
	@Test
	public void testGetDirection() throws Exception {
		order.m_action = "BUY";
		assertEquals(Direction.BUY, entry.getDirection());
		order.m_action = "SELL";
		assertEquals(Direction.SELL, entry.getDirection());
		order.m_action = "unsupported";
		assertNull(entry.getDirection());
	}
	
	@Test
	public void testGetPrice() throws Exception {
		order.m_lmtPrice = 12.34d;
		assertEquals(12.34d, entry.getPrice(), 0.01d);
		order.m_lmtPrice = 0d;
		assertNull(entry.getPrice());
	}
	
	@Test
	public void testGetStopLimitPrice() throws Exception {
		order.m_auxPrice = 82.15d;
		assertEquals(82.15d, entry.getStopLimitPrice(), 0.01d);
		order.m_auxPrice = 0d;
		assertNull(entry.getStopLimitPrice());
	}
	
	@Test
	public void testGetQty() throws Exception {
		order.m_totalQuantity = 100;
		assertEquals(new Long(100), entry.getQty());
	}
	
	@Test
	public void testGetType() throws Exception {
		Object fix[][] = {
				// IB type, local type
				{ "LMT", OrderType.LIMIT },
				{ "MKT", OrderType.MARKET },
				{ "STP LMT", null },
				{ "unknown", null },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			order.m_orderType = (String) fix[i][0];
			assertEquals(msg, fix[i][1], entry.getType());
		}
	}
	
	@Test
	public void testGetStatus() throws Exception {
		Object fix[][] = {
				// IB status, local status
				{ "PendingSubmit", OrderStatus.SENT },
				{ "PendingCancel", OrderStatus.CANCEL_SENT },
				{ "PreSubmitted", OrderStatus.ACTIVE },
				{ "Submitted", OrderStatus.ACTIVE },
				{ "Cancelled", OrderStatus.CANCELLED },
				{ "Filled", OrderStatus.FILLED },
				{ "Inactive", OrderStatus.ACTIVE },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			state.m_status = (String) fix[i][0];
			assertEquals(msg, fix[i][1], entry.getStatus());
		}
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(entry.equals(entry));
		assertFalse(entry.equals(null));
		assertFalse(entry.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		Contract cont2 = new Contract();
		cont2.m_conId = 870;
		Order order2 = new Order();
		order2.m_permId = 115;
		order2.m_orderId = 719;
		OrderState state2 = createState();
		state2.m_status = "bar";
		Variant<Integer> vId = new Variant<Integer>()
			.add(125)
			.add(213);
		Variant<Contract> vCont = new Variant<Contract>(vId)
			.add(contract)
			.add(cont2);
		Variant<Order> vOrd = new Variant<Order>(vCont)
			.add(order)
			.add(order2);
		Variant<OrderState> vStat = new Variant<OrderState>(vOrd)
			.add(state)
			.add(state2);
		Variant<?> iterator = vStat;
		int foundCnt = 0;
		OrderEntry x = null, found = null;
		do {
			x = new OrderEntry(vId.get(), vCont.get(), vOrd.get(), vStat.get());
			if ( entry.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(125, found.getId());
		assertSame(contract, found.getContract());
		assertSame(order, found.getOrder());
		assertSame(state, found.getOrderState());
	}
	
	@Test
	public void testIsKnownType() throws Exception {
		Object fix[][] = {
				// IB type, expected result
				{ "STP LMT", false },
				{ "MKT", true },
				{ "LMT", true },
				{ "UNK", false },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			order.m_orderType = (String) fix[i][0];
			assertEquals(msg, fix[i][1], entry.isKnownType());
		}
	}

}

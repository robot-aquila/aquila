package ru.prolib.aquila.ib.assembler;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

import com.ib.client.Contract;
import com.ib.client.Order;

public class PlaceOrderRequestTest {
	private Contract contract;
	private Order order;
	private PlaceOrderRequest request;

	@Before
	public void setUp() throws Exception {
		contract = new Contract();
		contract.m_conId = 815;
		order = new Order();
		order.m_orderId = 429;
		order.m_permId = 244;
		request = new PlaceOrderRequest(contract, order);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(request.equals(request));
		assertFalse(request.equals(null));
		assertFalse(request.equals(this));
	}

	@Test
	public void testEquals() throws Exception {
		Variant<Contract> vCon = new Variant<Contract>()
			.add(contract)
			.add(new Contract());
		Variant<Order> vOrd = new Variant<Order>(vCon)
			.add(order)
			.add(new Order());
		Variant<?> iterator = vOrd;
		int foundCnt = 0;
		PlaceOrderRequest x, found = null;
		do {
			x = new PlaceOrderRequest(vCon.get(), vOrd.get());
			if ( request.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(429, found.getOrderId());
		assertSame(contract, found.getContract());
		assertSame(order, found.getOrder());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		request = new PlaceOrderRequest(contract);
		assertNotNull(request.getOrder());
		assertEquals(new Order(), request.getOrder());
		assertSame(contract, request.getContract());
	}
	
}

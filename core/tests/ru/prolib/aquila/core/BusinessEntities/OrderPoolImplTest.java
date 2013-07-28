package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.utils.OrderPoolBase;
import ru.prolib.aquila.core.utils.Variant;

public class OrderPoolImplTest {
	private static Account account = new Account("TEST"),
		account2 = new Account("BEST");
	private IMocksControl control;
	private Terminal terminal;
	private Security security, security2;
	private EditableOrder order;
	private OrderPoolBase base;
	private OrderPoolImpl pool;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		security = control.createMock(Security.class);
		security2 = control.createMock(Security.class);
		order = control.createMock(EditableOrder.class);
		base = control.createMock(OrderPoolBase.class);
		pool = new OrderPoolImpl(terminal, base);
		pool.setAccount(account);
		pool.setSecurity(security);
	}
	
	@Test
	public void testPlaceOrders() throws Exception {
		base.placeOrders();
		control.replay();
		
		pool.placeOrders();
		
		control.verify();
	}
	
	@Test
	public void testCancelOrders() throws Exception {
		base.cancelOrders();
		control.replay();
		
		pool.cancelOrders();
		
		control.verify();
	}
	
	@Test
	public void testSetAccount() throws Exception {
		pool.setAccount(null);
		assertNull(pool.getAccount());
		pool.setAccount(account);
		assertSame(account, pool.getAccount());
	}
	
	@Test
	public void testSetSecurity() throws Exception {
		pool.setSecurity(null);
		assertNull(pool.getSecurity());
		pool.setSecurity(security);
		assertSame(security, pool.getSecurity());
	}
	
	@Test
	public void testBuy_LD() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security, 10L, 2d))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(10L, 2d));
		
		control.verify();
	}
	
	@Test
	public void testSell_LD() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security, 5L, 10d))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(5L, 10d));
		
		control.verify();
	}
	
	@Test
	public void testBuy_LDD() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.BUY),
				same(security), eq(10L), eq(15d),
				eq(new StopOrderActivator(13d)))).andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(10L, 15d, 13d));
		
		control.verify();
	}
	
	@Test
	public void testSell_LDD() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.SELL),
				same(security), eq(5L), eq(15d),
				eq(new StopOrderActivator(18d)))).andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(5L, 15d, 18d));
		
		control.verify();
	}
	
	@Test
	public void testBuy_SLD() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security2, 10L, 1d))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(security2, 10L, 1d));
		
		control.verify();
	}
	
	@Test
	public void testSell_SLD() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security2, 5L, 2d))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(security2, 5L, 2d));
		
		control.verify();
	}
	
	@Test
	public void testBuy_SLDD() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.BUY),
				same(security2), eq(10L), eq(12d),
				eq(new StopOrderActivator(11d)))).andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(security2, 10L, 12d, 11d));
		
		control.verify();
	}
	
	@Test
	public void testSell_SLDD() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.SELL),
				same(security2), eq(5L), eq(15d),
				eq(new StopOrderActivator(18d)))).andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(security2, 5L, 15d, 18d));
		
		control.verify();
	}
	
	@Test
	public void testBuy_ASLD() throws Exception {
		expect(terminal.createOrder(account2, Direction.BUY, security2, 1L, 2d))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(account2, security2, 1L, 2d));
		
		control.verify();
	}
	
	@Test
	public void testSell_ASLD() throws Exception {
		expect(terminal.createOrder(account2, Direction.SELL, security2, 5L,3d))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(account2, security2, 5L, 3d));
		
		control.verify();
	}
	
	@Test
	public void testBuy_ASLDD() throws Exception {
		expect(terminal.createOrder(eq(account2), eq(Direction.BUY),
				eq(security2), eq(1L), eq(6d), eq(new StopOrderActivator(5d))))
				.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(account2, security2, 1L, 6d, 5d));
		
		control.verify();
	}
	
	@Test
	public void testSell_ASLDD() throws Exception {
		expect(terminal.createOrder(eq(account2), eq(Direction.SELL),
				eq(security2), eq(7L), eq(1d), eq(new StopOrderActivator(2d))))
				.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(account2, security2, 7L, 1d, 2d));
		
		control.verify();
	}
	
	@Test
	public void testAdd() throws Exception {
		expect(base.add(order)).andReturn(order);
		control.replay();
		
		assertSame(order, pool.add(order));
		
		control.verify();
	}
	
	@Test
	public void testGetTerminal() throws Exception {
		assertSame(terminal, pool.getTerminal());
	}
	
	@Test
	public void testBuy_L() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security, 8L))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(8L));
		
		control.verify();
	}
	
	@Test
	public void testSell_L() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security, 5L))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(5L));
		
		control.verify();
	}
	
	@Test
	public void testIsPooled() throws Exception {
		expect(base.isPooled(order)).andReturn(true);
		control.replay();
		
		assertTrue(pool.isPooled(order));
		
		control.verify();
	}
	
	@Test
	public void testIsPending() throws Exception {
		expect(base.isPending(order)).andReturn(true);
		control.replay();
		
		assertTrue(pool.isPending(order));
		
		control.verify();
	}
	
	@Test
	public void testIsActive() throws Exception {
		expect(base.isActive(order)).andReturn(true);
		control.replay();
		
		assertTrue(pool.isActive(order));
		
		control.verify();
	}
	
	@Test
	public void testIsDone() throws Exception {
		expect(base.isDone(order)).andReturn(true);
		control.replay();
		
		assertTrue(pool.isDone(order));
		
		control.verify();
	}
	
	@Test
	public void testBuy_SL() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security2, 8L))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(security2, 8L));
		
		control.verify();
	}
	
	@Test
	public void testSell_SL() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security2, 12L))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(security2, 12L));
		
		control.verify();
	}
	
	@Test
	public void testBuy_ASL() throws Exception {
		expect(terminal.createOrder(account2, Direction.BUY, security2, 15L))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(account2, security2, 15L));
		
		control.verify();
	}
	
	@Test
	public void testSell_ASL() throws Exception {
		expect(terminal.createOrder(account2, Direction.SELL, security2, 10L))
			.andReturn(order);
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(account2, security2, 10L));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(pool.equals(pool));
		assertFalse(pool.equals(null));
		assertFalse(pool.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Terminal> vTerm = new Variant<Terminal>()
			.add(terminal)
			.add(control.createMock(Terminal.class));
		Variant<OrderPoolBase> vBase = new Variant<OrderPoolBase>(vTerm)
			.add(base)
			.add(control.createMock(OrderPoolBase.class));
		Variant<Account> vAcc = new Variant<Account>(vBase)
			.add(account)
			.add(account2);
		Variant<Security> vSec = new Variant<Security>(vAcc)
			.add(security)
			.add(security2);
		Variant<?> iterator = vSec;
		int foundCnt = 0;
		OrderPoolImpl x, found = null;
		do {
			x = new OrderPoolImpl(vTerm.get(), vBase.get());
			x.setAccount(vAcc.get());
			x.setSecurity(vSec.get());
			if ( pool.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(security, found.getSecurity());
		assertSame(account, found.getAccount());
		assertSame(base, found.getPoolBase());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		OrderPool expected = new OrderPoolImpl(terminal, new OrderPoolBase());
		assertEquals(expected, new OrderPoolImpl(terminal));
	}

}

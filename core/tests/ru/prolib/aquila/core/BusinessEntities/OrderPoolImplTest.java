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
	
	@Test
	public void testBuy_LC() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security, 8L))
			.andReturn(order);
		order.setComment(eq("zulu4"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(8L, "zulu4"));
		
		control.verify();
	}
	
	@Test
	public void testSell_LC() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security, 1L))
			.andReturn(order);
		order.setComment(eq("foobar"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(1L, "foobar"));
		
		control.verify();
	}
	
	@Test
	public void testBuy_LDC() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security, 5L, 2d))
			.andReturn(order);
		order.setComment(eq("bizon"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(5L, 2d, "bizon"));
		
		control.verify();
	}
	
	@Test
	public void testSell_LDC() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security, 2L, 5d))
			.andReturn(order);
		order.setComment(eq("bazooka"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(2L, 5d, "bazooka"));
		
		control.verify();
	}
	
	@Test
	public void testBuy_LDDC() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.BUY),
				eq(security), eq(10L), eq(8d), eq(new StopOrderActivator(9d))))
			.andReturn(order);
		order.setComment(eq("altair"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(10L, 8d, 9d, "altair"));
		
		control.verify();
	}
	
	@Test
	public void testSell_LDDC() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.SELL),
				eq(security), eq(15L), eq(9d), eq(new StopOrderActivator(11d))))
			.andReturn(order);
		order.setComment(eq("vector"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(15L, 9d, 11d, "vector"));
		
		control.verify();
	}
	
	@Test
	public void testBuy_SLC() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security2, 7L))
			.andReturn(order);
		order.setComment(eq("kappa"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(security2, 7L, "kappa"));
		
		control.verify();
	}
	
	@Test
	public void testSell_SLC() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security2, 4L))
			.andReturn(order);
		order.setComment(eq("zippo"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(security2, 4L, "zippo"));
		
		control.verify();
	}
	
	@Test
	public void testBuy_ASLC() throws Exception {
		expect(terminal.createOrder(account2, Direction.BUY, security2, 1L))
			.andReturn(order);
		order.setComment(eq("value"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(account2, security2, 1L, "value"));
		
		control.verify();
	}
	
	@Test
	public void testSell_ASLC() throws Exception {
		expect(terminal.createOrder(account2, Direction.SELL, security2, 2L))
			.andReturn(order);
		order.setComment(eq("zebra"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(account2, security2, 2L, "zebra"));
		
		control.verify();
	}
	
	@Test
	public void testBuy_SLDC() throws Exception {
		expect(terminal.createOrder(account, Direction.BUY, security2, 9L, 1d))
			.andReturn(order);
		order.setComment(eq("gizmo"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(security2, 9L, 1d, "gizmo"));
		
		control.verify();
	}
	
	@Test
	public void testSell_SLDC() throws Exception {
		expect(terminal.createOrder(account, Direction.SELL, security2, 3L, 4d))
			.andReturn(order);
		order.setComment(eq("vacuum"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(security2, 3L, 4d, "vacuum"));
		
		control.verify();
	}

	@Test
	public void testBuy_SLDDC() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.BUY),
				eq(security2), eq(18L), eq(24d),
				eq(new StopOrderActivator(25d))))
			.andReturn(order);
		order.setComment(eq("bitebox"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(security2, 18L, 24d, 25d, "bitebox"));
		
		control.verify();
	}
	
	@Test
	public void testSell_SLDDC() throws Exception {
		expect(terminal.createOrder(eq(account), eq(Direction.SELL),
				eq(security2), eq(12L), eq(18d),
				eq(new StopOrderActivator(13d))))
			.andReturn(order);
		order.setComment(eq("zimbabwe"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(security2, 12L, 18d, 13d, "zimbabwe"));
		
		control.verify();
	}
	
	@Test
	public void testBuy_ASLDC() throws Exception {
		expect(terminal.createOrder(account2, Direction.BUY, security2, 6L, 1d))
			.andReturn(order);
		order.setComment(eq("hacker"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(account2, security2, 6L, 1d, "hacker"));
		
		control.verify();
	}
	
	@Test
	public void testSell_ASLDC() throws Exception {
		expect(terminal.createOrder(account2, Direction.SELL, security2, 1L,5d))
			.andReturn(order);
		order.setComment(eq("foobar"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(account2, security2, 1L, 5d, "foobar"));
		
		control.verify();
	}
	
	@Test
	public void testBuy_ASLDDC() throws Exception {
		expect(terminal.createOrder(eq(account2), eq(Direction.BUY),
				eq(security2), eq(25L), eq(80d),
				eq(new StopOrderActivator(82d))))
			.andReturn(order);
		order.setComment(eq("toth"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.buy(account2, security2, 25L, 80d, 82d, "toth"));
		
		control.verify();
	}
	
	@Test
	public void testSell_ASLDDC() throws Exception {
		expect(terminal.createOrder(eq(account2), eq(Direction.SELL),
				eq(security2), eq(15L), eq(8d),
				eq(new StopOrderActivator(9d))))
			.andReturn(order);
		order.setComment(eq("zoo"));
		expect(base.add(same(order))).andReturn(order);
		control.replay();
		
		assertSame(order, pool.sell(account2, security2, 15L, 8d, 9d, "zoo"));
		
		control.verify();
	}
	
}

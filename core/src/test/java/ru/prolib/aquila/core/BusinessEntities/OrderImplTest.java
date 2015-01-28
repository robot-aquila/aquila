package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-22<br>
 * $Id: OrderImplTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderImplTest {
	private static SimpleDateFormat format;
	private static Account account;
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private OrderEventDispatcher dispatcher;
	private EventType type;
	private OrderImpl order;
	private S<OrderImpl> setter;
	private G<?> getter;
	private List<OrderStateHandler> stateHandlers;
	private Terminal terminal;
	private OrderActivator activator;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		account = new Account("LX01");
		descr = new SecurityDescriptor("AAPL", "SMART", "USD",SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(OrderEventDispatcher.class);
		activator = control.createMock(OrderActivator.class);
		type = control.createMock(EventType.class);
		stateHandlers = new LinkedList<OrderStateHandler>();
		terminal = control.createMock(Terminal.class); 

		order = new OrderImpl(dispatcher, stateHandlers, terminal);
		setter = null;
		getter = null;
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * Так же устанавливает объем, равный qty * price.
	 * <p>
	 * @param id номер сделки
	 * @param time время сделки в формате yyyy-MM-dd HH:mm:ss
	 * @param price цена
	 * @param qty количество
	 * @return сделка
	 * @throws Exception
	 */
	private Trade createTrade(Long id, String time, Double price, Long qty)
			throws Exception
	{
		return createTrade(id, time, price, qty, price * qty);
	}
	
	/**
	 * Создать сделку.
	 * <p>
	 * @param id номер сделки
	 * @param time время сделки в формате yyyy-MM-dd HH:mm:ss
	 * @param price цена
	 * @param qty количество
	 * @param vol объем сделки
	 * @return сделка
	 * @throws Exception
	 */
	private Trade createTrade(Long id, String time, Double price, Long qty,
			Double vol) throws Exception
	{
		Trade trade = new Trade(terminal);
		trade.setId(id);
		trade.setPrice(price);
		trade.setQty(qty);
		trade.setSecurityDescriptor(descr);
		trade.setTime(new DateTime(format.parse(time)));
		trade.setVolume(vol);
		return trade;		
	}
	
	@Test
	public void testVersion() throws Exception {
		assertEquals(1, Order.VERSION);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, order.getTerminal());
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertEquals(0x06, OrderImpl.VERSION);
		assertNull(order.getAccount());
		assertNull(order.getSecurityDescriptor());
		assertNull(order.getId());
		assertNull(order.getDirection());
		assertNull(order.getType());
		assertSame(OrderStatus.PENDING, order.getStatus());
		assertNull(order.getPreviousStatus());
		assertNull(order.getQty());
		assertNull(order.getQtyRest());
		assertNull(order.getPrice());
		assertEquals(0.0d, order.getExecutedVolume(), 0.01d);
		assertNull(order.getAvgExecutedPrice());
		assertNull(order.getTime());
		assertNull(order.getLastChangeTime());
		assertEquals(new OrderSystemInfo(), order.getSystemInfo());
	}
	
	@Test
	public void testOnRegister() throws Exception {
		expect(dispatcher.OnRegistered()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnRegistered());
		
		control.verify();
	}

	@Test
	public void testOnRegisterFailed() throws Exception {
		expect(dispatcher.OnRegisterFailed()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnRegisterFailed());
		
		control.verify();
	}
	
	@Test
	public void testOnCancelled() throws Exception {
		expect(dispatcher.OnCancelled()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnCancelled());
		
		control.verify();
	}
	
	@Test
	public void testOnCancelFailed() throws Exception {
		expect(dispatcher.OnCancelFailed()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnCancelFailed());
		
		control.verify();
	}

	@Test
	public void testOnFilled() throws Exception {
		expect(dispatcher.OnFilled()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnFilled());
		
		control.verify();
	}
	
	@Test
	public void testOnPartiallyFilled() throws Exception {
		expect(dispatcher.OnPartiallyFilled()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnPartiallyFilled());
		
		control.verify();
	}

	@Test
	public void testOnChanged() throws Exception {
		expect(dispatcher.OnChanged()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnChanged());
		
		control.verify();
	}
	
	@Test
	public void testOnDone() throws Exception {
		expect(dispatcher.OnDone()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnDone());
		
		control.verify();
	}
	
	@Test
	public void testOnFailed() throws Exception {
		expect(dispatcher.OnFailed()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnFailed());
		
		control.verify();
	}
	
	@Test
	public void testOnTrade() throws Exception {
		expect(dispatcher.OnTrade()).andReturn(type);
		control.replay();
		
		assertSame(type, order.OnTrade());
		
		control.verify();
	}
	
	/**
	 * Проверить работу геттера/сеттера с проверкой признака изменения.
	 * <p>
	 * Метод использует текущий экземпляр {@link #order}, {@link #setter} и
	 * {@link #getter}.
	 * <p>
	 * @param firstValue начальное значение
	 * @param secondValue конечное значение
	 * @param changeId идентификатор изменения для проверки (null - пропустить)
	 */
	private void testSetterGetterF(Object firstValue,
			Object secondValue, Integer changeId) throws Exception
	{
		Object fixture[][] = {
				{ null, 		null,			false },
				{ null, 		secondValue,	true  },
				{ firstValue,	secondValue,	true  },
				{ secondValue,	secondValue,	false },
				{ firstValue,   null,			true  },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			setter.set(order, fixture[i][0]);
			order.resetChanges();
			setter.set(order, fixture[i][1]);
			boolean expected = (Boolean) fixture[i][2];
			assertEquals(msg, expected, order.hasChanged());
			if ( changeId != null ) {
				assertEquals(msg, expected, order.hasChanged(changeId));
			}
			assertEquals(msg, fixture[i][1], getter.get(order));
		}
	}
	
	/**
	 * Проверить работу геттера/сеттера.
	 * <p>
	 * Метод использует текущий экземпляр {@link #order}, {@link #setter} и
	 * {@link #getter}.
	 * <p>
	 * @param firstValue начальное значение
	 * @param secondValue конечное значение
	 */
	private void testSetterGetter(Object firstValue, Object secondValue)
			throws Exception
	{
		testSetterGetterF(firstValue, secondValue, null);
	}
	
	@Test
	public void testSetAccount() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setAccount((Account) value);
			}
		};
		getter = new G<Account>() {
			@Override
			public Account get(Object object) throws ValueException {
				return ((OrderImpl) object).getAccount();
			}
		};
		testSetterGetter(new Account("LX001"), new Account("ZZZZZ"));
	}
	
	@Test
	public void testSetDirection() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setDirection((Direction) value);
			}
		};
		getter = new G<Direction>() {
			@Override
			public Direction get(Object object) throws ValueException {
				return ((OrderImpl) object).getDirection();
			}
		};
		testSetterGetter(Direction.BUY, Direction.SELL);
	}
	
	@Test
	public void testSetId() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setId((Integer) value);
			}
		};
		getter = new G<Integer>() {
			@Override
			public Integer get(Object object) throws ValueException {
				return ((OrderImpl) object).getId();
			}
		};
		testSetterGetter(100, 200);
	}
	
	@Test
	public void testGetPortfolio() throws Exception {
		order.setAccount(account);
		Portfolio portfolio = control.createMock(Portfolio.class);
		expect(terminal.getPortfolio(eq(account))).andReturn(portfolio);
		control.replay();
		assertSame(portfolio, order.getPortfolio());
		control.verify();
	}
	
	@Test
	public void testSetPrice() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setPrice((Double) value);
			}
		};
		getter = new G<Double>() {
			@Override
			public Double get(Object object) throws ValueException {
				return ((OrderImpl) object).getPrice();
			}
		};
		testSetterGetter(120.25D, 130.10D);
	}

	@Test
	public void testSetQty() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setQty((Long) value);
			}
		};
		getter = new G<Long>() {
			@Override
			public Long get(Object object) throws ValueException {
				return ((OrderImpl) object).getQty();
			}
		};
		testSetterGetter(1000L, 2000L);
	}
	
	@Test
	public void testSetQtyRest() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setQtyRest((Long) value);
			}
		};
		getter = new G<Long>() {
			@Override
			public Long get(Object object) throws ValueException {
				return ((OrderImpl) object).getQtyRest();
			}
		};
		testSetterGetter(5000L, 0L);
	}
	
	@Test
	public void testGetSecurity() throws Exception {
		order.setSecurityDescriptor(descr);
		Security security = control.createMock(Security.class);
		expect(terminal.getSecurity(eq(descr))).andReturn(security);
		control.replay();
		assertSame(security, order.getSecurity());
		control.verify();
	}
	
	@Test
	public void testSetSecurityDescriptor() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setSecurityDescriptor((SecurityDescriptor) value);
			}
		};
		getter = new G<SecurityDescriptor>() {
			@Override
			public SecurityDescriptor get(Object object) throws ValueException {
				return ((OrderImpl) object).getSecurityDescriptor();
			}
		};
		testSetterGetter(descr,
			new SecurityDescriptor("USD","IDEALPRO","USD",SecurityType.CASH));
	}
	
	@Test
	public void testSetType() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setType((OrderType) value);
			}
		};
		getter = new G<OrderType>() {
			@Override
			public OrderType get(Object object) throws ValueException {
				return ((OrderImpl) object).getType();
			}
		};
		testSetterGetter(OrderType.LIMIT, OrderType.MARKET);
	}

	@Test
	public void testSetStatus() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setStatus((OrderStatus) value);
			}
		};
		getter = new G<OrderStatus>() {
			@Override
			public OrderStatus get(Object object) throws ValueException {
				return ((OrderImpl) object).getStatus();
			}
		};
		testSetterGetterF(OrderStatus.ACTIVE, OrderStatus.FILLED,
				EditableOrder.STATUS_CHANGED);
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		OrderStateHandler h1,h2,h3;
		h1 = control.createMock(OrderStateHandler.class);
		h2 = control.createMock(OrderStateHandler.class);
		h3 = control.createMock(OrderStateHandler.class);
		stateHandlers.add(h1);
		stateHandlers.add(h2);
		stateHandlers.add(h3);
		h1.handle(order);
		h2.handle(order);
		h3.handle(order);
		control.replay();
		
		order.fireChangedEvent();
		
		control.verify();
	}
	
	@Test
	public void testSetExecutedVolume() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setExecutedVolume((Double) value);
			}
		};
		getter = new G<Double>() {
			@Override
			public Double get(Object object) throws ValueException {
				return ((OrderImpl) object).getExecutedVolume();
			}
		};
		testSetterGetter(12.34d, 34.56d);
	}
	
	@Test
	public void testSetAvgExecutedPrice() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setAvgExecutedPrice((Double) value);
			}
		};
		getter = new G<Double>() {
			@Override
			public Double get(Object object) throws ValueException {
				return ((OrderImpl) object).getAvgExecutedPrice();
			}
		};
		testSetterGetter(152.14d, 534.56d);
	}
	
	@Test
	public void testSetStatus_ChangesPreviousStatus() throws Exception {
		assertSame(OrderStatus.PENDING, order.getStatus());
		assertNull(order.getPreviousStatus());
		order.setStatus(OrderStatus.ACTIVE);
		assertSame(OrderStatus.ACTIVE, order.getStatus());
		assertSame(OrderStatus.PENDING, order.getPreviousStatus());
		order.setStatus(OrderStatus.FILLED);
		assertSame(OrderStatus.FILLED, order.getStatus());
		assertSame(OrderStatus.ACTIVE, order.getPreviousStatus());
	}
	
	@Test
	public void testSetTime() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setTime((DateTime) value);
			}
		};
		getter = new G<DateTime>() {
			@Override
			public DateTime get(Object object) throws ValueException {
				return ((OrderImpl) object).getTime();
			}
		};
		testSetterGetter(new DateTime(2011, 1, 1, 0, 0, 0),
						 new DateTime(2013, 2, 21, 6, 58, 0));
	}
	
	@Test
	public void testSetLastChangeTime() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setLastChangeTime((DateTime) value);
			}
		};
		getter = new G<DateTime>() {
			@Override
			public DateTime get(Object object) throws ValueException {
				return ((OrderImpl) object).getLastChangeTime();
			}
		};
		testSetterGetter(new DateTime(1991, 7,  1, 13,  0, 0),
						 new DateTime(2032, 2, 21,  6, 58, 0));	
	}
	
	@Test
	public void testSetActivator() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setActivator((OrderActivator) value);
			}
		};
		getter = new G<OrderActivator>() {
			@Override
			public OrderActivator get(Object object) {
				return ((OrderImpl) object).getActivator();
			}
		};
		OrderActivator a1 = control.createMock(OrderActivator.class),
			a2 = control.createMock(OrderActivator.class);
		testSetterGetter(a1, a2);
	}
	
	@Test
	public void testSetComment() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) {
				object.setComment((String) value);
			}
		};
		getter = new G<String>() {
			@Override
			public String get(Object object) {
				return ((OrderImpl) object).getComment();
			}
		};
		testSetterGetter("foo", "bar");
	}
	
	@Test
	public void testAddGetTrades() throws Exception {
		order.setQty(25L);
		List<Trade> expected = new Vector<Trade>();
		expected.add(createTrade(1L,"2013-05-01 00:00:01",12.30d, 1L, 24.6d));
		expected.add(createTrade(2L,"2013-05-01 00:00:05",12.80d,10L,256.0d));
		expected.add(createTrade(5L,"2013-05-01 00:00:02",13.01d, 5L,130.1d));
		expected.add(createTrade(8L,"2013-05-01 00:00:02",12.80d, 7L,179.2d));
		
		// при добавлении сделки сортируются
		order.addTrade(createTrade(2L,"2013-05-01 00:00:05",12.80d,10L,256.0d));
		order.addTrade(createTrade(1L,"2013-05-01 00:00:01",12.30d, 1L, 24.6d));
		order.addTrade(createTrade(8L,"2013-05-01 00:00:02",12.80d, 7L,179.2d));
		order.addTrade(createTrade(5L,"2013-05-01 00:00:02",13.01d, 5L,130.1d));
		assertEquals(expected, order.getTrades());
		// при добавлении перерасчитываются qty.rest, avg.exec.price, exec.vol
		assertEquals(new Long(2), order.getQtyRest());
		assertEquals(12.8239d, order.getAvgExecutedPrice(), 0.0001d);
		assertEquals(589.9d, order.getExecutedVolume(), 0.0001d);
	}
	
	@Test
	public void testHasTrade() throws Exception {
		order.setQty(20L);
		assertFalse(order.hasTrade(2L));
		assertFalse(order.hasTrade(8L));
		order.addTrade(createTrade(2L, "2013-05-01 00:00:05", 12.80d, 10L));
		order.addTrade(createTrade(8L, "2013-05-01 00:00:02", 12.80d,  7L));
		assertTrue(order.hasTrade(2L));
		assertTrue(order.hasTrade(8L));
		assertFalse(order.hasTrade(5L));
	}
	
	@Test
	public void testFireTradeEvent() throws Exception {
		Trade t0 = createTrade(100L, "2013-05-01 00:00:00", 25.19d, 10L);
		dispatcher.fireTrade(same(order), same(t0));
		control.replay();
		
		order.fireTradeEvent(t0);
	
		control.verify();
	}
	
	@Test
	public void testClearAllEventListsners() throws Exception {
		dispatcher.removeListeners();
		control.replay();
		
		order.clearAllEventListeners();
		
		control.verify();
	}
	
	@Test
	public void testGetLastTrade() throws Exception {
		order.setQty(20L);
		assertNull(order.getLastTrade());
		Trade t1 = createTrade(1L,"2013-05-01 00:00:01",12.30d, 1L, 24.6d);
		order.addTrade(t1);
		assertSame(t1, order.getLastTrade());
		Trade t2 = createTrade(2L,"2013-05-01 00:00:05",12.80d,10L,256.0d);
		order.addTrade(t2);
		assertEquals(t2, order.getLastTrade());
	}
	
	@Test
	public void testGetLastTradeTime() throws Exception {
		order.setQty(20L);
		assertNull(order.getLastTradeTime());
		order.addTrade(createTrade(1L,"2013-05-01 00:00:01",12.30d, 1L, 24.6d));
		assertEquals(new DateTime(2013, 5, 1, 0, 0, 1),
				order.getLastTradeTime());
		order.addTrade(createTrade(2L,"2013-05-01 00:00:05",12.80d,10L,256.0d));
		assertEquals(new DateTime(2013, 5, 1, 0, 0, 5),
				order.getLastTradeTime());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(order.equals(order));
		assertFalse(order.equals(null));
		assertFalse(order.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<Trade> trds1 = new Vector<Trade>();
		trds1.add(createTrade(1L,"2013-05-01 00:00:01",12.30d, 1L, 24.6d));
		trds1.add(createTrade(2L,"2013-05-01 00:00:05",12.80d,10L,256.0d));
		List<Trade> trds2 = new Vector<Trade>();
		trds2.add(createTrade(2L,"2013-05-01 00:00:05",12.80d,10L,256.0d));

		order.setAccount(account);
		order.setSecurityDescriptor(descr);
		order.setDirection(Direction.SELL);
		order.setId(1000);
		order.setPrice(135.67d);
		order.setQty(200L);
		order.setStatus(OrderStatus.CANCELLED);
		order.setType(OrderType.LIMIT);
		order.setTime(new DateTime(2013, 5, 15, 8, 32, 0));
		order.setLastChangeTime(new DateTime(1998, 1, 1, 1, 2, 3));
		for ( Trade trade : trds1 ) {
			order.addTrade(trade);
		}
		order.setAvailable(true);
		order.setQtyRest(190L);
		order.setExecutedVolume(249400.00d);
		order.setAvgExecutedPrice(182.34d);
		order.getSystemInfo().getRegistration().setRequest(new Object());
		order.setActivator(activator);
		
		double aprob = 0.3; // Probability of additional variant
		Random rnd = new Random();
		Variant<Account> vAcnt = new Variant<Account>()
			.add(account);
		if ( rnd.nextDouble() > aprob ) vAcnt.add(new Account("foobar"));
		Variant<SecurityDescriptor> vDescr =
				new Variant<SecurityDescriptor>(vAcnt)
			.add(descr);
		if ( rnd.nextDouble() > aprob ) {
			vDescr.add(new SecurityDescriptor("A","B","USD",SecurityType.UNK));
		}
		Variant<Direction> vDir = new Variant<Direction>(vDescr)
			.add(Direction.SELL);
		if ( rnd.nextDouble() > aprob ) vDir.add(Direction.BUY);
		Variant<Integer> vId = new Variant<Integer>(vDir)
			.add(1000);
		if ( rnd.nextDouble() > aprob ) vId.add(2220);
		Variant<Double> vPrice = new Variant<Double>(vId)
			.add(135.67d);
		if ( rnd.nextDouble() > aprob ) vPrice.add(null);
		Variant<Long> vQty = new Variant<Long>(vPrice)
			.add(200L);
		if ( rnd.nextDouble() > aprob ) vQty.add(400L);
		Variant<OrderStatus> vStat = new Variant<OrderStatus>(vQty)
			.add(OrderStatus.CANCELLED);
		if ( rnd.nextDouble() > aprob ) vStat.add(OrderStatus.FILLED);
		Variant<OrderType> vType = new Variant<OrderType>(vStat)
			.add(OrderType.LIMIT);
		if ( rnd.nextDouble() > aprob ) vType.add(OrderType.MARKET);
		Variant<Terminal> vTerm = new Variant<Terminal>(vType)
			.add(terminal);
		if ( rnd.nextDouble() > aprob ) {
			vTerm.add(control.createMock(Terminal.class));
		}
		Variant<DateTime> vTime = new Variant<DateTime>(vTerm)
			.add(new DateTime(2013, 5, 15, 8, 32, 0));
		if ( rnd.nextDouble() > aprob ) {
			vTime.add(new DateTime(2013, 1, 1, 0, 0, 0));
		}
		Variant<DateTime> vLastTime = new Variant<DateTime>(vTime)
			.add(new DateTime(1998, 1, 1, 1, 2, 3));
		if ( rnd.nextDouble() > aprob ) {
			vLastTime.add(null);
		}
		Variant<List<Trade>> vTrds = new Variant<List<Trade>>(vLastTime)
			.add(trds1);
		if ( rnd.nextDouble() > aprob ) vTrds.add(trds2);
		Variant<Boolean> vAvl = new Variant<Boolean>(vTrds)
			.add(true);
		if ( rnd.nextDouble() > aprob ) vAvl.add(false);
		Variant<Long> vQtyRst = new Variant<Long>(vAvl)
			.add(190L)
			.add(450L);
		Variant<Double> vExecVol = new Variant<Double>(vQtyRst)
			.add(249400.00d)
			.add(180230.00d);
		Variant<Double> vAvgPr = new Variant<Double>(vExecVol)
			.add(182.34d)
			.add(202.15d);
		Variant<Object> vRegReq = new Variant<Object>(vAvgPr)
			.add(order.getSystemInfo().getRegistration().getRequest())
			.add(new Object());
		Variant<OrderActivator> vAct = new Variant<OrderActivator>(vRegReq)
			.add(activator);
		if ( rnd.nextDouble() > aprob ) {
			vAct.add(control.createMock(OrderActivator.class));
		}
		Variant<?> iterator = vAct;
		int foundCnt = 0;
		OrderImpl x = null, found = null;
		do {
			x = new OrderImpl(dispatcher, stateHandlers, vTerm.get());
			x.setAccount(vAcnt.get());
			x.setSecurityDescriptor(vDescr.get());
			x.setDirection(vDir.get());
			x.setId(vId.get());
			x.setPrice(vPrice.get());
			x.setQty(vQty.get());
			x.setStatus(vStat.get());
			x.setType(vType.get());
			x.setTime(vTime.get());
			x.setLastChangeTime(vLastTime.get());
			for ( Trade trade : vTrds.get() ) {
				x.addTrade(trade);
			}
			x.setAvailable(vAvl.get());
			x.setQtyRest(vQtyRst.get());
			x.setAvgExecutedPrice(vAvgPr.get());
			x.setExecutedVolume(vExecVol.get());
			x.getSystemInfo().getRegistration().setRequest(vRegReq.get());
			x.setActivator(vAct.get());
			if ( order.equals(x) ) {
				foundCnt ++;
				found = x;
			}

		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(terminal, found.getTerminal());
		assertEquals(account, found.getAccount());
		assertEquals(descr, found.getSecurityDescriptor());
		assertEquals(Direction.SELL, found.getDirection());
		assertEquals(new Integer(1000), found.getId());
		assertEquals(135.67d, found.getPrice(), 0.01d);
		assertEquals(new Long(200L), found.getQty());
		assertEquals(OrderStatus.CANCELLED, found.getStatus());
		assertEquals(OrderType.LIMIT, found.getType());
		assertEquals(new DateTime(2013, 5, 15, 8, 32, 0), found.getTime());
		assertEquals(new DateTime(1998, 1, 1, 1, 2, 3),
				found.getLastChangeTime());
		assertEquals(trds1, found.getTrades());
		assertTrue(found.isAvailable());
		assertEquals(new Long(190L), found.getQtyRest());
		assertEquals(249400.00d, found.getExecutedVolume(), 0.01d);
		assertEquals(182.34d, found.getAvgExecutedPrice(), 0.01d);
		assertEquals(order.getSystemInfo(), found.getSystemInfo());
		assertEquals(activator, found.getActivator());
	}
	
}

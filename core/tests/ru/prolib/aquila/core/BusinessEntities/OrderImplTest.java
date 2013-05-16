package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderHandler;
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
	private EventSystem es;
	private EventDispatcher dispatcher, dispatcherMock;
	private EventType onRegister;
	private EventType onRegisterFailed;
	private EventType onCancelled;
	private EventType onCancelFailed;
	private EventType onFilled;
	private EventType onPartiallyFilled;
	private EventType onChanged;
	private EventType onDone;
	private EventType onFailed;
	private EventType onTrade;
	private OrderImpl order;
	private S<OrderImpl> setter;
	private G<?> getter;
	private List<OrderHandler> eventHandlers;
	private Terminal terminal;
	
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
		dispatcherMock = control.createMock(EventDispatcher.class);
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("Order");
		onRegister = dispatcher.createType("OnRegister");
		onRegisterFailed = dispatcher.createType("OnRegisterFailed");
		onCancelled = dispatcher.createType("OnCancelled");
		onCancelFailed = dispatcher.createType("OnCancelFailed");
		onFilled = dispatcher.createType("OnFilled");
		onPartiallyFilled = dispatcher.createType("OnPartiallyFilled");
		onChanged = dispatcher.createType("OnChanged");
		onDone = dispatcher.createType("OnDone");
		onFailed = dispatcher.createType("OnFailed");
		onTrade = dispatcher.createType("OnTrade");
		eventHandlers = new LinkedList<OrderHandler>();
		terminal = control.createMock(Terminal.class); 

		order = new OrderImpl(dispatcher, onRegister, onRegisterFailed,
				onCancelled, onCancelFailed, onFilled, onPartiallyFilled,
				onChanged, onDone, onFailed, onTrade, eventHandlers, terminal);
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
		trade.setTime(format.parse(time));
		trade.setVolume(vol);
		return trade;		
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, order.getTerminal());
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertEquals(0x03, OrderImpl.VERSION);
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
		assertNull(order.getLinkedOrderId());
		assertNull(order.getStopLimitPrice());
		assertNull(order.getTakeProfitPrice());
		assertNull(order.getOffset());
		assertNull(order.getSpread());
		assertEquals(0.0d, order.getExecutedVolume(), 0.01d);
		assertEquals(0.0d, order.getAvgExecutedPrice(), 0.01d);
		assertNull(order.getTime());
		assertNull(order.getLastChangeTime());
	}
	
	@Test
	public void testEventTypes() throws Exception {
		assertSame(dispatcher, order.getEventDispatcher());
		assertSame(onRegister, order.OnRegistered());
		assertSame(onRegisterFailed, order.OnRegisterFailed());
		assertSame(onCancelled, order.OnCancelled());
		assertSame(onCancelFailed, order.OnCancelFailed());
		assertSame(onFilled, order.OnFilled());
		assertSame(onPartiallyFilled, order.OnPartiallyFilled());
		assertSame(onChanged, order.OnChanged());
		assertSame(onDone, order.OnDone());
		assertSame(onFailed, order.OnFailed());
		assertSame(onTrade, order.OnTrade());
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
			public void set(OrderImpl object, Object value) throws ValueException {
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
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setDirection((OrderDirection) value);
			}
		};
		getter = new G<OrderDirection>() {
			@Override
			public OrderDirection get(Object object) throws ValueException {
				return ((OrderImpl) object).getDirection();
			}
		};
		testSetterGetter(OrderDirection.BUY, OrderDirection.SELL);
	}
	
	@Test
	public void testSetId() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setId((Long) value);
			}
		};
		getter = new G<Long>() {
			@Override
			public Long get(Object object) throws ValueException {
				return ((OrderImpl) object).getId();
			}
		};
		testSetterGetter(100L, 200L);
	}
	
	@Test
	public void testSetLinkedOrderId() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setLinkedOrderId((Long) value);
			}
		};
		getter = new G<Long>() {
			@Override
			public Long get(Object object) throws ValueException {
				return ((OrderImpl) object).getLinkedOrderId();
			}
		};
		testSetterGetter(108L, 208L);
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
			public void set(OrderImpl object, Object value) throws ValueException {
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
	public void testSetStopLimitPrice() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setStopLimitPrice((Double) value);
			}
		};
		getter = new G<Double>() {
			@Override
			public Double get(Object object) throws ValueException {
				return ((OrderImpl) object).getStopLimitPrice();
			}
		};
		testSetterGetter(1202.5D, 1301.0D);
	}
	
	@Test
	public void testSetTakeProfitPrice() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setTakeProfitPrice((Double) value);
			}
		};
		getter = new G<Double>() {
			@Override
			public Double get(Object object) throws ValueException {
				return ((OrderImpl) object).getTakeProfitPrice();
			}
		};
		testSetterGetter(4202.5D, 4301.0D);
	}


	@Test
	public void testSetQty() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
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
			public void set(OrderImpl object, Object value) throws ValueException {
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
			public void set(OrderImpl object, Object value) throws ValueException {
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
			new SecurityDescriptor("USD","IDEALPRO","JPY",SecurityType.CASH));
	}
	
	@Test
	public void testSetOffset() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setOffset((Price) value);
			}
		};
		getter = new G<Price>() {
			@Override
			public Price get(Object object) throws ValueException {
				return ((OrderImpl) object).getOffset();
			}
		};
		testSetterGetter(new Price(PriceUnit.PERCENT,	10.00d),
						 new Price(PriceUnit.MONEY,		20.00d));
	}
	
	@Test
	public void testSetSpread() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setSpread((Price) value);
			}
		};
		getter = new G<Price>() {
			@Override
			public Price get(Object object) throws ValueException {
				return ((OrderImpl) object).getSpread();
			}
		};
		testSetterGetter(new Price(PriceUnit.MONEY,		22.00d),
						 new Price(PriceUnit.PERCENT,	12.00d));
	}
	
	@Test
	public void testSetTransactionId() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setTransactionId((Long) value);
			}
		};
		getter = new G<Long>() {
			@Override
			public Long get(Object object) throws ValueException {
				return ((OrderImpl) object).getTransactionId();
			}
		};
		testSetterGetter(100L, 500L);
	}

	@Test
	public void testSetType() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
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
			public void set(OrderImpl object, Object value) throws ValueException {
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
				OrderImpl.STATUS_CHANGED);
	}
	
	@Test
	public void testFireChangedEvent() throws Exception {
		OrderHandler h1,h2,h3;
		h1 = control.createMock(OrderHandler.class);
		h2 = control.createMock(OrderHandler.class);
		h3 = control.createMock(OrderHandler.class);
		eventHandlers.add(h1);
		eventHandlers.add(h2);
		eventHandlers.add(h3);
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
			public void set(OrderImpl object, Object value) throws ValueException {
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
				object.setTime((Date) value);
			}
		};
		getter = new G<Date>() {
			@Override
			public Date get(Object object) throws ValueException {
				return ((OrderImpl) object).getTime();
			}
		};
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		testSetterGetter(df.parse("2011-01-01 00:00:00"),
						 df.parse("2013-02-21 06:58:00"));
	}
	
	@Test
	public void testSetLastChangeTime() throws Exception {
		setter = new S<OrderImpl>() {
			@Override
			public void set(OrderImpl object, Object value) throws ValueException {
				object.setLastChangeTime((Date) value);
			}
		};
		getter = new G<Date>() {
			@Override
			public Date get(Object object) throws ValueException {
				return ((OrderImpl) object).getLastChangeTime();
			}
		};
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		testSetterGetter(df.parse("1991-07-01 13:00:00"),
						 df.parse("2032-02-21 06:58:00"));
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
		order = new OrderImpl(dispatcherMock, onRegister, onRegisterFailed,
				onCancelled, onCancelFailed, onFilled, onPartiallyFilled,
				onChanged, onDone, onFailed, onTrade, eventHandlers, terminal);
		Trade t0 = createTrade(100L, "2013-05-01 00:00:00", 25.19d, 10L);
		dispatcherMock.dispatch(new OrderTradeEvent(onTrade, order, t0));
		control.replay();
		
		order.fireTradeEvent(t0);
	
		control.verify();
	}
	
	@Test
	public void testClearAllEventListsners() throws Exception {
		onRegister = control.createMock(EventType.class);
		onRegisterFailed = control.createMock(EventType.class);
		onCancelled = control.createMock(EventType.class);
		onCancelFailed = control.createMock(EventType.class);
		onFilled = control.createMock(EventType.class);
		onPartiallyFilled = control.createMock(EventType.class);
		onChanged = control.createMock(EventType.class);
		onDone = control.createMock(EventType.class);
		onFailed = control.createMock(EventType.class);
		onTrade = control.createMock(EventType.class);
		expect(dispatcherMock.asString()).andStubReturn("order");
		expect(onRegister.asString()).andStubReturn("reg");
		expect(onRegisterFailed.asString()).andStubReturn("reg-fail");
		expect(onCancelled.asString()).andStubReturn("cancel");
		expect(onCancelFailed.asString()).andStubReturn("cancel-fail");
		expect(onFilled.asString()).andStubReturn("fill");
		expect(onPartiallyFilled.asString()).andStubReturn("part-fill");
		expect(onChanged.asString()).andStubReturn("chng");
		expect(onDone.asString()).andStubReturn("done");
		expect(onFailed.asString()).andStubReturn("fail");
		expect(onTrade.asString()).andStubReturn("trade");
		dispatcherMock.close();
		order = new OrderImpl(dispatcherMock, onRegister, onRegisterFailed,
				onCancelled, onCancelFailed, onFilled, onPartiallyFilled,
				onChanged, onDone, onFailed, onTrade, eventHandlers, terminal);
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
		assertEquals(format.parse("2013-05-01 00:00:01"),
				order.getLastTradeTime());
		order.addTrade(createTrade(2L,"2013-05-01 00:00:05",12.80d,10L,256.0d));
		assertEquals(format.parse("2013-05-01 00:00:05"),
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
		
		List<OrderHandler> hndl1 = new Vector<OrderHandler>();
		hndl1.add(control.createMock(OrderHandler.class));
		hndl1.add(control.createMock(OrderHandler.class));
		List<OrderHandler> hndl2 = new Vector<OrderHandler>();
		hndl2.add(control.createMock(OrderHandler.class));

		order.setAccount(account);
		order.setSecurityDescriptor(descr);
		order.setDirection(OrderDirection.SELL);
		order.setId(1000L);
		order.setPrice(135.67d);
		order.setQty(200L);
		order.setStatus(OrderStatus.CANCELLED);
		order.setTransactionId(100500L);
		order.setType(OrderType.LIMIT);
		order.setLinkedOrderId(null);
		order.setStopLimitPrice(200.14d);
		order.setTakeProfitPrice(400.00d);
		order.setOffset(new Price(PriceUnit.MONEY, 1.0d));
		order.setSpread(new Price(PriceUnit.PERCENT, 0.5d));
		for ( OrderHandler handler : hndl1 ) {
			eventHandlers.add(handler);
		}
		order.setTime(format.parse("2013-05-15 08:32:00"));
		order.setLastChangeTime(format.parse("1998-01-01 01:02:03"));
		for ( Trade trade : trds1 ) {
			order.addTrade(trade);
		}
		order.setAvailable(true);
		order.setQtyRest(190L);
		order.setExecutedVolume(249400.00d);
		order.setAvgExecutedPrice(182.34d);
		
		double aprob = 0.4; // Probability of additional variant
		Random rnd = new Random();
		Variant<String> vDispId = new Variant<String>()
			.add("Order");
		if ( rnd.nextDouble() > aprob ) vDispId.add("Another");
		Variant<String> vRegId = new Variant<String>(vDispId)
			.add("OnRegister");
		if ( rnd.nextDouble() > aprob ) vRegId.add("OnRegisterX");
		Variant<String> vRegFailId = new Variant<String>(vRegId)
			.add("OnRegisterFailed");
		if ( rnd.nextDouble() > aprob ) vRegFailId.add("OnRegisterFailedX");
		Variant<String> vCnclId = new Variant<String>(vRegFailId)
			.add("OnCancelled");
		if ( rnd.nextDouble() > aprob ) vCnclId.add("OnCancelledX");
		Variant<String> vCnclFailId = new Variant<String>(vCnclId)
			.add("OnCancelFailed");
		if ( rnd.nextDouble() > aprob ) vCnclFailId.add("OnCancelFailedX");
		Variant<String> vFillId = new Variant<String>(vCnclFailId)
			.add("OnFilled");
		if ( rnd.nextDouble() > aprob ) vFillId.add("OnFilledX");
		Variant<String> vPartFillId = new Variant<String>(vFillId)
			.add("OnPartiallyFilled");
		if ( rnd.nextDouble() > aprob ) vPartFillId.add("OnPartiallyFilledX");
		Variant<String> vChngId = new Variant<String>(vPartFillId)
			.add("OnChanged");
		if ( rnd.nextDouble() > aprob ) vChngId.add("OnChangedX");
		Variant<String> vDoneId = new Variant<String>(vChngId)
			.add("OnDone");
		if ( rnd.nextDouble() > aprob ) vDoneId.add("OnDoneX");
		Variant<String> vFailId = new Variant<String>(vDoneId)
			.add("OnFailed");
		if ( rnd.nextDouble() > aprob ) vFailId.add("OnFailedX");
		Variant<String> vTrdId = new Variant<String>(vFailId)
			.add("OnTrade");
		if ( rnd.nextDouble() > aprob ) vTrdId.add("OnTradeX");
		Variant<Account> vAcnt = new Variant<Account>(vTrdId)
			.add(account);
		if ( rnd.nextDouble() > aprob ) vAcnt.add(new Account("foobar"));
		Variant<SecurityDescriptor> vDescr =
				new Variant<SecurityDescriptor>(vAcnt)
			.add(descr);
		if ( rnd.nextDouble() > aprob ) {
			vDescr.add(new SecurityDescriptor("A","B","C",SecurityType.UNK));
		}
		Variant<OrderDirection> vDir = new Variant<OrderDirection>(vDescr)
			.add(OrderDirection.SELL);
		if ( rnd.nextDouble() > aprob ) vDir.add(OrderDirection.BUY);
		Variant<Long> vId = new Variant<Long>(vDir)
			.add(1000L);
		if ( rnd.nextDouble() > aprob ) vId.add(2220L);
		Variant<Double> vPrice = new Variant<Double>(vId)
			.add(135.67d);
		if ( rnd.nextDouble() > aprob ) vPrice.add(null);
		Variant<Long> vQty = new Variant<Long>(vPrice)
			.add(200L);
		if ( rnd.nextDouble() > aprob ) vQty.add(400L);
		Variant<OrderStatus> vStat = new Variant<OrderStatus>(vQty)
			.add(OrderStatus.CANCELLED);
		if ( rnd.nextDouble() > aprob ) vStat.add(OrderStatus.FILLED);
		Variant<Long> vTrnId = new Variant<Long>(vStat)
			.add(100500L);
		if ( rnd.nextDouble() > aprob ) vTrnId.add(null);
		Variant<OrderType> vType = new Variant<OrderType>(vTrnId)
			.add(OrderType.LIMIT);
		if ( rnd.nextDouble() > aprob ) vType.add(OrderType.MARKET);
		Variant<Long> vLnkId = new Variant<Long>(vType)
			.add(null);
		if ( rnd.nextDouble() > aprob ) vLnkId.add(500L);
		Variant<Double> vSlp = new Variant<Double>(vLnkId)
			.add(200.14d);
		if ( rnd.nextDouble() > aprob ) vSlp.add(180.92d);
		Variant<Double> vTpp = new Variant<Double>(vSlp)
			.add(400.00d);
		if ( rnd.nextDouble() > aprob ) vTpp.add(13.44d);
		Variant<Price> vOff = new Variant<Price>(vTpp)
			.add(new Price(PriceUnit.MONEY, 1.0d));
		if ( rnd.nextDouble() > aprob ) vOff.add(null);
		Variant<Price> vSprd = new Variant<Price>(vOff)
			.add(new Price(PriceUnit.PERCENT, 0.5d));
		if ( rnd.nextDouble() > aprob ) {
			vSprd.add(null);
		}
		Variant<List<OrderHandler>> vHndl =
				new Variant<List<OrderHandler>>(vSprd)
			.add(hndl1);
		if ( rnd.nextDouble() > aprob ) vHndl.add(hndl2);
		Variant<Terminal> vTerm = new Variant<Terminal>(vHndl)
			.add(terminal);
		if ( rnd.nextDouble() > aprob ) {
			vTerm.add(control.createMock(Terminal.class));
		}
		Variant<Date> vTime = new Variant<Date>(vTerm)
			.add(format.parse("2013-05-15 08:32:00"));
		if ( rnd.nextDouble() > aprob ) {
			vTime.add(format.parse("2013-01-01 00:00:00"));
		}
		Variant<Date> vLastTime = new Variant<Date>(vTime)
			.add(format.parse("1998-01-01 01:02:03"));
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
		Variant<?> iterator = vAvgPr;
		int foundCnt = 0;
		OrderImpl x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new OrderImpl(d, d.createType(vRegId.get()),
					d.createType(vRegFailId.get()),
					d.createType(vCnclId.get()),
					d.createType(vCnclFailId.get()),
					d.createType(vFillId.get()),
					d.createType(vPartFillId.get()),
					d.createType(vChngId.get()),
					d.createType(vDoneId.get()),
					d.createType(vFailId.get()),
					d.createType(vTrdId.get()),
					vHndl.get(), vTerm.get());
			x.setAccount(vAcnt.get());
			x.setSecurityDescriptor(vDescr.get());
			x.setDirection(vDir.get());
			x.setId(vId.get());
			x.setPrice(vPrice.get());
			x.setQty(vQty.get());
			x.setStatus(vStat.get());
			x.setTransactionId(vTrnId.get());
			x.setType(vType.get());
			x.setLinkedOrderId(vLnkId.get());
			x.setStopLimitPrice(vSlp.get());
			x.setTakeProfitPrice(vTpp.get());
			x.setOffset(vOff.get());
			x.setSpread(vSprd.get());
			x.setTime(vTime.get());
			x.setLastChangeTime(vLastTime.get());
			for ( Trade trade : vTrds.get() ) {
				x.addTrade(trade);
			}
			x.setAvailable(vAvl.get());
			x.setQtyRest(vQtyRst.get());
			x.setAvgExecutedPrice(vAvgPr.get());
			x.setExecutedVolume(vExecVol.get());
			if ( order.equals(x) ) {
				foundCnt ++;
				found = x;
			}

		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onRegister, found.OnRegistered());
		assertEquals(onRegisterFailed, found.OnRegisterFailed());
		assertEquals(onCancelled, found.OnCancelled());
		assertEquals(onCancelFailed, found.OnCancelFailed());
		assertEquals(onFilled, found.OnFilled());
		assertEquals(onPartiallyFilled, found.OnPartiallyFilled());
		assertEquals(onChanged, found.OnChanged());
		assertEquals(onDone, found.OnDone());
		assertEquals(onFailed, found.OnFailed());
		assertEquals(onTrade, found.OnTrade());
		assertEquals(hndl1, found.getEventHandlers());
		assertEquals(terminal, found.getTerminal());
		assertEquals(account, found.getAccount());
		assertEquals(descr, found.getSecurityDescriptor());
		assertEquals(OrderDirection.SELL, found.getDirection());
		assertEquals(new Long(1000L), found.getId());
		assertEquals(135.67d, found.getPrice(), 0.01d);
		assertEquals(new Long(200L), found.getQty());
		assertEquals(OrderStatus.CANCELLED, found.getStatus());
		assertEquals(new Long(100500L), found.getTransactionId());
		assertEquals(OrderType.LIMIT, found.getType());
		assertNull(order.getLinkedOrderId());
		assertEquals(200.14d, found.getStopLimitPrice(), 0.01d);
		assertEquals(400.00d, found.getTakeProfitPrice(), 0.01d);
		assertEquals(new Price(PriceUnit.MONEY, 1.0d), found.getOffset());
		assertEquals(new Price(PriceUnit.PERCENT, 0.5d), found.getSpread());
		assertEquals(format.parse("2013-05-15 08:32:00"), found.getTime());
		assertEquals(format.parse("1998-01-01 01:02:03"),
				found.getLastChangeTime());
		assertEquals(trds1, found.getTrades());
		assertTrue(found.isAvailable());
		assertEquals(new Long(190L), found.getQtyRest());
		assertEquals(249400.00d, found.getExecutedVolume(), 0.01d);
		assertEquals(182.34d, found.getAvgExecutedPrice(), 0.01d);
	}

}

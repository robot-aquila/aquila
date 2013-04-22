package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderHandler;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * 2012-09-22<br>
 * $Id: OrderImplTest.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderImplTest {
	private final Account account = new Account("LX01");
	private final SecurityDescriptor secDescr =
			new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
	private IMocksControl control;
	private EventSystem eventSystem;
	private EventQueue queue;
	private EventDispatcher dispatcher;
	private EventType onRegister;
	private EventType onRegisterFailed;
	private EventType onCancelled;
	private EventType onCancelFailed;
	private EventType onFilled;
	private EventType onPartiallyFilled;
	private EventType onChanged;
	private EventType onDone;
	private EventType onFailed;
	private OrderImpl order;
	private S<OrderImpl> setter;
	private G<?> getter;
	private List<OrderHandler> eventHandlers;
	private Terminal terminal;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
		dispatcher = eventSystem.createEventDispatcher();
		onRegister = eventSystem.createGenericType(dispatcher);
		onRegisterFailed = eventSystem.createGenericType(dispatcher);
		onCancelled = eventSystem.createGenericType(dispatcher);
		onCancelFailed = eventSystem.createGenericType(dispatcher);
		onFilled = eventSystem.createGenericType(dispatcher);
		onPartiallyFilled = eventSystem.createGenericType(dispatcher);
		onChanged = eventSystem.createGenericType(dispatcher);
		onDone = eventSystem.createGenericType(dispatcher);
		onFailed = eventSystem.createGenericType(dispatcher);
		eventHandlers = new LinkedList<OrderHandler>();
		terminal = control.createMock(Terminal.class); 

		order = new OrderImpl(dispatcher, onRegister, onRegisterFailed,
				onCancelled, onCancelFailed, onFilled, onPartiallyFilled,
				onChanged, onDone, onFailed, eventHandlers, terminal);
		queue.start();
		setter = null;
		getter = null;
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
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
		order.setSecurityDescriptor(secDescr);
		Security security = control.createMock(Security.class);
		expect(terminal.getSecurity(eq(secDescr))).andReturn(security);
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
		testSetterGetter(secDescr,
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
	
}

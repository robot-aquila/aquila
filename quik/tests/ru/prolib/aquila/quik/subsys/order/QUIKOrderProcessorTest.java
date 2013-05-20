package ru.prolib.aquila.quik.subsys.order;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.quik.api.ApiService;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * 2013-01-24<br>
 * $Id: QUIKOrderProcessorTest.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class QUIKOrderProcessorTest {
	private static SecurityDescriptor secDescr;
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private EditableTerminal terminal;
	private QUIKOrderProcessor processor;
	private EditableOrder order;
	private EditableOrders orders, stopOrders;
	private EventType type;
	private ApiService api;
	private Counter transId;
	private Security security;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		secDescr=new SecurityDescriptor("RIH3","SPBFUT","USD",SecurityType.FUT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(QUIKServiceLocator.class);
		terminal = control.createMock(EditableTerminal.class);
		order = control.createMock(EditableOrder.class);
		api = control.createMock(ApiService.class);
		transId = control.createMock(Counter.class);
		security = control.createMock(Security.class);
		type = control.createMock(EventType.class);
		orders = control.createMock(EditableOrders.class);
		stopOrders = control.createMock(EditableOrders.class);
		processor = new QUIKOrderProcessor(locator);
		setCommonExpectations();
	}
	
	/**
	 * Установить типовые ожидания, связанные с сервис-локатором.
	 */
	private void setCommonExpectations() {
		expect(locator.getTransactionNumerator()).andStubReturn(transId);
		expect(locator.getTerminal()).andStubReturn(terminal);
		expect(locator.getApi()).andStubReturn(api);
		expect(type.asString()).andStubReturn("test");
		expect(terminal.getOrdersInstance()).andStubReturn(orders);
		expect(terminal.getStopOrdersInstance()).andStubReturn(stopOrders);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, processor.getServiceLocator());
	}
	
	@Test
	public void testCancelOrder_SkipIfNotActive() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.CANCELLED);
		control.replay();
		processor.cancelOrder(order);
		control.verify();
	}
	
	private void checkCancelOrder(OrderType orderType) throws Exception {
		setUp();
		expect(order.getStatus()).andStubReturn(OrderStatus.ACTIVE);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.getClassCode()).andStubReturn("SECTION");
		expect(order.getId()).andStubReturn(835L);
		expect(order.getType()).andStubReturn(orderType);
		expect(transId.incrementAndGet()).andReturn(137);
		expect(api.OnTransReply(137)).andReturn(type);
		type.addListener(eq(new CancelOrderHandler(locator, orders, 835L)));
		api.send("TRANS_ID=137; CLASSCODE=SECTION; ACTION=KILL_ORDER; "
				+ "ORDER_KEY=835");
		control.replay();
		processor.cancelOrder(order);
		control.verify();
	}
	
	@Test
	public void testCancelOrder_Ok() throws Exception {
		checkCancelOrder(OrderType.LIMIT);
		checkCancelOrder(OrderType.MARKET);
	}
	
	private void checkCancelStopOrder(OrderType orderType) throws Exception {
		setUp();
		expect(order.getStatus()).andStubReturn(OrderStatus.ACTIVE);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.getClassCode()).andStubReturn("BUZZ");
		expect(order.getId()).andStubReturn(823L);
		expect(order.getType()).andStubReturn(orderType);
		expect(transId.incrementAndGet()).andReturn(120);
		expect(api.OnTransReply(120)).andReturn(type);
		type.addListener(eq(new CancelOrderHandler(locator, stopOrders, 823L)));
		api.send("TRANS_ID=120; CLASSCODE=BUZZ; "
				+ "ACTION=KILL_STOP_ORDER; STOP_ORDER_KEY=823");
		control.replay();
		processor.cancelOrder(order);
		control.verify();
	}
	
	@Test
	public void testCancelOrder_StopOrderOk() throws Exception {
		checkCancelStopOrder(OrderType.STOP_LIMIT);
		checkCancelStopOrder(OrderType.TAKE_PROFIT);
		checkCancelStopOrder(OrderType.TAKE_PROFIT_AND_STOP_LIMIT);
	}
	
	@Test (expected=OrderException.class)
	public void testPlaceOrder_ThrowsIfNotPending() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.ACTIVE);
		control.replay();
		
		processor.placeOrder(order);
	}
	
	@Test
	public void testPlaceOrder_ThrowsIfUnsupported() throws Exception {
		OrderType unsupported[] = {
				OrderType.TAKE_PROFIT,
				OrderType.TAKE_PROFIT_AND_STOP_LIMIT,
		};
		for ( int i = 0; i < unsupported.length; i ++ ) {
			String msg = "At #" + i;
			OrderType type = unsupported[i];
			control.resetToNice();
			setCommonExpectations();
			expect(order.getStatus()).andReturn(OrderStatus.PENDING);
			expect(order.getType()).andReturn(type);
			control.replay();
			try {
				processor.placeOrder(order);
				fail(msg + " Expected exception: " +
					QUIKOrderTypeUnsupportedException.class.getSimpleName());
			} catch ( QUIKOrderTypeUnsupportedException e ) {
				assertEquals(msg, "Order type unsupported: " + type,
						e.getMessage());
			}
			control.verify();
		}
	}
	
	@Test
	public void testPlaceOrder_MarketOrderBuy() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(transId.incrementAndGet()).andReturn(926);
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getQty()).andStubReturn(1050l);
		orders.registerPendingOrder(926L, order);
		expect(api.OnTransReply(926l)).andReturn(type);
		type.addListener(eq(new PlaceOrderHandler(locator, orders)));
		api.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
				+ "ACCOUNT=LX1; CLASSCODE=SPBFUT; SECCODE=RIH3; "
				+ "OPERATION=B; ACTION=NEW_ORDER; TYPE=M; PRICE=0; "
				+ "QUANTITY=1050"));
		control.replay();
		processor.placeOrder(order);
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_MarketOrderSell() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(transId.incrementAndGet()).andReturn(712);
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getQty()).andStubReturn(1l);
		orders.registerPendingOrder(712L, order);
		expect(api.OnTransReply(712l)).andReturn(type);
		type.addListener(eq(new PlaceOrderHandler(locator, orders)));
		api.send(eq("TRANS_ID=712; CLIENT_CODE=K-86; "
				+ "ACCOUNT=LX1; CLASSCODE=SPBFUT; SECCODE=RIH3; "
				+ "OPERATION=S; ACTION=NEW_ORDER; TYPE=M; PRICE=0; "
				+ "QUANTITY=1"));
		control.replay();
		processor.placeOrder(order);
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_LimitOrderBuy() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(transId.incrementAndGet()).andReturn(926);
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(123.456d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(123.456)).andStubReturn("12.45");
		orders.registerPendingOrder(926L, order);
		expect(api.OnTransReply(926l)).andReturn(type);
		type.addListener(eq(new PlaceOrderHandler(locator, orders)));
		api.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
				+ "ACCOUNT=LX1; CLASSCODE=SPBFUT; SECCODE=RIH3; "
				+ "OPERATION=B; ACTION=NEW_ORDER; TYPE=L; PRICE=12.45; "
				+ "QUANTITY=1050"));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_LimitOrderSell() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(transId.incrementAndGet()).andReturn(926);
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(123.456d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(123.456)).andStubReturn("12.45");
		orders.registerPendingOrder(926L, order);
		expect(api.OnTransReply(926l)).andReturn(type);
		type.addListener(eq(new PlaceOrderHandler(locator, orders)));
		api.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
				+ "ACCOUNT=LX1; CLASSCODE=SPBFUT; SECCODE=RIH3; "
				+ "OPERATION=S; ACTION=NEW_ORDER; TYPE=L; PRICE=12.45; "
				+ "QUANTITY=1050"));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_StopLimitBuy() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(transId.incrementAndGet()).andReturn(926);
		expect(order.getType()).andStubReturn(OrderType.STOP_LIMIT);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(12.456d);
		expect(order.getStopLimitPrice()).andStubReturn(120d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(120d)).andReturn("12.00");
		expect(security.shrinkPrice(12.456)).andReturn("12.45");
		stopOrders.registerPendingOrder(926L, order);
		expect(api.OnTransReply(926l)).andReturn(type);
		type.addListener(eq(new PlaceOrderHandler(locator, stopOrders)));
		api.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
				+ "ACCOUNT=LX1; CLASSCODE=SPBFUT; SECCODE=RIH3; "
				+ "OPERATION=B; ACTION=NEW_STOP_ORDER; STOPPRICE=12.00; "
				+ "PRICE=12.45; QUANTITY=1050"));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testPlaceOrder_StopLimitSell() throws Exception {
		expect(order.getStatus()).andStubReturn(OrderStatus.PENDING);
		expect(transId.incrementAndGet()).andReturn(926);
		expect(order.getType()).andStubReturn(OrderType.STOP_LIMIT);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(12.456d);
		expect(order.getStopLimitPrice()).andStubReturn(120d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(120d)).andReturn("12.00");
		expect(security.shrinkPrice(12.456)).andReturn("12.45");
		stopOrders.registerPendingOrder(926L, order);
		expect(api.OnTransReply(926l)).andReturn(type);
		type.addListener(eq(new PlaceOrderHandler(locator, stopOrders)));
		api.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
				+ "ACCOUNT=LX1; CLASSCODE=SPBFUT; SECCODE=RIH3; "
				+ "OPERATION=S; ACTION=NEW_STOP_ORDER; STOPPRICE=12.00; "
				+ "PRICE=12.45; QUANTITY=1050"));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		QUIKServiceLocator loc2 = control.createMock(QUIKServiceLocator.class);
		assertTrue(processor.equals(processor));
		assertTrue(processor.equals(new QUIKOrderProcessor(locator)));
		assertFalse(processor.equals(new QUIKOrderProcessor(loc2)));
		assertFalse(processor.equals(null));
		assertFalse(processor.equals(this));
	}
	
}

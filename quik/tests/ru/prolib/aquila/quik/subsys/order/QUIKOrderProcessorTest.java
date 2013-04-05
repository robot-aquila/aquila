package ru.prolib.aquila.quik.subsys.order;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;
import ru.prolib.aquila.t2q.T2QConnStatus;
import ru.prolib.aquila.t2q.T2QService;
import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * 2013-01-24<br>
 * $Id: QUIKOrderProcessorTest.java 576 2013-03-14 12:07:25Z whirlwind $
 */
public class QUIKOrderProcessorTest {
	private static SecurityDescriptor secDescr;
	private IMocksControl control;
	private QUIKServiceLocator locator;
	private Counter failedOrderId;
	private EditableTerminal terminal;
	private QUIKOrderProcessor processor;
	private EditableOrder order;
	private T2QService transService;
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
		failedOrderId = control.createMock(Counter.class);
		terminal = control.createMock(EditableTerminal.class);
		order = control.createMock(EditableOrder.class);
		transService = control.createMock(T2QService.class);
		transId = control.createMock(Counter.class);
		security = control.createMock(Security.class);
		processor = new QUIKOrderProcessor(locator);
		setCommonExpectations();
	}
	
	/**
	 * Установить типовые ожидания, связанные с сервис-локатором.
	 */
	private void setCommonExpectations() {
		expect(locator.getFailedOrderNumerator()).andStubReturn(failedOrderId);
		expect(locator.getTransactionNumerator()).andStubReturn(transId);
		expect(locator.getTerminal()).andStubReturn(terminal);
		expect(locator.getTransactionService()).andStubReturn(transService);
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
	
	private void checkCancelOrder(OrderType type) throws Exception {
		setUp();
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(transId.incrementAndGet()).andReturn(137);
		expect(order.getSecurity()).andReturn(security);
		expect(security.getClassCode()).andReturn("SECTION");
		expect(order.getId()).andReturn(8273l);
		expect(order.getType()).andStubReturn(type);
		transService.send("TRANS_ID=137; CLASSCODE=SECTION; ACTION=KILL_ORDER; "
				+ "ORDER_KEY=8273");
		control.replay();
		processor.cancelOrder(order);
		control.verify();
	}
	
	@Test
	public void testCancelOrder_Ok() throws Exception {
		checkCancelOrder(OrderType.LIMIT);
		checkCancelOrder(OrderType.MARKET);
	}
	
	private void checkCancelStopOrder(OrderType type) throws Exception {
		setUp();
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(transId.incrementAndGet()).andReturn(137);
		expect(order.getSecurity()).andReturn(security);
		expect(security.getClassCode()).andReturn("SECTION");
		expect(order.getId()).andReturn(8273l);
		expect(order.getType()).andStubReturn(type);
		transService.send("TRANS_ID=137; CLASSCODE=SECTION; "
				+ "ACTION=KILL_STOP_ORDER; STOP_ORDER_KEY=8273");
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
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getTransactionId()).andStubReturn(926l);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getQty()).andStubReturn(1050l);
		transService.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
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
		expect(order.getType()).andStubReturn(OrderType.MARKET);
		expect(order.getTransactionId()).andStubReturn(712l);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getQty()).andStubReturn(1l);
		transService.send(eq("TRANS_ID=712; CLIENT_CODE=K-86; "
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
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		expect(order.getTransactionId()).andStubReturn(926l);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(123.456d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(123.456)).andStubReturn("12.45");
		transService.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
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
		expect(order.getType()).andStubReturn(OrderType.LIMIT);
		expect(order.getTransactionId()).andStubReturn(926l);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(123.456d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(123.456)).andStubReturn("12.45");
		transService.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
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
		expect(order.getType()).andStubReturn(OrderType.STOP_LIMIT);
		expect(order.getTransactionId()).andStubReturn(926l);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.BUY);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(12.456d);
		expect(order.getStopLimitPrice()).andStubReturn(120d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(120d)).andReturn("12.00");
		expect(security.shrinkPrice(12.456)).andReturn("12.45");
		transService.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
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
		expect(order.getType()).andStubReturn(OrderType.STOP_LIMIT);
		expect(order.getTransactionId()).andStubReturn(926l);
		expect(order.getAccount()).andStubReturn(new Account("F","K-86","LX1"));
		expect(order.getSecurityDescriptor()).andStubReturn(secDescr);
		expect(order.getDirection()).andStubReturn(OrderDirection.SELL);
		expect(order.getQty()).andStubReturn(1050l);
		expect(order.getPrice()).andStubReturn(12.456d);
		expect(order.getStopLimitPrice()).andStubReturn(120d);
		expect(order.getSecurity()).andStubReturn(security);
		expect(security.shrinkPrice(120d)).andReturn("12.00");
		expect(security.shrinkPrice(12.456)).andReturn("12.45");
		transService.send(eq("TRANS_ID=926; CLIENT_CODE=K-86; "
				+ "ACCOUNT=LX1; CLASSCODE=SPBFUT; SECCODE=RIH3; "
				+ "OPERATION=S; ACTION=NEW_STOP_ORDER; STOPPRICE=12.00; "
				+ "PRICE=12.45; QUANTITY=1050"));
		control.replay();
		
		processor.placeOrder(order);
		
		control.verify();
	}
	
	@Test
	public void testOnConnStatus_SkipsQuikConn() throws Exception {
		control.replay();
		processor.OnConnStatus(T2QConnStatus.QUIK_CONN);
		control.verify();
	}
	
	@Test
	public void testOnConnStatus_SkipsQuikDisc() throws Exception {
		control.replay();
		processor.OnConnStatus(T2QConnStatus.QUIK_DISC);
		control.verify();
	}
	
	@Test
	public void testOnConnStatus_DllConn() throws Exception {
		terminal.fireTerminalConnectedEvent();
		control.replay();
		processor.OnConnStatus(T2QConnStatus.DLL_CONN);
		control.verify();
	}
	
	@Test
	public void testOnConnStatus_DllDisc() throws Exception {
		terminal.fireTerminalDisconnectedEvent();
		control.replay();
		processor.OnConnStatus(T2QConnStatus.DLL_DISC);
		control.verify();
	}
	
	@Test
	public void testOnTransReply_SkipSent() throws Exception {
		control.replay();
		processor.OnTransReply(T2QTransStatus.SENT, 125, null, "Sent");
		control.verify();
	}
	
	@Test
	public void testOnTransReply_SkipRecv() throws Exception {
		control.replay();
		processor.OnTransReply(T2QTransStatus.RECV, 276, null, "Recv");
		control.verify();
	}
	
	@Test
	public void testOnTransReply_SkipDone() throws Exception {
		control.replay();
		processor.OnTransReply(T2QTransStatus.DONE, 122, null, "Done");
		control.verify();
	}
	
	@Test
	public void testOnTransReply_NonOrderError() throws Exception {
		expect(failedOrderId.decrementAndGet()).andReturn(-1);
		expect(terminal.makePendingOrderAsRegisteredIfExists(824l, -1l))
			.andReturn(null);
		expect(terminal.makePendingStopOrderAsRegisteredIfExists(824l, -1l))
			.andReturn(null);
		control.replay();
		processor.OnTransReply(T2QTransStatus.ERR_REJ, 824, null, "Test error");
		control.verify();
	}
	
	@Test
	public void testOnTransReply_OrderError() throws Exception {
		expect(failedOrderId.decrementAndGet()).andReturn(-1);
		expect(terminal.makePendingOrderAsRegisteredIfExists(824l, -1l))
			.andReturn(order);
		order.setStatus(OrderStatus.FAILED);
		order.setAvailable(true);
		terminal.fireOrderAvailableEvent(order);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		processor.OnTransReply(T2QTransStatus.ERR_AUTH, 824, null, "Test error");
		control.verify();
	}

	@Test
	public void testOnTransReply_StopOrderError() throws Exception {
		expect(failedOrderId.decrementAndGet()).andReturn(-15);
		expect(terminal.makePendingOrderAsRegisteredIfExists(112l, -15l))
			.andReturn(null);
		expect(terminal.makePendingStopOrderAsRegisteredIfExists(112l, -15l))
			.andReturn(order);
		order.setStatus(OrderStatus.FAILED);
		order.setAvailable(true);
		terminal.fireStopOrderAvailableEvent(order);
		order.fireChangedEvent();
		order.resetChanges();
		control.replay();
		processor.OnTransReply(T2QTransStatus.ERR_LIMIT, 112, null, "Test error");
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
	
	@Test
	public void test_() throws Exception {
		fail("TODO: incomplete");
	}
	
}

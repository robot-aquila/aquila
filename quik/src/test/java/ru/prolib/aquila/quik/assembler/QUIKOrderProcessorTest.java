package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.api.QUIKClient;

public class QUIKOrderProcessorTest {
	private IMocksControl control;
	private QUIKTerminal terminal;
	private QUIKClient client;
	private HandlerFactory factory;
	private EditableOrder order;
	private Counter numerator;
	private QUIKOrderProcessor processor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKTerminal.class);
		factory = control.createMock(HandlerFactory.class);
		order = control.createMock(EditableOrder.class);
		numerator = control.createMock(Counter.class);
		client = control.createMock(QUIKClient.class);
		processor = new QUIKOrderProcessor(factory);

		expect(terminal.getOrderIdSequence()).andStubReturn(numerator);
		expect(terminal.getClient()).andStubReturn(client);
		expect(order.getId()).andStubReturn(835);
	}
	
	@Test
	public void testCancelOrder() throws Exception {
		expect(order.getStatus()).andReturn(OrderStatus.ACTIVE);
		expect(numerator.incrementAndGet()).andReturn(445);
		CancelHandler handler = control.createMock(CancelHandler.class);
		expect(factory.createCancelOrder(445, order)).andReturn(handler);
		client.setHandler(445, handler);
		handler.cancelOrder();
		control.replay();
		
		processor.cancelOrder(terminal, order);
		
		control.verify();
	}
	
	@Test
	public void testCancelOrder_Skip() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.FILLED,
				OrderStatus.REJECTED,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(expected[i]);
			control.replay();
			
			processor.cancelOrder(terminal, order);
			
			control.verify();
		}
	}
	
	@Test
	public void testCancelOrder_Throws() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.CONDITION,
				OrderStatus.PENDING,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(expected[i]);
			control.replay();
			
			try {
				processor.cancelOrder(terminal, order);
				fail("Expected: " + OrderException.class);
			} catch ( OrderException e ) {
				
			}
			
			control.verify();
		}
	}
	
	@Test
	public void testPlaceOrder() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.PENDING,
				OrderStatus.CONDITION,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(expected[i]);
			PlaceHandler handler = control.createMock(PlaceHandler.class);
			expect(factory.createPlaceOrder(order)).andReturn(handler);
			expect(order.getId()).andReturn(6612);
			client.setHandler(6612, handler);
			handler.placeOrder();
			control.replay();
			
			processor.placeOrder(terminal, order);
			
			control.verify();
		}
	}
	
	@Test
	public void testPlaceOrder_Throws() throws Exception {
		OrderStatus expected[] = {
				OrderStatus.ACTIVE,
				OrderStatus.CANCEL_FAILED,
				OrderStatus.CANCEL_SENT,
				OrderStatus.CANCELLED,
				OrderStatus.FILLED,
				OrderStatus.REJECTED,
				OrderStatus.SENT,
		};
		for ( int i = 0; i < expected.length; i ++ ) {
			setUp();
			expect(order.getStatus()).andReturn(expected[i]);
			control.replay();
			
			try {
				processor.placeOrder(terminal, order);
				fail("Expected: " + OrderException.class);
			} catch ( OrderException e ) {
				
			}
			
			control.verify();
		}
	}
	
}

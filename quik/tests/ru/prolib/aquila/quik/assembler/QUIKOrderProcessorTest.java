package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Variant;
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
		processor = new QUIKOrderProcessor(terminal, factory);

		expect(terminal.getOrderNumerator()).andStubReturn(numerator);
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
		
		processor.cancelOrder(order);
		
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
			
			processor.cancelOrder(order);
			
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
				processor.cancelOrder(order);
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
			
			processor.placeOrder(order);
			
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
				processor.placeOrder(order);
				fail("Expected: " + OrderException.class);
			} catch ( OrderException e ) {
				
			}
			
			control.verify();
		}
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(processor.equals(processor));
		assertFalse(processor.equals(null));
		assertFalse(processor.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKTerminal> vTerm = new Variant<QUIKTerminal>()
			.add(terminal)
			.add(control.createMock(QUIKTerminal.class));
		Variant<HandlerFactory> vFact = new Variant<HandlerFactory>()
			.add(factory)
			.add(control.createMock(HandlerFactory.class));
		Variant<?> iterator = vFact;
		int foundCnt = 0;
		QUIKOrderProcessor x, found = null;
		do {
			x = new QUIKOrderProcessor(vTerm.get(), vFact.get());
			if ( processor.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(factory, found.getFactory());
	}

	@Test
	public void testConstruct1() throws Exception {
		QUIKOrderProcessor
			expected = new QUIKOrderProcessor(terminal, new HandlerFactory()),
			actual = new QUIKOrderProcessor(terminal);
		
		assertEquals(expected, actual);
	}
	
}

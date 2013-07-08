package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderStateHandler;
import ru.prolib.aquila.core.utils.Variant;

public class OrderEventHandlerTest {
	private static IMocksControl control;
	private static EventDispatcher dispatcher;
	private static OrderStateValidator validator;
	private static EventType type;
	private static EditableOrder order;
	private static OrderStateHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		validator = control.createMock(OrderStateValidator.class);
		type = control.createMock(EventType.class);
		order = control.createMock(EditableOrder.class);
		handler = new OrderStateHandler(dispatcher, validator, type);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(dispatcher, handler.getEventDispatcher());
		assertSame(validator, handler.getValidator());
		assertSame(type, handler.getEventType());
	}
	
	@Test
	public void testHandle_IfValidationPassed() throws Exception {
		OrderEvent expected = new OrderEvent(type, order);
		expect(validator.validate(same(order))).andReturn(true);
		dispatcher.dispatch(eq(expected));
		control.replay();
		handler.handle(order);
		control.verify();
	}
	
	@Test
	public void testHandle_IfValidationFails() throws Exception {
		expect(validator.validate(same(order))).andReturn(false);
		control.replay();
		handler.handle(order);
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>()
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<OrderStateValidator> vVldr =
				new Variant<OrderStateValidator>(vDisp)
			.add(validator)
			.add(control.createMock(OrderStateValidator.class));
		Variant<EventType> vType = new Variant<EventType>(vVldr)
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<?> iterator = vType;
		int foundCnt = 0;
		OrderStateHandler x = null, found = null;
		do {
			x = new OrderStateHandler(vDisp.get(), vVldr.get(), vType.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(validator, found.getValidator());
		assertSame(type, found.getEventType());
	}

}

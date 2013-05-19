package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderEvent;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderEventHandler;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-25<br>
 * $Id: OrderEventHandlerTest.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderEventHandlerTest {
	private static IMocksControl control;
	private static EventDispatcher dispatcher;
	private static Validator validator;
	private static EventType type;
	private static EditableOrder order;
	private static OrderEventHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		validator = control.createMock(Validator.class);
		type = control.createMock(EventType.class);
		order = control.createMock(EditableOrder.class);
		handler = new OrderEventHandler(dispatcher, validator, type);
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
	public void testHandle_ThrowsIfValidatorThrows() throws Exception {
		ValidatorException expected = new ValidatorException("test");
		expect(validator.validate(same(order))).andThrow(expected);
		control.replay();
		
		try {
			handler.handle(order);
			fail("Expected: " + OrderException.class.getSimpleName());
		} catch ( OrderException e ) {
			assertSame(expected, e.getCause());
			control.verify();
		}
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
		Variant<Validator> vVldr = new Variant<Validator>(vDisp)
			.add(validator)
			.add(control.createMock(Validator.class));
		Variant<EventType> vType = new Variant<EventType>(vVldr)
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<?> iterator = vType;
		int foundCnt = 0;
		OrderEventHandler x = null, found = null;
		do {
			x = new OrderEventHandler(vDisp.get(), vVldr.get(), vType.get());
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

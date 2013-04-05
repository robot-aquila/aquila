package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import org.easymock.*;
import org.junit.*;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderEventHandler;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderFactoryImpl;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderHandler;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsCancelFailed;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsCancelled;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsChanged;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsDone;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsFailed;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsFilled;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsPartiallyFilled;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsRegisterFailed;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsRegistered;

/**
 * 2012-10-17<br>
 * $Id: OrderFactoryImplTest.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class OrderFactoryImplTest {
	private static IMocksControl control;
	private static EventSystem es;
	private static EventDispatcher dispatcher;
	private static EventType onRegister,onRegisterFailed,
							 onCancelled, onCancelFailed,
							 onFilled, onPartiallyFilled,
							 onChanged, onDone,
							 onFailed;
	private static Terminal terminal;
	private static OrderFactoryImpl factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		dispatcher = control.createMock(EventDispatcher.class);
		onRegister = control.createMock(EventType.class);
		onRegisterFailed = control.createMock(EventType.class);
		onCancelled = control.createMock(EventType.class);
		onCancelFailed = control.createMock(EventType.class);
		onFilled = control.createMock(EventType.class);
		onPartiallyFilled = control.createMock(EventType.class);
		onChanged = control.createMock(EventType.class);
		onDone = control.createMock(EventType.class);
		onFailed = control.createMock(EventType.class);
		terminal = control.createMock(Terminal.class);
		factory = new OrderFactoryImpl(es, terminal);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(es, factory.getEventSystem());
		assertSame(terminal, factory.getTerminal());
	}
	
	@Test
	public void testCreateOrder() throws Exception {
		expect(es.createEventDispatcher("Order"))
			.andReturn(dispatcher);
		expect(es.createGenericType(dispatcher, "OnRegister"))
			.andReturn(onRegister);
		expect(es.createGenericType(dispatcher, "OnRegisterFailed"))
			.andReturn(onRegisterFailed);
		expect(es.createGenericType(dispatcher, "OnCancelled"))
			.andReturn(onCancelled);
		expect(es.createGenericType(dispatcher, "OnCancelFailed"))
			.andReturn(onCancelFailed);
		expect(es.createGenericType(dispatcher, "OnFilled"))
			.andReturn(onFilled);
		expect(es.createGenericType(dispatcher, "OnPartiallyFilled"))
			.andReturn(onPartiallyFilled);
		expect(es.createGenericType(dispatcher, "OnChanged"))
			.andReturn(onChanged);
		expect(es.createGenericType(dispatcher, "OnDone"))
			.andReturn(onDone);
		expect(es.createGenericType(dispatcher, "OnFailed"))
			.andReturn(onFailed);
		control.replay();
		
		OrderImpl order = (OrderImpl) factory.createOrder();
		
		control.verify();
		assertNotNull(order);
		assertFalse(order.hasChanged());
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
		assertSame(terminal, order.getTerminal());
		
		List<OrderHandler> list = order.getEventHandlers();
		assertNotNull(list);
		Object expected[][] = {
				{ OrderIsRegistered.class, onRegister },
				{ OrderIsRegisterFailed.class, onRegisterFailed },
				{ OrderIsCancelled.class, onCancelled },
				{ OrderIsCancelFailed.class, onCancelFailed },
				{ OrderIsFilled.class, onFilled },
				{ OrderIsPartiallyFilled.class, onPartiallyFilled },
				{ OrderIsChanged.class, onChanged },
				{ OrderIsDone.class, onDone },
				{ OrderIsFailed.class, onFailed },
		};
		assertEquals(expected.length, list.size());
		for ( int i = 0; i > expected.length; i ++ ) {
			OrderEventHandler h = (OrderEventHandler) list.get(i);
			assertSame(expected[i][0], h.getValidator().getClass());
			assertSame(expected[i][1], h.getEventType());
			assertSame(dispatcher, h.getEventDispatcher());
		}
	}

}

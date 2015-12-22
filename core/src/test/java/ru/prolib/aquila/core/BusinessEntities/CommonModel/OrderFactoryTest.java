package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderEventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsCancelFailed;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsCancelled;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsChanged;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsDone;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsFailed;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsFilled;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsPartiallyFilled;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsRegisterFailed;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderIsRegistered;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderStateHandler;

public class OrderFactoryTest {
	private IMocksControl control;
	private EventSystem eventSystem;
	private EditableTerminal terminal;
	private OrderFactory factory;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventSystem = new EventSystemImpl();
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getEventSystem()).andStubReturn(eventSystem);
		factory = new OrderFactoryImpl();
	}

	@Test
	public void testCreateOrder() {
		control.replay();
		
		OrderImpl actual = (OrderImpl) factory.createOrder(terminal);
		
		control.verify();
		assertNotNull(actual);
		assertSame(terminal, actual.getTerminal());
		
		OrderEventDispatcher d = actual.getEventDispatcher();
		List<OrderStateHandler> h = new Vector<OrderStateHandler>();
		h.add(new OrderStateHandler(d, new OrderIsRegistered(), d.OnRegistered()));
		h.add(new OrderStateHandler(d, new OrderIsRegisterFailed(), d.OnRegisterFailed()));
		h.add(new OrderStateHandler(d, new OrderIsCancelled(), d.OnCancelled()));
		h.add(new OrderStateHandler(d, new OrderIsCancelFailed(), d.OnCancelFailed()));
		h.add(new OrderStateHandler(d, new OrderIsFilled(), d.OnFilled()));
		h.add(new OrderStateHandler(d, new OrderIsPartiallyFilled(), d.OnPartiallyFilled()));
		h.add(new OrderStateHandler(d, new OrderIsChanged(), d.OnChanged()));
		h.add(new OrderStateHandler(d, new OrderIsDone(), d.OnDone()));
		h.add(new OrderStateHandler(d, new OrderIsFailed(), d.OnFailed()));
		
		assertEquals(h, actual.getStateHandlers());
	}

}

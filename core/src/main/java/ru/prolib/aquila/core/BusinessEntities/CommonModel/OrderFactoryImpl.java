package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import java.util.List;
import java.util.Vector;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
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
import ru.prolib.aquila.core.BusinessEntities.utils.OrderStateValidator;

/**
 * Common implementation of an order factory.
 * This factory produce instance of a common implementation of the order class. 
 */
public class OrderFactoryImpl implements OrderFactory {
	
	public OrderFactoryImpl() {
		
	}

	@Override
	public EditableOrder createOrder(EditableTerminal terminal) {
		OrderEventDispatcher d;
		d =	new OrderEventDispatcher(terminal.getEventSystem()); 

		List<OrderStateHandler> h = new Vector<OrderStateHandler>();
		add(h, d, new OrderIsRegistered(), d.OnRegistered());
		add(h, d, new OrderIsRegisterFailed(), d.OnRegisterFailed());
		add(h, d, new OrderIsCancelled(), d.OnCancelled());
		add(h, d, new OrderIsCancelFailed(), d.OnCancelFailed());
		add(h, d, new OrderIsFilled(), d.OnFilled());
		add(h, d, new OrderIsPartiallyFilled(), d.OnPartiallyFilled());
		add(h, d, new OrderIsChanged(), d.OnChanged());
		add(h, d, new OrderIsDone(), d.OnDone());
		add(h, d, new OrderIsFailed(), d.OnFailed());
		return new OrderImpl(d, h, terminal);
	}
		
	private void add(List<OrderStateHandler> list,
			OrderEventDispatcher dispatcher, OrderStateValidator validator,
			EventType targetType)
	{
		list.add(new OrderStateHandler(dispatcher, validator, targetType));
	}

}

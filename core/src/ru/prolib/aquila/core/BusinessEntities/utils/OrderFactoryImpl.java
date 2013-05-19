package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsCancelFailed;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsCancelled;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsChanged;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsDone;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsFailed;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsFilled;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsPartiallyFilled;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsRegisterFailed;
import ru.prolib.aquila.core.BusinessEntities.validator.OrderIsRegistered;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Стандартная фабрика заявок.
 * <p>
 * 2012-10-17<br>
 * $Id: OrderFactoryImpl.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class OrderFactoryImpl implements OrderFactory {
	private static final int IDX_REGISTER = 0;
	private static final int IDX_REGISTER_FAILED = 1;
	private static final int IDX_CANCELLED = 2;
	private static final int IDX_CANCEL_FAILED = 3;
	private static final int IDX_FILLED = 4;
	private static final int IDX_PARTIALLY_FILLED = 5;
	private static final int IDX_CHANGED = 6;
	private static final int IDX_DONE = 7;
	private static final int IDX_FAILED = 8;
	
	private final EventSystem es;
	private final Terminal terminal;

	/**
	 * Конструктор.
	 * <p>
	 * @param eventSystem фасад событийной системы
	 * @param terminal терминал
	 */
	public OrderFactoryImpl(EventSystem eventSystem, Terminal terminal) {
		super();
		this.es = eventSystem;
		this.terminal = terminal;
	}
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return событийная система
	 */
	public EventSystem getEventSystem() {
		return es;
	}
	
	/**
	 * Получить экземпляр терминала.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public EditableOrder createOrder() {
		EventDispatcher dispatcher = es.createEventDispatcher("Order");
		Validator validators[] = {
				new OrderIsRegistered(),
				new OrderIsRegisterFailed(),
				new OrderIsCancelled(),
				new OrderIsCancelFailed(),
				new OrderIsFilled(),
				new OrderIsPartiallyFilled(),
				new OrderIsChanged(),
				new OrderIsDone(),
				new OrderIsFailed()
		};
		List<OrderEventHandler> handlers = new LinkedList<OrderEventHandler>();
		List<OrderHandler> handlers2 = new LinkedList<OrderHandler>();
		OrderEventHandler h = null;
		String[] id = {
				"OnRegister",
				"OnRegisterFailed",
				"OnCancelled",
				"OnCancelFailed",
				"OnFilled",
				"OnPartiallyFilled",
				"OnChanged",
				"OnDone",
				"OnFailed",
		};
		for ( int i = 0; i < validators.length; i ++ ) {
			h = new OrderEventHandler(dispatcher, validators[i],
					es.createGenericType(dispatcher, id[i]));
			handlers.add(h);
			handlers2.add(h);
		}
		return new OrderImpl(dispatcher,
				handlers.get(IDX_REGISTER).getEventType(),
				handlers.get(IDX_REGISTER_FAILED).getEventType(),
				handlers.get(IDX_CANCELLED).getEventType(),
				handlers.get(IDX_CANCEL_FAILED).getEventType(),
				handlers.get(IDX_FILLED).getEventType(),
				handlers.get(IDX_PARTIALLY_FILLED).getEventType(),
				handlers.get(IDX_CHANGED).getEventType(),
				handlers.get(IDX_DONE).getEventType(),
				handlers.get(IDX_FAILED).getEventType(),
				es.createGenericType(dispatcher, "OnTrade"),
				handlers,
				terminal);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == OrderFactoryImpl.class ) {
			return new EqualsBuilder()
				.append(es, ((OrderFactoryImpl) other).es)
				.isEquals();
		} else {
			return false;
		}
	}

}

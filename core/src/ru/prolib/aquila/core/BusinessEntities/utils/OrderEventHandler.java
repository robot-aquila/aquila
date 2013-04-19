package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.OrderEvent;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Генератор события заявки.
 * <p>
 * 2012-09-25<br>
 * $Id: OrderEventHandler.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public class OrderEventHandler implements OrderHandler {
	private final EventDispatcher dispatcher;
	private final Validator validator;
	private final EventType type;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param validator валидатор условия генерации события
	 * @param type тип генерируемого события 
	 */
	public OrderEventHandler(EventDispatcher dispatcher,
							 Validator validator,
							 EventType type)
	{
		super();
		this.dispatcher = dispatcher;
		this.validator = validator;
		this.type = type;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить валидатор условия генерации события.
	 * <p>
	 * @return валидатор условия
	 */
	public Validator getValidator() {
		return validator;
	}
	
	/**
	 * Получить тип генерируемого события.
	 * <p>
	 * @return тип события
	 */
	public EventType getEventType() {
		return type;
	}

	@Override
	public void handle(EditableOrder order) throws OrderException {
		try {
			if ( validator.validate(order) ) {
				dispatcher.dispatch(new OrderEvent(type, order));
			}
		} catch ( ValidatorException e ) {
			throw new OrderException(e);
		}
	}

}

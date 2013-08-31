package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Генератор события заявки.
 */
public class OrderStateHandler {
	private final OrderEventDispatcher dispatcher;
	private final OrderStateValidator validator;
	private final EventType type;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param validator валидатор условия генерации события
	 * @param type тип генерируемого события 
	 */
	public OrderStateHandler(OrderEventDispatcher dispatcher,
							 OrderStateValidator validator,
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
	public OrderEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить валидатор условия генерации события.
	 * <p>
	 * @return валидатор условия
	 */
	public OrderStateValidator getValidator() {
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

	/**
	 * Обработать состояние заявки.
	 * <p>
	 * Данный метод выполняет проверку текущего состояния объекта и, если
	 * состояние удовлетворяет определенным условиям, генерирует соответствующее
	 * событие заявки.
	 * <p>
	 * @param order
	 */
	public void handle(EditableOrder order) {
		if ( validator.validate(order) ) {
			dispatcher.dispatch(new OrderEvent(type, order));
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderStateHandler.class ) {
			return false;
		}
		OrderStateHandler o = (OrderStateHandler) other;
		return new EqualsBuilder()
			.append(o.dispatcher, dispatcher)
			.append(o.type, type)
			.append(o.validator, validator)
			.isEquals();
	}

}

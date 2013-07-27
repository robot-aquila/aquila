package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Контейнер активатора заявки.
 * <p>
 * Данный класс инкапсулирует связанную с активатором заявку и реализует методы
 * взаимодействия активатора с заявкой, изолируя реализацию активатора от
 * однообразного механизма связывания и размещения заявки в торговой системе.
 */
public class OrderActivatorLink {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(OrderActivatorLink.class);
	}
	
	private EditableOrder order;
	private OrderActivator activator;

	/**
	 * Связать активатор с заявкой.
	 * <p>
	 * Каждый активатор может быть связан только с одной заявкой в статусе
	 * {@link OrderStatus#PENDING} и только один раз. При попытке переназначить
	 * заяку для уже связанного контроллера будет возбуждено соответствующее
	 * исключение. 
	 * <p>
	 * Данный служебный метод не должен вызываться пользовательским кодом.
	 * Этот метод используется терминалом для связывания контроллера с заявкой
	 * и наоборот. Связывание активатора с заявкой приводит к смене статуса
	 * заявки на {@link OrderStatus#CONDITION} и генерации события об изменении
	 * заявки. После генерации события выполняется сброс маркера изменений.
	 * <p>
	 * @param order заявка
	 * @throws OrderException контроллер уже связан с заявкой или заявка
	 * в несоответствующем статусе
	 */
	public synchronized void link(OrderActivator activator, EditableOrder order)
		throws OrderException
	{
		synchronized ( order ) {
			if ( this.order != null ) {
				throw new OrderException("Already linked");
			}
			OrderStatus status = order.getStatus(); 
			if ( status != OrderStatus.PENDING ) {
				throw new OrderException("Rejected by status: " + status);
			}
			this.order = order;
			this.activator = activator;
			order.setActivator(activator);
			order.setStatus(OrderStatus.CONDITION);
			fireChanged();
		}
	}
	
	/**
	 * Генерировать событие об изменении состояния.
	 * <p>
	 * Этот служебный метод должен использоваться активаторами для
	 * информирования обозревателей заявки об изменении состояния активатором
	 * или атрибутов заявки, которые были изменены в результате действий
	 * активатора. После генерации события выполняется сброс маркера изменений. 
	 */
	public synchronized void fireChanged() {
		synchronized ( order ) {
			order.setChanged(EditableOrder.ACTIVATOR_CHANGED);
			order.fireChangedEvent();
			order.resetChanges();
		}
	}
	
	/**
	 * Разместить заявку в торговой системе.
	 * <p>
	 * Обеспечивает доступный для активатора механизм размещения заявки в
	 * торговой системе. Заявка размещается только в случае, если она находится
	 * в статусе {@link OrderStatus#CONDITION}. Ошибки размещения заявки
	 * не обрабатываются, так как терминал должен гарантировать выставления
	 * корректного статуса заявки в случае невозможности выполнить поручение.
	 */
	public synchronized void activate() {
		synchronized ( order ) {
			OrderStatus status = order.getStatus(); 
			if ( status != OrderStatus.CONDITION ) {
				Object args[] = { order.getId(), status };
				logger.warn("Unable to activate order #{} in status {}", args);
				return;
			}
			try {
				order.getTerminal().placeOrder(order);
			} catch ( OrderException e ) {
				logger.error("Error activate order: ", e);
			}
		}
	}
	
	/**
	 * Получить подконтрользую заявку.
	 * <p>
	 * Данный метод может быть использован активатором в случае, если условия
	 * работы подразумевают смену атрибутов заявки (например, цену или
	 * количество).
	 * <p>
	 * @return экземпляр связанной заявки
	 */
	public synchronized EditableOrder getOrder() {
		return order;
	}
	
	/**
	 * Установить экземпляр заявки.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @param order заявка
	 */
	void setOrder(EditableOrder order) {
		this.order = order;
	}
	
	/**
	 * Установить экземпляр активатора.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @param activator активатор заявки
	 */
	void setActivator(OrderActivator activator) {
		this.activator = activator;
	}
	
	/**
	 * Получить активатор заявки.
	 * <p>
	 * Служебный, только для тестов.
	 * <p>
	 * @return активатор
	 */
	OrderActivator getActivator() {
		return activator;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderActivatorLink.class ) {
			return false;
		}
		OrderActivatorLink o = (OrderActivatorLink) other;
		return new EqualsBuilder()
			.append(o.order, order)
			.append(o.activator, activator)
			.isEquals();
	}
	
}

package ru.prolib.aquila.ib.subsys.run;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.event.IBEventOrder;

/**
 * Обновление заявки.
 * <p>
 * 2013-01-07<br>
 * $Id: IBRunnableUpdateOrder.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBRunnableUpdateOrder implements Runnable {
	private final OrderResolver resolver;
	private final S<EditableOrder> modifier;
	private final IBEventOrder event;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param resolver определитель заявки
	 * @param modifier модификатор заявки
	 * @param event событие-основание
	 */
	public IBRunnableUpdateOrder(OrderResolver resolver,
			S<EditableOrder> modifier, IBEventOrder event)
	{
		super();
		this.resolver = resolver;
		this.modifier = modifier;
		this.event = event;
	}
	
	/**
	 * Получить определитель заявки.
	 * <p>
	 * @return определитель заявки
	 */
	public OrderResolver getOrderResolver() {
		return resolver;
	}
	
	/**
	 * Получить модификатор заявки.
	 * <p>
	 * @return модификатор заявки
	 */
	public S<EditableOrder> getOrderModifier() {
		return modifier;
	}
	
	/**
	 * Получить событие-основание.
	 * <p>
	 * @return событие
	 */
	public IBEventOrder getEvent() {
		return event;
	}

	@Override
	public void run() {
		long id = event.getOrderId();
		modifier.set(resolver.resolveOrder(id, id), event);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 50345)
			.append(resolver)
			.append(modifier)
			.append(event)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBRunnableUpdateOrder.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRunnableUpdateOrder o = (IBRunnableUpdateOrder) other;
		return new EqualsBuilder()
			.append(resolver, o.resolver)
			.append(modifier, o.modifier)
			.append(event, o.event)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return "Update order #" + event.getOrderId();
	}

}

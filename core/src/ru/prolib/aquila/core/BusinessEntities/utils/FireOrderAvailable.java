package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Генератор события: доступна новая заявка.
 * <p>
 * 2012-12-17<br>
 * $Id: FireEventOrderAvailable.java 339 2012-12-17 00:35:39Z whirlwind $
 */
public class FireOrderAvailable implements FireEditableEvent {
	private final EditableOrders orders;
	
	public FireOrderAvailable(EditableOrders orders) {
		super();
		this.orders = orders;
	}
	
	public EditableOrders getOrders() {
		return orders;
	}

	@Override
	public void fireEvent(Editable object) {
		orders.fireOrderAvailableEvent((Order) object);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121217, 43533)
			.append(orders)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == FireOrderAvailable.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		FireOrderAvailable o = (FireOrderAvailable) other;
		return new EqualsBuilder()
			.append(orders, o.orders)
			.isEquals();
	}

}

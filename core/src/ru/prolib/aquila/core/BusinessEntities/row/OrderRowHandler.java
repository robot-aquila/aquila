package ru.prolib.aquila.core.BusinessEntities.row;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderResolver;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.*;

/**
 * Обработчик ряда с данными заявки.
 * <p>
 * 2012-10-16<br>
 * $Id: OrderRowHandler.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderRowHandler implements RowHandler {
	private final FirePanicEvent firePanic;
	private final OrderResolver resolver;
	private final S<EditableOrder> modifier;
	
	public OrderRowHandler(FirePanicEvent firePanic, OrderResolver resolver,
			S<EditableOrder> modifier)
	{
		super();
		this.firePanic = firePanic;
		this.resolver = resolver;
		this.modifier = modifier;
	}
	
	public FirePanicEvent getFirePanicEvent() {
		return firePanic;
	}
	
	public OrderResolver getOrderResolver() {
		return resolver;
	}
	
	public S<EditableOrder> getOrderModifier() {
		return modifier;
	}

	@Override
	public void handle(Row row) {
		Long id = (Long) row.get(Spec.ORD_ID);
		Long transId = (Long) row.get(Spec.ORD_TRANSID);
		if ( id == null ) {
			firePanic.firePanicEvent(1, "Cannot handle order: orderId is NULL");
		} else {
			EditableOrder order = resolver.resolveOrder(id, transId);
			synchronized ( order ) {
				modifier.set(order, row);
			}			
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof OrderRowHandler ) {
			OrderRowHandler o = (OrderRowHandler) other;
			return new EqualsBuilder()
				.append(firePanic, o.firePanic)
				.append(resolver, o.resolver)
				.append(modifier, o.modifier)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121109, 144445)
			.append(firePanic)
			.append(resolver)
			.append(modifier)
			.toHashCode();
	}

}

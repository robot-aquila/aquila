package ru.prolib.aquila.ib.subsys.order;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.SetterArgs;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * Валидатор аргумента сеттера на соответствие типу {@link IBEventOpenOrder}.
 * <p> 
 * Данный валидатор используется в условном сеттере для переключения между
 * модификаторами заявки на основании заявок типа {@link IBEventOpenOrder}
 * и {@link IBEventOrderStatus}.
 * <p>
 * 2013-01-07<br>
 * $Id: IBIsEventOpenOrder.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBIsEventOpenOrder implements Validator {
	
	/**
	 * Конструктор.
	 */
	public IBIsEventOpenOrder() {
		super();
	}

	@Override
	public boolean validate(Object in) {
		if ( in instanceof SetterArgs ) {
			SetterArgs args = (SetterArgs) in;
			if ( args.getValue() instanceof IBEventOpenOrder ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 41817)
			.append(IBIsEventOpenOrder.class)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBIsEventOpenOrder.class;
	}

}

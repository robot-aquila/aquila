package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер средней цены исполненной части заявки.
 */
public class OrderSetAvgExecutedPrice implements S<EditableOrder> {
	
	public OrderSetAvgExecutedPrice() {
		super();
	}

	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value.getClass() == Double.class ) {
			object.setAvgExecutedPrice((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass()== OrderSetAvgExecutedPrice.class; 
	}

}

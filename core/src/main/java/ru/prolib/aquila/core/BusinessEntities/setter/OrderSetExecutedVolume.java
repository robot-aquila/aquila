package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер исполненного объема заявки.
 * <p>
 * 2012-12-25<br>
 * $Id: OrderSetExecutedVolume.java 378 2012-12-25 05:52:36Z whirlwind $
 */
public class OrderSetExecutedVolume implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetExecutedVolume() {
		super();
	}

	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value.getClass() == Double.class ) {
			object.setExecutedVolume((Double) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass()== OrderSetExecutedVolume.class; 
	}

}

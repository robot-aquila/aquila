package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер дескриптора инструмента заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetSecurityDescriptor.java 374 2012-12-25 02:22:40Z whirlwind $
 */
public class OrderSetSecurityDescriptor implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetSecurityDescriptor() {
		super();
	}

	/**
	 * Установить дескриптор инструмента заявки.
	 */
	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value instanceof SecurityDescriptor ) {
			object.setSecurityDescriptor((SecurityDescriptor) value);
		}
	}
	
	@Override
	public boolean equals(Object object) {
		return object != null
			&& object.getClass() == OrderSetSecurityDescriptor.class;
	}

}

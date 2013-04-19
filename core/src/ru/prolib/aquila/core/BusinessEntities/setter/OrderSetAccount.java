package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер счета заявки.
 * <p>
 * 2012-09-26<br>
 * $Id: OrderSetAccount.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class OrderSetAccount implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetAccount() {
		super();
	}

	/**
	 * Установить счет заявки.
	 */
	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value.getClass() == Account.class ) {
			object.setAccount((Account) value);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == OrderSetAccount.class ) {
			return true;
		} else {
			return false;
		}
	}

}

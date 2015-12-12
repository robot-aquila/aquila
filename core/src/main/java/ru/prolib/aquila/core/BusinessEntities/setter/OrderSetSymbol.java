package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер дескриптора инструмента заявки.
 * <p>
 */
public class OrderSetSymbol implements S<EditableOrder> {
	
	/**
	 * Создать сеттер.
	 */
	public OrderSetSymbol() {
		super();
	}

	/**
	 * Установить дескриптор инструмента заявки.
	 */
	@Override
	public void set(EditableOrder object, Object value) throws ValueException {
		if ( value != null && value instanceof Symbol ) {
			object.setSymbol((Symbol) value);
		}
	}
	
	@Override
	public boolean equals(Object object) {
		return object != null
			&& object.getClass() == OrderSetSymbol.class;
	}

}

package ru.prolib.aquila.core.BusinessEntities.CommonModel;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * An order factory interface.
 */
public interface OrderFactory {
	
	public EditableOrder createOrder(EditableTerminal terminal);

}

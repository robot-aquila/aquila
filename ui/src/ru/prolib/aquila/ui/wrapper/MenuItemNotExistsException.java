package ru.prolib.aquila.ui.wrapper;

/**
 * Запрошен несуществующий элемент меню.
 * <p>
 * 2013-03-01<br>
 * $Id: MenuItemNotExistsException.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class MenuItemNotExistsException extends MenuException {
	private static final long serialVersionUID = 5929467572537236255L;

	public MenuItemNotExistsException(String itemId) {
		super("Item not exists: " + itemId);
	}

}

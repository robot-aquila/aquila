package ru.prolib.aquila.ui.wrapper;

/**
 * Элемент меню с указанным идентификатором уже существует.
 * <p>
 * 2013-03-01<br>
 * $Id: MenuItemAlreadyExistsException.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class MenuItemAlreadyExistsException extends MenuException {
	private static final long serialVersionUID = 1253313974249132251L;

	public MenuItemAlreadyExistsException(String itemId) {
		super("Item already exists: " + itemId);
	}

}

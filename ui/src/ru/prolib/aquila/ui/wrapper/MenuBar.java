package ru.prolib.aquila.ui.wrapper;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ru.prolib.aquila.core.EventSystem;

/**
 * Конструктор/обертка JMenuBar.
 * <p>
 * 2013-03-01<br>
 * $Id: MenuBar.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class MenuBar {
	private final JMenuBar underlying;
	private final EventSystem es;
	private final Map<String, Menu> childMenu;
	
	public MenuBar(EventSystem es) {
		super();
		this.es = es;
		underlying = new JMenuBar();
		childMenu = new HashMap<String, Menu>();
	}
	
	public JMenuBar getUnderlyingObject() {
		return underlying;
	}
	
	public synchronized boolean isMenuExists(String id) {
		return childMenu.containsKey(id);
	}
	
	public synchronized Menu getMenu(String id) throws MenuException {
		Menu menu = childMenu.get(id);
		if ( menu == null ) {
			throw new MenuItemNotExistsException(id);
		}
		return menu;
	}
	
	public synchronized Menu addMenu(String id, String title)
		throws MenuException
	{
		if ( isMenuExists(id) ) {
			throw new MenuItemAlreadyExistsException(id);
		}
		Menu menu = new Menu(new JMenu(title), es);
		childMenu.put(id, menu);
		underlying.add(menu.getUnderlyingObject());
		return menu;
	}

}

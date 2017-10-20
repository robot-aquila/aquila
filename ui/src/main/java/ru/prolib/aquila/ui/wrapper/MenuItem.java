package ru.prolib.aquila.ui.wrapper;

import javax.swing.*;

import ru.prolib.aquila.core.*;

/**
 * Обертка пункта меню.
 * <p>
 * 2013-03-01<br>
 * $Id: MenuItem.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class MenuItem {
	private final JMenuItem underlying;
	private final EventType onCommand;
	
	public MenuItem(JMenuItem underlying, EventType onCommand) {
		super();
		this.underlying = underlying;
		this.onCommand = onCommand;
	}

	public EventType OnCommand() {
		return onCommand;
	}
	
	public JMenuItem getUnderlyingObject() {
		return underlying;
	}
	
	public void setEnabled(boolean flag) {
		underlying.setEnabled(flag);
	}

}

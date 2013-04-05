package ru.prolib.aquila.ui.wrapper;

public class TableColumnAlreadyExistsException extends Exception {

	/**
	 * $Id: TableColumnAlreadyExistsException.java 575 2013-03-13 23:40:00Z huan.kaktus $
	 */
	private static final long serialVersionUID = 1533363168550904493L;
	
	public TableColumnAlreadyExistsException(String name) {
		super("Table column already exists: "+name);
	}

}

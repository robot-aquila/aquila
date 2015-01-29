package ru.prolib.aquila.ui.wrapper;

/**
 * $Id: TableColumnNotExistsException.java 575 2013-03-13 23:40:00Z huan.kaktus $
 */
public class TableColumnNotExistsException extends Exception {

	private static final long serialVersionUID = -6525724550230711243L;
	
	public TableColumnNotExistsException(String name) {
		super("Table column not exists: "+name);
	}

}

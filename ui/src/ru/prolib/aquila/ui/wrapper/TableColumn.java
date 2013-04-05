package ru.prolib.aquila.ui.wrapper;

import ru.prolib.aquila.core.data.G;
/**
 * $Id: TableColumn.java 575 2013-03-13 23:40:00Z huan.kaktus $
 */
public class TableColumn {
	private String name;
	private G<?> getter;
	private int width;
	
	public TableColumn(String name, G<?> getter) {
		this.name = name;
		this.getter = getter;
	}
	
	public TableColumn(String name, G<?> getter, int width) {
		this.name = name;
		this.getter = getter;
		this.width = width;
	}
	
	public String getName() {
		return name;
	}
	
	public G<?> getGetter() {
		return getter;
	}
	
	public int getWidth() {
		return width;
	}
}

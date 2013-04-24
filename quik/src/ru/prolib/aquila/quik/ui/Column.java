package ru.prolib.aquila.quik.ui;

import ru.prolib.aquila.core.data.G;

/**
 * Дескриптор колонки.
 */
public class Column {
	private final String id;
	private final G<?> getter;
	private final int width;
	public static final int SHORT = 10;
	public static final int MIDDLE = 20;
	public static final int lONG = 40;
	
	public Column(String id, G<?> getter, int width) {
		super();
		this.id = id;
		this.getter = getter;
		this.width = width;
	}
	
	public String getId() {
		return id;
	}
	
	public G<?> getGetter() {
		return getter;
	}
	
	public int getWidth() {
		return width;
	}

}

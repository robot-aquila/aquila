package ru.prolib.aquila.ui.table;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.text.MsgID;

/**
 * Дескриптор колонки.
 */
public class Column {
	private final MsgID id;
	private final G<?> getter;
	private final int width;
	public static final int SHORT = 10;
	public static final int MIDDLE = 20;
	public static final int LONG = 60;
	
	public Column(MsgID id, G<?> getter, int width) {
		super();
		this.id = id;
		this.getter = getter;
		this.width = width;
	}
	
	public MsgID getId() {
		return id;
	}
	
	public G<?> getGetter() {
		return getter;
	}
	
	public int getWidth() {
		return width;
	}

}

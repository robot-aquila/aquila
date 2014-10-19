package ru.prolib.aquila.core.data;

import java.util.*;

/** 
 * Элементарный ридер тиков.
 * <p>
 * Предназначен для создания потоков данных на основе коллекции тиков. 
 */
public class SimpleTickReader extends SimpleIterator<Tick> implements TickReader {
	
	public SimpleTickReader(List<Tick> ticks) {
		super(ticks);
	}
	
	public SimpleTickReader() {
		super();
	}

}

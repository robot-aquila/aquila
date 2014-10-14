package ru.prolib.aquila.core.data;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** 
 * Элементарный ридер тиков.
 * <p>
 * Предназначен для создания потоков данных на основе коллекции тиков. 
 */
public class SimpleTickReader implements TickReader {
	private final Deque<Tick> ticks;
	
	public SimpleTickReader(List<Tick> ticks) {
		super();
		this.ticks = new ArrayDeque<Tick>(ticks);
	}

	@Override
	public Tick read() throws IOException {
		return ticks.pollFirst();
	}

	@Override
	public void close() {
		ticks.clear();
	}

}

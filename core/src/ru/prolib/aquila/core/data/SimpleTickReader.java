package ru.prolib.aquila.core.data;

import java.util.*;

/** 
 * Элементарный ридер тиков.
 * <p>
 * Предназначен для создания потоков данных на основе коллекции тиков. 
 */
public class SimpleTickReader implements TickReader {
	private final Deque<Tick> ticks;
	private Tick curr;
	private boolean closed = false;
	
	public SimpleTickReader(List<Tick> ticks) {
		super();
		this.ticks = new ArrayDeque<Tick>(ticks);
	}
	
	public SimpleTickReader() {
		super();
		this.ticks = new ArrayDeque<Tick>();
	}

	@Override
	public Tick current() throws DataException {
		if ( curr == null || closed ) {
			throw new DataException("No data under cursor");
		}
		return curr;
	}
	
	@Override
	public boolean next() throws DataException {
		if ( ticks.size() > 0 ) {
			curr = ticks.pollFirst();
			return true;
		} else {
			closed = true;
			return false;
		}
	}

	@Override
	public void close() {
		ticks.clear();
		closed = true;
	}

}

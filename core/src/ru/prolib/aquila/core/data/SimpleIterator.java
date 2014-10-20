package ru.prolib.aquila.core.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Простой универсальный итератор на базе коллекции.
 * <p>
 * @param <T> - тип значений
 */
public class SimpleIterator<T> implements Aqiterator<T> {
	protected final Deque<T> data;
	private T curr;
	private boolean closed = false;

	public SimpleIterator(List<T> data) {
		super();
		this.data = new ArrayDeque<T>(data);
	}
	
	public SimpleIterator() {
		super();
		this.data = new ArrayDeque<T>();
	}

	@Override
	public T item() throws DataException {
		if ( curr == null || closed ) {
			throw new DataException("No data under cursor");
		}
		return curr;
	}

	@Override
	public boolean next() throws DataException {
		if ( data.size() > 0 ) {
			curr = data.pollFirst();
			return true;
		} else {
			closed = true;
			return false;
		}
	}

	@Override
	public void close() {
		data.clear();
		closed = true;
	}

}
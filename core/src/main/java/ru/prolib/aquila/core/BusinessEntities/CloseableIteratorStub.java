package ru.prolib.aquila.core.BusinessEntities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CloseableIteratorStub<T> implements CloseableIterator<T> {
	private final List<T> elements;
	private int current = -1;
	
	public CloseableIteratorStub() {
		this.elements = new ArrayList<>();
	}
	
	public CloseableIteratorStub(List<? extends T> elements) {
		this.elements = new ArrayList<>(elements);
	}
	
	/**
	 * Add element to the end of the set of elements.
	 * <p>
	 * @param element - element to add
	 * @return this
	 */
	public CloseableIteratorStub<T> add(T element) {
		this.elements.add(element);
		return this;
	}

	@Override
	public void close() throws IOException {
		elements.clear();
	}

	@Override
	public boolean next() throws IOException {
		return ++current < elements.size();
	}

	@Override
	public T item() throws IOException, NoSuchElementException {
		try {
			return elements.get(current);
		} catch ( IndexOutOfBoundsException e ) {
			throw new NoSuchElementException();
		}
	}

}

package ru.prolib.aquila.core.BusinessEntities;

import java.io.Closeable;
import java.io.IOException;
import java.util.NoSuchElementException;

public interface CloseableIterator<T> extends Closeable {
	
	/**
	 * Move cursor to the next item.
	 * <p>
	 * @return returns true if the iterator has next element under cursor
	 * @throws IOException - an error occurred
	 */
	public boolean next() throws IOException;
	
	/**
	 * Get item under cursor.
	 * <p>
	 * @return the item under the cursor
	 * @throws IOException - an error occurred
	 * @throws NoSuchElementException - no element under the cursor
	 */
	public T item() throws IOException, NoSuchElementException;

}

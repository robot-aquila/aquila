package ru.prolib.aquila.data;

import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;

/**
 * Reader factory interface.
 */
public interface ReaderFactory<T> {

	/**
	 * Create item reader.
	 * <p>
	 * @return the reader instance
	 * @throws IOException - an error occurred
	 */
	public CloseableIterator<T> createReader() throws IOException;
	
}

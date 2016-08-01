package ru.prolib.aquila.data;

import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;

/**
 * Reader factory interface.
 */
public interface ReaderFactory<T> {

	public CloseableIterator<? extends T> createReader() throws IOException;
	
}

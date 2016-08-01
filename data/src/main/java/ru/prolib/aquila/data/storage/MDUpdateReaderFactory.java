package ru.prolib.aquila.data.storage;

import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.MDUpdate;

/**
 * Market depth update reader factory interface.
 */
public interface MDUpdateReaderFactory {

	public CloseableIterator<MDUpdate> createReader() throws IOException;
	
}

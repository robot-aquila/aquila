package ru.prolib.aquila.data.storage.segstor;

import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;

public interface SymbolSegmentStorage<T> {
	
	/**
	 * Get available symbols.
	 * <p>
	 * @return symbols
	 */
	Set<Symbol> listSymbols();
	
	boolean isExists(SymbolSegment segment);
	
 	SegmentMetaData getMetaData(SymbolSegment segment) throws DataStorageException;
	CloseableIterator<T> createReader(SymbolSegment segment) throws DataStorageException;
	
}

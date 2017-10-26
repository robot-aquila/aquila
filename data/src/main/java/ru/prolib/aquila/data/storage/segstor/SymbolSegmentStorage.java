package ru.prolib.aquila.data.storage.segstor;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.data.storage.DataStorageException;

public interface SymbolSegmentStorage<T> extends SymbolSegmentStorageCommon {
	
	boolean isExists(SymbolSegment segment);
	
 	SegmentMetaData getMetaData(SymbolSegment segment) throws DataStorageException;
	CloseableIterator<T> createReader(SymbolSegment segment) throws DataStorageException;
	
}

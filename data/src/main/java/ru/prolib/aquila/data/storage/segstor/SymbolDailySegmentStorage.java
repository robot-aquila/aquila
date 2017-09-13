package ru.prolib.aquila.data.storage.segstor;

import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;

public interface SymbolDailySegmentStorage<T> {
	
	/**
	 * Get available symbols.
	 * <p>
	 * @return symbols
	 */
	Set<Symbol> listSymbols();

	boolean isExists(SymbolDaily segment);

	List<SymbolDaily> listDailySegments(Symbol symbol) throws DataStorageException;
	List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, DatePoint to)
			throws DataStorageException;
	List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from)
			throws DataStorageException;
	List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, int maxCount)
			throws DataStorageException;
	List<SymbolDaily> listDailySegments(Symbol symbol, int maxCount, DatePoint to)
			throws DataStorageException;
	List<SymbolDaily> listDailySegments(Symbol symbol, MonthPoint month)
			throws DataStorageException;

	SegmentMetaData getMetaData(SymbolDaily segment) throws DataStorageException;
	CloseableIterator<T> createReader(SymbolDaily segment) throws DataStorageException;

}

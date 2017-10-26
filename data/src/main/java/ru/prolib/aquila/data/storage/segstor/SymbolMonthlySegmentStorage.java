package ru.prolib.aquila.data.storage.segstor;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;

/**
 * Storage of symbol data grouped by months.
 * <p>
 * This interface represents a storage which stores segments of symbol data.
 * Each segment associated with symbol and month of year.
 * <p>
 * @param <T> - type of stored data
 */
public interface SymbolMonthlySegmentStorage<T> extends SymbolSegmentStorageCommon {

	boolean isExists(SymbolMonthly segment);

	List<SymbolMonthly> listMonthlySegments(Symbol symbol) throws DataStorageException;
	List<SymbolMonthly> listMonthlySegments(Symbol symbol, MonthPoint from, MonthPoint to)
			throws DataStorageException;
	List<SymbolMonthly> listMonthlySegments(Symbol symbol, MonthPoint from)
			throws DataStorageException;
	List<SymbolMonthly> listMonthlySegments(Symbol symbol, MonthPoint from, int maxCount)
			throws DataStorageException;
	List<SymbolMonthly> listMonthlySegments(Symbol symbol, int maxCount, MonthPoint to)
			throws DataStorageException;
	List<SymbolMonthly> listMonthlySegments(Symbol symbol, YearPoint year)
			throws DataStorageException;
	
	SegmentMetaData getMetaData(SymbolMonthly segment) throws DataStorageException;
	CloseableIterator<T> createReader(SymbolMonthly segment) throws DataStorageException;
	
}

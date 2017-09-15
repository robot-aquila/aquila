package ru.prolib.aquila.data.storage.segstor;

import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;

public interface SymbolMonthlySegmentStorage<T> {

	/**
	 * Get available symbols.
	 * <p>
	 * @return symbols
	 */
	Set<Symbol> listSymbols();

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
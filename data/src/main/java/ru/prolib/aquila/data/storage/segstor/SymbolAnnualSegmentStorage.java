package ru.prolib.aquila.data.storage.segstor;

import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;

public interface SymbolAnnualSegmentStorage<T> {

	/**
	 * Get available symbols.
	 * <p>
	 * @return symbols
	 */
	Set<Symbol> listSymbols();

	boolean isExists(SymbolAnnual segment);

	List<SymbolAnnual> listAnnualSegments(Symbol symbol) throws DataStorageException;
	List<SymbolAnnual> listAnnualSegments(Symbol symbol, YearPoint from, YearPoint to)
			throws DataStorageException;
	List<SymbolAnnual> listAnnualSegments(Symbol symbol, YearPoint from)
			throws DataStorageException;
	List<SymbolAnnual> listAnnualSegments(Symbol symbol, YearPoint from, int maxCount)
			throws DataStorageException;
	List<SymbolAnnual> listAnnualSegments(Symbol symbol, int maxCount, YearPoint to)
			throws DataStorageException;
	
	SegmentMetaData getMetaData(SymbolAnnual segment) throws DataStorageException;
	CloseableIterator<T> createReader(SymbolAnnual segment) throws DataStorageException;
	
}

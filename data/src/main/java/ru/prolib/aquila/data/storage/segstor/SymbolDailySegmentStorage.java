package ru.prolib.aquila.data.storage.segstor;

import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;

/**
 * Storage interface of the symbol's daily segments.
 * <p>
 * @param <T> - type of stored data (record data type)
 */
public interface SymbolDailySegmentStorage<T> {
	
	/**
	 * Get available symbols.
	 * <p>
	 * @return symbols
	 */
	Set<Symbol> listSymbols();

	/**
	 * Check that the specified segment is exists.
	 * <p>
	 * @param segment - segment reference
	 * @return true if the segment exists, false otherwise
	 */
	boolean isExists(SymbolDaily segment);

	/**
	 * List all segments for the symbol.
	 * <p>
	 * @param symbol - the symbol
	 * @return ordered list of segments according to specified criteria
	 * @throws DataStorageException if an error occurred
	 */
	List<SymbolDaily> listDailySegments(Symbol symbol) throws DataStorageException;
	
	/**
	 * List segments between two dates.
	 * <p>
	 * @param symbol - the symbol
	 * @param from - date of period start (inclusive)
	 * @param to - date of period end (exclusive) 
	 * @return ordered list of segments according to specified criteria
	 * @throws DataStorageException if an error occurred
	 */
	List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, DatePoint to)
			throws DataStorageException;
	
	/**
	 * List segments starting from date.
	 * <p>
	 * @param symbol - the symbol
	 * @param from - date of period start (inclusive)
	 * @return ordered list of segments according to specified criteria
	 * @throws DataStorageException if an error occurred
	 */
	List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from)
			throws DataStorageException;
	
	/**
	 * List segments starting from the date with limit by number of segments.
	 * <p>
	 * @param symbol - the symbol
	 * @param from - date of period start (inclusive)
	 * @param maxNumSegments - max number of segments to include
	 * @return ordered list of segments according to specified criteria
	 * @throws DataStorageException if an error occurred
	 */
	List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, int maxNumSegments)
			throws DataStorageException;
	
	/**
	 * List segments before the date with limit by max number of segments.
	 * @param symbol - the symbol
	 * @param maxNumSegments - max number of segments to include
	 * @param to - date of period end (exclusive)
	 * @return ordered list of segments according to specified criteria
	 * @throws DataStorageException if an error occurred
	 */
	List<SymbolDaily> listDailySegments(Symbol symbol, int maxNumSegments, DatePoint to)
			throws DataStorageException;
	
	/**
	 * List segments of the month.
	 * <p>
	 * @param symbol - the symbol
	 * @param month - month to scan for segments
	 * @return ordered list of segments according to specified criteria
	 * @throws DataStorageException if an error occurred
	 */
	List<SymbolDaily> listDailySegments(Symbol symbol, MonthPoint month)
			throws DataStorageException;

	/**
	 * Get metadata of the segment.
	 * <p>
	 * @param segment - segment reference
	 * @return segment metadata
	 * @throws SymbolDailySegmentNotExistsException if segment not exists
	 * @throws DataStorageException if an error occurred
	 */
	SegmentMetaData getMetaData(SymbolDaily segment) throws DataStorageException;
	
	/**
	 * Create reader of segment data.
	 * <p>
	 * @param segment - segment reference
	 * @return segment data reader
	 * @throws SymbolDailySegmentNotExistsException if segment not exists
	 * @throws DataStorageException if an error occurred
	 */
	CloseableIterator<T> createReader(SymbolDaily segment) throws DataStorageException;

}

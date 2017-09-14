package ru.prolib.aquila.data.storage.segstor.file;

import java.io.File;
import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolAnnual;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;

public interface SegmentFileManager {

	/**
	 * Get directory to store daily segments of specified month.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @return daily segments directory
	 */
	File getDirectory(SymbolMonthly criteria);
	
	/**
	 * Get directory to store monthly segments of specified year.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @return monthly segments directory
	 */
	File getDirectory(SymbolAnnual criteria);
	
	/**
	 * Get directory to store common segments of specified symbol.
	 * <p>
	 * @param criteria - symbol
	 * @return common segments directory
	 */
	File getDirectory(Symbol criteria);

	/**
	 * Scan for symbols.
	 * <p>
	 * @return set of existing symbols
	 */
	Set<Symbol> scanForSymbolDirectories();
	
	/**
	 * Scan for year directories of symbol.
	 * <p>
	 * @param criteria - symbol
	 * @return sorted list of annual descriptors for existing directories
	 */
	List<SymbolAnnual> scanForYearDirectories(Symbol criteria);
	
	/**
	 * Scan for month directories of symbol and year.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @return sorted list of monthly descriptors for existing directories
	 */
	List<SymbolMonthly> scanForMonthDirectories(SymbolAnnual criteria);
	
	/**
	 * Get file info for common symbol segment.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return segment file info
	 */
	SegmentFileInfo getFileInfo(Symbol criteria, String suffix);
	
	/**
	 * Get file info for daily symbol segment.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @param suffix - segment file suffix
	 * @return segment file info
	 */
	SegmentFileInfo getFileInfo(SymbolDaily criteria, String suffix);
	
	/**
	 * Get file info for monthly symbol segment.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @param suffix - segment file suffix
	 * @return segment file info
	 */
	SegmentFileInfo getFileInfo(SymbolMonthly criteria, String suffix);
	
	/**
	 * Get file info for annual symbol segment.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @param suffix - segment file suffix
	 * @return segment file info
	 */
	SegmentFileInfo getFileInfo(SymbolAnnual criteria, String suffix);

	/**
	 * Check for common symbol segment exists.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return true if segment file exists, false - otherwise
	 */
	boolean hasSymbolSegment(Symbol criteria, String suffix);

	/**
	 * Scan for common segments of all existing symbols.
	 * <p>
	 * @param suffix - segment file suffix
	 * @return all symbols whose have segment file with the suffix specified
	 */
	List<Symbol> scanForSymbolSegments(String suffix);
	
	/**
	 * Scan for annual segments of the symbol specified.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return sorted list of annual descriptors for existing segments
	 */
	List<SymbolAnnual> scanForAnnualSegments(Symbol criteria, String suffix);
	
	/**
	 * Scan for monthly segments of the symbol specified.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @param suffix - segment file suffix
	 * @return sorted list of monthly descriptors for existing segments
	 */
	List<SymbolMonthly> scanForMonthlySegments(SymbolAnnual criteria, String suffix);
	
	/**
	 * Scan for daily segments of the symbol specified.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @param suffix - segment file suffix
	 * @return sorted list of daily descriptors for existing segments
	 */
	List<SymbolDaily> scanForDailySegments(SymbolMonthly criteria, String suffix);

	/**
	 * Check for annual segments exists.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return true if at least one annual segment exists, false - otherwise
	 */
	boolean hasAnnualSegments(Symbol criteria, String suffix);
	
	/**
	 * Check for monthly segments exists.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return true if at least one monthly segment exists, false - otherwise
	 */
	boolean hasMonthlySegments(Symbol criteria, String suffix);
	
	/**
	 * Check for daily segments exists.
	 * <p>
	 * @param criteria - criteria
	 * @param suffix - segment file suffix
	 * @return true if at least one daily segment exists, false - otherwise
	 */
	boolean hasDailySegments(Symbol criteria, String suffix);
	
	/**
	 * Get first available annual segment.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return descriptor of the first annual segment
	 * @throws DataStorageException no segments found
	 */
	SymbolAnnual getFirstAnnualSegment(Symbol criteria, String suffix) throws DataStorageException;
	
	/**
	 * Get first available monthly segment.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return descriptor of the first monthly segment
	 * @throws DataStorageException no segments found
	 */
	SymbolMonthly getFirstMonthlySegment(Symbol criteria, String suffix) throws DataStorageException;
	
	/**
	 * Get first available daily segment.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return descriptor of the first daily segment
	 * @throws DataStorageException no segments found
	 */
	SymbolDaily getFirstDailySegment(Symbol criteria, String suffix) throws DataStorageException;
	
	/**
	 * Get last available annual segment.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return descriptor of the last annual segment
	 * @throws DataStorageException no segments found
	 */
	SymbolAnnual getLastAnnualSegment(Symbol criteria, String suffix) throws DataStorageException;
	
	/**
	 * Get last available monthly segment.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return descriptor of the last monthly segment
	 * @throws DataStorageException no segments found
	 */
	SymbolMonthly getLastMonthlySegment(Symbol criteria, String suffix) throws DataStorageException;
	
	/**
	 * Get last daily segment.
	 * <p>
	 * @param criteria - symbol
	 * @param suffix - segment file suffix
	 * @return descriptor of the last daily segment
	 * @throws DataStorageException no segments found
	 */
	SymbolDaily getLastDailySegment(Symbol criteria, String suffix) throws DataStorageException;
	
}

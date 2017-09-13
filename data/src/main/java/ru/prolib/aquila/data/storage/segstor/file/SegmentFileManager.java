package ru.prolib.aquila.data.storage.segstor.file;

import java.io.File;
import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
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
	 * @return list of annual descriptors for existing directories
	 */
	List<SymbolAnnual> scanForYearDirectories(Symbol criteria);
	
	/**
	 * Scan for month directories of symbol and year.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @return list of monthly descriptors for existing directories
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
	 * @return list of annual descriptors for existing segments
	 */
	List<SymbolAnnual> scanForAnnualSegments(Symbol criteria, String suffix);
	
	/**
	 * Scan for monthly segments of the symbol specified.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @param suffix - segment file suffix
	 * @return list of monthly descriptors for existing segments
	 */
	List<SymbolMonthly> scanForMonthlySegments(SymbolAnnual criteria, String suffix);
	
	/**
	 * Scan for daily segments of the symbol specified.
	 * <p>
	 * @param criteria - descriptor of symbol and time
	 * @param suffix - segment file suffix
	 * @return list of daily descriptors for existing segments
	 */
	List<SymbolDaily> scanForDailySegments(SymbolMonthly criteria, String suffix);

}

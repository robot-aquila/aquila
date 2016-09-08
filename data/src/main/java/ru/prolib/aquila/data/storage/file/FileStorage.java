package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DatedSymbol;

public interface FileStorage {
	
	/**
	 * List existing dated segments between the dates.
	 * <p>
	 * @param symbol - the symbol to scan
	 * @param from - date from
	 * @param to - date to
	 * @return list of available data segments of the dated symbol
	 * @throws DataStorageException - an error occurred
	 */
	public List<LocalDate> listExistingSegments(Symbol symbol,
			LocalDate from, LocalDate to) throws DataStorageException;
	
	/**
	 * Get dated segment file for reading.
	 * <p>
	 * @param descr - dated symbol descriptor
	 * @return the data segment file of dated symbol (may not exists)
	 */
	public File getSegmentFile(DatedSymbol descr);

	/**
	 * Get file of symbol data for reading.
	 * <p>
	 * @param symbol - the symbol
	 * @return the symbol data file (may not exists)
	 */
	public File getDataFile(Symbol symbol);

	/**
	 * Get file of symbol data for writing.
	 * <p>
	 * @param symbol - the symbol
	 * @return the symbol data file (existence of intermediate directories guaranteed)
	 * @throws DataStorageException - an error occurred
	 */
	public File getDataFileForWriting(Symbol symbol) throws DataStorageException;
	
	/**
	 * Get dated segment temporary file.
	 * <p>
	 * The temporary files are useful when the data should not be used until
	 * completion of data gathering process. When the temporary file will be
	 * ready to use (contains a consistent data) the temporary file should be
	 * committed to the storage. See
	 * {@link #commitTemporarySegmentFile(DatedSymbol)} for details.
	 * <p>
	 * @param descr - dated symbol descriptor
	 * @return the data segment file of dated symbol 
	 * @throws DataStorageException - an error occurred
	 */
	public File getTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException;
	
	/**
	 * Commit temporary segment file file of dated symbol.
	 * <p>
	 * @param descr - dated symbol descriptor
	 * @throws DataStorageException - an error occurred
	 */
	public void commitTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException;
	
	/**
	 * Get list of known symbols.
	 * <p>
	 * @return list of known symbols
	 * @throws DataStorageException - an error occurred
	 */
	public Set<Symbol> scanForSymbols() throws DataStorageException;

}

package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DatedSymbol;

public interface FileStorageNamespace {
	
	/**
	 * Get directory of data files which associated with symbol and date.
	 * <p>
	 * This method just combines all data to the path without checks of its
	 * existence. The output directory may not exists.
	 * <p>
	 * @param descr - the data segment descriptor
	 * @return path to a directory associated with the descriptor
	 */
	public File getDirectory(DatedSymbol descr);
	
	/**
	 * Get directory for writing of data files which associated with symbol and date.
	 * <p>
	 * This method creates all intermediate directories if necessary.
	 * <p>
	 * @param descr - the data segment descriptor
	 * @return path to a directory associated with the descriptor
	 * @throws IOException - an error occurred
	 */
	public File getDirectoryForWriting(DatedSymbol descr) throws IOException;
	
	/**
	 * Get directory of data files which associated with symbol.
	 * <p>
	 * This method just combines all data to the path without checks of its
	 * existence. The output directory may not exists.
	 * <p>
	 * @param symbol - the symbol
	 * @return path to a directory associated with the symbol
	 */
	public File getDirectory(Symbol symbol);
	
	/**
	 * Get directory for writing of data files which associated with symbol.
	 * <p>
	 * This method creates all intermediate directories if necessary.
	 * <p>
	 * @param symbol - the symbol
	 * @return path to a directory associated with the symbol
	 * @throws IOException - an error occurred
	 */
	public File getDirectoryForWriting(Symbol symbol) throws IOException;
	
	/**
	 * Get list of known symbols.
	 * <p>
	 * @return list of symbols which have at least one data directory
	 * @throws IOException - an error occurred
	 */
	public Set<Symbol> scanForSymbols() throws IOException;

}

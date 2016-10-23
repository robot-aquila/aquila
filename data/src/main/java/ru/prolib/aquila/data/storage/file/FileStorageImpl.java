package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.data.DatedSymbol;
import ru.prolib.aquila.data.storage.DataStorageException;

/**
 * Common file storage implementation.
 */
public class FileStorageImpl implements FileStorage {
	private static final String FS = File.separator;
	private static final FilenameFilter LEVEL1_FILTER = new FilenameFilter() {
		@Override public boolean accept(File dir, String name) {
			return name.length() == 2
				&& name.matches("^[A-Z\\d]{2}$")
				&& new File(dir, name).isDirectory();
		}
	};

	private final IdUtils idUtils = new IdUtils();
	private final String storageID;
	private final FileConfig config;
	private final File root;
	
	public FileStorageImpl(File root, String storageID, FileConfig config) {
		this.root = root;
		this.storageID = storageID;
		this.config = config;
	}
	
	public File getRoot() {
		return root;
	}
	
	public String getStorageID() {
		return storageID;
	}
	
	public FileConfig getConfig() {
		return config;
	}

	@Override
	public List<LocalDate> listExistingSegments(Symbol symbol,
			LocalDate from, LocalDate to) throws DataStorageException
	{
		if ( from.isAfter(to) ) {
			throw new IllegalArgumentException("Start date must be less or equal to end date");
		}
		List<LocalDate> list = new ArrayList<>();
		while ( from.isBefore(to) || from.isEqual(to) ) {
			if ( getSegmentFile(new DatedSymbol(symbol, from)).exists() ) {
				list.add(from);
			}
			from = from.plusDays(1);
		}
		Collections.sort(list);
		return list;
	}
	
	@Override
	public List<LocalDate> listExistingSegments(Symbol symbol,
			LocalDate from, int maxCount) throws DataStorageException
	{
		return null;
	}

	@Override
	public File getTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException
	{
		try {
			File dir = getDirectoryForWriting(descr);
			File file = new File(dir, getTemporaryFilename(descr));
			file.delete();
			return file;
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot create directory structure: ", e);
		}
	}
	
	@Override
	public File getSegmentFile(DatedSymbol descr) {
		return new File(getDirectory(descr), getRegularFilename(descr));
	}

	@Override
	public void commitTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException
	{
		File dir = getDirectory(descr);
		File source = new File(dir, getTemporaryFilename(descr));
		File target = new File(dir, getRegularFilename(descr));
		target.delete();
		try {
			FileUtils.moveFile(source, target);
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot move file: " + source + " -> " + target, e);
		}
	}
	
	@Override
	public File getDataFile(Symbol symbol) {
		return new File(getDirectory(symbol), getRegularFilename(symbol));
	}

	@Override
	public File getDataFileForWriting(Symbol symbol) throws DataStorageException {
		try {
			return new File(getDirectoryForWriting(symbol), getRegularFilename(symbol));
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot create directory structure: ", e);
		}
	}

	@Override
	public Set<Symbol> scanForSymbols() throws DataStorageException {
		final Set<Symbol> symbols = new HashSet<>();
		final FilenameFilter leve2Filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				try {
					symbols.add(idUtils.toSymbol(name));
				} catch ( IllegalArgumentException e ) { }
				return false;
			}
		};
		for ( String x : root.list(LEVEL1_FILTER) ) {
			new File(root, x).list(leve2Filter);
		}
		return symbols;
	}
	
	private String getTemporaryFilename(DatedSymbol descr) {
		return idUtils.getSafeFilename(descr.getSymbol(), descr.getDate(),
				config.getTemporarySuffix());
	}
	
	private String getRegularFilename(DatedSymbol descr) {
		return idUtils.getSafeFilename(descr.getSymbol(), descr.getDate(),
				config.getRegularSuffix());
	}
	
	private String getRegularFilename(Symbol symbol) {
		return idUtils.getSafeFilename(symbol, config.getRegularSuffix());
	}
	
	private String level2Directory(Symbol symbol) {
		return idUtils.getSafeSymbolId(symbol);
	}
	
	private String level1Directory(Symbol symbol) {
		return StringUtils.upperCase(DigestUtils.md5Hex(symbol.toString()).substring(0, 2));
	}
	
	/**
	 * Get directory of data files which associated with symbol.
	 * <p>
	 * This method just combines all data to the path without checks of its
	 * existence. The output directory may not exists.
	 * <p>
	 * @param symbol - the symbol
	 * @return path to a directory associated with the symbol
	 */
	private File getDirectory(Symbol symbol) {
		return new File(root, level1Directory(symbol) + FS + level2Directory(symbol));
	}
	
	/**
	 * Get directory of data files which associated with symbol and date.
	 * <p>
	 * This method just combines all data to the path without checks of its
	 * existence. The output directory may not exists.
	 * <p>
	 * @param descr - the data segment descriptor
	 * @return path to a directory associated with the descriptor
	 */
	private File getDirectory(DatedSymbol descr) {
		Symbol symbol = descr.getSymbol();
		LocalDate date = descr.getDate();
		return new File(root, level1Directory(symbol)
				+ FS + level2Directory(symbol)
				+ FS + String.format("%04d", date.getYear())
				+ FS + String.format("%02d", date.getMonthValue()));
	}
	
	/**
	 * Get directory for writing of data files which associated with symbol and date.
	 * <p>
	 * This method creates all intermediate directories if necessary.
	 * <p>
	 * @param descr - the data segment descriptor
	 * @return path to a directory associated with the descriptor
	 * @throws IOException - an error occurred
	 */
	private File getDirectoryForWriting(DatedSymbol descr) throws IOException {
		File path = getDirectory(descr);
		FileUtils.forceMkdir(path);
		return path;
	}
	
	/**
	 * Get directory for writing of data files which associated with symbol.
	 * <p>
	 * This method creates all intermediate directories if necessary.
	 * <p>
	 * @param symbol - the symbol
	 * @return path to a directory associated with the symbol
	 * @throws IOException - an error occurred
	 */
	private File getDirectoryForWriting(Symbol symbol) throws IOException {
		File path = getDirectory(symbol);
		FileUtils.forceMkdir(path);
		return path;
	}

}

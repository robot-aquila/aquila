package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
@Deprecated
public class SymbolFileStorageImpl implements SymbolFileStorage {
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
	
	public SymbolFileStorageImpl(File root, String storageID, FileConfig config) {
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
			final LocalDate from, int maxCount) throws DataStorageException
	{
		final List<LocalDate> result = new ArrayList<>();
		File symbolRoot = new File(root, level1Directory(symbol)
				+ FS + level2Directory(symbol));
		// 1) Scan for year folders
		final List<Integer> yearToScan = new ArrayList<>();
		symbolRoot.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if ( ! isValidYearDir(name) ) {
					return false;
				}
				int year = parseYearDir(name);
				if ( year < from.getYear() ) {
					return false;
				}
				yearToScan.add(year);
				return false;
			}
		});
		// 2) For each year scan for month folders
		for ( final Integer year : yearToScan ) {
			final List<Integer> monthToScan = new ArrayList<>();
			new File(symbolRoot, formatYearDir(year)).list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if ( ! isValidMonthDir(name) ) {
						return false;
					}
					int month = parseMonthDir(name);
					if ( year == from.getYear() ) {
						if ( month < from.getMonthValue() ) {
							return false;
						}
					}
					monthToScan.add(month);
					return false;
				}
			});
			// 2.2) Make subscan folder to search for data files
			for ( Integer month : monthToScan ) {
				File monthRoot = new File(symbolRoot, formatYearDir(year) + FS + formatMonthDir(month));
				if ( monthRoot.isDirectory() ) {
					monthRoot.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							LocalDate x = null;
							try {
								x = idUtils.parseSafeFilename3(name, symbol, config.getRegularSuffix());
								if ( ! x.isBefore(from) ) {
									result.add(x);
								}
							} catch ( DateTimeParseException e ) { }
							return false;
						}
					});
				}
			}
		}
		Collections.sort(result);
		int count = result.size();
		return count <= maxCount ? result : new ArrayList<>(result.subList(0, maxCount));
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
	
	private boolean isValidYearDir(String name) {
		return StringUtils.isNumeric(name);
	}
	
	private String formatYearDir(int year) {
		return String.format("%04d", year);
	}
	
	/**
	 * Parses the string representation of a year directory to a year value.
	 * <p>
	 * @param name - the name of directory. Expected that the name is already verified.
	 * @return year value
	 */
	private int parseYearDir(String name) {
		return Integer.valueOf(name.replaceFirst("^0+(?!$)", ""));
	}
	
	private boolean isValidMonthDir(String name) {
		switch ( name ) {
		case "01":
		case "02":
		case "03":
		case "04":
		case "05":
		case "06":
		case "07":
		case "08":
		case "09":
		case "10":
		case "11":
		case "12":
			return true;
		default:
			return false;
		}
	}
	
	private String formatMonthDir(int month) {
		return String.format("%02d", month);
	}
	
	/**
	 * Parses the string representation of a month directory to a month value.
	 * <p>
	 * @param name - the name of directory. Expected that the name is already verified.
	 * @return month value
	 */
	private int parseMonthDir(String name) {
		return Integer.valueOf(name.replaceFirst("^0+(?!$)", ""));
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
				+ FS + formatYearDir(date.getYear())
				+ FS + formatMonthDir(date.getMonthValue()));
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

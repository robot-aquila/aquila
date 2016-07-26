package ru.prolib.aquila.utils.finexp.futures;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.IdUtils;

public class DataStorageImpl implements DataStorage {
	private static final String MAIN_SUFFIX = ".csv.gz";
	private static final String TEMP_SUFFIX = ".part" + MAIN_SUFFIX;
	private final File root;
	private final IdUtils idUtils = new IdUtils();
	
	public DataStorageImpl(File root) {
		this.root = root;
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
			if ( getSegmentFile(symbol, from).exists() ) {
				list.add(from);
			}
			from = from.plusDays(1);
		}
		Collections.sort(list);
		return list;
	}

	@Override
	public File getSegmentTemporaryFile(Symbol symbol, LocalDate date)
			throws DataStorageException
	{
		File dir = getSegmentDirectory(symbol, date);
		try {
			FileUtils.forceMkdir(dir);
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot create directory structure: " + dir, e);
		}
		return new File(dir, getTempFilename(symbol, date));
	}
	
	@Override
	public File getSegmentFile(Symbol symbol, LocalDate date) {
		return new File(getSegmentDirectory(symbol, date), getMainFilename(symbol, date));
	}

	@Override
	public void commitSegmentTemporaryFile(Symbol symbol, LocalDate date)
			throws DataStorageException
	{
		File dir = getSegmentDirectory(symbol, date);
		File source = new File(dir, getTempFilename(symbol, date));
		File target = new File(dir, getMainFilename(symbol, date));
		try {
			FileUtils.moveFile(source, target);
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot move file: " + source + " -> " + target, e);
		}
	}
	
	protected File getSegmentDirectory(Symbol symbol, LocalDate date) {
		String FS = File.separator;
		// Directory structure:
		// 1) first two characters of hexMD5 hash on symbols string representation
		// 2) safe encoded symbols string representation
		// 3) four digits of year
		// 4) two digits of month 
		return new File(root, StringUtils.upperCase(DigestUtils.md5Hex(symbol.toString()).substring(0, 2))
				+ FS + idUtils.getSafeSymbolId(symbol)
				+ FS + String.format("%04d", date.getYear())
				+ FS + String.format("%02d", date.getMonthValue()));
	}
	
	
	private String getTempFilename(Symbol symbol, LocalDate date) {
		return idUtils.getSafeFilename(symbol, date, TEMP_SUFFIX);
	}
	
	private String getMainFilename(Symbol symbol, LocalDate date) {
		return idUtils.getSafeFilename(symbol, date, MAIN_SUFFIX);
	}

}

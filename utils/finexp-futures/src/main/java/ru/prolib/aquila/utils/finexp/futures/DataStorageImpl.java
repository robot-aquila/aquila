package ru.prolib.aquila.utils.finexp.futures;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.data.storage.DatedSymbol;
import ru.prolib.aquila.data.storage.file.FileStorageNamespace;
import ru.prolib.aquila.data.storage.file.FileStorageNamespaceV1;

public class DataStorageImpl implements DataStorage {
	private static final String MAIN_SUFFIX = ".csv.gz";
	private static final String TEMP_SUFFIX = ".part" + MAIN_SUFFIX;
	private final FileStorageNamespace namespace;
	private final IdUtils idUtils = new IdUtils();
	
	public DataStorageImpl(File root) {
		this.namespace = new FileStorageNamespaceV1(root);
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
	public File getSegmentTemporaryFile(DatedSymbol descr)
			throws DataStorageException
	{
		try {
			File dir = namespace.getDirectoryForWriting(descr);
			File file = new File(dir, getTempFilename(descr));
			file.delete();
			return file;
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot create directory structure: ", e);
		}
	}
	
	@Override
	public File getSegmentFile(DatedSymbol descr) {
		return new File(namespace.getDirectory(descr), getMainFilename(descr));
	}

	@Override
	public void commitSegmentTemporaryFile(DatedSymbol descr)
			throws DataStorageException
	{
		File dir = namespace.getDirectory(descr);
		File source = new File(dir, getTempFilename(descr));
		File target = new File(dir, getMainFilename(descr));
		target.delete();
		try {
			FileUtils.moveFile(source, target);
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot move file: " + source + " -> " + target, e);
		}
	}
	
	private String getBaseFilename(DatedSymbol descr) {
		return idUtils.getSafeFilename(descr.getSymbol(), descr.getDate(), "");
	}
	
	private String getTempFilename(DatedSymbol descr) {
		return getBaseFilename(descr) + TEMP_SUFFIX;
	}
	
	private String getMainFilename(DatedSymbol descr) {
		return getBaseFilename(descr) + MAIN_SUFFIX;
	}

}

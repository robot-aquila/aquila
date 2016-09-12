package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.data.DatedSymbol;
import ru.prolib.aquila.data.storage.DataStorageException;

/**
 * Common file storage implementation.
 */
public class FileStorageImpl implements FileStorage {
	private final IdUtils idUtils = new IdUtils();
	private final String storageID;
	private final FileStorageNamespace namespace;
	private final FSService service;
	
	public FileStorageImpl(FileStorageNamespace namespace, String storageID, FSService service) {
		this.namespace = namespace;
		this.storageID = storageID;
		this.service = service;
	}
	
	public FileStorageNamespace getNamespace() {
		return namespace;
	}
	
	public String getStorageID() {
		return storageID;
	}
	
	public FSService getService() {
		return service;
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
	public File getTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException
	{
		try {
			File dir = namespace.getDirectoryForWriting(descr);
			File file = new File(dir, getTemporaryFilename(descr));
			file.delete();
			return file;
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot create directory structure: ", e);
		}
	}
	
	@Override
	public File getSegmentFile(DatedSymbol descr) {
		return new File(namespace.getDirectory(descr), getRegularFilename(descr));
	}

	@Override
	public void commitTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException
	{
		File dir = namespace.getDirectory(descr);
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
		return new File(namespace.getDirectory(symbol), getRegularFilename(symbol));
	}

	@Override
	public File getDataFileForWriting(Symbol symbol) throws DataStorageException {
		try {
			return new File(namespace.getDirectoryForWriting(symbol), getRegularFilename(symbol));
		} catch ( IOException e ) {
			throw new DataStorageException("Cannot create directory structure: ", e);
		}
	}
	
	private String getTemporaryFilename(DatedSymbol descr) {
		return idUtils.getSafeFilename(descr.getSymbol(), descr.getDate(),
				service.getTemporarySuffix());
	}
	
	private String getRegularFilename(DatedSymbol descr) {
		return idUtils.getSafeFilename(descr.getSymbol(), descr.getDate(),
				service.getRegularSuffix());
	}
	
	private String getRegularFilename(Symbol symbol) {
		return idUtils.getSafeFilename(symbol, service.getRegularSuffix());
	}

	@Override
	public Set<Symbol> scanForSymbols() throws DataStorageException {
		try {
			return namespace.scanForSymbols();
		} catch ( IOException e ) {
			throw new DataStorageException(e);
		}
	}

}

package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.IdUtils;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DatedSymbol;

/**
 * Common file storage implementation.
 */
public class FileStorageImpl implements FileStorage {
	private final IdUtils idUtils = new IdUtils();
	private final FileStorageNamespace namespace;
	private final FilesetInfo filesetInfo;
	
	public FileStorageImpl(FileStorageNamespace namespace, FilesetInfo filesetInfo) {
		this.namespace = namespace;
		this.filesetInfo = filesetInfo;
	}
	
	/**
	 * Create a file storage.
	 * <p>
	 * This constructor is used to create a file storage with the
	 * {@link FileStorageNamespaceV1} implementation of the namespace.
	 * <p>
	 * @param root - the root directory
	 * @param filesetInfo - the fileset info
	 */
	public FileStorageImpl(File root, FilesetInfo filesetInfo) {
		this(new FileStorageNamespaceV1(root), filesetInfo);
	}
	
	public FileStorageNamespace getNamespace() {
		return namespace;
	}
	
	public FilesetInfo getFilesetInfo() {
		return filesetInfo;
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
	
	private String getTemporaryFilename(DatedSymbol descr) {
		return idUtils.getSafeFilename(descr.getSymbol(), descr.getDate(),
				filesetInfo.getTemporarySuffix());
	}
	
	private String getRegularFilename(DatedSymbol descr) {
		return idUtils.getSafeFilename(descr.getSymbol(), descr.getDate(),
				filesetInfo.getRegularSuffix());
	}

}

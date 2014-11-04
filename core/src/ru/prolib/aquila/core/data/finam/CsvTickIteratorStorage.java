package ru.prolib.aquila.core.data.finam;

import java.io.File;
import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.internal.IdUtils;

/**
 * Хранилище сделок на основе csv-файлов формата FINAM.
 */
public class CsvTickIteratorStorage implements TickIteratorStorage {
	private static final String SEPARATOR = "-";
	private final File root;
	private final IdUtils idUtils;
	
	public CsvTickIteratorStorage(File root) {
		this(root, new IdUtils());
	}
	
	public CsvTickIteratorStorage(File root, IdUtils idUtils) {
		super();
		this.root = root;
		this.idUtils = idUtils;
	}

	@Override
	public Aqiterator<Tick> getIterator(String dataId, DateTime start)
			throws DataException
	{
		Aqiterator<FileEntry> it = new SubScanIteratorBuilder<FileEntry>(
			new DirectoryScannerY(), new SubScanIteratorBuilder<FileEntry>(
				new DirectoryScannerM(),
				new DirectoryScannerD(dataId + SEPARATOR)))
			.makeScan(new FileEntry(new File(root,dataId),start.toLocalDate()));
		return new TickReaderFilterByTime(new TickReaderFromFiles(
			new FileEntry2FileIterator(it), new CsvTickReaderFactory()), start);
	}

	@Override
	public Aqiterator<Tick>
		getIterator(SecurityDescriptor descr, DateTime start)
			throws DataException
	{
		return getIterator(idUtils.getSafeFilename(descr), start);
	}

}

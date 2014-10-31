package ru.prolib.aquila.core.data.finam;

import java.io.File;
import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.finam.storage.*;

/**
 * Хранилище сделок на основе csv-файлов формата FINAM.
 */
public class CsvTickDataStorage implements TickDataStorage {
	private final File root;
	private final DataStorageHelper helper;
	
	public CsvTickDataStorage(File root, DataStorageHelper helper) {
		super();
		this.root = root;
		this.helper = helper;
	}
	
	public CsvTickDataStorage(File root) {
		this(root, new DataStorageHelper());
	}

	@Override
	public Aqiterator<Tick> getTicks(SecurityDescriptor descr, DateTime start)
			throws DataException
	{
		String prefix = helper.getSafeFilename(descr);
		return new TickReaderFilterByTime(new TickReaderFromFiles(
			new FileEntry2FileIterator(helper.createTickFilesScanner(prefix + "-")
				.makeScan(new FileEntry(new File(root, prefix),
										start.toLocalDate()))),
			new CsvTickReaderFactory()), start);
	}

}

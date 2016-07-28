package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DatedSymbol;

public interface FileStorage {
	
	public List<LocalDate> listExistingSegments(Symbol symbol,
			LocalDate from, LocalDate to) throws DataStorageException;
	
	public File getSegmentFile(DatedSymbol descr);
	
	public File getTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException;
	
	public void commitTemporarySegmentFile(DatedSymbol descr)
			throws DataStorageException;

}

package ru.prolib.aquila.utils.finexp.futures;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface DataStorage {
	
	public List<LocalDate> listExistingSegments(Symbol symbol,
			LocalDate from, LocalDate to) throws DataStorageException;
	
	public File getSegmentTemporaryFile(Symbol symbol, LocalDate date)
			throws DataStorageException;
	
	public void commitSegmentTemporaryFile(Symbol symbol, LocalDate date)
			throws DataStorageException;
	
	public File getSegmentFile(Symbol symbol, LocalDate date);

}

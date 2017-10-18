package ru.prolib.aquila.web.utils.finam.segstor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SymbolAnnual;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentNotExistsException;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;
import ru.prolib.aquila.data.storage.segstor.file.V1SegmentFileManagerImpl;
import ru.prolib.aquila.web.utils.finam.datasim.FinamCsvL1UpdateReader;

/**
 * The storage to access daily trades in FINAM export format.
 */
public class FinamL1UpdateSegmentStorage implements SymbolDailySegmentStorage<L1Update> {
	private final SegmentFileManager segmentManager;
	private final String suffix = ".csv.gz";
	
	public FinamL1UpdateSegmentStorage(SegmentFileManager segmentManager) {
		this.segmentManager = segmentManager;
	}
	
	public FinamL1UpdateSegmentStorage(File root) {
		this(new V1SegmentFileManagerImpl(root));
	}

	@Override
	public Set<Symbol> listSymbols() {
		return segmentManager.scanForSymbolDirectories();
	}

	@Override
	public boolean isExists(SymbolDaily segment) {
		return segmentManager.getFileInfo(segment, suffix).getFullPath().exists();
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol) throws DataStorageException {
		List<SymbolDaily> result = new ArrayList<>();
		for ( SymbolAnnual year : segmentManager.scanForYearDirectories(symbol) ) {
			for ( SymbolMonthly month : segmentManager.scanForMonthDirectories(year) ) {
				result.addAll(segmentManager.scanForDailySegments(month, suffix));
			}
		}
		return result;
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, DatePoint to)
			throws DataStorageException
	{
		List<SymbolDaily> result = new ArrayList<>();
		for ( SymbolDaily segment : listDailySegments(symbol) ) {
			DatePoint x = segment.getPoint();
			if ( x.compareTo(from) >= 0 && x.compareTo(to) < 0 ) {
				result.add(segment);
			}
		}
		return result;
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from)
			throws DataStorageException
	{
		List<SymbolDaily> result = new ArrayList<>();
		for ( SymbolDaily segment : listDailySegments(symbol) ) {
			DatePoint x = segment.getPoint();
			if ( x.compareTo(from) >= 0 ) {
				result.add(segment);
			}
		}
		return result;
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, DatePoint from, int maxCount)
			throws DataStorageException
	{
		List<SymbolDaily> result = new ArrayList<>();
		for ( SymbolDaily segment : listDailySegments(symbol) ) {
			DatePoint x = segment.getPoint();
			if ( x.compareTo(from) >= 0 ) {
				result.add(segment);
				if ( result.size() >= maxCount ) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, int maxCount, DatePoint to)
			throws DataStorageException
	{
		LinkedList<SymbolDaily> result = new LinkedList<>();
		List<SymbolDaily> all = listDailySegments(symbol);
		for ( int i = all.size() - 1; i >= 0; i -- ) {
			SymbolDaily segment = all.get(i);
			DatePoint x = segment.getPoint();
			if ( x.compareTo(to) < 0 ) {
				result.addFirst(segment);
				if ( result.size() >= maxCount ) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public List<SymbolDaily> listDailySegments(Symbol symbol, MonthPoint month)
			throws DataStorageException
	{
		List<SymbolDaily> result = new ArrayList<>();
		for ( SymbolDaily segment : listDailySegments(symbol) ) {
			LocalDate d = segment.getPoint().getDate();
			MonthPoint x = new MonthPoint(d.getYear(), d.getMonth());
			if ( x.equals(month) ) {
				result.add(segment);
			}
		}
		return result;
	}

	@Override
	public SegmentMetaData getMetaData(SymbolDaily segment)
			throws DataStorageException
	{
		SegmentFileInfo fi = segmentManager.getFileInfo(segment, suffix);
		if ( ! fi.getFullPath().exists() ) {
			throw new SymbolDailySegmentNotExistsException(segment);
		}
		return new FinamL1UpdateSegmentMetaData(fi.getFullPath(), segment.getSymbol());
	}

	@Override
	public CloseableIterator<L1Update> createReader(SymbolDaily segment)
			throws DataStorageException
	{
		try {
			return new FinamCsvL1UpdateReader(segment.getSymbol(),
					segmentManager.getFileInfo(segment, suffix).getFullPath());
		} catch ( IOException e ) {
			throw new SymbolDailySegmentNotExistsException(segment, e);
		}
	}

}

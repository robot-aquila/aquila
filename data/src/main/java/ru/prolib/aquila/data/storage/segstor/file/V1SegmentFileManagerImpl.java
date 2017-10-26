package ru.prolib.aquila.data.storage.segstor.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.StrCoder;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolAnnual;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.file.v1.L1NestedDirFilter;
import ru.prolib.aquila.data.storage.segstor.file.v1.L2SymbolDirFilter;
import ru.prolib.aquila.data.storage.segstor.file.v1.L3YearDirFilter;
import ru.prolib.aquila.data.storage.segstor.file.v1.L3YearFileFilter;
import ru.prolib.aquila.data.storage.segstor.file.v1.L4MonthDirFilter;
import ru.prolib.aquila.data.storage.segstor.file.v1.L4MonthFileFilter;
import ru.prolib.aquila.data.storage.segstor.file.v1.L5DayFileFilter;

public class V1SegmentFileManagerImpl implements SegmentFileManager {
	private static final String FS = File.separator;
	private static final StrCoder coder = StrCoder.getInstance();
	private static final FilenameFilter L1FILTER = new L1NestedDirFilter();
	private final File root;
	
	public V1SegmentFileManagerImpl(File root) {
		this.root = root;
	}
	
	public File getRoot() {
		return root;
	}

	@Override
	public Set<Symbol> scanForSymbolDirectories() {
		L2SymbolDirFilter scanner = new L2SymbolDirFilter();
		for ( String dummy : root.list(L1FILTER) ) {
			new File(root, dummy).list(scanner);
		}
		return scanner.getFoundSymbols();
	}

	@Override
	public File getDirectory(SymbolMonthly criteria) {
		return new File(root, getDirectoryL1(criteria.getSymbol()) + FS
				+ getDirectoryL2(criteria.getSymbol()) + FS
				+ getDirectoryL3(criteria.getPoint().getYear()) + FS
				+ getDirectoryL4(criteria.getPoint().getMonth()));
	}

	@Override
	public File getDirectory(SymbolAnnual criteria) {
		return new File(root, getDirectoryL1(criteria.getSymbol()) + FS
				+ getDirectoryL2(criteria.getSymbol()) + FS
				+ getDirectoryL3(criteria.getPoint().getYear()));
	}

	@Override
	public File getDirectory(Symbol criteria) {
		return new File(root, getDirectoryL1(criteria) + FS + getDirectoryL2(criteria));
	}

	@Override
	public List<SymbolAnnual> scanForYearDirectories(Symbol criteria) {
		L3YearDirFilter scanner = new L3YearDirFilter();
		getDirectory(criteria).list(scanner);
		List<SymbolAnnual> result = new ArrayList<>();
		for ( Integer year : scanner.getFoundYears() ) {
			result.add(new SymbolAnnual(criteria, year));
		}
		return result; // The result already sorted by scanner
	}

	@Override
	public List<SymbolMonthly> scanForMonthDirectories(SymbolAnnual criteria) {
		L4MonthDirFilter scanner = new L4MonthDirFilter();
		getDirectory(criteria).list(scanner);
		List<SymbolMonthly> result = new ArrayList<>();
		for ( Integer month : scanner.getFoundMonths() ) {
			result.add(new SymbolMonthly(criteria.getSymbol(), criteria.getPoint().getYear(), month));
		}
		return result; // The result already sorted by scanner
	}

	@Override
	public SegmentFileInfo getFileInfo(Symbol criteria, String suffix) {
		return new SegmentFileInfoImpl().setFullPath(getDirectory(criteria),
				getBaseName(criteria), encodeSuffix(suffix));
	}

	@Override
	public SegmentFileInfoImpl getFileInfo(SymbolDaily criteria, String suffix) {
		return new SegmentFileInfoImpl().setFullPath(getDirectory(criteria.toMonthly()),
				getBaseName(criteria), encodeSuffix(suffix));
	}

	@Override
	public SegmentFileInfo getFileInfo(SymbolMonthly criteria, String suffix) {
		return new SegmentFileInfoImpl().setFullPath(getDirectory(criteria.toAnnual()),
				getBaseName(criteria), encodeSuffix(suffix));
	}

	@Override
	public SegmentFileInfo getFileInfo(SymbolAnnual criteria, String suffix) {
		return new SegmentFileInfoImpl().setFullPath(getDirectory(criteria.getSymbol()),
				getBaseName(criteria), encodeSuffix(suffix));
	}
	
	@Override
	public boolean hasSymbolSegment(Symbol symbol, String suffix) {
		return getFileInfo(symbol, suffix).getFullPath().exists();
	}

	@Override
	public List<Symbol> scanForSymbolSegments(String suffix) {
		List<Symbol> result = new ArrayList<>();
		for ( Symbol symbol : scanForSymbolDirectories() ) {
			if ( hasSymbolSegment(symbol, suffix) ) {
				result.add(symbol);
			}
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public List<SymbolAnnual> scanForAnnualSegments(Symbol criteria, String suffix) {
		L3YearFileFilter scanner = new L3YearFileFilter(criteria, encodeSuffix(suffix));
		getDirectory(criteria).list(scanner);
		return scanner.getFoundSegments(); // The result already sorted by scanner
	}

	@Override
	public List<SymbolMonthly> scanForMonthlySegments(SymbolAnnual criteria, String suffix) {
		L4MonthFileFilter scanner = new L4MonthFileFilter(criteria, encodeSuffix(suffix));
		getDirectory(criteria).list(scanner);
		return scanner.getFoundSegments(); // The result already sorted by scanner
	}

	@Override
	public List<SymbolDaily> scanForDailySegments(SymbolMonthly criteria, String suffix) {
		L5DayFileFilter scanner = new L5DayFileFilter(criteria, encodeSuffix(suffix));
		getDirectory(criteria).list(scanner);
		return scanner.getFoundSegments(); // The result already sorted by scanner
	}
	
	@Override
	public boolean hasAnnualSegments(Symbol criteria, String suffix) {
		return scanForAnnualSegments(criteria, suffix).size() > 0;
	}
	
	@Override
	public boolean hasMonthlySegments(Symbol criteria, String suffix) {
		for ( SymbolAnnual dir : scanForYearDirectories(criteria) ) {
			if ( scanForMonthlySegments(dir, suffix).size() > 0 ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasDailySegments(Symbol criteria, String suffix) {
		L3YearDirFilter yearScanner = new L3YearDirFilter();
		getDirectory(criteria).list(yearScanner);
		for ( int year : yearScanner.getFoundYears() ) {
			L4MonthDirFilter monthScanner = new L4MonthDirFilter();
			getDirectory(new SymbolAnnual(criteria, year)).list(monthScanner);
			for ( int month : monthScanner.getFoundMonths() ) {
				if ( scanForDailySegments(new SymbolMonthly(criteria, year, month), suffix).size() > 0 ) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public SymbolAnnual getFirstAnnualSegment(Symbol criteria, String suffix)
			throws DataStorageException
	{
		List<SymbolAnnual> list = scanForAnnualSegments(criteria, suffix);
		if ( list.size() == 0 ) {
			throw new DataStorageException("No segments found");
		}
		return list.get(0);
	}
	
	@Override
	public SymbolMonthly getFirstMonthlySegment(Symbol criteria, String suffix)
			throws DataStorageException
	{
		List<SymbolAnnual> years = scanForYearDirectories(criteria);
		if ( years.size() == 0 ) {
			throw new DataStorageException("No segments found");
		}
		for ( SymbolAnnual year : years ) {
			List<SymbolMonthly> list = scanForMonthlySegments(year, suffix);
			if ( list.size() > 0 ) {
				return list.get(0);
			}
		}
		throw new DataStorageException("No segments found");
	}
	
	@Override
	public SymbolDaily getFirstDailySegment(Symbol criteria, String suffix)
			throws DataStorageException
	{
		List<SymbolAnnual> years = scanForYearDirectories(criteria);
		if ( years.size() == 0 ) {
			throw new DataStorageException("No segments found");
		}
		for ( SymbolAnnual year : years ) {
			List<SymbolMonthly> months = scanForMonthDirectories(year);
			for ( SymbolMonthly month : months ) {
				List<SymbolDaily> list = scanForDailySegments(month, suffix);
				if ( list.size() > 0 ) {
					return list.get(0);
				}
			}			
		}
		throw new DataStorageException("No segments found");
	}
	
	@Override
	public SymbolAnnual getLastAnnualSegment(Symbol criteria, String suffix)
			throws DataStorageException
	{
		List<SymbolAnnual> list = scanForAnnualSegments(criteria, suffix);
		if ( list.size() == 0 ) {
			throw new DataStorageException("No segments found");
		}
		return list.get(list.size() - 1);
	}
	
	@Override
	public SymbolMonthly getLastMonthlySegment(Symbol criteria, String suffix)
			throws DataStorageException
	{
		List<SymbolAnnual> years = scanForYearDirectories(criteria);
		if ( years.size() == 0 ) {
			throw new DataStorageException("No segments found");
		}
		for ( int i = years.size() - 1; i >= 0; i -- ) {
			SymbolAnnual year = years.get(i);
			List<SymbolMonthly> list = scanForMonthlySegments(year, suffix);
			if ( list.size() > 0 ) {
				return list.get(list.size() - 1);
			}
		}
		throw new DataStorageException("No segments found");
	}
	
	@Override
	public SymbolDaily getLastDailySegment(Symbol criteria, String suffix)
			throws DataStorageException
	{
		List<SymbolAnnual> years = scanForYearDirectories(criteria);
		if ( years.size() == 0 ) {
			throw new DataStorageException("No segments found");
		}
		for ( int iy = years.size() - 1; iy >= 0; iy -- ) {
			SymbolAnnual year = years.get(iy);
			List<SymbolMonthly> months = scanForMonthDirectories(year);
			for ( int im = months.size() - 1; im >= 0; im -- ) {
				SymbolMonthly month = months.get(im);
				List<SymbolDaily> list = scanForDailySegments(month, suffix);
				if ( list.size() > 0 ) {
					return list.get(list.size() - 1);
				}
			}
		}
		throw new DataStorageException("No segments found");
	}
	
	private String getDirectoryL1(Symbol symbol) {
		return StringUtils.upperCase(DigestUtils.md5Hex(symbol.toString()).substring(0, 2));
	}
	
	private String getDirectoryL2(Symbol symbol) {
		return coder.encode(symbol.toString());
	}
	
	private String getDirectoryL3(int year) {
		return Integer.toString(year);
	}

	private String getDirectoryL4(Month month) {
		return getDirectoryL4(month.getValue());
	}
	
	private String getDirectoryL4(int month) {
		return String.format("%02d", month);
	}
	
	private String getBaseName(Symbol symbol) {
		return coder.encode(symbol.toString());
	}
	
	private String getBaseName(SymbolDaily criteria) {
		LocalDate date = criteria.getPoint().getDate();
		return coder.encode(criteria.getSymbol().toString())
				+ "-"
				+ date.getYear()
				+ String.format("%02d", date.getMonthValue())
				+ String.format("%02d", date.getDayOfMonth());
	}
	
	private String getBaseName(SymbolMonthly criteria) {
		return coder.encode(criteria.getSymbol().toString())
				+ "-"
				+ criteria.getPoint().getYear()
				+ String.format("%02d", criteria.getPoint().getMonth().getValue());
	}

	private String getBaseName(SymbolAnnual criteria) {
		return coder.encode(criteria.getSymbol().toString())
				+ "-"
				+ criteria.getPoint().getYear();
	}
	
	private String encodeSuffix(String suffix) {
		try {
			suffix = URLEncoder.encode(suffix, "UTF-8")
					.replace("*", "%2A");
			return suffix;
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
}

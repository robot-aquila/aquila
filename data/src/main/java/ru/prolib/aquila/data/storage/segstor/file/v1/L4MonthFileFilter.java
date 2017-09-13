package ru.prolib.aquila.data.storage.segstor.file.v1;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.utils.StrCoder;
import ru.prolib.aquila.data.storage.segstor.SymbolAnnual;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;

public class L4MonthFileFilter implements FilenameFilter {
	private final SymbolAnnual year;
	private final Set<Integer> months;
	private final String prefix;
	private final String suffix;
	
	public L4MonthFileFilter(SymbolAnnual year, String suffix) {
		this.year = year;
		this.months = new HashSet<>();
		this.suffix = suffix;
		this.prefix = StrCoder.getInstance().encode(year.getSymbol().toString())
				+ "-"
				+ year.getPoint().getYear();
		
	}

	@Override
	public boolean accept(File dir, String name) {
		if ( ! new File(dir, name).isFile() ) {
			return false;
		}
		if ( ! name.startsWith(prefix) || ! name.endsWith(suffix) ) {
			return false;
		}
		String chunk = name.substring(prefix.length(), name.length() - suffix.length());
		if ( ! StringUtils.isNumeric(chunk) ) {
			return false;
		}
		switch ( chunk ) {
		case "01": months.add(1); break;
		case "02": months.add(2); break;
		case "03": months.add(3); break;
		case "04": months.add(4); break;
		case "05": months.add(5); break;
		case "06": months.add(6); break;
		case "07": months.add(7); break;
		case "08": months.add(8); break;
		case "09": months.add(9); break;
		case "10": months.add(10); break;
		case "11": months.add(11); break;
		case "12": months.add(12); break;
		default:
			return false;
			
		}
		return true;
	}
	
	public List<SymbolMonthly> getFoundSegments() {
		List<SymbolMonthly> result = new ArrayList<>();
		for ( Integer month : months ) {
			result.add(new SymbolMonthly(year.getSymbol(), year.getPoint().getYear(), month));
		}
		Collections.sort(result);
		return result;
	}

}

package ru.prolib.aquila.data.storage.segstor.file.v1;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.StrCoder;
import ru.prolib.aquila.data.storage.segstor.SymbolAnnual;

/**
 * Level-3 filter to scan for annual segment files.
 */
public class L3YearFileFilter implements FilenameFilter {
	private final Symbol symbol;
	private final Set<Integer> years;
	private final String prefix;
	private final String suffix;
	
	public L3YearFileFilter(Symbol symbol, String nameSuffix) {
		this.symbol = symbol;
		this.years = new HashSet<>();
		this.suffix = nameSuffix;
		this.prefix = StrCoder.getInstance().encode(symbol.toString()) + "-";
	}

	@Override
	public boolean accept(File dir, String name) {
		if ( ! new File(dir, name).isFile() ) {
			return false;
		}
		if ( ! name.startsWith(prefix) || ! name.endsWith(suffix) ) {
			return false;
		}
		String chunk = name.substring(prefix.length(), name.length() - suffix.length())
				.replaceFirst("^0+(?!$)", "");
		if ( ! StringUtils.isNumeric(chunk) ) {
			return false;
		}
		years.add(Integer.parseInt(chunk));
		return true;
	}
	
	public List<SymbolAnnual> getFoundSegments() {
		List<SymbolAnnual> result = new ArrayList<>();
		for ( Integer year : years ) {
			result.add(new SymbolAnnual(symbol, year));
		}
		Collections.sort(result);
		return result;
	}

}

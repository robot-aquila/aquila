package ru.prolib.aquila.data.storage.segstor.file.v1;

import java.io.File;
import java.io.FilenameFilter;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ru.prolib.aquila.core.utils.StrCoder;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;

public class L5DayFileFilter implements FilenameFilter {
	private final SymbolMonthly month;
	private final Set<Integer> days;
	private final String prefix;
	private final String suffix;
	
	public L5DayFileFilter(SymbolMonthly month, String suffix) {
		this.month = month;
		this.days = new HashSet<>();
		this.suffix = suffix;
		this.prefix = StrCoder.getInstance().encode(month.getSymbol().toString())
				+ "-"
				+ month.getPoint().getYear()
				+ String.format("%02d", month.getPoint().getMonth().getValue());
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
		int dayOfMonth = Integer.parseInt(chunk);
		try {
			LocalDate.of(month.getPoint().getYear(),
					month.getPoint().getMonth().getValue(),
					dayOfMonth);
		} catch ( DateTimeException e ) {
			return false;
		}
		days.add(dayOfMonth);
		return true;
	}
	
	public List<SymbolDaily> getFoundSegments() {
		List<SymbolDaily> result = new ArrayList<>();
		for ( Integer dayOfMonth : days ) {
			result.add(new SymbolDaily(month.getSymbol(),
					month.getPoint().getYear(),
					month.getPoint().getMonth().getValue(),
					dayOfMonth));
		}
		Collections.sort(result);
		return result;
	}

}

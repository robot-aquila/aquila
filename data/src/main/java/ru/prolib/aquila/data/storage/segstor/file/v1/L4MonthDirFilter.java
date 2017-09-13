package ru.prolib.aquila.data.storage.segstor.file.v1;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class L4MonthDirFilter implements FilenameFilter {
	private final Set<Integer> months;
	
	public L4MonthDirFilter() {
		months = new HashSet<>();
	}

	@Override
	public boolean accept(File dir, String name) {
		if ( ! new File(dir, name).isDirectory() ) {
			return false;
		}
		switch ( name ) {
		case "01":
		case "02":
		case "03":
		case "04":
		case "05":
		case "06":
		case "07":
		case "08":
		case "09":
		case "10":
		case "11":
		case "12":
			break;
		default:
			return false;
		}
		months.add(Integer.parseInt(name.replaceFirst("^0+(?!$)", "")));
		return true;
	}
	
	public List<Integer> getFoundMonths() {
		List<Integer> list = new ArrayList<>(months);
		Collections.sort(list);
		return list;
	}

}

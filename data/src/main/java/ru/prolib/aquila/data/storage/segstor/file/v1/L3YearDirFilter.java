package ru.prolib.aquila.data.storage.segstor.file.v1;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Level-3 cache directory filter to scan for year subdirectories.
 * <p>
 * This filter is applicable to scan level-2 directories.
 * Level-3 may contains files assiciated with symbol at all and subdirectories
 * to store annual data. This is a filter for all possible year subdirectories.
 * All years found during the scan may be obtained through appropriate method.
 * Isn't allowed to scan several level-2 directories with same filter instance
 * to access list of found years because the base directory will point to
 * different symbols. In this case the data will be logically incorrect.
 */
public class L3YearDirFilter implements FilenameFilter {
	private final Set<Integer> years;
	
	public L3YearDirFilter() {
		years = new HashSet<>();
	}

	@Override
	public boolean accept(File dir, String name) {
		if ( ! new File(dir, name).isDirectory() ) {
			return false;
		}
		if ( ! StringUtils.isNumeric(name) ) {
			return false;
		}
		years.add(Integer.parseInt(name));
		return true;
	}
	
	public List<Integer> getFoundYears() {
		List<Integer> list = new ArrayList<>(years);
		Collections.sort(list);
		return list;
	}

}

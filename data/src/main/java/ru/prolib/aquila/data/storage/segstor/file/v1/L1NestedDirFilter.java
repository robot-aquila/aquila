package ru.prolib.aquila.data.storage.segstor.file.v1;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Level-1 cache directory filter for nested caches.
 * <p>
 * This filter is applicable to the cache root directory.
 * Level-1 represents the first two letters of MD5 hex code on symbol.
 * This filter checks all valid subdirectories which are possible to be a nested cache.
 */
public class L1NestedDirFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return name.length() == 2
			&& name.matches("^[A-Z\\d]{2}$")
			&& new File(dir, name).isDirectory();
	}

}

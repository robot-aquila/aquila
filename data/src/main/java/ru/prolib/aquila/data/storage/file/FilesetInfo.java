package ru.prolib.aquila.data.storage.file;

import java.util.HashSet;
import java.util.Set;

/**
 * Fileset info.
 * <p>
 * This class provides an information about files of single set of files.
 * To prevent file overrides this class has not a public constructor. Use
 * appropriate static method to create a new instance. 
 */
public class FilesetInfo {
	private static final Set<String> registeredSuffixes;
	
	static {
		registeredSuffixes = new HashSet<>();
	}

	/**
	 * Register a new fileset.
	 * <p>
	 * @param regularSuffix - regular files suffix
	 * @param temporarySuffix - temporary files suffix
	 * @return a fileset info object
	 * @throws IllegalArgumentException - one or both of suffixes are
	 * already registered and may cause file overrides
	 */
	public static synchronized FilesetInfo
		createInstance(String regularSuffix, String temporarySuffix)
			throws IllegalArgumentException
	{
		if ( registeredSuffixes.contains(regularSuffix) ) {
			throw new IllegalArgumentException("Suffix already exists: " + regularSuffix);
		}
		if ( registeredSuffixes.contains(temporarySuffix) ) {
			throw new IllegalArgumentException("Suffix already exists: " + temporarySuffix);
		}
		if ( regularSuffix.equals(temporarySuffix) ) {
			throw new IllegalArgumentException("Suffixes are equals: " + regularSuffix);
		}
		registeredSuffixes.add(temporarySuffix);
		registeredSuffixes.add(regularSuffix);
		return new FilesetInfo(regularSuffix, temporarySuffix);
	}
	
	/**
	 * This is a service method for testing purposes. 
	 */
	static synchronized void clearSuffixesCache() {
		registeredSuffixes.clear();
	}
	
	private final String regularSuffix, temporarySuffix;
	
	private FilesetInfo(String regularSuffix, String temporarySuffix) {
		this.regularSuffix = regularSuffix;
		this.temporarySuffix = temporarySuffix;
	}

	/**
	 * Get a regular file suffix.
	 * <p>
	 * This suffix is used to store a data for regular access. It shouldn't be
	 * changed between calls.
	 * <p>
	 * @return suffix of regular files
	 */
	public String getRegularSuffix() {
		return regularSuffix;
	}

	/**
	 * Get a temporary file suffix.
	 * <p>
	 * This suffix is used to store a temporary data which being processed. It
	 * shouldn't be changed between calls.
	 * <p>
	 * @return suffix of temporary files
	 */
	public String getTemporarySuffix() {
		return temporarySuffix;
	}

}

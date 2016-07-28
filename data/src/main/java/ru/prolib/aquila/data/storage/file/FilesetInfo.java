package ru.prolib.aquila.data.storage.file;

/**
 * Fileset info.
 * <p>
 * This class provides an information about files of single set of files.
 */
public interface FilesetInfo {
	
	/**
	 * Get a regular file suffix.
	 * <p>
	 * This suffix is used to store a data for regular access. It shouldn't be
	 * changed between calls.
	 * <p>
	 * @return suffix of regular files
	 */
	public String getRegularSuffix();
	
	/**
	 * Get a temporary file suffix.
	 * <p>
	 * This suffix is used to store a temporary data which being processed. It
	 * shouldn't be changed between calls.
	 * <p>
	 * @return suffix of temporary files
	 */
	public String getTemporarySuffix();

}

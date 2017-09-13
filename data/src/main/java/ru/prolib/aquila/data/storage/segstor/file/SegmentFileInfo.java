package ru.prolib.aquila.data.storage.segstor.file;

import java.io.File;

public interface SegmentFileInfo {

	/**
	 * Get a full path to segment file.
	 * <p>
	 * @return full path
	 */
	File getFullPath();

	/**
	 * Get path to a directory which contains segment file.
	 * <p>
	 * @return path to directory
	 */
	File getDirectory();

	/**
	 * Get base name of a file to store segment data.
	 * <p>
	 * @return base name
	 */
	String getBaseName();

	/**
	 * Get file name suffix to store segment data.
	 * <p>
	 * @return suffix
	 */
	String getNameSuffix();

}
package ru.prolib.aquila.data.storage.file;

/**
 * Configuration of set of files.
 * <p>
 * This class provides an information about files united by common meaning.
 */
public class FileConfig {
	private final String regular, temporary;
	
	public FileConfig(String regularSuffix, String temporarySuffix) {
		this.regular = regularSuffix;
		this.temporary = temporarySuffix;
	}

	/**
	 * Get suffix of regular file.
	 * <p>
	 * This suffix is used to store a data for regular access.
	 * <p>
	 * @return suffix of regular files
	 */
	public String getRegularSuffix() {
		return regular;
	}

	/**
	 * Get suffix of temporary file.
	 * <p>
	 * This suffix is used to store a temporary data which being processed.
	 * <p>
	 * @return suffix of temporary files
	 */
	public String getTemporarySuffix() {
		return temporary;
	}

}

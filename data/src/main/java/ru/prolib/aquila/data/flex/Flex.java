package ru.prolib.aquila.data.flex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Flex facade.
 * <p>
 * To activate Flex features all underlying objects must be created using this facade.
 */
public class Flex {
	
	/**
	 * Create file input stream.
	 * <p>
	 * @param file - the file to be opened for reading.
	 * @return the input stream instance
	 * @throws IOException - an error occurred
	 */
	public FileInputStream createInputStream(File file) throws IOException {
		return new FileInputStream(file);
	}
	
	/**
	 * Create file output stream.
	 * <p>
	 * @param file - the file to be opened for writing.
	 * @param append - if true, then bytes will be written to the end of the file rather than the beginning
	 * @return the output stream instance
	 * @throws IOException - an error occurred
	 */
	public FileOutputStream createOutputStream(File file, boolean append)
			throws IOException
	{
		return new FileOutputStream(file, append);
	}

}

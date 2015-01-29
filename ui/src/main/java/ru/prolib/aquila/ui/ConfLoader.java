package ru.prolib.aquila.ui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Wini;

@Deprecated
public class ConfLoader {
	/**
	 * $Id: ConfLoader.java 554 2013-03-01 13:43:04Z whirlwind $
	 */
	private String sFileName;
	private String sDirSeparator = System.getProperty("file.separator");
	
	public ConfLoader(String fileName) {
		super();
		sFileName = fileName;
	}
	
	public ConfLoader(String[] filepath) {
		sFileName = StringUtils.join(filepath, sDirSeparator);
	}
	
	public Wini load() throws IOException {
		String sFilePath = "";		
		File currentDir = new File(".");
		sFilePath = currentDir.getCanonicalPath() + sDirSeparator + sFileName;
		return new Wini(new File(sFilePath));
	}
}

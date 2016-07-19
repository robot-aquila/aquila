package ru.prolib.aquila.finam.tools.web;

import java.io.File;

public interface DataExportFileDownloader {
	
	public File download(DataExportForm form) throws DataExportException;

}

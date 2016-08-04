package ru.prolib.aquila.web.utils;

import java.io.OutputStream;
import java.net.URI;

/**
 * Interface of a file downloader.
 */
public interface FileDownloader {

	public void download(URI uri, OutputStream output)
			throws DataExportException;

}
package ru.prolib.aquila.finam.tools.web;

import java.io.OutputStream;
import java.net.URI;

public interface FileDownloader {

	public void download(URI uri, OutputStream output)
			throws DataExportException;

}
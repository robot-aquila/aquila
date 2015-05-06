package ru.prolib.aquila.datatools.finam.downloader;

import java.io.File;

public interface Downloader {
	
	public File download(WebForm form, String attachmentFileName)
		throws DownloaderException;

}

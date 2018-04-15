package ru.prolib.aquila.web.utils;

import java.io.OutputStream;
import java.net.URI;

/**
 * Interface of a file downloader.
 */
@Deprecated
public interface FileDownloader {

	/**
	 * Download a file.
	 * <p>
	 * @param uri - URI to download
	 * @param output - target stream
	 * @throws WUInvalidResponseException - response validation failed
	 * @throws WUProtocolException - application protocol exception
	 * @throws WUIOException - lower-level IO exception
	 */
	public void download(URI uri, OutputStream output)
		throws WUInvalidResponseException, WUProtocolException, WUIOException;

}
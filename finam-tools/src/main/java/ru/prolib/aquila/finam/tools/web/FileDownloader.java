package ru.prolib.aquila.finam.tools.web;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.http.client.ClientProtocolException;

public interface FileDownloader {

	public void download(URI uri, File target) throws UnexpectedResponse,
			ClientProtocolException, IOException;

}
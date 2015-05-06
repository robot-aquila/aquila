package ru.prolib.aquila.datatools.finam.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUnitDownloader implements Downloader {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(HtmlUnitDownloader.class);
	}
	
	private final SeleniumHtmlUnitModifiedDriver driver;
	
	public HtmlUnitDownloader(SeleniumHtmlUnitModifiedDriver driver) {
		super();
		this.driver = driver;
	}

	public HtmlUnitDownloader() {
		this(new SeleniumHtmlUnitModifiedDriver());
	}
	
	public SeleniumHtmlUnitModifiedDriver getWebDriver() {
		return driver;
	}
	
	public File download(WebForm form, String attachmentFileName)
		throws DownloaderException
	{
		Page respPage = null;
		FileOutputStream output = null;
		try {
			// Expected that the current page is the form page
			HtmlPage page = (HtmlPage) driver.getCurrentWindow().getEnclosedPage();
			HtmlButton button = (HtmlButton)
				page.getByXPath(form.getSubmitButtonXPath()).get(0);

			logger.debug("Downloading started");
			respPage = button.click();
			logger.debug("Response received");
			WebResponse response = respPage.getWebResponse();
			// yes, it is. expotfile - is an error on finam side
			String contentType = response.getContentType(); 
			if ( ! "finam/expotfile".equals(contentType) ) {
				throw new DownloaderException
					("Unexpected content type: " + contentType);
			}
			File destination = File.createTempFile("finam-", ".txt");
			destination.deleteOnExit();
			InputStream input = response.getContentAsStream();
			output = new FileOutputStream(destination);
			long written = IOUtils.copyLarge(input, output);
			logger.debug("Download finished: {} (size is {} bytes)",
					new Object[] { destination, written });
			return destination;
		//} catch (   ) {
			// TODO: add HtmlUnit node exception
			
		} catch ( IOException e ) {
			throw new DownloaderException("Error downloading file", e);
		} finally {
			if ( output != null ) IOUtils.closeQuietly(output);
			if ( respPage != null ) respPage.cleanUp();
		}
	}

}

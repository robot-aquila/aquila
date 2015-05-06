package ru.prolib.aquila.datatools.finam.downloader;

import java.io.File;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxDownloader implements Downloader {
	private static final Logger logger;
	private static final String targetDir;
	private static final long DOWNLOAD_TIMEOUT = 300000; // ms
	
	static {
		logger = LoggerFactory.getLogger(FirefoxDownloader.class);
		targetDir = System.getProperty("java.io.tmpdir");
	}
	
	private final FirefoxDriver driver;
	
	public FirefoxDownloader(FirefoxDriver driver) {
		super();
		this.driver = driver;
	}
	
	public FirefoxDownloader() {
		this(new FirefoxDriver(createProfile()));
	}
	
	private static FirefoxProfile createProfile() {
    	FirefoxProfile profile = new FirefoxProfile();
    	// http://browsers.about.com/od/aboutconfigentries/a/browser_download_folderList.htm
    	profile.setPreference("browser.download.folderList", 2);
    	profile.setPreference("browser.download.manager.showWhenStarting", false);
    	profile.setPreference("browser.download.dir", targetDir);
    	profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
    			"finam/expotfile"); // yes, this error in the finam response
    	return profile;
	}
	
	public FirefoxDriver getWebDriver() {
		return driver;
	}

	public File download(WebForm form, String attachmentFileName)
		throws DownloaderException
	{
		File destination = new File(targetDir, attachmentFileName);
		destination.deleteOnExit();
		// TODO: IDK
		//if ( destination.exists() ) {
		//	destination.delete();
		//}
		logger.debug("Sending download request");
		form.getSubmitButton().click();
		logger.debug("Response received");
		long started = System.currentTimeMillis();
		logger.debug("Waiting until download finished: " + destination);
		for (;;) {
			if ( destination.exists() ) {
				sleep(10); // TODO: how to check that download is complete?
				logger.debug("Download finished: {} (size is {} bytes)",
						new Object[] { destination, destination.length() });
				break;
			}
			sleep(1);
			if ( System.currentTimeMillis() - started > DOWNLOAD_TIMEOUT ) {
				logger.error("Timeout while downloading: " + destination);
				throw new DownloaderException("Timeout while downloading");				
			}
		}
		return destination;
	}
	
	private void sleep(long seconds) throws DownloaderException {
		try {
			Thread.sleep(seconds * 1000);
		} catch ( InterruptedException e ) {
			throw new DownloaderException("Interrupted", e);
		}
	}

}

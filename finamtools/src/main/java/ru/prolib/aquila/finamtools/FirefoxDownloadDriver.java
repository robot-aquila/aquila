package ru.prolib.aquila.finamtools;

import java.io.File;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxDownloadDriver implements DownloadDriver {
	private static final Logger logger;
	private static final String targetDir;
	private static final long DOWNLOAD_TIMEOUT = 300000; // ms
	
	static {
		logger = LoggerFactory.getLogger(FirefoxDownloadDriver.class);
		targetDir = System.getProperty("java.io.tmpdir");
	}
	
	private final FirefoxDriver driver;
	
	public FirefoxDownloadDriver(FirefoxDriver driver) {
		super();
		this.driver = driver;
	}
	
	public FirefoxDownloadDriver() {
		this(new FirefoxDriver(createProfile()));
		//this(new HtmlUnitDriver(true));		
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

	public File download(FinamDownloader downloader, String attachmentFileName) {
		File destination = new File(targetDir, attachmentFileName);
		destination.deleteOnExit();
		logger.info("Starting downloading: " + destination);
		downloader.getSubmitButton().click();
		long started = System.currentTimeMillis();
		boolean flag = false;
		logger.info("Waiting for finish downloading: " + destination);
		do {
			if ( destination.exists() ) {
				logger.info("Download finished: " + destination);
				flag = true;
				break;
			}
			sleep();
		} while (System.currentTimeMillis() - started <= DOWNLOAD_TIMEOUT);
		if ( ! flag ) {
			logger.error("Timeout while downloading: " + destination);
			throw new FinamDownloaderException("Timeout while downloading");
		}
		return destination;
	}
	
	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch ( InterruptedException e ) {
			throw new FinamDownloaderException("Interrupted", e);
		}
	}

}

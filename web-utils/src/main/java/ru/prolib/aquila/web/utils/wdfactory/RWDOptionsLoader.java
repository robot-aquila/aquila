package ru.prolib.aquila.web.utils.wdfactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class RWDOptionsLoader {
	public static final String DRIVER_FIREFOX = "firefox";
	public static final String DRIVER_CHROME = "chrome";
	public static final String INI_SECTION_NAME = "remote-driver";
	public static final String INI_KEY_DRIVER = "driver";
	public static final String INI_KEY_HUB_URL = "hub.url";
	public static final String INI_KEY_DOWNLOAD_DIR = "download.dir";
	public static final String INI_KEY_DOWNLOAD_DIR_LOCAL = "download.dir.local";
	public static final String INI_KEY_DOWNLOAD_TIMEOUT = "download.timeout";
	public static final String DEFAULT_HUB_URL = "http://localhost:4444/wd/hub";
	public static final String DEFAULT_DRIVER = DRIVER_CHROME;
	public static final String DEFAULT_DOWNLOAD_DIR = "/var/aquila-rwd-download";
	public static final String DEFAULT_DOWNLOAD_DIR_LOCAL = DEFAULT_DOWNLOAD_DIR;
	public static final long   DEFAULT_DOWNLOAD_TIMEOUT = 600000; // 10 minutes
	public static final String ENV_KEY_DRIVER = "AQUILA_IT_DRIVER";
	public static final String ENV_KEY_HUB_URL = "AQUILA_IT_HUB_URL";
	
	public static class RWDOptions {
		private final String driverID;
		private final URL hubUrl;
		private final Capabilities capabilities;
		private final File downloadDir, downloadDirLocal;
		private final long downloadTimeout;
		
		public RWDOptions(String driver_id,
				URL hubUrl,
				Capabilities capabilities,
				File download_dir,
				File download_dir_local,
				long download_timeout)
		{
			this.driverID = driver_id;
			this.hubUrl = hubUrl;
			this.capabilities = capabilities;
			this.downloadDir = download_dir;
			this.downloadDirLocal = download_dir_local;
			this.downloadTimeout = download_timeout;
		}
		
		public String getDriverID() {
			return driverID;
		}
		
		public URL getHubUrl() {
			return hubUrl;
		}
		
		public Capabilities getCapabilities() {
			return capabilities;
		}
		
		/**
		 * Get directory to download files.
		 * <p>
		 * @return path to download directory
		 */
		public File getDownloadDir() {
			return downloadDir;
		}
		
		/**
		 * Get download directory mapped to local filesystem.
		 * <p>
		 * @return path to download directory
		 */
		public File getDownloadDirLocal() {
			return downloadDirLocal;
		}
		
		/**
		 * Get timeout of file downloading in milliseconds.
		 * <p>
		 * @return timeout
		 */
		public long getDownloadTimeout() {
			return downloadTimeout;
		}
		
	}
	
	static URL toURL(String url) {
		try {
			return new URL(url);
		} catch ( MalformedURLException e ) {
			throw new IllegalArgumentException(e);
		}
	}
	
	protected final FFOptionsLoader firefoxOptionsLoader;
	
	public RWDOptionsLoader(FFOptionsLoader firefox_options_loader) {
		this.firefoxOptionsLoader = firefox_options_loader;
	}
	
	public RWDOptionsLoader() {
		this(new FFOptionsLoader());
	}
	
	public RWDOptions defaultOptions() {
		return new RWDOptions(
				DEFAULT_DRIVER,
				toURL(DEFAULT_HUB_URL),
				new DesiredCapabilities(),
				new File(DEFAULT_DOWNLOAD_DIR),
				new File(DEFAULT_DOWNLOAD_DIR_LOCAL),
				DEFAULT_DOWNLOAD_TIMEOUT);
	}
	
	public RWDOptions loadOptions(File config_file) throws IOException {
		String driver = DEFAULT_DRIVER, hub_url = DEFAULT_HUB_URL,
				download_dir_str = DEFAULT_DOWNLOAD_DIR,
				download_dir_local_str = DEFAULT_DOWNLOAD_DIR_LOCAL,
				download_timeout_str = null;
		long download_timeout = DEFAULT_DOWNLOAD_TIMEOUT;
		
		// Lowest priority is the configuration file
		if ( config_file.exists() ) {
			Ini ini = new Ini(config_file);
			if ( ini.containsKey(INI_SECTION_NAME) ) {
				Section config = ini.get(INI_SECTION_NAME);
				hub_url = config.getOrDefault(INI_KEY_HUB_URL, hub_url);
				driver = config.getOrDefault(INI_KEY_DRIVER, driver);
				download_dir_str = config.getOrDefault(INI_KEY_DOWNLOAD_DIR, download_dir_str);
				download_dir_local_str = config.getOrDefault(INI_KEY_DOWNLOAD_DIR_LOCAL, download_dir_local_str);
				download_timeout_str = config.getOrDefault(INI_KEY_DOWNLOAD_TIMEOUT, null);
			}
		}
		if ( download_timeout_str != null ) {
			try {
				download_timeout = Long.valueOf(download_timeout_str);
			} catch ( NumberFormatException e ) {
				throw new IOException("Invalid download timeout value: " + download_timeout_str, e);
			}
		}
		
		// The next priority is environment variables
		Map<String, String> env = System.getenv();
		hub_url = env.getOrDefault(ENV_KEY_HUB_URL, hub_url);
		driver = env.getOrDefault(ENV_KEY_DRIVER, driver);
		
		Capabilities capabilities = null;
		switch ( driver ) {
		case DRIVER_FIREFOX:
			//FirefoxOptions fo = firefoxOptionsLoader.loadOptions(config_file);
			//capabilities = fo;
			//fo.setCapability(FirefoxDriver.MARIONETTE, true);
			//if ( download_dir != null ) {
			//	throw new IllegalStateException("Downloading using this driver is not supported");
			//}
			//break;
			throw new IllegalStateException("Firefox driver currently not supported");
		case DRIVER_CHROME:
			ChromeOptions opts = new ChromeOptions();
			capabilities = opts;
			Map<String, Object> prefs = new HashMap<>();
			prefs.put("download.default_directory", download_dir_str);
			prefs.put("download.prompt_for_download", 0);
			prefs.put("download.directory_upgrade", 1);
			prefs.put("safebrowsing.enabled", 0);
			opts.setExperimentalOption("prefs", prefs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported driver: " + driver);
		}
		return new RWDOptions(driver,
				toURL(hub_url),
				capabilities,
				new File(download_dir_str),
				new File(download_dir_local_str),
				download_timeout
			);
	}

}

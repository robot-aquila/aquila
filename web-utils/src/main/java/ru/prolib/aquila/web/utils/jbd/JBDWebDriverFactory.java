package ru.prolib.aquila.web.utils.jbd;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.openqa.selenium.WebDriver;

import ru.prolib.aquila.web.utils.WebDriverFactory;
import com.machinepublishers.jbrowserdriver.ProxyConfig;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;

/**
 * JBrowserDriver factory.
 * <p>
 * This class handles JBrowserDriver settings builder which 
 */
public class JBDWebDriverFactory implements WebDriverFactory {
	public static final int DEFAULT_TIMEOUT_MS = 30000;
	private final Settings.Builder builder;
	
	public JBDWebDriverFactory(Settings.Builder builder) {
		this.builder = builder;
	}
	
	public JBDWebDriverFactory() {
		this(Settings.builder());
	}
	
	@Override
	public WebDriver createWebDriver() {
		return new JBrowserDriverFixed(builder.build());
	}
	
	public Settings.Builder getSettingsBuilder() {
		return builder;
	}
	
	/**
	 * Configure driver with most tested settings to work with MOEX site.
	 * <p> 
	 * @param timeoutMs - use common timeout for socket idle, connection request and connect 
	 * @return this
	 */
	public JBDWebDriverFactory withMoexTestedSettings(int timeoutMs) {
		builder.timezone(Timezone.EUROPE_MOSCOW)
			.ssl("compatible")
			.blockAds(true)
			.headless(true)
			.quickRender(true)
			.socketTimeout(timeoutMs)
			.connectionReqTimeout(timeoutMs)
			.connectTimeout(timeoutMs)
			.maxConnections(128)
			.javascript(true)
			.logJavascript(true)
			.saveAttachments(true)
			.userAgent(new UserAgent(
					UserAgent.Family.MOZILLA,
					"Mozilla",
					"Linux",
					"i686",
					"65.0",
					"Mozilla/5.0 (X11; Linux i686; rv:65.0) Gecko/20100101 Firefox/65.0"
				));
		return this;
	}
	
	public JBDWebDriverFactory withMoexTestedSettings() {
		return withMoexTestedSettings(DEFAULT_TIMEOUT_MS);
	}

	public JBDWebDriverFactory loadIni(File file) throws IOException {
		Ini ini = new Ini(file);
		if ( ini.containsKey("jbrowser-driver") ) {
			Section sec = ini.get("jbrowser-driver");
			if ( sec.containsKey("ssl") ) {
				String ssl = sec.get("ssl").trim();
				switch ( ssl ) {
				case "":
					builder.ssl(null);
					break;
				case "compatible":
				case "trustanything":
				default:
					builder.ssl(ssl);
					break;
				}
			}
			if ( sec.containsKey("loggerLevel") ) {
				String level = sec.get("loggerLevel").trim();
				switch ( level ) {
				case "":
					builder.loggerLevel(null);
					break;
				case "ALL":
				case "CONFIG":
				case "FINE":
				case "FINER":
				case "FINEST":
				case "INFO":
				case "OFF":
				case "SEVERE":
				case "WARNING":
					builder.loggerLevel(Level.parse(level));
					break;
				default:
					throw new IOException("Incorrect loggerLevel value: " + level);
				}
			}
			if ( sec.containsKey("ajaxWait") ) {
				String ajaxWait = sec.get("ajaxWait").trim();
				if ( ! "".equals(ajaxWait) ) {
					try {
						long x = Long.parseLong(ajaxWait);
						builder.ajaxWait(x);
					} catch ( NumberFormatException e ) {
						throw new IOException("Incorrect ajaxWait value: " + ajaxWait, e);
					}
				}
			}
			if ( sec.containsKey("ajaxResourceTimeout") ) {
				String ajaxResourceTimeout = sec.get("ajaxResourceTimeout").trim();
				if ( ! "".equals(ajaxResourceTimeout) ) {
					try {
						long x = Long.parseLong(ajaxResourceTimeout);
						builder.ajaxResourceTimeout(x);						
					} catch ( NumberFormatException e ) {
						throw new IOException("Incorrect ajaxResourceTimeout value: " + ajaxResourceTimeout, e);
					}
				}
			}
			if ( sec.containsKey("logWire") ) {
				String logWire = sec.get("logWire").trim();
				switch ( logWire ) {
				case "":
					break;
				case "true":
					builder.logWire(true);
					break;
				case "false":
					builder.logWire(false);
					break;
				default:
					throw new IOException("Incorrect logWire value: " + logWire);
				}
			}
			if ( sec.containsKey("cache") ) {
				String cache = sec.get("cache").trim();
				switch ( cache ) {
				case "":
					break;
				case "true":
					builder.cache(true);
					break;
				case "false":
					builder.cache(false);
					break;
				default:
					throw new IOException("Incorrect cache value: " + cache);
				}
			}
			if ( sec.containsKey("cache.dir") ) {
				String cacheDir = sec.get("cache.dir").trim();
				if ( cacheDir.length() > 0 ) {
					builder.cacheDir(new File(cacheDir));
				}
			}
			if ( sec.containsKey("cache.entries") ) {
				String cacheEntries = sec.get("cache.entries").trim();
				if ( ! "".equals(cacheEntries) ) {
					int x;
					try {
						x = Integer.parseInt(cacheEntries);
						builder.cacheEntries(x);
					} catch ( NumberFormatException e ) {
						throw new IOException("Incorrect cache.entries value: " + cacheEntries, e);
					}
				}
			}
			if ( sec.containsKey("cache.entrySize") ) {
				String cacheEntrySize = sec.get("cache.entrySize").trim();
				if ( ! "".equals(cacheEntrySize) ) {
					long x;
					try {
						x = Long.parseLong(cacheEntrySize);
						builder.cacheEntrySize(x);
					} catch ( NumberFormatException e ) {
						throw new IOException("Incorrect cache.entrySize value: " + cacheEntrySize, e);
					}
				}
			}
			if ( sec.containsKey("proxy.type") ) {
				String proxyType = sec.get("proxy.type").trim();
				if ( ! "".equals(proxyType) ) {
					ProxyConfig.Type vProxyType = null;
					switch ( proxyType ) {
					case "HTTP":
						vProxyType = ProxyConfig.Type.HTTP;
						break;
					case "SOCKS":
						vProxyType = ProxyConfig.Type.SOCKS;
						break;
					default:
						throw new IOException("Incorrect proxy.type value: " + proxyType);
					}
					String proxyHost = "", proxyPort = "", proxyUser = "", proxyPass = "";
					if ( ! sec.containsKey("proxy.host") ) {
						throw new IOException("Proxy enabled but proxy.host not specified");
					}
					if ( ! sec.containsKey("proxy.port") ) {
						throw new IOException("Proxy enabled but proxy.port not specified");
					}
					proxyHost = sec.get("proxy.host").trim();
					proxyPort = sec.get("proxy.port").trim();
					int vProxyPort = 0;
					try {
						vProxyPort = Integer.parseInt(proxyPort);
					} catch ( NumberFormatException e ) {
						throw new IOException("Incorrect proxy.port value: " + proxyPort, e);
					}
					if ( sec.containsKey("proxy.user") ) {
						proxyUser = sec.get("proxy.user").trim();
					}
					if ( sec.containsKey("proxy.pass") ) {
						proxyPass = sec.get("proxy.pass").trim();
					}
					if ( proxyUser.length() > 0 ) {
						builder.proxy(new ProxyConfig(vProxyType, proxyHost, vProxyPort, proxyUser, proxyPass));
					} else {
						builder.proxy(new ProxyConfig(vProxyType, proxyHost, vProxyPort));
					}
				}
			}
		}
		return this;
	}

}

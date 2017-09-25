package ru.prolib.aquila.web.utils.moex;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.web.utils.HttpClientFactory;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.WebDriverFactory;

public class Moex implements Closeable {
	@SuppressWarnings("unused")
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Moex.class);
	}
	
	private final Lock lock = new ReentrantLock();
	private final CloseableHttpClient httpClient;
	private final WebDriver webDriver;
	private final MoexContractForm webForm;
	private boolean closeResources = false;
	
	public Moex(CloseableHttpClient httpClient, WebDriver webDriver) {
		this.httpClient = httpClient;
		this.webDriver = webDriver;
		this.webForm = new MoexContractForm(webDriver);
		closeResources = false;
	}
	
	public Moex() {
		this(HttpClientFactory.createDefaultClient(), WebDriverFactory.createJBrowserDriver());
		closeResources = true;
	}

	@Override
	public void close() throws IOException {
		lock.lock();
		try {
			if ( closeResources ) {
				IOUtils.closeQuietly(httpClient);
				try {
					webDriver.close();
				} catch ( WebDriverException e ) {
					// JBrowserDrive bug when closing
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public Map<Integer, Object> getContractDetails(Symbol symbol)
			throws WUWebPageException
	{
		return webForm.getInstrumentDescription(symbol.toString());
	}
	
	public List<String> getActiveFuturesList() throws WUWebPageException {
		return webForm.getActiveFuturesList();
	}

}

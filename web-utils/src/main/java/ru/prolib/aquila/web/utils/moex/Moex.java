package ru.prolib.aquila.web.utils.moex;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.web.utils.WUException;
import ru.prolib.aquila.web.utils.WebDriverFactory;

public class Moex implements Closeable {
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
		this(HttpClients.createDefault(), WebDriverFactory.createJBrowserDriver());
		closeResources = true;
	}

	@Override
	public void close() throws IOException {
		lock.lock();
		try {
			if ( closeResources ) {
				IOUtils.closeQuietly(httpClient);
				webDriver.close();
			}
		} finally {
			lock.unlock();
		}
	}

	public Map<Integer, Object> getContractDetails(Symbol symbol)
			throws WUException
	{
		return webForm.getInstrumentDescription(symbol.toString());
	}
	
	public List<String> getActiveFuturesList() throws WUException {
		return webForm.getActiveFuturesList();
	}

}

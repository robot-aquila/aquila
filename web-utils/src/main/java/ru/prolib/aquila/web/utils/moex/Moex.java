package ru.prolib.aquila.web.utils.moex;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.web.utils.WUWebPageException;

/**
 * MOEX site service facade.
 */
public class Moex implements Closeable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Moex.class);
	}
	
	private final WebDriver webDriver;
	private final MoexContractForm webForm;
	private final boolean closeResources;
	private final String parkUrl = "https://www.moex.com/robots.txt";
	
	/**
	 * Constructor.
	 * <p>
	 * @param webDriver - web driver
	 * @param closeResources - if true then driver will be closed on closing this object.
	 * This option is useful when using a shared web driver.
	 */
	public Moex(WebDriver webDriver, boolean closeResources) {
		this.webDriver = webDriver;
		this.webForm = new MoexContractForm(webDriver);
		this.closeResources = closeResources;
	}

	@Override
	public void close() throws IOException {
		if ( closeResources ) {
			try {
				webDriver.close();
			} catch ( WebDriverException e ) {
				logger.error("WebDriver close error: ", e);
			}
			try {
				webDriver.quit();
			} catch ( WebDriverException e ) {
				logger.error("WebDriver quit error: ", e);
			}
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
	
	public void park() {
		try {
			webDriver.get(parkUrl);
		} catch ( WebDriverException e ) {
			logger.error("Parking failed: ", e);
		}
	}

}

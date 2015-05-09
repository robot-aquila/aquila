package ru.prolib.aquila.datatools.finam;

import java.io.File;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.datatools.finam.downloader.WebForm;

public class TickDataDownloader {
	public static final int HTMLUNIT_DRIVER = 0;
	public static final int FIREFOX_DRIVER = 1;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TickDataDownloader.class);
	}
	
	private final int driver;
	private String market;
	private String quote;
	private LocalDate date;
	
	public TickDataDownloader(int driver) {
		super();
		this.driver = driver;
		market = "МосБиржа фьючерсы";
		quote = "RTS";
		date = new LocalDate().minusDays(1);
	}
	
	public TickDataDownloader() {
		this(HTMLUNIT_DRIVER);
	}
	
	/**
	 * Use the market name.
	 * <p>
	 * Default value is "МосБиржа фьючерсы"
	 * <p>
	 * @param market - the name
	 * @return this object
	 */
	public TickDataDownloader withMarket(String market) {
		this.market = market;
		return this;
	}
	
	/**
	 * Use the quote name.
	 * <p>
	 * Default value is "RTS".
	 * <p>
	 * @param quote - the name
	 * @return this object
	 */
	public TickDataDownloader withQuote(String quote) {
		this.quote = quote;
		return this;
	}
	
	/**
	 * Use the trading session date.
	 * <p>
	 * Default value is previous day.
	 * <p>
	 * @param date - the date
	 * @return this object
	 */
	public TickDataDownloader withDate(LocalDate date) {
		this.date = date;
		return this;
	}
	
	/**
	 * Download file with the tick data.
	 * <p>
	 * @return the file
	 * @throws FinamException if error occurred while downloading
	 */
	public File download() throws FinamException {
		WebForm form  = null;
		String id = String.format("[%s@%s] for %s", quote, market, date);
		try {
			logger.debug("Start downloading tick data: {}", id);
			form = createWebForm();
			File file = form.selectMarket(market)
				.selectQuote(quote)
				.selectDateTo(date)
				.selectDateFrom(date)
				.selectPeriodTick()
				.selectFileFormat_TimePriceVolId()
				.download();
			logger.debug("Tick data {} download finished: {} (size {} bytes)",
					new Object[] { id, file, file.length() });
			return file;
			
		} catch ( Exception e ) {
			logger.error("Error downloading file: ", e);
			throw new FinamException("Error downloading file", e);
		} finally {
			if ( form != null ) form.close();
		}
	}
	
	protected WebForm createWebForm() {
		return driver == FIREFOX_DRIVER ? WebForm.createFirefoxDownloader()
				: WebForm.createHtmlUnitDownloader();
	}

}

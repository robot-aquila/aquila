package ru.prolib.aquila.finam.tools.web;

import java.io.File;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExport {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DataExport.class);
	}
	
	private String market;
	private String quote;
	private LocalDate date;
	private DataExportForm exportForm;
	
	public DataExport(DataExportForm exportForm) {
		super();
		this.exportForm = exportForm;
		market = "МосБиржа фьючерсы";
		quote = "RTS";
		date = LocalDate.now().minusDays(1);
	}
	
	/**
	 * Use the market name.
	 * <p>
	 * Default value is "МосБиржа фьючерсы"
	 * <p>
	 * @param market - the name
	 * @return this object
	 */
	public DataExport withMarket(String market) {
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
	public DataExport withQuote(String quote) {
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
	public DataExport withDate(LocalDate date) {
		this.date = date;
		return this;
	}
	
	/**
	 * Download file with the tick data.
	 * <p>
	 * @return the file
	 * @throws FinamException if error occurred while downloading
	 */
	public File download() throws DataExportException {
		String id = String.format("[%s@%s] for %s", quote, market, date);
		try {
			logger.debug("Start downloading tick data: {}", id);
			exportForm.selectMarket(market)
				.selectQuote(quote)
				.selectDateTo(date)
				.selectDateFrom(date)
				.selectPeriod_Ticks()
				.selectFileFormat_TimePriceVolId();
			// TODO: download not yet implemented
			return null;
		} catch ( Exception e ) {
			logger.error("Error downloading file: ", e);
			throw new DataExportException("Error downloading file", e);
		} finally {
			if ( exportForm != null ) {
				exportForm.close();
			}
		}
	}

}

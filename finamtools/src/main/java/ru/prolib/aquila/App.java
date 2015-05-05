package ru.prolib.aquila;

import java.io.File;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.finamtools.FinamDownloader;
import ru.prolib.aquila.finamtools.FirefoxDownloadDriver;

public class App  {
	private static final Logger logger = LoggerFactory.getLogger(App.class); 
	
    public static void main( String[] args ) throws Exception {
    	FinamDownloader downloader = createFirefoxDownloader();
    	downloader.selectMarket("МосБиржа акции")
    		.selectQuote("ГАЗПРОМ ао")
    		.selectPeriodTick();
    	DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd");
    	LocalDate date = new LocalDate(2015, 5, 5);
    	for ( int i = 0; i < 1; i ++ ) {
    		downloader.selectDateTo(date)
    			.selectDateFrom(date);
    		
    		File file = downloader.download();
    		logger.info("--- --- ---");
    		logger.info("For date: " + date);
    		logger.info("Downloaded file: " + file + " size=" + file.length());
    		File dest = new File("/home/whirlwind/tmp", df.print(date) + ".csv");
    		logger.info("Try rename to: " + dest);
    		logger.info(file.renameTo(dest) ? "success" : "failed");

    		date = date.plusDays(1);
    	}
     	downloader.close();
    }
    
    private static FinamDownloader createFirefoxDownloader() {
    	FirefoxDownloadDriver driver = new FirefoxDownloadDriver();
    	return new FinamDownloader(driver.getWebDriver(), driver);
    }
    
}

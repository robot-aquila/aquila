package ru.prolib.aquila;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.datatools.finam.TickDataDownloader;

public class App  {
	private static final Logger logger = LoggerFactory.getLogger(App.class); 
	
    public static void main( String[] args ) throws Exception {
    	logger.error("This method untested. Check it before run.");
    	return;
    	//TickDataDownloader downloader =
    	//	new TickDataDownloader(/*TickDataDownloader.FIREFOX_DRIVER*/)
    	//	.withQuote("Si-6.15(SiM5)");
       	/*
    	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDate date = LocalDate.of(2015, 4, 24);
    	for ( int i = 0; i < 4; i ++ ) {
    		downloader.withDate(date);
    		File file = downloader.download();
    		
    		logger.info("--- --- ---");
    		logger.info("For date: " + date);
    		logger.info("Downloaded file: " + file + " size=" + file.length());
    		File dest = new File("/home/whirlwind/tmp", df.format(date) + ".csv");
    		logger.info("Try rename to: " + dest);
    		logger.info(file.renameTo(dest) ? "success" : "failed");
    		    		
    		date = date.plusDays(1);
    	}
    	*/
    }
    
}

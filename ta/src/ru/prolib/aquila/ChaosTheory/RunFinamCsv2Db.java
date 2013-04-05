package ru.prolib.aquila.ChaosTheory;

import java.io.File;
import java.util.Date;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.quik.ExportQuik;

/**
 * Экспорт котировок из CSV файлов формата ФИНАМ в базу данных.
 */
public class RunFinamCsv2Db {
	static Logger logger = LoggerFactory.getLogger(RunFinamCsv2Db.class);
	static ServiceLocator locator;
	
	static void usage() {
		System.err.println("Usage: <config> <asset> [log4j-config]");
	}
	
	static int getTotalRecords(String file) throws Exception {
		logger.info("Start reading file");
		locator = ServiceLocatorImpl.getInstance(new File(file));
		locator.getMarketData().prepare();
		int totalRecords = 0;
		while ( next() ) {
			totalRecords ++;
		}
		logger.info("Total {} records found", totalRecords);
		return totalRecords;
	}
	
	static boolean next() throws Exception {
		MarketData data = locator.getMarketData();
		int before = data.getTime().getLength();
		data.update();
		return before != data.getTime().getLength();
	}

	public static void main(String[] args) throws Exception {
		if ( args.length < 2 ) {
			usage();
			return;
		}
		if ( args.length >= 3 ) {
			PropertyConfigurator.configure(args[2]);
		}
		int totalRecords = getTotalRecords(args[0]);
		String asset = args[1];

		locator = ServiceLocatorImpl.getInstance(new File(args[0]));
		
		ExportQuik dispatcher = (ExportQuik) locator.getExportService();
		int number = 1;
		int records = 1;
		logger.info("Start export");
		long start = System.currentTimeMillis();
		MarketData data = locator.getMarketData();
		data.prepare();
		while ( next() ) {
			Date dealTime = data.getTime().get();
			// open + volume
			dispatcher.dispatch(number++, dealTime, asset,
					data.getOpen().get(),
					data.getVolume().get().longValue());
			// high
			dispatcher.dispatch(number++, dealTime, asset,
					data.getHigh().get(), 0);
			// low
			dispatcher.dispatch(number++, dealTime, asset,
					data.getLow().get(), 0);
			// close
			dispatcher.dispatch(number++, dealTime, asset,
					data.getClose().get(), 0);
			
			records ++;
			if ( System.currentTimeMillis() - start > 30000 ) {
				double progress = ((double)records / (double)totalRecords);
				logger.info("Progress: {}%", (progress * 100));
				start = System.currentTimeMillis();
			}
		}
		dispatcher.flushAll();
		logger.info("Export finished");
	}

}

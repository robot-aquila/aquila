package ru.prolib.aquila.utils.finexp.futures.finam;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.LongTermTask;
import ru.prolib.aquila.data.DatedSymbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.file.SymbolFileStorage;
import ru.prolib.aquila.utils.finexp.futures.CmdLine;
import ru.prolib.aquila.web.utils.WUException;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.finam.Fidexp;
import ru.prolib.aquila.web.utils.finam.FidexpFactory;
import ru.prolib.aquila.web.utils.moex.Moex;
import ru.prolib.aquila.web.utils.moex.MoexContractField;
import ru.prolib.aquila.web.utils.moex.MoexFactory;

public class FinamWebTickDataTracker implements Runnable, Closeable {
	private static final Logger logger;
	private static final String TASK_NAME = "FINAM-WEB-TICK-DATA-TRACKER";
	private static final int LOOKUP_MAX_DAYS = 30;
	private static final int MARKET_ID = 14;
	private static final int MIN_DOWNLOAD_SEGMENTS = 3;
	private static final int MAX_DOWNLOAD_SEGMENTS = 7;
	private static final long PAUSE_BETWEEN_DOWNLOAD_MIN = 5; // seconds
	private static final long PAUSE_BETWEEN_DOWNLOAD_MAX = 15;
	private static final long PAUSE_BETWEEN_SYMBOL_MIN = 30; // seconds
	private static final long PAUSE_BETWEEN_SYMBOL_MAX = 120;
	private static final long PAUSE_BETWEEN_TASK_MIN = 60 * 8; // minutes
	private static final long PAUSE_BETWEEN_TASK_MAX = 60 * 12;
	
	static {
		logger = LoggerFactory.getLogger(FinamWebTickDataTracker.class);
	}
	
	private final SymbolFileStorage fileStorage;
	private final CountDownLatch globalExit;
	private final Scheduler scheduler;
	private final CommandLine cmdLine;
	private final MoexFactory moexFactory;
	private final FidexpFactory finamFactory;
	
	public FinamWebTickDataTracker(SymbolFileStorage fileStorage,
			CountDownLatch globalExit, Scheduler scheduler,
			CommandLine cmdLine, MoexFactory moexFactory, FidexpFactory finamFactory)
	{
		this.fileStorage = fileStorage;
		this.globalExit = globalExit;
		this.scheduler = scheduler;
		this.cmdLine = cmdLine;
		this.moexFactory = moexFactory;
		this.finamFactory = finamFactory;
	}

	@Override
	public void run() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		Fidexp facade = finamFactory.createInstance();
		Moex moex = moexFactory.createInstance();
		try {
			logger.debug("Update started.");
			if ( cmdLine.hasOption(CmdLine.LOPT_SKIP_INTEGRITY_TEST) ) {
				logger.debug("Web-interface integrity test skipped.");
			} else {
				logger.debug("Performing a web-interface integrity test.");
				facade.testFormIntegrity();
				logger.debug("Web-interface integrity test passed");
			}
			Map<Integer, String> quoteMap = facade.getTrueFuturesQuotes(true);
			// 2) Shuffle the list randomly
			List<Integer> quoteIds = new ArrayList<>(quoteMap.keySet());
			Collections.shuffle(quoteIds);
			LocalDate today = LocalDate.now();
			LocalDate endDate = today.minusDays(1);
			LocalDate startDate = today.minusDays(LOOKUP_MAX_DAYS);
			logger.debug("Selected data period: {} to {}", new Object[] { startDate, endDate });
			// for each futures:
			int remainedSymbols = quoteIds.size();
			boolean lastSymbolHasDownload = false;
			for ( int i = 0; i < quoteIds.size(); i ++ ) {
				lastSymbolHasDownload = false;
				int quoteID = quoteIds.get(i);
				String ticker = quoteMap.get(quoteID);
				logger.debug("Start processing symbol: {}", ticker);
				remainedSymbols --;
				Map<Integer, Object> contrDetails = null;
				try {
					contrDetails = moex.getContractDetails(new Symbol(ticker));
				} catch ( WUWebPageException e ) {
					if ( e.getMessage().startsWith("Contract not exists") ) {
						logger.debug("Skip {} because it isn't a real futures.", ticker);
					} else {
						logger.error("Error checking contract existence: ", e);
					}
					continue;
				}
				
				Symbol symbol = new Symbol(ticker);

				// 1) Scan local database for existing data segments from LocalDate - X to LocalDate - 1
				//		where X is a lookup max depth in days
				List<LocalDate> existingSegments = fileStorage.listExistingSegments(symbol, startDate, endDate);
				// 2) If there are some non-existing segments then download randomly 2-5
				// data segments starting of earliest date
				LocalDate current = startDate;
				LocalDate firstTradingDay = (LocalDate) contrDetails.get(MoexContractField.FIRST_TRADING_DAY);
				LocalDate lastTradingDay = (LocalDate) contrDetails.get(MoexContractField.LAST_TRADING_DAY);
				int remainedDownloads = random.nextInt(MIN_DOWNLOAD_SEGMENTS, MAX_DOWNLOAD_SEGMENTS + 1);
				boolean isFirstDownload = true;
				while ( current.isBefore(today) && remainedDownloads > 0 ) {
					DatedSymbol descr = new DatedSymbol(symbol, current);
					if ( lastTradingDay != null && current.isAfter(lastTradingDay) ) {
						logger.debug("Skip downloading {} because it is after the last trading day {}", descr, lastTradingDay);
						break;
					}

					if ( ! existingSegments.contains(current) ) {
						if ( firstTradingDay != null && current.isBefore(firstTradingDay) ) {
							logger.debug("Skip downloading {} because it is before the first trading day {}", descr, firstTradingDay);
							current = firstTradingDay;
							continue;
						}

						if ( ! isFirstDownload ) {
							long pause = random.nextLong(PAUSE_BETWEEN_DOWNLOAD_MIN, PAUSE_BETWEEN_DOWNLOAD_MAX + 1);
							logger.debug("Waiting for {} seconds before going next download (remained {}).", pause, remainedDownloads);
							if ( globalExit.await(pause, TimeUnit.SECONDS) ) {
								logger.debug("The global exit signal received.");
								break;
							}
						}
						downloadDataSegment(facade, descr, quoteID);
						remainedDownloads --;
						isFirstDownload = false;
						lastSymbolHasDownload = true;
					} else {
						logger.debug("Segment already downloaded: {}", descr);
					}
					current = current.plusDays(1);
				}

				if ( globalExit.getCount() == 0 ) {
					break;
				}

				logger.debug("End processing symbol: {} (remained {} of {})",
					new Object[] { ticker, remainedSymbols, quoteIds.size()} );
				if ( remainedSymbols > 0 && lastSymbolHasDownload ) {
					// 3) Wait some random time
					long pause = random.nextLong(PAUSE_BETWEEN_SYMBOL_MIN, PAUSE_BETWEEN_SYMBOL_MAX + 1);
					logger.debug("Waiting for {} seconds before going next symbol (remained {}/{})",
						new Object[] { pause, remainedSymbols, quoteIds.size() });
					if ( globalExit.await(pause, TimeUnit.SECONDS) ) {
						logger.debug("The global exit signal received.");
						break;
					}
				}
			}
		} catch ( InterruptedException e ) {
			logger.warn("The task was interrupted.");
			Thread.currentThread().interrupt();
			return;
		} catch ( WUException e ) {
			logger.error("Something wrong with the web-interface. We'll try later. ", e);
		} catch ( DataStorageException e ) {
			logger.error("The data storage exceptions should be investigated. Initiate global exit.", e);
			globalExit.countDown();
			return;
		} catch ( Throwable e ) {
			logger.error("Unhandled exception detected", e);
		} finally {
			IOUtils.closeQuietly(facade);
			IOUtils.closeQuietly(moex);
		}

		if ( globalExit.getCount() > 0 ) {
			// 3) After all schedule the next update time in the next 8-12 hours
			long pause = random.nextLong(PAUSE_BETWEEN_TASK_MIN, PAUSE_BETWEEN_TASK_MAX + 1);
			Instant nextUpdateTime = scheduler.getCurrentTime().plusSeconds(pause * 60);
			logger.debug("The next update scheduled: {} minutes ahead at {}",
					pause, LocalDateTime.ofInstant(nextUpdateTime, ZoneId.systemDefault()));
			reschedule(nextUpdateTime);
		}
		logger.debug("Update finished.");
	}

	@Override
	public void close() throws IOException {
		
	}
	
	public void reschedule(Instant at) {
		if ( globalExit.getCount() > 0 ) {
			scheduler.schedule(new LongTermTask(this, TASK_NAME), at);
		}
	}

	private void downloadDataSegment(Fidexp finam, DatedSymbol descr, int finamQuoteID)
		throws DataStorageException, WUException
	{
		logger.debug("Start downloading segment: {}", descr);
		File tempFile = fileStorage.getTemporarySegmentFile(descr);
		File mainFile = fileStorage.getSegmentFile(descr);
		finam.downloadTickData(MARKET_ID, finamQuoteID, descr.getDate(), tempFile);
		fileStorage.commitTemporarySegmentFile(descr);
		logger.debug("Download finished: {} (size: {})", mainFile, mainFile.length()); 
	}

}

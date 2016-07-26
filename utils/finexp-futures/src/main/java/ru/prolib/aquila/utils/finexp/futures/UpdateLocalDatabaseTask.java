package ru.prolib.aquila.utils.finexp.futures;

import java.io.File;
import java.net.URI;
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
import org.openqa.selenium.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.finam.tools.web.CandleTime;
import ru.prolib.aquila.finam.tools.web.DataExport;
import ru.prolib.aquila.finam.tools.web.DataExportException;
import ru.prolib.aquila.finam.tools.web.DataExportParams;
import ru.prolib.aquila.finam.tools.web.DataFormat;
import ru.prolib.aquila.finam.tools.web.DateFormat;
import ru.prolib.aquila.finam.tools.web.DigitSeparator;
import ru.prolib.aquila.finam.tools.web.FieldSeparator;
import ru.prolib.aquila.finam.tools.web.FileExt;
import ru.prolib.aquila.finam.tools.web.Period;
import ru.prolib.aquila.finam.tools.web.TimeFormat;

public class UpdateLocalDatabaseTask implements Runnable {
	private static final Logger logger;
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
		logger = LoggerFactory.getLogger(UpdateLocalDatabaseTask.class);
	}
	
	private final DataStorage dataStorage;
	private final CountDownLatch globalExit;
	private final Scheduler scheduler;
	private final CommandLine cmdLine;
	
	public UpdateLocalDatabaseTask(DataStorage dataStorage,
			CountDownLatch globalExit, Scheduler scheduler,
			CommandLine cmdLine)
	{
		this.dataStorage = dataStorage;
		this.globalExit = globalExit;
		this.scheduler = scheduler;
		this.cmdLine = cmdLine;
	}

	@Override
	public void run() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		DataExport facade = new DataExport();
		try {
			logger.debug("Update started.");
			if ( cmdLine.hasOption(CmdLine.LOPT_SKIP_INTEGRITY_TEST) ) {
				logger.debug("Web-interface integrity test skipped.");
			} else {
				logger.debug("Performing a web-interface integrity test.");
				facade.testFormIntegrity();
				logger.debug("Web-interface integrity test passed");
			}
			// 1) Get an actual features list
			Map<Integer, String> quoteMap = facade.getTrueFuturesQuotes(true);
			URI baseUri = facade.getFormActionURI();
			// 2) Shuffle the list randomly
			List<Integer> quoteIds = new ArrayList<>(quoteMap.keySet());
			Collections.shuffle(quoteIds);
			LocalDate today = LocalDate.now();
			LocalDate endDate = today.minusDays(1);
			LocalDate startDate = today.minusDays(LOOKUP_MAX_DAYS);
			DataExportParams params = createBasicParams();
			logger.debug("Selected data period: {} to {}", new Object[] { startDate, endDate });
			// for each futures:
			int remainedSymbols = quoteIds.size();
			for ( int i = 0; i < quoteIds.size(); i ++ ) {
				int id = quoteIds.get(i);
				Symbol symbol = new Symbol(quoteMap.get(id));
				params.setQuoteID(id);
				params.setContractName(symbol.toString());

				// 1) Scan local database for existing data segments from LocalDate - X to LocalDate - 1
				//		where X is a lookup max depth in days
				List<LocalDate> existingSegments = dataStorage.listExistingSegments(symbol, startDate, endDate);
				// 2) If there are some non-existing segments then download randomly 2-5
				// data segments starting of earliest date
				LocalDate current = startDate;
				int remainedDownloads = random.nextInt(MIN_DOWNLOAD_SEGMENTS, MAX_DOWNLOAD_SEGMENTS + 1);
				while ( current.isBefore(today) && remainedDownloads > 0 ) {
					if ( ! existingSegments.contains(current) ) {
						logger.debug("Start downloading segment: {} at {}", new Object[] { symbol, current });
						params.setDateFrom(current);
						params.setDateTo(current);
						File tempFile = dataStorage.getSegmentTemporaryFile(symbol, current);
						File mainFile = dataStorage.getSegmentFile(symbol, current);
						facade.download(baseUri, params, tempFile);
						dataStorage.commitSegmentTemporaryFile(symbol, current);
						logger.debug("Download finished (size: {}): {} ",
								new Object[] { mainFile.length(), mainFile } ); 
						
						remainedDownloads --;
						if ( remainedDownloads > 0 ) {
							long pause = random.nextLong(PAUSE_BETWEEN_DOWNLOAD_MIN, PAUSE_BETWEEN_DOWNLOAD_MAX + 1);
							logger.debug("Waiting for {} seconds before going next download (remained {}).",
									new Object[] { pause, remainedDownloads } );
							if ( globalExit.await(pause, TimeUnit.SECONDS) ) {
								logger.debug("The global exit signal received.");
								break;
							}
						}
					}
					current = current.plusDays(1);
				}

				remainedSymbols --;
				if ( remainedSymbols > 0 ) {
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
		} catch ( DataExportException e ) {
			logger.error("Something wrong with the web-interface. We'll try later. ", e);
		} catch ( DataStorageException e ) {
			logger.error("The data storage exceptions should be investigated. Initiate global exit.", e);
			globalExit.countDown();
			return;
		} finally {
			IOUtils.closeQuietly(facade);
		}

		if ( globalExit.getCount() > 0 ) {
			// 3) After all schedule the next update time in the next 8-12 hours
			long pause = random.nextLong(PAUSE_BETWEEN_TASK_MIN, PAUSE_BETWEEN_TASK_MAX + 1);
			Instant nextUpdateTime = Instant.now().plusSeconds(pause * 60);
			logger.debug("The next update scheduled: {} minutes ahead at {}",
					LocalDateTime.ofInstant(nextUpdateTime, ZoneId.systemDefault()));
			scheduler.schedule(this, nextUpdateTime);
		}
		logger.debug("Update finished.");
	}
	
	private DataExportParams createBasicParams() {
		return new DataExportParams()
			.setMarketId(MARKET_ID)
			.setPeriod(Period.TICKS)
			.setFileName("dummy-file")
			.setFileExt(FileExt.CSV)
			.setDateFormat(DateFormat.YYYYMMDD)
			.setTimeFormat(TimeFormat.HHMMSS)
			.setCandleTime(CandleTime.START_OF_CANDLE)
			.setUseMoscowTime(true)
			.setFieldSeparator(FieldSeparator.COMMA)
			.setDigitSeparator(DigitSeparator.NONE)
			.setDataFormat(DataFormat.DATE_TIME_LAST_VOL)
			.setAddHeader(true)
			.setFillEmptyPeriods(false);
	}

}

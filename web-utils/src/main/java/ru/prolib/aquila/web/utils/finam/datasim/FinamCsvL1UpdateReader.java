package ru.prolib.aquila.web.utils.finam.datasim;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Read the data of a single CSV file stored in FINAM export format.
 * <p>
 * We need to know a price scale when create because FINAM
 * format does not contain such information.
 */
public class FinamCsvL1UpdateReader implements CloseableIterator<L1Update> {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(FinamCsvL1UpdateReader.class);
	}
	
	private static final int FIRST_TICK_NUMBER = 1;
	private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");
	private static final String HDR_DATE = "<DATE>";
	private static final String HDR_TIME = "<TIME>";
	private static final String HDR_PRICE = "<LAST>";
	private static final String HDR_SIZE = "<VOL>";
	private static final DateTimeFormatter timeFmt, dateFmt;
	
	static {
		timeFmt = DateTimeFormatter.ofPattern("HHmmss");
		dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");
	}
	
	private final CsvReader csvReader;
	private final Symbol symbol;
	private final int priceScale;
	private boolean closed = false;
	private boolean firstRecord = true;
	private L1Update lastUpdate;
	private Instant prevTime;
	private int localTickCounter = FIRST_TICK_NUMBER, timeErrorCount;
	
	public FinamCsvL1UpdateReader(Symbol symbol, CsvReader csvReader, int priceScale) {
		this.symbol = symbol;
		this.csvReader = csvReader;
		this.priceScale = priceScale;
	}
	
	public FinamCsvL1UpdateReader(Symbol symbol, InputStream inputStream, int priceScale) {
		this(symbol, new CsvReader(new InputStreamReader(inputStream)), priceScale);
	}
	
	public FinamCsvL1UpdateReader(Symbol symbol, File file, int priceScale) throws IOException {
		this(symbol, new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))), priceScale);
	}
	
	public FinamCsvL1UpdateReader(Symbol symbol, String file, int priceScale) throws IOException {
		this(symbol, new File(file), priceScale);
	}
	
	public int getTimeErrorCount() {
		return timeErrorCount;
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			csvReader.close();
			closed = true;
			if ( timeErrorCount > 0 ) {
				logger.warn("L1 data of {} contains time errors: {} case(s)", symbol, timeErrorCount);
			}
		}
	}

	@Override
	public boolean next() throws IOException {
		lastUpdate = null;
		if ( firstRecord ) {
			firstRecord = false;
			if ( ! csvReader.readHeaders() ) {
				return false;
			}
		}
		if ( ! csvReader.readRecord() ) {
			return false;
		}
		String date_str, time_str;
		L1UpdateBuilder builder = new L1UpdateBuilder(symbol).withTrade();
		LocalDate date = LocalDate.parse(date_str = csvReader.get(HDR_DATE), dateFmt);
		LocalTime time = LocalTime.parse(time_str = csvReader.get(HDR_TIME), timeFmt);
		Instant curr_time = ZonedDateTime.of(date, time, ZONE).toInstant();
		if ( prevTime == null ) {
			prevTime = curr_time;
			localTickCounter = FIRST_TICK_NUMBER;
		} else if ( ! prevTime.equals(curr_time) ) {
			if ( prevTime.compareTo(curr_time) > 0 ) {
				// This is an error: a time sequence is broken
				timeErrorCount ++;
			}
			prevTime = curr_time;
			localTickCounter = FIRST_TICK_NUMBER;
		} else {
			localTickCounter ++;
		}
		builder.withTime(curr_time)
			.withComment(String.format("%s%s#%010d", date_str, time_str, localTickCounter))
			// TODO: replace me on something more faster
			.withPrice(CDecimalBD.of(csvReader.get(HDR_PRICE)).withScale(priceScale))
			.withSize(Long.valueOf(csvReader.get(HDR_SIZE)));
		lastUpdate = builder.buildL1Update();
		return true;
	}

	@Override
	public L1Update item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("The reader has already been closed");
		}
		if ( lastUpdate == null ) {
			throw new NoSuchElementException();
		}
		return lastUpdate;
	}

}

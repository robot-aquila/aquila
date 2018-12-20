package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;

import com.csvreader.CsvReader;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class QEMACompareWithQUIKTest {
	private static final ZoneId zone_id;
	private static final ZTFrame time_frame;
	private static final DateTimeFormatter time_format, date_format;
	private static final File fix_path, fix_ohlc_csv, fix_ema252_csv;
	
	static {
		zone_id = ZoneId.of("Europe/Moscow");
		time_frame = ZTFrame.M5MSK;
		time_format = DateTimeFormatter.ofPattern("HHmmss");
		date_format = DateTimeFormatter.ofPattern("yyyyMMdd");
		fix_path = new File("fixture");
		fix_ohlc_csv = new File(fix_path, "QUIK-RIZ8-201808-OHLC.csv");
		fix_ema252_csv = new File(fix_path, "QUIK-RIZ8-201808-EMA252.csv");
	}
	
	static LocalDate parseDate(String dateString) {
		return LocalDate.parse(dateString, date_format);
	}
	
	static LocalTime parseTime(String timeString) {
		return LocalTime.parse(timeString, time_format);
	}
	
	static Instant parseDateTime(String dateString, String timeString) {
		return ZonedDateTime.of(
				parseDate(dateString),
				parseTime(timeString),
				zone_id
			).toInstant();
	}
	
	static CDecimal parseDecimal(String valueString) {
		return CDecimalBD.of(valueString);
	}
	
	static CDecimal parseDecimal0(String valueString) {
		return parseDecimal(valueString).withScale(0);
	}
	
	static TSeries<Candle> loadOHLC() throws Exception {
		TSeriesImpl<Candle> result = new TSeriesImpl<>("OHLC", time_frame);
		CsvReader reader = new CsvReader(fix_ohlc_csv.getAbsolutePath());
		try {
			assertTrue(reader.readHeaders());
			while ( reader.readRecord() ) {
				Instant time = parseDateTime(reader.get("<DATE>"), reader.get("<TIME>"));
				result.set(time, new CandleBuilder()
					.withTimeFrame(time_frame)
					.withTime(time)
					.withOpenPrice(parseDecimal0(reader.get("<OPEN>")))
					.withHighPrice(parseDecimal0(reader.get("<HIGH>")))
					.withLowPrice(parseDecimal0(reader.get("<LOW>")))
					.withClosePrice(parseDecimal0(reader.get("<CLOSE>")))
					.withVolume(parseDecimal0(reader.get("<VOL>")))
					.buildCandle());
			}
		} finally {
			reader.close();
		}
		return result;
	}
	
	static TSeries<CDecimal> loadEMA252() throws Exception {
		TSeriesImpl<CDecimal> result = new TSeriesImpl<>("EMA252", time_frame);
		CsvReader reader = new CsvReader(fix_ema252_csv.getAbsolutePath());
		try {
			assertTrue(reader.readHeaders());
			while ( reader.readRecord() ) {
				Instant time = parseDateTime(reader.get("<DATE>"), reader.get("<TIME>"));
				result.set(time, parseDecimal(reader.get("<CLOSE>")));
			}
		} finally {
			reader.close();
		}
		return result;
	}

	private TSeries<Candle> s_ohlc_loaded;
	private TSeries<CDecimal> s_ema252_loaded;
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void test() throws Exception {
		s_ohlc_loaded = loadOHLC();
		s_ema252_loaded = loadEMA252();
		TSeries<CDecimal> s_ohlc_close = new CandleCloseTSeries(s_ohlc_loaded);
		QEMATSeriesFast s_ema252_mine = new QEMATSeriesFast("X", s_ohlc_close, 252, 10);
		int count = s_ohlc_loaded.getLength();
		for ( int i = 0; i < count; i ++ ) {
			Instant time = s_ohlc_loaded.toKey(i);
			CDecimal ema_quik = s_ema252_loaded.get(time);
			CDecimal ema_mine = s_ema252_mine.get(i);
			if ( ema_mine != null ) {
				ema_mine = ema_mine.withScale(6);
			}
			String msg = "At #" + i;
			assertEquals(msg, ema_quik, ema_mine);
		}
	}

}

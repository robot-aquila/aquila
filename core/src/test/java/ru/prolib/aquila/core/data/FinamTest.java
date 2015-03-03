package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.ParseException;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.finam.Quote2CsvWriter;

/**
 * 2013-03-08<br>
 * $Id: FinamTest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class FinamTest {
	private static DateTimeFormatter df;
	private static Finam finam;
	private EventSystem es;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		df = DateTimeFormat.forPattern("yyyyMMddHHmmss");
		finam = new Finam();
	}

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testLoadCandles_WithVolume() throws Exception {
		Object fix[][] = {
			{ "20130307100000", 134.97d, 135.21d, 134.52d, 134.67d, 5386540L },
			{ "20130307110000", 134.68d, 135.59d, 134.62d, 135.40d, 1906010L },
		};
		EditableCandleSeries expected = new CandleSeriesImpl(es, Timeframe.M5);
		for ( int i = 0; i < fix.length; i ++ ) {
			DateTime time = df.parseDateTime((String) fix[i][0]);
			expected.add(new Candle(Timeframe.M5.getInterval(time),
					(Double) fix[i][1], (Double) fix[i][2],
					(Double) fix[i][3], (Double) fix[i][4],
					(Long) fix[i][5]));
		}
		
		EditableCandleSeries actual = new CandleSeriesImpl(es, Timeframe.M5);
		finam.loadCandles(new File("fixture/GAZP.txt"), actual);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoadCandles_WithoutVolume() throws Exception {
		Object fix[][] = {
				{ "20130301100000",152910.0,152910.0,152270.0,152350.0 },
				{ "20130301100500",152350.0,152430.0,152260.0,152370.0 },
				{ "20130301101000",152370.0,152380.0,152060.0,152200.0 },
				{ "20130301101500",152200.0,152250.0,152130.0,152190.0 },
		};
		EditableCandleSeries expected = new CandleSeriesImpl(es, Timeframe.M5);
		for ( int i = 0; i < fix.length; i ++ ) {
			DateTime time = df.parseDateTime((String) fix[i][0]);
			expected.add(new Candle(Timeframe.M5.getInterval(time),
					(Double) fix[i][1], (Double) fix[i][2],
					(Double) fix[i][3], (Double) fix[i][4], 0L));
		}

		EditableCandleSeries actual = new CandleSeriesImpl(es, Timeframe.M5);
		finam.loadCandles(new File("fixture/SPFB.RTS.txt"), actual);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateWriter() throws Exception {
		File file = File.createTempFile("finam-", ".csv");
		file.deleteOnExit();
		EditableCandleSeries candles = new CandleSeriesImpl(es, Timeframe.M5);
		
		CandlesWriter expected = new Quote2CsvWriter(candles, file);
		assertEquals(expected, finam.createWriter(file, candles));
	}
	
	@Test
	public void testCreateTickReader_Csv() throws Exception {
		Aqiterator<Tick> reader =
				finam.createTickReader("fixture/GAZP_ticks.csv");
		new TickReader_FunctionalTest().testStreamContent(reader);
	}
	
	@Test (expected=FileNotFoundException.class)
	public void testCreateTickReader_Csv_ThrowsIfFileNotFound()
			throws Exception
	{
		finam.createTickReader("fixture/GAZ-maz-vaz-baz.txt");
	}
	
	@Test
	public void testCreateTickReader_GZippedCsv() throws Exception {
		Aqiterator<Tick> reader =
				finam.createTickReader("fixture/GAZP_ticks.csv.gz");
		new TickReader_FunctionalTest().testStreamContent(reader);
	}
	
	@Test (expected=NullPointerException.class)
	public void testCreateTickReader_ThrowsIfFilenameIsNull()
			throws Exception
	{
		finam.createTickReader(null);
	}
	
	@Test
	public void testParseDateTime() throws Exception {
		DateTime expected = new DateTime(2015, 1, 29, 8, 52, 38);
		DateTime actual = Finam.parseDateTime("20150129", "085238");
		assertEquals(expected, actual);
	}
	
	@Test (expected=ParseException.class)
	public void testParseDateTime_ThrowsIfBadDateFormat() throws Exception {
		Finam.parseDateTime("xxx", "085238");
	}
	
	@Test (expected=ParseException.class)
	public void testParseDateTime_ThrowsIfBadTimeFormat() throws Exception {
		Finam.parseDateTime("20150129", "xxx");
	}

	/**
	 * Тест багфикса парсинга, когда парсинг 2014-12-04 10:00:00 давал
	 * 2014-12-04 11:00:00, то есть добавлялся час откуда-то.
	 * Этот баг проявился на jdk 1.7.
	 * См. {@link #testLoadCandles_2014_12_04_10_00_00_Bugfix()} 
	 * @throws Exception
	 */
	@Test
	public void testParseDateTime_2014_12_04_10_00_00_Bugfix()
			throws Exception
	{
		DateTime expected = new DateTime(2014, 12, 4, 10, 0, 0);
		DateTime actual = Finam.parseDateTime("20141204", "100000");
		assertEquals(expected, actual);
	}

	
	@Test (expected=ParseException.class)
	public void testLoadCandles_ThrowsIfBadDateFormat() throws Exception {
		File file = File.createTempFile("finam-", ".csv");
		file.deleteOnExit();
		FileWriter writer = new FileWriter(file);
		writer.write("<DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>\n");
		writer.write("20141204,100000,0,0,0,0,0\n");
		writer.write("xxxxxxxx,110002,0,0,0,0,0\n");
		writer.close();
		
		EditableCandleSeries actual = new CandleSeriesImpl(es, Timeframe.M5);
		finam.loadCandles(file, actual);
	}
	
	@Test (expected=ParseException.class)
	public void testLoadCandles_ThrowsIfBadTimeFormat() throws Exception {
		File file = File.createTempFile("finam-", ".csv");
		file.deleteOnExit();
		FileWriter writer = new FileWriter(file);
		writer.write("<DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>\n");
		writer.write("20141204,100000,0,0,0,0,0\n");
		writer.write("20141204,xxxxxx,0,0,0,0,0\n");
		writer.close();
		
		EditableCandleSeries actual = new CandleSeriesImpl(es, Timeframe.M5);
		finam.loadCandles(file, actual);
	}
	
	@Test
	public void testLoadCandles_2014_12_04_10_00_00_Bugfix() throws Exception {
		File file = File.createTempFile("finam-", ".csv");
		file.deleteOnExit();
		FileWriter writer = new FileWriter(file);
		writer.write("<DATE>,<TIME>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>\n");
		writer.write("20141204,100001,0,0,0,0,0\n");
		writer.write("20141204,110002,0,0,0,0,0\n");
		writer.close();
		
		EditableCandleSeries actual = new CandleSeriesImpl(es, Timeframe.M5);
		finam.loadCandles(file, actual);
		assertEquals(df.parseDateTime("20141204100000"), actual.get(0).getStartTime());
		assertEquals(df.parseDateTime("20141204110000"), actual.get(1).getStartTime());
	}

}

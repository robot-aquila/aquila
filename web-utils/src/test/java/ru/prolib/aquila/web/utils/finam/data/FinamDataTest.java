package ru.prolib.aquila.web.utils.finam.data;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.utils.PriceScaleDBImpl;
import ru.prolib.aquila.data.storage.MDStorage;

public class FinamDataTest {
	private static final ZoneId MSK = ZoneId.of("Europe/Moscow");
	private static final File dataDir = new File("fixture2/data");
	private static final File cacheDir = new File("fixture2/cache");
	private static final Symbol symbol1 = new Symbol("SBRF-3.19");
	private static TFSymbol tfs_m5, tfs_h1, tfs_d1;
	private PriceScaleDBImpl psdb;
	private FinamData service;
	private MDStorage<TFSymbol, Candle> mds;
	
	static Instant ZT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(MSK).toInstant();
	}
	
	static List<Candle> FIX_M5MSK() {
		List<Candle> expected = new ArrayList<>();
		expected.add(newCandle(tfs_m5, "2018-11-16T20:30:00", 20200, 20246, 20200, 20207,  3));
		expected.add(newCandle(tfs_m5, "2018-11-16T21:20:00", 20284, 20308, 20284, 20308, 50));
		expected.add(newCandle(tfs_m5, "2018-11-16T21:35:00", 20277, 20277, 20276, 20276,  2));
		expected.add(newCandle(tfs_m5, "2018-11-16T21:40:00", 20311, 20311, 20311, 20311,  1));
		expected.add(newCandle(tfs_m5, "2018-11-16T22:20:00", 20300, 20300, 20300, 20300,  3));
		expected.add(newCandle(tfs_m5, "2018-11-16T22:35:00", 20320, 20320, 20320, 20320,  1));
		expected.add(newCandle(tfs_m5, "2018-11-16T23:25:00", 20348, 20348, 20348, 20348,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T10:00:00", 20425, 20425, 20375, 20375, 12));
		expected.add(newCandle(tfs_m5, "2018-11-19T10:05:00", 20450, 20450, 20450, 20450,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T10:10:00", 20458, 20458, 20458, 20458,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T10:15:00", 20428, 20428, 20426, 20426,  4));
		expected.add(newCandle(tfs_m5, "2018-11-19T10:45:00", 20343, 20343, 20343, 20343,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T10:50:00", 20350, 20350, 20350, 20350,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T11:15:00", 20384, 20384, 20384, 20384,  5));
		expected.add(newCandle(tfs_m5, "2018-11-19T11:20:00", 20330, 20330, 20330, 20330,  2));
		expected.add(newCandle(tfs_m5, "2018-11-19T11:35:00", 20400, 20400, 20400, 20400,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T11:50:00", 20432, 20432, 20432, 20432,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T12:45:00", 20331, 20331, 20330, 20330,  3));
		expected.add(newCandle(tfs_m5, "2018-11-19T12:50:00", 20357, 20357, 20357, 20357,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T12:55:00", 20367, 20367, 20367, 20367,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T13:30:00", 20449, 20449, 20449, 20449,  2));
		expected.add(newCandle(tfs_m5, "2018-11-19T13:35:00", 20500, 20528, 20500, 20528,  7));
		expected.add(newCandle(tfs_m5, "2018-11-19T13:40:00", 20569, 20585, 20550, 20550, 28));
		expected.add(newCandle(tfs_m5, "2018-11-19T13:55:00", 20504, 20504, 20504, 20504,  3));
		expected.add(newCandle(tfs_m5, "2018-11-19T14:10:00", 20503, 20503, 20503, 20503,  5));
		expected.add(newCandle(tfs_m5, "2018-11-19T14:20:00", 20480, 20480, 20480, 20480,  9));
		expected.add(newCandle(tfs_m5, "2018-11-19T14:40:00", 20434, 20434, 20434, 20434,  5));
		expected.add(newCandle(tfs_m5, "2018-11-19T15:40:00", 20530, 20530, 20530, 20530, 10));
		expected.add(newCandle(tfs_m5, "2018-11-19T15:45:00", 20546, 20546, 20546, 20546, 11));
		expected.add(newCandle(tfs_m5, "2018-11-19T16:35:00", 20520, 20520, 20493, 20493, 14));
		expected.add(newCandle(tfs_m5, "2018-11-19T16:50:00", 20447, 20447, 20447, 20447,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T17:05:00", 20448, 20448, 20448, 20448,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T17:35:00", 20425, 20425, 20425, 20425, 20));
		expected.add(newCandle(tfs_m5, "2018-11-19T18:15:00", 20342, 20342, 20342, 20342, 10));
		expected.add(newCandle(tfs_m5, "2018-11-19T18:20:00", 20303, 20303, 20268, 20277, 42));
		expected.add(newCandle(tfs_m5, "2018-11-19T18:30:00", 20321, 20321, 20321, 20321, 10));
		expected.add(newCandle(tfs_m5, "2018-11-19T19:35:00", 20340, 20340, 20340, 20340,  2));
		expected.add(newCandle(tfs_m5, "2018-11-19T19:45:00", 20322, 20322, 20322, 20322, 50));
		expected.add(newCandle(tfs_m5, "2018-11-19T20:10:00", 20342, 20342, 20342, 20342,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T22:00:00", 20349, 20349, 20349, 20349,  1));
		expected.add(newCandle(tfs_m5, "2018-11-19T22:15:00", 20306, 20306, 20306, 20306, 20));
		expected.add(newCandle(tfs_m5, "2018-11-19T23:25:00", 20408, 20413, 20408, 20413, 25));
		return expected;
	}
	
	static List<Candle> FIX_H1MSK() {
		List<Candle> expected = new ArrayList<>();
		expected.add(newCandle(tfs_h1, "2018-11-15T16:00:00", 20647, 20647, 20591, 20596, 46));
		expected.add(newCandle(tfs_h1, "2018-11-15T17:00:00", 20541, 20590, 20515, 20590, 15));
		expected.add(newCandle(tfs_h1, "2018-11-15T18:00:00", 20604, 20604, 20523, 20540,  6));
		expected.add(newCandle(tfs_h1, "2018-11-15T19:00:00", 20526, 20594, 20526, 20594,  6));
		expected.add(newCandle(tfs_h1, "2018-11-16T10:00:00", 20664, 20664, 20600, 20600, 30));
		expected.add(newCandle(tfs_h1, "2018-11-16T11:00:00", 20700, 20857, 20700, 20855, 23));
		expected.add(newCandle(tfs_h1, "2018-11-16T12:00:00", 20760, 20760, 20700, 20742, 24));
		expected.add(newCandle(tfs_h1, "2018-11-16T13:00:00", 20800, 20800, 20800, 20800,  2));
		expected.add(newCandle(tfs_h1, "2018-11-16T14:00:00", 20730, 20730, 20648, 20687, 24));
		expected.add(newCandle(tfs_h1, "2018-11-16T15:00:00", 20670, 20670, 20564, 20574, 33));
		expected.add(newCandle(tfs_h1, "2018-11-16T16:00:00", 20550, 20550, 20452, 20452,342));
		expected.add(newCandle(tfs_h1, "2018-11-16T17:00:00", 20510, 20510, 20400, 20400,  5));
		expected.add(newCandle(tfs_h1, "2018-11-16T19:00:00", 20350, 20397, 20164, 20246, 41));
		expected.add(newCandle(tfs_h1, "2018-11-16T20:00:00", 20200, 20246, 20200, 20207,  3));
		expected.add(newCandle(tfs_h1, "2018-11-16T21:00:00", 20284, 20311, 20276, 20311, 53));
		expected.add(newCandle(tfs_h1, "2018-11-16T22:00:00", 20300, 20320, 20300, 20320,  4));
		expected.add(newCandle(tfs_h1, "2018-11-16T23:00:00", 20348, 20348, 20348, 20348,  1));
		expected.add(newCandle(tfs_h1, "2018-11-19T10:00:00", 20425, 20458, 20343, 20350, 20));
		expected.add(newCandle(tfs_h1, "2018-11-19T11:00:00", 20384, 20432, 20330, 20432,  9));
		expected.add(newCandle(tfs_h1, "2018-11-19T12:00:00", 20331, 20367, 20330, 20367,  5));
		expected.add(newCandle(tfs_h1, "2018-11-19T13:00:00", 20449, 20585, 20449, 20504, 40));
		expected.add(newCandle(tfs_h1, "2018-11-19T14:00:00", 20503, 20503, 20434, 20434, 19));
		expected.add(newCandle(tfs_h1, "2018-11-19T15:00:00", 20530, 20546, 20530, 20546, 21));
		expected.add(newCandle(tfs_h1, "2018-11-19T16:00:00", 20520, 20520, 20447, 20447, 15));
		expected.add(newCandle(tfs_h1, "2018-11-19T17:00:00", 20448, 20448, 20425, 20425, 21));
		expected.add(newCandle(tfs_h1, "2018-11-19T18:00:00", 20342, 20342, 20268, 20321, 62));
		expected.add(newCandle(tfs_h1, "2018-11-19T19:00:00", 20340, 20340, 20322, 20322, 52));
		expected.add(newCandle(tfs_h1, "2018-11-19T20:00:00", 20342, 20342, 20342, 20342,  1));
		expected.add(newCandle(tfs_h1, "2018-11-19T22:00:00", 20349, 20349, 20306, 20306, 21));
		expected.add(newCandle(tfs_h1, "2018-11-19T23:00:00", 20408, 20413, 20408, 20413, 25));
		return expected;
	}
	
	static List<Candle> FIX_D1MSK() {
		List<Candle> expected = new ArrayList<>();
		expected.add(newCandle(tfs_d1, "2018-10-01T00:00:00", 21100, 21304, 20950, 21304, 325)); //  0
		expected.add(newCandle(tfs_d1, "2018-10-02T00:00:00", 21250, 21270, 20570, 20649, 269));
		expected.add(newCandle(tfs_d1, "2018-10-03T00:00:00", 20628, 20939, 20400, 20601, 281));
		expected.add(newCandle(tfs_d1, "2018-10-04T00:00:00", 20518, 20518, 19483, 19483, 258));
		expected.add(newCandle(tfs_d1, "2018-10-05T00:00:00", 19582, 19824, 19200, 19470, 236));
		expected.add(newCandle(tfs_d1, "2018-10-08T00:00:00", 19450, 20110, 18931, 20110, 405));
		expected.add(newCandle(tfs_d1, "2018-10-09T00:00:00", 20153, 20240, 19537, 19874,  58));
		expected.add(newCandle(tfs_d1, "2018-10-10T00:00:00", 19900, 20100, 19300, 19300,  58));
		expected.add(newCandle(tfs_d1, "2018-10-11T00:00:00", 19127, 19447, 18900, 19229, 295));
		expected.add(newCandle(tfs_d1, "2018-10-12T00:00:00", 19332, 19959, 19332, 19959, 115));
		expected.add(newCandle(tfs_d1, "2018-10-15T00:00:00", 19850, 19900, 19655, 19698,  57)); // 10
		expected.add(newCandle(tfs_d1, "2018-10-16T00:00:00", 19700, 20395, 19647, 20339,  41));
		expected.add(newCandle(tfs_d1, "2018-10-17T00:00:00", 20475, 20517, 19933, 20020, 104));
		expected.add(newCandle(tfs_d1, "2018-10-18T00:00:00", 19920, 20000, 19600, 19600, 100));
		expected.add(newCandle(tfs_d1, "2018-10-19T00:00:00", 19500, 19540, 18958, 18999, 195));
		expected.add(newCandle(tfs_d1, "2018-10-22T00:00:00", 19126, 19380, 18551, 18686, 398));
		expected.add(newCandle(tfs_d1, "2018-10-23T00:00:00", 18600, 18988, 18300, 18988, 248));
		expected.add(newCandle(tfs_d1, "2018-10-24T00:00:00", 18886, 19499, 18665, 19080, 293));
		expected.add(newCandle(tfs_d1, "2018-10-25T00:00:00", 19173, 19446, 18978, 19368, 592));
		expected.add(newCandle(tfs_d1, "2018-10-26T00:00:00", 19132, 19132, 18437, 18611, 545));
		expected.add(newCandle(tfs_d1, "2018-10-29T00:00:00", 18641, 19134, 18444, 18811, 538)); // 20
		expected.add(newCandle(tfs_d1, "2018-10-30T00:00:00", 19009, 19230, 18750, 19090, 240));
		expected.add(newCandle(tfs_d1, "2018-10-31T00:00:00", 19200, 19691, 19050, 19306, 281));
		expected.add(newCandle(tfs_d1, "2018-11-01T00:00:00", 19422, 19643, 19230, 19504, 309));
		expected.add(newCandle(tfs_d1, "2018-11-02T00:00:00", 19583, 19857, 19282, 19759, 350));
		expected.add(newCandle(tfs_d1, "2018-11-06T00:00:00", 20000, 20450, 20000, 20244, 544));
		expected.add(newCandle(tfs_d1, "2018-11-07T00:00:00", 20178, 20800, 20080, 20719, 405));
		expected.add(newCandle(tfs_d1, "2018-11-08T00:00:00", 20744, 20969, 20466, 20466, 689));
		expected.add(newCandle(tfs_d1, "2018-11-09T00:00:00", 20524, 20634, 19991, 20300, 626));
		expected.add(newCandle(tfs_d1, "2018-11-12T00:00:00", 20345, 20722, 20050, 20050,2023));
		expected.add(newCandle(tfs_d1, "2018-11-13T00:00:00", 20084, 20500, 19932, 20061, 861)); // 30
		expected.add(newCandle(tfs_d1, "2018-11-14T00:00:00", 20062, 20645, 19895, 20645, 425));
		expected.add(newCandle(tfs_d1, "2018-11-15T00:00:00", 20592, 20764, 20500, 20594, 718));
		expected.add(newCandle(tfs_d1, "2018-11-16T00:00:00", 20664, 20857, 20164, 20348, 585));
		expected.add(newCandle(tfs_d1, "2018-11-19T00:00:00", 20425, 20585, 20268, 20413, 311));
		expected.add(newCandle(tfs_d1, "2018-11-20T00:00:00", 20222, 20250, 19700, 19779, 876));
		return expected;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		tfs_m5 = new TFSymbol(symbol1, ZTFrame.M5MSK);
		tfs_h1 = new TFSymbol(symbol1, ZTFrame.H1MSK);
		tfs_d1 = new TFSymbol(symbol1, ZTFrame.D1MSK);
	}
	
	public static Candle newCandle(TFSymbol tfs,
			String timeString,
			long open,
			long high,
			long low,
			long close,
			long vol)
	{
		return new CandleBuilder()
			.withTimeFrame(tfs.getTimeFrame())
			.withTime(ZT(timeString))
			.withOpenPrice(open)
			.withHighPrice(high)
			.withLowPrice(low)
			.withClosePrice(close)
			.withVolume(vol)
			.buildCandle();
	}

	@Before
	public void setUp() throws Exception {
		psdb = new PriceScaleDBImpl();
		psdb.setScale(symbol1, 0);
		service = new FinamData();
		mds = service.createCachingOHLCV(dataDir, cacheDir, psdb);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(cacheDir);
	}
	
	@Test
	public void testMDStorage_CachingOHLCV_M5() throws Exception {
		Instant timeTo = ZT("2018-11-20T10:00:00"); // shouldn't be included
		
		List<Candle> actual = new ArrayList<>();
		try ( CloseableIterator<Candle> it = mds.createReader(tfs_m5, 42, timeTo) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}
		
		assertEquals(FIX_M5MSK(), actual);
	}

	@Test
	public void testMDStorage_CachingOHLCV_H1() throws Exception {
		Instant timeTo = ZT("2018-11-20T10:00:00"); // shouldn't be included
		
		List<Candle> actual = new ArrayList<>();
		try ( CloseableIterator<Candle> it = mds.createReader(tfs_h1, 30, timeTo) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}
		
		assertEquals(FIX_H1MSK(), actual);
	}
	
	@Test
	public void testMDStorage_CachingOHLCV_D1() throws Exception {
		Instant timeTo = ZT("2018-11-20T00:00:00"); // shouldn't be included
		
		List<Candle> actual = new ArrayList<>();
		try ( CloseableIterator<Candle> it = mds.createReader(tfs_d1, 35, timeTo) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}

		List<Candle> expected = new ArrayList<>(FIX_D1MSK().subList(0, 35));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMDStorage_CachingOHLCV_D1_CreateReaderFrom() throws Exception {
		Instant timeFrom = ZT("2018-10-12T15:48:29"); // should be aligned
		
		List<Candle> actual = new ArrayList<>();
		try ( CloseableIterator<Candle> it = mds.createReaderFrom(tfs_d1, timeFrom) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}
		
		List<Candle> expected = new ArrayList<>(FIX_D1MSK().subList(9, 36));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMDStorage_CachingOHLCV_D1_CreateReader_KTI() throws Exception {
		Instant timeFrom = ZT("2018-10-12T15:48:29"); // should be aligned

		List<Candle> actual = new ArrayList<>();
		try ( CloseableIterator<Candle> it = mds.createReader(tfs_d1, timeFrom, 10) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}

		List<Candle> expected = new ArrayList<>(FIX_D1MSK().subList(9, 19));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMDStorage_CachingOHLCV_D1_CreateReader_KTT() throws Exception {
		Instant timeFrom = ZT("2018-10-12T15:48:29"); // should be aligned
		Instant timeTo = ZT("2018-11-01T07:59:27.902"); // should be aligned and excluded
		
		List<Candle> actual = new ArrayList<>();
		try ( CloseableIterator<Candle> it = mds.createReader(tfs_d1, timeFrom, timeTo) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}
		
		List<Candle> expected = new ArrayList<>(FIX_D1MSK().subList(9, 23));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMDStorage_CachingOHLCV_D1_CreateReader_KIT() throws Exception {
		testMDStorage_CachingOHLCV_D1();
	}
	
	@Test
	public void testMDStorage_CachingOHLCV_D1_CreateReaderTo() throws Exception {
		Instant timeTo = ZT("2018-11-01T07:59:27.902"); // should be aligned and excluded
		
		List<Candle> actual = new ArrayList<>();
		try ( CloseableIterator<Candle> it = mds.createReaderTo(tfs_d1, timeTo) ) {
			while ( it.next() ) {
				actual.add(it.item());
			}
		}
		
		List<Candle> expected = new ArrayList<>(FIX_D1MSK().subList(0, 23));
		assertEquals(expected, actual);
	}

}

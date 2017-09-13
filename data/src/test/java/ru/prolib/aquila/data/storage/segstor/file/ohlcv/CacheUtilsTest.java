package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaDataImpl;

public class CacheUtilsTest {
	private static final File temporary, committed;
	
	static {
		temporary = new File("fixture/temp/test.file.temp");
		committed = new File("fixture/temp/test.file");
	}
	
	Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private CacheUtils utils;

	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(new File("fixture/temp"));
		utils = new CacheUtils();
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(new File("fixture/temp"));
	}
	
	@Test
	public void testGetInstance() {
		CacheUtils instance = CacheUtils.getInstance();
		assertNotNull(instance);
		assertSame(instance, CacheUtils.getInstance());
	}
	
	@Test
	public void testCreateWriter() throws Exception {
		CacheSegmentWriter writer = (CacheSegmentWriter) utils.createWriter(committed);
		
		assertNotNull(writer);
		assertEquals(temporary, writer.getTemporary());
		assertEquals(committed, writer.getCommitted());
		writer.close();
	}
	
	@Test
	public void testCreateReader() throws Exception {
		FileUtils.writeStringToFile(committed, "zulu\ncharlie\ngamma");
		
		BufferedReader reader = utils.createReader(committed);
		assertEquals("zulu", reader.readLine());
		assertEquals("charlie", reader.readLine());
		assertEquals("gamma", reader.readLine());
		assertNull(reader.readLine());
		reader.close();
	}
	
	@Test
	public void testReadHeader_1F() throws Exception {
		FileUtils.writeStringToFile(committed, "OHLCVv1\n"
				+ "2\n"
				+ "10\n"
				+ "1xxxxxx foo/path.dat\n"
				+ "2xxxxxx bar/path.dat\n");
		
		CacheHeader actual = utils.readHeader(committed);
		
		CacheHeader expected = new CacheHeaderImpl()
				.setNumberOfElements(10L)
				.addSourceDescriptor("1xxxxxx foo/path.dat")
				.addSourceDescriptor("2xxxxxx bar/path.dat");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadHeader_1R() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("OHLCVv1\n"
				+ "2\n"
				+ "10\n"
				+ "1xxxxxx foo/path.dat\n"
				+ "2xxxxxx bar/path.dat\n"));
		
		CacheHeader actual = utils.readHeader(reader);
		
		reader.close();
		CacheHeader expected = new CacheHeaderImpl()
				.setNumberOfElements(10L)
				.addSourceDescriptor("1xxxxxx foo/path.dat")
				.addSourceDescriptor("2xxxxxx bar/path.dat");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadHeader_1R_IfFormatIdNotFound() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader(""));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: format ID not found", e.getMessage());
		}
	}
	
	@Test
	public void testReadHeader_1R_IfUnknownVersion() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("version_id\n"
				+ "2\n"
				+ "10\n"
				+ "1xxxxxx foo/path.dat\n"
				+ "2xxxxxx bar/path.dat\n"));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: unknown version: version_id", e.getMessage());
		}
	}
	
	@Test
	public void testReadHeader_1R_IfNumberOfDescriptorsNotFound() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("OHLCVv1"));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: number of descriptors not found", e.getMessage());
		}
	}
	
	@Test
	public void testReadHeader_1R_IfNumbefOfDescriptorsNotANumber() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("OHLCVv1\nfoobar"));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: number of descriptors format error: foobar", e.getMessage());
		}
	}
	
	@Test
	public void testReadHeader_1R_IfNumberOfElementsNotFound() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("OHLCVv1\n2"));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: number of elements not found", e.getMessage());
		}
	}
	
	@Test
	public void testReadHeder_1R_IfNumberOfElementsNotANumber() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("OHLCVv1\n2\nzulu25"));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: number of elements format error: zulu25", e.getMessage());
		}
	}
	
	@Test
	public void testReadHeader_1R_IfNumberOfSourceDescriptorsMismatch() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("OHLCVv1\n"
				+ "2\n"
				+ "10\n"
				+ "2xxxxxx bar/path.dat\n"));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: number of source descriptors mismatch: 2", e.getMessage());
		}
	}
	
	@Test
	public void testReadHeader_1R_IfIncorrectSourceDescriptors() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader("OHLCVv1\n"
				+ "2\n"
				+ "10\n"
				+ "zulumba_balbena\n"
				+ "2xxxxxx bar/path.dat\n"));

		try {
			utils.readHeader(reader);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Unknown file format: incorrect source descriptor: 0: zulumba_balbena", e.getMessage());
		}
	}

	@Test (expected=FileNotFoundException.class)
	public void testGetMetaData_ThrowsIfFileNotFound() throws Exception {
		utils.getMetaData(new File("foo/bar"), 100L);
	}
	
	@Test
	public void testGetMetaData() throws Exception {
		FileUtils.writeStringToFile(temporary, "1234567890");
		temporary.setLastModified(T("1996-01-01T00:00:00Z").toEpochMilli());
		
		SegmentMetaData actual = utils.getMetaData(temporary, 800L);
		
		SegmentMetaData expected = new SegmentMetaDataImpl()
				.setPath(new File("fixture/temp/test.file.temp").getPath())
				.setUpdateTime(T("1996-01-01T00:00:00Z"))
				.setNumberOfElements(800L)
				.setHashCode(DigestUtils.md2Hex("1996-01-01T00:00:00Z_10")); // upd.time + size
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWriteHeader() throws Exception {
		String LS = System.lineSeparator();
		StringWriter writer = new StringWriter();
		
		utils.writeHeader(writer, new CacheHeaderImpl()
				.setNumberOfElements(100L)
				.addSourceDescriptor("xxxxxxx1 foo/path.dat")
				.addSourceDescriptor("xxxxxxx2 bar/path.dat"));
		
		String expected = "OHLCVv1" + LS
				+ 2 + LS
				+ 100 + LS
				+ "xxxxxxx1 foo/path.dat" + LS
				+ "xxxxxxx2 bar/path.dat" + LS;
		assertEquals(expected, writer.toString());
	}
	
	@Test
	public void testWriteSeries() throws Exception {
		String LS = System.lineSeparator();
		Interval interval1 = Interval.of(T("2017-09-12T13:12:00Z"), Duration.ofMinutes(1)),
				interval2 = Interval.of(T("2017-09-12T13:13:00Z"), Duration.ofMinutes(1)),
				interval3 = Interval.of(T("2017-09-12T13:15:00Z"), Duration.ofMinutes(1)),
				interval4 = Interval.of(T("2017-09-12T13:17:00Z"), Duration.ofMinutes(1));
		TSeriesImpl<Candle> series = new TSeriesImpl<Candle>(TimeFrame.M1);
		series.set(interval1.getStart(), new Candle(interval1, 100d, 102d,  98d,  99d, 1000L));
		series.set(interval2.getStart(), new Candle(interval2,  99d, 105d,  99d, 105d, 2000L));
		series.set(interval3.getStart(), new Candle(interval3, 105d, 107d, 101d, 101d, 3000L));
		series.set(interval4.getStart(), new Candle(interval4, 101d, 112d, 100d, 108d, 4000L));
		StringWriter writer = new StringWriter();
		
		utils.writeSeries(writer, series);
		
		String expected = "2017-09-12T13:12:00Z,100.0,102.0,98.0,99.0,1000" + LS
				+ "2017-09-12T13:13:00Z,99.0,105.0,99.0,105.0,2000" + LS
				+ "2017-09-12T13:15:00Z,105.0,107.0,101.0,101.0,3000" + LS
				+ "2017-09-12T13:17:00Z,101.0,112.0,100.0,108.0,4000" + LS;
		assertEquals(expected, writer.toString());
	}
	
	@Test
	public void testCreateIterator_1S() throws Exception {
		Interval interval1 = Interval.of(T("2017-09-12T13:12:00Z"), Duration.ofMinutes(1)),
				interval2 = Interval.of(T("2017-09-12T13:13:00Z"), Duration.ofMinutes(1)),
				interval3 = Interval.of(T("2017-09-12T13:15:00Z"), Duration.ofMinutes(1)),
				interval4 = Interval.of(T("2017-09-12T13:17:00Z"), Duration.ofMinutes(1));
		TSeriesImpl<Candle> series = new TSeriesImpl<Candle>(TimeFrame.M1);
		series.set(interval1.getStart(), new Candle(interval1, 100d, 102d,  98d,  99d, 1000L));
		series.set(interval2.getStart(), new Candle(interval2,  99d, 105d,  99d, 105d, 2000L));
		series.set(interval3.getStart(), new Candle(interval3, 105d, 107d, 101d, 101d, 3000L));
		series.set(interval4.getStart(), new Candle(interval4, 101d, 112d, 100d, 108d, 4000L));
		
		CloseableIterator<Candle> iterator = utils.createIterator(series);
		
		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		expected.add(series.get(0));
		expected.add(series.get(1));
		expected.add(series.get(2));
		expected.add(series.get(3));
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateIterator_1R() throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader(
				  "2017-09-12T13:12:00Z,100.0,102.0,98.0,99.0,1000\n"
				+ "2017-09-12T13:13:00Z,99.0,105.0,99.0,105.0,2000\n"
				+ "2017-09-12T13:15:00Z,105.0,107.0,101.0,101.0,3000\n"
				+ "2017-09-12T13:17:00Z,101.0,112.0,100.0,108.0,4000\n"));
		
		CloseableIterator<Candle> iterator = utils.createIterator(reader, TimeFrame.M1);

		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		Interval interval1 = Interval.of(T("2017-09-12T13:12:00Z"), Duration.ofMinutes(1)),
				interval2 = Interval.of(T("2017-09-12T13:13:00Z"), Duration.ofMinutes(1)),
				interval3 = Interval.of(T("2017-09-12T13:15:00Z"), Duration.ofMinutes(1)),
				interval4 = Interval.of(T("2017-09-12T13:17:00Z"), Duration.ofMinutes(1));
		expected.add(new Candle(interval1, 100d, 102d,  98d,  99d, 1000L));
		expected.add(new Candle(interval2,  99d, 105d,  99d, 105d, 2000L));
		expected.add(new Candle(interval3, 105d, 107d, 101d, 101d, 3000L));
		expected.add(new Candle(interval4, 101d, 112d, 100d, 108d, 4000L));
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParseOHLCVv1() throws Exception {
		Interval interval1 = Interval.of(T("2015-05-12T13:45:00Z"), Duration.ofMinutes(1));
		Candle expected = new Candle(interval1, 100.0d, 102.14d, 95.01d, 101.15d, 1500L);
		
		String line = "2015-05-12T13:45:24Z,100.0,102.14,95.01,101.15,1500";
		Candle actual = utils.parseOHLCVv1(line, TimeFrame.M1);

		assertEquals(expected, actual);
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfNumberOfTokensMismatch() throws Exception {
		String line = "foo,bar";
		
		try {
			utils.parseOHLCVv1(line, TimeFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Number of fields mismatch: expected 6 but 2", e.getMessage());
		}
	}

	@Test
	public void testParseOHLCVv1_ThrowsIfBadTimeFormat() throws Exception {
		String line = "foobar,100.0,102.14,95.01,101.15,1500";

		try {
			utils.parseOHLCVv1(line, TimeFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad time format: foobar", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadOpenPriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,foo,102.14,95.01,101.15,1500";
		
		try {
			utils.parseOHLCVv1(line, TimeFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad open price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadHighPriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,foo,95.01,101.15,1500";
		
		try {
			utils.parseOHLCVv1(line, TimeFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad high price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadLowPriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,102.14,foo,101.15,1500";
		
		try {
			utils.parseOHLCVv1(line, TimeFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad low price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadClosePriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,102.14,95.01,foo,1500";
		
		try {
			utils.parseOHLCVv1(line, TimeFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad close price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadVolumeFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,102.14,95.01,101.15,foo";
		
		try {
			utils.parseOHLCVv1(line, TimeFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad volume format: foo", e.getMessage());
		}
	}

	@Test
	public void testBuildUsingSourceData() throws Exception {
		L1UpdateBuilder builder = new L1UpdateBuilder(new Symbol("XXL")).withTrade();
		List<L1Update> fixture = new ArrayList<>();
		fixture.add(builder.withTime("2017-09-12T16:25:00Z").withPrice(105).withSize(100).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:25:45Z").withPrice(102).withSize(200).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:26:05Z").withPrice(103).withSize(110).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:31:19Z").withPrice(108).withSize(205).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:34:50Z").withPrice(110).withSize(400).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:34:59Z").withPrice(105).withSize(100).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:42:19Z").withPrice(102).withSize(520).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:43:00Z").withPrice(103).withSize(200).buildL1Update());
		fixture.add(builder.withTime("2017-09-12T16:44:30Z").withPrice(101).withSize(100).buildL1Update());
		CloseableIterator<L1Update> source = new CloseableIteratorStub<>(fixture);
		
		EditableTSeries<Candle> series = utils.buildUsingSourceData(source, TimeFrame.M5);
		
		assertNotNull(series);
		Duration d = Duration.ofMinutes(5);
		List<Candle> expected = new ArrayList<>();
		expected.add(new Candle(Interval.of(T("2017-09-12T16:25:00Z"), d), 105, 105, 102, 103, 410));
		expected.add(new Candle(Interval.of(T("2017-09-12T16:30:00Z"), d), 108, 110, 105, 105, 705));
		expected.add(new Candle(Interval.of(T("2017-09-12T16:40:00Z"), d), 102, 103, 101, 101, 820));
		assertEquals(expected.size(), series.getLength());
		for ( int i = 0; i < expected.size(); i ++ ) {
			assertEquals("At#" + i, expected.get(i), series.get(i));
		}
	}

}

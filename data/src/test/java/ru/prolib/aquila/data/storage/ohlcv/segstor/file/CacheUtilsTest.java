package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesL1UpdateAggregator;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheHeader;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheHeaderImpl;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheSegmentWriter;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheUtils;
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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
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
		File temporary = new File("fixture/temp/foo/bar/test.file.temp");
		File committed = new File("fixture/temp/foo/bar/test.file");
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
		Candle candle1 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:12:00Z")
				.withOpenPrice(100L)
				.withHighPrice(102L)
				.withLowPrice(98L)
				.withClosePrice(99L)
				.withVolume(1000L)
				.buildCandle(),
			candle2 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:13:00Z")
				.withOpenPrice(99L)
				.withHighPrice(105L)
				.withLowPrice(99L)
				.withClosePrice(105L)
				.withVolume(2000L)
				.buildCandle(),
			candle3 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:15:00Z")
				.withOpenPrice(105L)
				.withHighPrice(107L)
				.withLowPrice(101L)
				.withClosePrice(101L)
				.withVolume(3000L)
				.buildCandle(),
			candle4 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:17:00Z")
				.withOpenPrice("101.548")
				.withHighPrice(112L)
				.withLowPrice(100L)
				.withClosePrice(108L)
				.withVolume(4000L)
				.buildCandle();
		TSeriesImpl<Candle> series = new TSeriesImpl<Candle>(ZTFrame.M1);
		series.set(candle1.getStartTime(), candle1);
		series.set(candle2.getStartTime(), candle2);
		series.set(candle3.getStartTime(), candle3);
		series.set(candle4.getStartTime(), candle4);
		StringWriter writer = new StringWriter();
		
		utils.writeSeries(writer, series);
		
		String expected = "2017-09-12T13:12:00Z,100,102,98,99,1000" + LS
				+ "2017-09-12T13:13:00Z,99,105,99,105,2000" + LS
				+ "2017-09-12T13:15:00Z,105,107,101,101,3000" + LS
				+ "2017-09-12T13:17:00Z,101.548,112,100,108,4000" + LS;
		assertEquals(expected, writer.toString());
	}
	
	@Test
	public void testCreateIterator_1S() throws Exception {
		Candle candle1 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:12:00Z")
				.withOpenPrice(100L)
				.withHighPrice(102L)
				.withLowPrice(98L)
				.withClosePrice(99L)
				.withVolume(1000L)
				.buildCandle(),
			candle2 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:13:00Z")
				.withOpenPrice(99L)
				.withHighPrice(105L)
				.withLowPrice(99L)
				.withClosePrice(105L)
				.withVolume(2000L)
				.buildCandle(),
			candle3 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:15:00Z")
				.withOpenPrice(105L)
				.withHighPrice(107L)
				.withLowPrice(101L)
				.withClosePrice(101L)
				.withVolume(3000L)
				.buildCandle(),
			candle4 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:17:00Z")
				.withOpenPrice(101L)
				.withHighPrice(112L)
				.withLowPrice(100L)
				.withClosePrice(108L)
				.withVolume(4000L)
				.buildCandle();
		TSeriesImpl<Candle> series = new TSeriesImpl<Candle>(ZTFrame.M1);
		series.set(candle1.getStartTime(), candle1);
		series.set(candle2.getStartTime(), candle2);
		series.set(candle3.getStartTime(), candle3);
		series.set(candle4.getStartTime(), candle4);
		
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
				  "2017-09-12T13:12:00Z,100,102,98,99,1000\n"
				+ "2017-09-12T13:13:00Z,99,105,99,105,2000\n"
				+ "2017-09-12T13:15:00Z,105,107,101,101,3000\n"
				+ "2017-09-12T13:17:00Z,101.12,112.4597,100,108,4000\n"));
		
		CloseableIterator<Candle> iterator = utils.createIterator(reader, ZTFrame.M1);

		List<Candle> actual = new ArrayList<>(), expected = new ArrayList<>();
		Candle candle1 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:12:00Z")
				.withOpenPrice(100L)
				.withHighPrice(102L)
				.withLowPrice(98L)
				.withClosePrice(99L)
				.withVolume(1000L)
				.buildCandle(),
			candle2 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:13:00Z")
				.withOpenPrice(99L)
				.withHighPrice(105L)
				.withLowPrice(99L)
				.withClosePrice(105L)
				.withVolume(2000L)
				.buildCandle(),
			candle3 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:15:00Z")
				.withOpenPrice(105L)
				.withHighPrice(107L)
				.withLowPrice(101L)
				.withClosePrice(101L)
				.withVolume(3000L)
				.buildCandle(),
			candle4 = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2017-09-12T13:17:00Z")
				.withOpenPrice("101.12")
				.withHighPrice("112.4597")
				.withLowPrice(100L)
				.withClosePrice(108L)
				.withVolume(4000L)
				.buildCandle();
		expected.add(candle1);
		expected.add(candle2);
		expected.add(candle3);
		expected.add(candle4);
		while ( iterator.next() ) {
			actual.add(iterator.item());
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void testParseOHLCVv1() throws Exception {
		Candle expected = new CandleBuilder()
				.withTimeFrame(ZTFrame.M1)
				.withTime("2015-05-12T13:45:00Z")
				.withOpenPrice("100.05")
				.withHighPrice("102.14")
				.withLowPrice("95.01")
				.withClosePrice("101.15")
				.withVolume(1500L)
				.buildCandle();
		String line = "2015-05-12T13:45:24Z,100.05,102.14,95.01,101.15,1500";
		Candle actual = utils.parseOHLCVv1(line, ZTFrame.M1);

		assertEquals(expected, actual);
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfNumberOfTokensMismatch() throws Exception {
		String line = "foo,bar";
		
		try {
			utils.parseOHLCVv1(line, ZTFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Number of fields mismatch: expected 6 but 2", e.getMessage());
		}
	}

	@Test
	public void testParseOHLCVv1_ThrowsIfBadTimeFormat() throws Exception {
		String line = "foobar,100.0,102.14,95.01,101.15,1500";

		try {
			utils.parseOHLCVv1(line, ZTFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad time format: foobar", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadOpenPriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,foo,102.14,95.01,101.15,1500";
		
		try {
			utils.parseOHLCVv1(line, ZTFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad open price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadHighPriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,foo,95.01,101.15,1500";
		
		try {
			utils.parseOHLCVv1(line, ZTFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad high price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadLowPriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,102.14,foo,101.15,1500";
		
		try {
			utils.parseOHLCVv1(line, ZTFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad low price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadClosePriceFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,102.14,95.01,foo,1500";
		
		try {
			utils.parseOHLCVv1(line, ZTFrame.M1);
			fail("Expected exception: " + IOException.class.getSimpleName());
		} catch ( IOException e ) {
			assertEquals("Bad close price format: foo", e.getMessage());
		}
	}
	
	@Test
	public void testParseOHLCVv1_ThrowsIfBadVolumeFormat() throws Exception {
		String line = "2015-05-12T13:45:24Z,100.0,102.14,95.01,101.15,foo";
		
		try {
			utils.parseOHLCVv1(line, ZTFrame.M1);
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
		
		EditableTSeries<Candle> series = utils.buildUsingSourceData(source, ZTFrame.M5,
				CandleSeriesL1UpdateAggregator.getInstance());
		
		assertNotNull(series);
		List<Candle> expected = new ArrayList<>();
		expected.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-09-12T16:25:00Z")
				.withOpenPrice(105L)
				.withHighPrice(105L)
				.withLowPrice(102L)
				.withClosePrice(103L)
				.withVolume(410L)
				.buildCandle());
		expected.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-09-12T16:30:00Z")
				.withOpenPrice(108L)
				.withHighPrice(110L)
				.withLowPrice(105L)
				.withClosePrice(105L)
				.withVolume(705L)
				.buildCandle());
		expected.add(new CandleBuilder()
				.withTimeFrame(ZTFrame.M5)
				.withTime("2017-09-12T16:40:00Z")
				.withOpenPrice(102L)
				.withHighPrice(103L)
				.withLowPrice(101L)
				.withClosePrice(101L)
				.withVolume(820L)
				.buildCandle());
		assertEquals(expected.size(), series.getLength());
		for ( int i = 0; i < expected.size(); i ++ ) {
			assertEquals("At#" + i, expected.get(i), series.get(i));
		}
	}

}

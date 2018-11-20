package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.timeframe.TFDays;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesAggregator;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.D1CacheOverCandleSDSS.*;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaDataImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.YearPoint;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

@SuppressWarnings("unchecked")
public class D1CacheOverCandleSDSSTest {
	private static Symbol symbol1, symbol2, symbol3;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	static <T> List<T> toList(T... args) {
		List<T> result = new ArrayList<>();
		for ( T x : args ) {
			result.add(x);
		}
		return result;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("ZXL");
		symbol2 = new Symbol("ZXV");
		symbol3 = new Symbol("XPP");
	}
	
	private IMocksControl control;
	private SymbolDailySegmentStorage<Candle> sdssMock;
	private CandleSeriesAggregator<Candle> aggrMock;
	private SegmentFileManager cmMock;
	private CacheUtils cuMock;
	private TestHelper helperMock, helper;
	private D1CacheOverCandleSDSS service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sdssMock = control.createMock(SymbolDailySegmentStorage.class);
		aggrMock = control.createMock(CandleSeriesAggregator.class);
		cmMock = control.createMock(SegmentFileManager.class);
		cuMock = control.createMock(CacheUtils.class);
		helperMock = control.createMock(TestHelper.class);
		service = new D1CacheOverCandleSDSS(ZTFrame.D1, sdssMock, cuMock, helperMock);
		helper = new TestHelper(ZTFrame.D1, sdssMock, aggrMock, cmMock, cuMock);
	}

	@Test
	public void testActualSegmentInfo_Ctor() {
		SegmentFileInfo sfiMock = control.createMock(SegmentFileInfo.class);
		List<SymbolDaily> sdl = new ArrayList<>();
		sdl.add(new SymbolDaily(symbol1, 1997, 12, 1));
		sdl.add(new SymbolDaily(symbol1, 1997, 12, 8));
		List<SegmentMetaData> smdl = new ArrayList<>();
		smdl.add(control.createMock(SegmentMetaData.class));
		smdl.add(control.createMock(SegmentMetaData.class));
		
		ActualSegmentInfo asi = new ActualSegmentInfo(sfiMock, sdl, smdl);
		
		assertEquals(sfiMock, asi.getTargetFileInfo());
		assertEquals(sdl, asi.getSourceSegmentList());
		assertEquals(smdl, asi.getSourceMetaDataList());
	}
	
	@Test
	public void testActualSegmentInfo_GetFullPath() {
		SegmentFileInfo sfiMock = control.createMock(SegmentFileInfo.class);
		File pathMock = control.createMock(File.class);
		expect(sfiMock.getFullPath()).andReturn(pathMock);
		control.replay();
		ActualSegmentInfo asi = new ActualSegmentInfo(sfiMock, null, null);
		
		assertEquals(pathMock, asi.getFullPath());
		
		control.verify();
	}
	
	@Test
	public void testActualSegmentInfo_ToHeader() {
		List<SegmentMetaData> smdl = new ArrayList<>();
		smdl.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg1.bin")
				.setHashCode("712hafd6676761")
				.setUpdateTime(T("2018-11-18T03:40:22Z"))
				.setNumberOfElements(50));
		smdl.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg2.bin")
				.setHashCode("88671788822991")
				.setUpdateTime(T("2018-11-18T01:12:47Z"))
				.setNumberOfElements(10));
		ActualSegmentInfo asi = new ActualSegmentInfo(null, null, smdl);
		
		CacheHeader actual = asi.toHeader(227);
		
		CacheHeader expected = new CacheHeaderImpl()
			.addSourceDescriptor("712hafd6676761", "/foo/bar/seg1.bin")
			.addSourceDescriptor("88671788822991", "/foo/bar/seg2.bin")
			.setNumberOfElements(227);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSegmentData_Ctor() {
		CacheHeader hdrMock = control.createMock(CacheHeader.class);
		EditableTSeries<Candle> ohlcMock = control.createMock(EditableTSeries.class);
		File path = new File("zilda/bar");
		
		SegmentData sd = new SegmentData(hdrMock, ohlcMock, path);
		
		assertSame(hdrMock, sd.getHeader());
		assertSame(ohlcMock, sd.getCandles());
		assertSame(path, sd.getFullPath());
	}
	
	@Test
	public void testSegmentData_GetNumberOfElements() {
		CacheHeader hdrMock = control.createMock(CacheHeader.class);
		expect(hdrMock.getNumberOfElements()).andReturn(815L);
		control.replay();
		SegmentData sd = new SegmentData(hdrMock, null, null);
		
		assertEquals(815L, sd.getNumberOfElements());
		
		control.verify();
	}
	
	@Test
	public void testTestHelper_Ctor() {
		assertEquals(ZTFrame.D1, helper.getTFrame());
		assertSame(sdssMock, helper.getSDSS());
		assertSame(aggrMock, helper.getAggregator());
		assertSame(cmMock, helper.getCacheManager());
		assertSame(cuMock, helper.getCacheUtils());
	}
	
	@Test
	public void testTestHelper_LoadCurrentInfo() throws Exception {
		SegmentFileInfo sfiMock = control.createMock(SegmentFileInfo.class);
		List<SymbolDaily> sds = new ArrayList<>();
		sds.add(new SymbolDaily(symbol2, 2018, 1, 13));
		sds.add(new SymbolDaily(symbol2, 2018, 1, 18));
		sds.add(new SymbolDaily(symbol2, 2018, 1, 20));
		SegmentMetaData smdMock1 = control.createMock(SegmentMetaData.class);
		SegmentMetaData smdMock2 = control.createMock(SegmentMetaData.class);
		SegmentMetaData smdMock3 = control.createMock(SegmentMetaData.class);
		expect(cmMock.getFileInfo(new SymbolMonthly(symbol2, 2018, 1), "-OHLCV-D1.cache")).andReturn(sfiMock);
		expect(sdssMock.listDailySegments(symbol2, new MonthPoint(2018, Month.JANUARY))).andReturn(sds);
		expect(sdssMock.getMetaData(new SymbolDaily(symbol2, 2018, 1, 13))).andReturn(smdMock1);
		expect(sdssMock.getMetaData(new SymbolDaily(symbol2, 2018, 1, 18))).andReturn(smdMock2);
		expect(sdssMock.getMetaData(new SymbolDaily(symbol2, 2018, 1, 20))).andReturn(smdMock3);
		control.replay();
		
		ActualSegmentInfo actual = helper.loadCurrentInfo(new SymbolMonthly(symbol2, 2018, 1));
		
		control.verify();
		assertNotNull(actual);
		assertSame(sfiMock, actual.getTargetFileInfo());
		assertEquals(sds, actual.getSourceSegmentList());
		List<SegmentMetaData> expectedMDs = new ArrayList<>();
		expectedMDs.add(smdMock1);
		expectedMDs.add(smdMock2);
		expectedMDs.add(smdMock3);
		assertEquals(expectedMDs, actual.getSourceMetaDataList());
	}
	
	@Test
	public void testTestHelper_SameSources_NumberOfSegmentsMismatch() throws Exception {
		List<SegmentMetaData> smds = new ArrayList<>();
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg1.bin")
				.setHashCode("766123130092")
				.setNumberOfElements(10L)
				.setUpdateTime(T("1992-04-12T17:28:19Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg2.bin")
				.setHashCode("826103947712")
				.setNumberOfElements(20L)
				.setUpdateTime(T("1995-12-20T02:15:26Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg3.bin")
				.setHashCode("178268399123")
				.setNumberOfElements(30L)
				.setUpdateTime(T("1997-08-22T19:30:48Z")));
		ActualSegmentInfo asi = new ActualSegmentInfo(null, null, smds);
		CacheHeader header = new CacheHeaderImpl()
				.setNumberOfElements(315L)
				.addSourceDescriptor("766123130092", "/foo/bar/seg1.bin")
				.addSourceDescriptor("826103947712", "/foo/bar/seg2.bin");
		
		assertFalse(helper.sameSources(asi, header));
	}

	@Test
	public void testTestHelper_SameSources_HashCodeMismatch() throws Exception {
		List<SegmentMetaData> smds = new ArrayList<>();
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg1.bin")
				.setHashCode("766123130092")
				.setNumberOfElements(10L)
				.setUpdateTime(T("1992-04-12T17:28:19Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg2.bin")
				.setHashCode("826103947712")
				.setNumberOfElements(20L)
				.setUpdateTime(T("1995-12-20T02:15:26Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg3.bin")
				.setHashCode("178268399123")
				.setNumberOfElements(30L)
				.setUpdateTime(T("1997-08-22T19:30:48Z")));
		ActualSegmentInfo asi = new ActualSegmentInfo(null, null, smds);
		CacheHeader header = new CacheHeaderImpl()
				.setNumberOfElements(315L)
				.addSourceDescriptor("766123130092", "/foo/bar/seg1.bin")
				.addSourceDescriptor("xxxxxxxxxxxx", "/foo/bar/seg2.bin")
				.addSourceDescriptor("178268399123", "/foo/bar/seg3.bin");

		assertFalse(helper.sameSources(asi, header));
	}

	@Test
	public void testTestHelper_SameSources_PathMismatch() throws Exception {
		List<SegmentMetaData> smds = new ArrayList<>();
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg1.bin")
				.setHashCode("766123130092")
				.setNumberOfElements(10L)
				.setUpdateTime(T("1992-04-12T17:28:19Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg2.bin")
				.setHashCode("826103947712")
				.setNumberOfElements(20L)
				.setUpdateTime(T("1995-12-20T02:15:26Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg3.bin")
				.setHashCode("178268399123")
				.setNumberOfElements(30L)
				.setUpdateTime(T("1997-08-22T19:30:48Z")));
		ActualSegmentInfo asi = new ActualSegmentInfo(null, null, smds);
		CacheHeader header = new CacheHeaderImpl()
				.setNumberOfElements(315L)
				.addSourceDescriptor("766123130092", "/foo/bar/seg1.bin")
				.addSourceDescriptor("826103947712", "/foo/bar/seg2.XXX")
				.addSourceDescriptor("178268399123", "/foo/bar/seg3.bin");

		assertFalse(helper.sameSources(asi, header));
	}

	@Test
	public void testTestHelper_SameSources_OK() throws Exception {
		List<SegmentMetaData> smds = new ArrayList<>();
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg1.bin")
				.setHashCode("766123130092")
				.setNumberOfElements(10L)
				.setUpdateTime(T("1992-04-12T17:28:19Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg2.bin")
				.setHashCode("826103947712")
				.setNumberOfElements(20L)
				.setUpdateTime(T("1995-12-20T02:15:26Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/seg3.bin")
				.setHashCode("178268399123")
				.setNumberOfElements(30L)
				.setUpdateTime(T("1997-08-22T19:30:48Z")));
		ActualSegmentInfo asi = new ActualSegmentInfo(null, null, smds);
		CacheHeader header = new CacheHeaderImpl()
				.setNumberOfElements(315L)
				.addSourceDescriptor("766123130092", "/foo/bar/seg1.bin")
				.addSourceDescriptor("826103947712", "/foo/bar/seg2.bin")
				.addSourceDescriptor("178268399123", "/foo/bar/seg3.bin");

		assertTrue(helper.sameSources(asi, header));
	}

	@Test
	public void testTestHelper_BuildSegment_HasNotSegments() throws Exception {
		SegmentFileInfo sfiMock = control.createMock(SegmentFileInfo.class);
		expect(sfiMock.getFullPath()).andStubReturn(new File("foo/bar"));
		ActualSegmentInfo asi = new ActualSegmentInfo(sfiMock, new ArrayList<>(), new ArrayList<>());
		
		SegmentData actual = helper.buildSegment(asi);
		
		assertEquals(new CacheHeaderImpl().setNumberOfElements(0L),
				actual.getHeader());
		EditableTSeries<Candle> actualOhlc = actual.getCandles();
		assertNotNull(actualOhlc);
		assertEquals(TSeriesImpl.class, actualOhlc.getClass());
		assertEquals(0, actualOhlc.getLength());
	}
	
	@Test
	public void testTestHelper_BuildSegment_HasSegments() throws Exception {
		SegmentFileInfo sfiMock = control.createMock(SegmentFileInfo.class);
		expect(sfiMock.getFullPath()).andStubReturn(new File("foo/bar"));
		List<SymbolDaily> sds = new ArrayList<>();
		sds.add(new SymbolDaily(symbol2, 2005, 5, 12));
		sds.add(new SymbolDaily(symbol2, 2005, 5, 19));
		sds.add(new SymbolDaily(symbol2, 2005, 5, 23));
		List<SegmentMetaData> smds = new ArrayList<>();
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/com1.dat")
				.setHashCode("727190904ae")
				.setNumberOfElements(12)
				.setUpdateTime(T("2018-10-10T00:00:00Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/com2.dat")
				.setHashCode("7efh0a87632")
				.setNumberOfElements(15)
				.setUpdateTime(T("2018-10-11T00:00:00Z")));
		smds.add(new SegmentMetaDataImpl()
				.setPath("/foo/bar/com3.dat")
				.setHashCode("8236feba712")
				.setNumberOfElements(46)
				.setUpdateTime(T("2018-10-12T00:00:00Z")));
		ActualSegmentInfo asi = new ActualSegmentInfo(sfiMock, sds, smds);
		EditableTSeries<Candle> targetMock = control.createMock(EditableTSeries.class);
		CloseableIterator<Candle> readerMock1, readerMock2, readerMock3;
		readerMock1 = control.createMock(CloseableIterator.class);
		readerMock2 = control.createMock(CloseableIterator.class);
		readerMock3 = control.createMock(CloseableIterator.class);
		expect(sdssMock.createReader(new SymbolDaily(symbol2, 2005, 5, 12))).andReturn(readerMock1);
		expect(cuMock.buildUsingSourceData(readerMock1, ZTFrame.D1, aggrMock)).andReturn(targetMock);
		readerMock1.close();
		expect(sdssMock.createReader(new SymbolDaily(symbol2, 2005, 5, 19))).andReturn(readerMock2);
		expect(cuMock.buildUsingSourceData(readerMock2, targetMock, aggrMock)).andReturn(targetMock);
		readerMock2.close();
		expect(sdssMock.createReader(new SymbolDaily(symbol2, 2005, 5, 23))).andReturn(readerMock3);
		expect(cuMock.buildUsingSourceData(readerMock3, targetMock, aggrMock)).andReturn(targetMock);
		readerMock3.close();
		expect(targetMock.getLength()).andReturn(58);
		control.replay();
		
		SegmentData actual = helper.buildSegment(asi);
		
		control.verify();
		CacheHeader expectedHeader = new CacheHeaderImpl()
				.setNumberOfElements(58L)
				.addSourceDescriptor("727190904ae", "/foo/bar/com1.dat")
				.addSourceDescriptor("7efh0a87632", "/foo/bar/com2.dat")
				.addSourceDescriptor("8236feba712", "/foo/bar/com3.dat");
		assertEquals(expectedHeader, actual.getHeader());
		assertSame(targetMock, actual.getCandles());
		assertEquals(new File("foo/bar"), actual.getFullPath());
	}
	
	@Test
	public void testTestHelper_SaveSegment() throws Exception {
		File pathMock = control.createMock(File.class);
		CacheHeader hdrMock = control.createMock(CacheHeader.class);
		EditableTSeries<Candle> ohlcMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		SegmentData data = new SegmentData(hdrMock, ohlcMock, pathMock);
		expect(cuMock.createWriter(pathMock)).andReturn(writerMock);
		cuMock.writeHeader(writerMock, hdrMock);
		cuMock.writeSeries(writerMock, ohlcMock);
		writerMock.close();
		control.replay();
		
		helper.saveSegment(data);
		
		control.verify();
	}
	
	@Test
	public void testTestHelper_Compact() throws Exception {
		List<SymbolDaily> sds = new ArrayList<>();
		sds.add(new SymbolDaily(symbol1, 1917, 11, 7));
		sds.add(new SymbolDaily(symbol1, 1917, 11, 15));
		sds.add(new SymbolDaily(symbol1, 1917, 11, 27));
		sds.add(new SymbolDaily(symbol1, 1917, 12, 15));
		sds.add(new SymbolDaily(symbol1, 1917, 12, 16));
		sds.add(new SymbolDaily(symbol1, 1925,  1, 12));
		sds.add(new SymbolDaily(symbol1, 1925,  1, 13));
		sds.add(new SymbolDaily(symbol1, 1925,  1, 14));
		sds.add(new SymbolDaily(symbol1, 1925,  1, 15));
		sds.add(new SymbolDaily(symbol1, 1970,  7,  1));
		sds.add(new SymbolDaily(symbol1, 1970,  7,  3));
		sds.add(new SymbolDaily(symbol1, 1970,  7, 26));
		sds.add(new SymbolDaily(symbol1, 1975, 10, 12));
		
		List<SymbolMonthly> actual = helper.compact(sds);
		
		List<SymbolMonthly> expected = new ArrayList<>();
		expected.add(new SymbolMonthly(symbol1, 1917, 11));
		expected.add(new SymbolMonthly(symbol1, 1917, 12));
		expected.add(new SymbolMonthly(symbol1, 1925,  1));
		expected.add(new SymbolMonthly(symbol1, 1970,  7));
		expected.add(new SymbolMonthly(symbol1, 1975, 10));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCtor4_ZTF() {
		assertSame(sdssMock, service.getSDSS());
		assertEquals(ZTFrame.D1, service.getTimeFrame());
		assertSame(cuMock, service.getCacheUtils());
		assertSame(helperMock, service.getTestHelper());
	}
	
	@Test
	public void testCtor5_ZTF() {
		
		service = new D1CacheOverCandleSDSS(ZTFrame.D1MSK,
				sdssMock, aggrMock, cmMock, cuMock);
		
		assertSame(sdssMock, service.getSDSS());
		assertEquals(ZTFrame.D1MSK, service.getTimeFrame());
		assertSame(cuMock, service.getCacheUtils());
		TestHelper th = service.getTestHelper();
		assertNotNull(th);
		assertEquals(ZTFrame.D1MSK, th.getTFrame());
		assertSame(cmMock, th.getCacheManager());
		assertSame(sdssMock, th.getSDSS());
		assertSame(aggrMock, th.getAggregator());
		assertSame(cuMock, th.getCacheUtils());
	}
	
	@Test
	public void testCtor5_TF() {
		ZoneId zid = ZoneId.of("Europe/Moscow");
		expect(sdssMock.getZoneID()).andStubReturn(zid);
		control.replay();
		
		service = new D1CacheOverCandleSDSS(new TFDays(1),
				sdssMock, aggrMock, cmMock, cuMock);
		
		assertSame(sdssMock, service.getSDSS());
		assertEquals(ZTFrame.D1MSK, service.getTimeFrame());
		assertSame(cuMock, service.getCacheUtils());
		TestHelper th = service.getTestHelper();
		assertNotNull(th);
		assertEquals(ZTFrame.D1MSK, th.getTFrame());
		assertSame(cmMock, th.getCacheManager());
		assertSame(sdssMock, th.getSDSS());
		assertSame(aggrMock, th.getAggregator());
		assertSame(cuMock, th.getCacheUtils());
	}
	
	@Test
	public void testCtor4_TF() {
		ZoneId zid = ZoneId.of("Europe/Moscow");
		expect(sdssMock.getZoneID()).andStubReturn(zid);
		control.replay();
		
		service = new D1CacheOverCandleSDSS(new TFDays(1),
				sdssMock, aggrMock, cmMock);
		
		assertSame(sdssMock, service.getSDSS());
		assertEquals(ZTFrame.D1MSK, service.getTimeFrame());
		assertSame(CacheUtils.getInstance(), service.getCacheUtils());
		TestHelper th = service.getTestHelper();
		assertNotNull(th);
		assertEquals(ZTFrame.D1MSK, th.getTFrame());
		assertSame(cmMock, th.getCacheManager());
		assertSame(sdssMock, th.getSDSS());
		assertSame(aggrMock, th.getAggregator());
		assertSame(CacheUtils.getInstance(), th.getCacheUtils());
	}
	
	@Test
	public void testGetZoneID() {
		ZoneId zidMock = control.createMock(ZoneId.class);
		expect(sdssMock.getZoneID()).andReturn(zidMock);
		control.replay();
		
		ZoneId actual = service.getZoneID();
		
		control.verify();
		assertEquals(zidMock, actual);
	}
	
	@Test
	public void testListSymbols() {
		Set<Symbol> symbols = new HashSet<>();
		symbols.add(symbol1);
		symbols.add(symbol2);
		symbols.add(symbol3);
		expect(sdssMock.listSymbols()).andReturn(symbols);
		control.replay();
		
		assertEquals(symbols, service.listSymbols());
		
		control.verify();
	}
	
	@Test
	public void testIsExists_TrueIfHasDailySegments() throws Exception {
		expect(sdssMock.listDailySegments(symbol1, new MonthPoint(2017, Month.SEPTEMBER)))
			.andReturn(toList(
					new SymbolDaily(symbol1, 2017, 9, 11),
					new SymbolDaily(symbol1, 2017, 9, 25)
				));
		control.replay();
		
		assertTrue(service.isExists(new SymbolMonthly(symbol1, 2017, 9)));
		
		control.verify();
	}
	
	@Test
	public void testIsExists_FalseIfHasNotDailySegments() throws Exception {
		expect(sdssMock.listDailySegments(symbol2, new MonthPoint(1998, Month.AUGUST)))
			.andReturn(toList());
		control.replay();
		
		assertFalse(service.isExists(new SymbolMonthly(symbol2, 1998, 8)));
		
		control.verify();
	}
	
	@Test
	public void testListMonthlySegments1_S() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> smlMock = control.createMock(List.class);
		expect(sdssMock.listDailySegments(symbol3)).andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(smlMock);
		control.replay();
		
		assertSame(smlMock, service.listMonthlySegments(symbol3));
		
		control.verify();
	}
	
	@Test
	public void testListMonthlySegments3_SMM() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> smlMock = control.createMock(List.class);
		expect(sdssMock.listDailySegments(symbol1,
				new DatePoint(1998, 5, 1),
				new DatePoint(2000, 9, 30))
			).andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(smlMock);
		control.replay();
		
		assertSame(smlMock, service.listMonthlySegments(symbol1,
				new MonthPoint(1998, Month.MAY),
				new MonthPoint(2000, Month.SEPTEMBER)));
		
		control.verify();
	}
	
	@Test
	public void testListMonthlySegments2_SM() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> smlMock = control.createMock(List.class);
		expect(sdssMock.listDailySegments(symbol2, new DatePoint(2015, 7, 1)))
			.andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(smlMock);
		control.replay();
		
		assertSame(smlMock, service.listMonthlySegments(symbol2,
				new MonthPoint(2015, Month.JULY)));
		
		control.verify();
	}
	
	@Test
	public void testListMonthlySegments3_SMI_IfLessThanMaxCount() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> sml = new ArrayList<>();
		sml.add(new SymbolMonthly(symbol3, 2017,  4));
		sml.add(new SymbolMonthly(symbol3, 2017,  7));
		sml.add(new SymbolMonthly(symbol3, 2017,  8));
		expect(sdssMock.listDailySegments(symbol3, new DatePoint(2017, 2, 1)))
			.andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(sml);
		control.replay();
		
		List<SymbolMonthly> actual = service.listMonthlySegments(symbol3,
				new MonthPoint(2017, Month.FEBRUARY), 5);
		
		control.verify();
		List<SymbolMonthly> expected = new ArrayList<>();
		expected.add(new SymbolMonthly(symbol3, 2017,  4));
		expected.add(new SymbolMonthly(symbol3, 2017,  7));
		expected.add(new SymbolMonthly(symbol3, 2017,  8));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListMonthlySegments3_SMI_IfGreaterThanMaxCount() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> sml = new ArrayList<>();
		sml.add(new SymbolMonthly(symbol3, 2017,  4));
		sml.add(new SymbolMonthly(symbol3, 2017,  7));
		sml.add(new SymbolMonthly(symbol3, 2017,  8));
		sml.add(new SymbolMonthly(symbol3, 2017, 10));
		sml.add(new SymbolMonthly(symbol3, 2017, 11));
		sml.add(new SymbolMonthly(symbol3, 2017, 12));
		sml.add(new SymbolMonthly(symbol3, 2018,  5));
		expect(sdssMock.listDailySegments(symbol3, new DatePoint(2017, 2, 1)))
			.andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(sml);
		control.replay();
		
		List<SymbolMonthly> actual = service.listMonthlySegments(symbol3,
				new MonthPoint(2017, Month.FEBRUARY), 5);
		
		control.verify();
		List<SymbolMonthly> expected = new ArrayList<>();
		expected.add(new SymbolMonthly(symbol3, 2017,  4));
		expected.add(new SymbolMonthly(symbol3, 2017,  7));
		expected.add(new SymbolMonthly(symbol3, 2017,  8));
		expected.add(new SymbolMonthly(symbol3, 2017, 10));
		expected.add(new SymbolMonthly(symbol3, 2017, 11));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testListMonthlySegments3_SIM_IfLessThanMaxCount() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> sml = new ArrayList<>();
		sml.add(new SymbolMonthly(symbol3, 2017,  4));
		sml.add(new SymbolMonthly(symbol3, 2017,  7));
		sml.add(new SymbolMonthly(symbol3, 2017,  8));
		expect(sdssMock.listDailySegments(symbol1,
				new DatePoint(0, 1, 1),
				new DatePoint(2017, 8, 31)
			)).andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(sml);
		control.replay();
		
		List<SymbolMonthly> actual = service.listMonthlySegments(symbol1,
				5, new MonthPoint(2017, Month.AUGUST));
		
		control.verify();
		List<SymbolMonthly> expected = new ArrayList<>();
		expected.add(new SymbolMonthly(symbol3, 2017,  4));
		expected.add(new SymbolMonthly(symbol3, 2017,  7));
		expected.add(new SymbolMonthly(symbol3, 2017,  8));
		assertEquals(expected, actual);
	}

	@Test
	public void testListMonthlySegments3_SIM_IfGreaterThanMaxCount() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> sml = new ArrayList<>();
		sml.add(new SymbolMonthly(symbol3, 2017,  4));
		sml.add(new SymbolMonthly(symbol3, 2017,  7));
		sml.add(new SymbolMonthly(symbol3, 2017,  8));
		sml.add(new SymbolMonthly(symbol3, 2017, 10));
		sml.add(new SymbolMonthly(symbol3, 2017, 11));
		sml.add(new SymbolMonthly(symbol3, 2017, 12));
		sml.add(new SymbolMonthly(symbol3, 2018,  5));
		expect(sdssMock.listDailySegments(symbol1,
				new DatePoint(0, 1, 1),
				new DatePoint(2018, 5, 31)
			)).andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(sml);
		control.replay();
		
		List<SymbolMonthly> actual = service.listMonthlySegments(symbol1,
				5, new MonthPoint(2018, Month.MAY));
		
		control.verify();
		List<SymbolMonthly> expected = new ArrayList<>();
		expected.add(new SymbolMonthly(symbol3, 2017,  8));
		expected.add(new SymbolMonthly(symbol3, 2017, 10));
		expected.add(new SymbolMonthly(symbol3, 2017, 11));
		expected.add(new SymbolMonthly(symbol3, 2017, 12));
		expected.add(new SymbolMonthly(symbol3, 2018,  5));
		assertEquals(expected, actual);
	}

	@Test
	public void testListMonthlySegments2_SY() throws Exception {
		List<SymbolDaily> sdlMock = control.createMock(List.class);
		List<SymbolMonthly> smlMock = control.createMock(List.class);
		expect(sdssMock.listDailySegments(symbol3,
				new DatePoint(1915, 1, 1), new DatePoint(1915, 12, 31)
			)).andReturn(sdlMock);
		expect(helperMock.compact(sdlMock)).andReturn(smlMock);
		control.replay();
		
		assertEquals(smlMock, service.listMonthlySegments(symbol3, new YearPoint(1915)));
		
		control.verify();
	}
	
	@Test
	public void testGetMetaData_ReadCache() throws Exception {
		ActualSegmentInfo asiMock = control.createMock(ActualSegmentInfo.class);
		File pathMock = control.createMock(File.class);
		CacheHeader hdrMock = control.createMock(CacheHeader.class);
		SegmentMetaData mdMock = control.createMock(SegmentMetaData.class);
		expect(asiMock.getFullPath()).andStubReturn(pathMock);
		expect(helperMock.loadCurrentInfo(new SymbolMonthly(symbol2, 1250, 2)))
			.andReturn(asiMock);
		expect(pathMock.exists()).andReturn(true);
		expect(cuMock.readHeader(pathMock)).andReturn(hdrMock);
		expect(helperMock.sameSources(asiMock, hdrMock)).andReturn(true);
		expect(hdrMock.getNumberOfElements()).andReturn(105L);
		expect(cuMock.getMetaData(pathMock, 105L)).andReturn(mdMock);
		control.replay();
		
		SegmentMetaData actual = service.getMetaData(new SymbolMonthly(symbol2, 1250, 2));
		
		control.verify();
		assertEquals(mdMock, actual);
	}

	@Test
	public void testGetMetaData_UpdateCache_EntryNotExists() throws Exception {
		ActualSegmentInfo asiMock = control.createMock(ActualSegmentInfo.class);
		File pathMock = control.createMock(File.class);
		SegmentMetaData mdMock = control.createMock(SegmentMetaData.class);
		SegmentData sdMock = control.createMock(SegmentData.class);
		expect(asiMock.getFullPath()).andStubReturn(pathMock);
		expect(helperMock.loadCurrentInfo(new SymbolMonthly(symbol2, 1250, 2)))
			.andReturn(asiMock);
		expect(pathMock.exists()).andReturn(false);
		expect(helperMock.buildSegment(asiMock)).andReturn(sdMock);
		helperMock.saveSegment(sdMock);
		expect(sdMock.getNumberOfElements()).andReturn(200L);
		expect(cuMock.getMetaData(pathMock, 200L)).andReturn(mdMock);
		control.replay();
		
		SegmentMetaData actual = service.getMetaData(new SymbolMonthly(symbol2, 1250, 2));
		
		control.verify();
		assertEquals(mdMock, actual);
	}

	@Test
	public void testGetMetaData_UpdateCache_EntryOutdated() throws Exception {
		ActualSegmentInfo asiMock = control.createMock(ActualSegmentInfo.class);
		File pathMock = control.createMock(File.class);
		CacheHeader hdrMock = control.createMock(CacheHeader.class);
		SegmentMetaData mdMock = control.createMock(SegmentMetaData.class);
		SegmentData sdMock = control.createMock(SegmentData.class);
		expect(asiMock.getFullPath()).andStubReturn(pathMock);
		expect(helperMock.loadCurrentInfo(new SymbolMonthly(symbol2, 1250, 2)))
			.andReturn(asiMock);
		expect(pathMock.exists()).andReturn(true);
		expect(cuMock.readHeader(pathMock)).andReturn(hdrMock);
		expect(helperMock.sameSources(asiMock, hdrMock)).andReturn(false);
		expect(helperMock.buildSegment(asiMock)).andReturn(sdMock);
		helperMock.saveSegment(sdMock);
		expect(sdMock.getNumberOfElements()).andReturn(200L);
		expect(cuMock.getMetaData(pathMock, 200L)).andReturn(mdMock);
		control.replay();
		
		SegmentMetaData actual = service.getMetaData(new SymbolMonthly(symbol2, 1250, 2));
		
		control.verify();
		assertEquals(mdMock, actual);
	}

	@Test
	public void testCreateReader_ReadCache() throws Exception {
		ActualSegmentInfo asiMock = control.createMock(ActualSegmentInfo.class);
		File pathMock = control.createMock(File.class);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CacheHeader hdrMock = control.createMock(CacheHeader.class);
		CloseableIterator<Candle> itMock = control.createMock(CloseableIterator.class);
		expect(asiMock.getFullPath()).andStubReturn(pathMock);
		expect(helperMock.loadCurrentInfo(new SymbolMonthly(symbol3, 2015, 1)))
			.andReturn(asiMock);
		expect(pathMock.exists()).andReturn(true);
		expect(cuMock.createReader(pathMock)).andReturn(readerMock);
		expect(cuMock.readHeader(readerMock)).andReturn(hdrMock);
		expect(helperMock.sameSources(asiMock, hdrMock)).andReturn(true);
		expect(cuMock.createIterator(readerMock, ZTFrame.D1)).andReturn(itMock);
		control.replay();
		
		CloseableIterator<Candle> actual =
				service.createReader(new SymbolMonthly(symbol3, 2015, 1));
		
		control.verify();
		assertEquals(itMock, actual);
	}
	
	@Test
	public void testCreateReader_UpdateCache_EntryNotExists() throws Exception {
		ActualSegmentInfo asiMock = control.createMock(ActualSegmentInfo.class);
		File pathMock = control.createMock(File.class);
		CloseableIterator<Candle> itMock = control.createMock(CloseableIterator.class);
		SegmentData sdMock = control.createMock(SegmentData.class);
		EditableTSeries<Candle> ohlcMock = control.createMock(EditableTSeries.class);
		expect(asiMock.getFullPath()).andStubReturn(pathMock);
		expect(helperMock.loadCurrentInfo(new SymbolMonthly(symbol3, 2015, 1)))
			.andReturn(asiMock);
		expect(pathMock.exists()).andReturn(false);
		expect(helperMock.buildSegment(asiMock)).andReturn(sdMock);
		helperMock.saveSegment(sdMock);
		expect(sdMock.getCandles()).andReturn(ohlcMock);
		expect(cuMock.createIterator(ohlcMock)).andReturn(itMock);
		control.replay();
		
		CloseableIterator<Candle> actual =
				service.createReader(new SymbolMonthly(symbol3, 2015, 1));
		
		control.verify();
		assertEquals(itMock, actual);
	}

	@Test
	public void testCreateReader_UpdateCache_EntryOutdated() throws Exception {
		ActualSegmentInfo asiMock = control.createMock(ActualSegmentInfo.class);
		File pathMock = control.createMock(File.class);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CacheHeader hdrMock = control.createMock(CacheHeader.class);
		CloseableIterator<Candle> itMock = control.createMock(CloseableIterator.class);
		SegmentData sdMock = control.createMock(SegmentData.class);
		EditableTSeries<Candle> ohlcMock = control.createMock(EditableTSeries.class);
		expect(asiMock.getFullPath()).andStubReturn(pathMock);
		expect(helperMock.loadCurrentInfo(new SymbolMonthly(symbol3, 2015, 1)))
			.andReturn(asiMock);
		expect(pathMock.exists()).andReturn(true);
		expect(cuMock.createReader(pathMock)).andReturn(readerMock);
		expect(cuMock.readHeader(readerMock)).andReturn(hdrMock);
		expect(helperMock.sameSources(asiMock, hdrMock)).andReturn(false);
		readerMock.close();
		expect(helperMock.buildSegment(asiMock)).andReturn(sdMock);
		helperMock.saveSegment(sdMock);
		expect(sdMock.getCandles()).andReturn(ohlcMock);
		expect(cuMock.createIterator(ohlcMock)).andReturn(itMock);
		control.replay();
		
		CloseableIterator<Candle> actual =
				service.createReader(new SymbolMonthly(symbol3, 2015, 1));
		
		control.verify();
		assertEquals(itMock, actual);

	}

}

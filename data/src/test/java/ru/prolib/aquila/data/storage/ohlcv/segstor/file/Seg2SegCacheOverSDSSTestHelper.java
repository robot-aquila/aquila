package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.IMocksControl;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesAggregator;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheHeaderImpl;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheUtils;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaDataImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfoImpl;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

/**
 * Tests of cache over daily segment storage when one source segment per one
 * cache segment. Suitable for any intraday timeframes and any data type of
 * source records.
 * <p>
 * @param <RecordType> - data type of source segments
 */
public class Seg2SegCacheOverSDSSTestHelper<RecordType> {
	private static final Symbol symbol1, symbol2, symbol3;
	private static DatePoint point1, point2;
	
	static {
		symbol1 = new Symbol("XFER");
		symbol2 = new Symbol("BOLT");
		symbol3 = new Symbol("BUZZ");
		point1 = new DatePoint(2009,  1,  1);
		point2 = new DatePoint(2015, 12, 31);
	}
	
	private final IMocksControl control;
	private final SymbolDailySegmentStorage<RecordType> sourceStorageMock;
	private final SymbolDailySegmentStorage<Candle> storage;
	private final SegmentFileManager cacheManagerMock;
	private final CacheUtils utilsMock;
	private final ZTFrame tframe;
	private final CandleSeriesAggregator<RecordType> aggregator;
	private final String cacheFileSuffix;
	
	/**
	 * Constructor.
	 * <p>
	 * @param storage - test storage
	 * @param control - mocks control
	 * @param sourceStorageMock - underlying storage mock
	 * @param cacheManagerMock - mock of cache files storage
	 * @param utilsMock - cache utilities mock
	 * @param tframe - time frame of target series
	 * @param aggregator - candle aggregator
	 */
	public Seg2SegCacheOverSDSSTestHelper(SymbolDailySegmentStorage<Candle> storage,
			IMocksControl control,
			SymbolDailySegmentStorage<RecordType> sourceStorageMock,
			SegmentFileManager cacheManagerMock,
			CacheUtils utilsMock,
			ZTFrame tframe,
			String cacheFileSuffix,
			CandleSeriesAggregator<RecordType> aggregator)
	{
		this.control = control;	
		this.sourceStorageMock = sourceStorageMock;
		this.storage = storage;
		this.cacheManagerMock = cacheManagerMock;
		this.utilsMock = utilsMock;
		this.tframe = tframe;
		this.aggregator = aggregator;
		this.cacheFileSuffix = cacheFileSuffix;
	}
	
	public void testGetZoneID() {
		ZoneId zone = ZoneId.of("Europe/Moscow");
		expect(sourceStorageMock.getZoneID()).andReturn(zone);
		control.replay();
		
		assertEquals(zone, storage.getZoneID());
		
		control.verify();
	}
	
	public void testListSymbols() {
		Set<Symbol> symbols = new HashSet<>();
		symbols.add(symbol1);
		symbols.add(symbol2);
		symbols.add(symbol3);
		expect(sourceStorageMock.listSymbols()).andReturn(symbols);
		control.replay();
		
		assertEquals(symbols, storage.listSymbols());
		
		control.verify();
	}
	
	public void testIsExists() {
		expect(sourceStorageMock.isExists(new SymbolDaily(symbol1, 2017, 9, 11)))
			.andReturn(true)
			.andReturn(false);
		control.replay();
		
		assertTrue(storage.isExists(new SymbolDaily(symbol1, 2017, 9, 11)));
		assertFalse(storage.isExists(new SymbolDaily(symbol1, 2017, 9, 11)));
		
		control.verify();
	}
	
	public void testListDailySegments_1S() throws Exception {
		List<SymbolDaily> result = new ArrayList<>();
		result.add(new SymbolDaily(symbol2, 2017, 9, 11));
		result.add(new SymbolDaily(symbol2, 2017, 9, 12));
		result.add(new SymbolDaily(symbol2, 2017, 9, 13));
		expect(sourceStorageMock.listDailySegments(symbol2)).andReturn(result);
		control.replay();
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2);
		
		control.verify();
		assertEquals(result, actual);
	}
	
	public void testListDailySegments_3SPP() throws Exception {
		List<SymbolDaily> result = new ArrayList<>();
		result.add(new SymbolDaily(symbol1, 2010, 5, 11));
		result.add(new SymbolDaily(symbol1, 2010, 9, 12));
		expect(sourceStorageMock.listDailySegments(symbol1, point1, point2)).andReturn(result);
		control.replay();
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol1, point1, point2);

		control.verify();
		assertEquals(result, actual);
	}
	
	public void testListDailySegments_2SP() throws Exception {
		List<SymbolDaily> result = new ArrayList<>();
		result.add(new SymbolDaily(symbol2, 2017, 9, 11));
		result.add(new SymbolDaily(symbol2, 2017, 9, 12));
		expect(sourceStorageMock.listDailySegments(symbol2, point1)).andReturn(result);
		control.replay();
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2, point1);

		control.verify();
		assertEquals(result, actual);
	}
	
	public void testListDailySegments_3SPI() throws Exception {
		List<SymbolDaily> result = new ArrayList<>();
		result.add(new SymbolDaily(symbol1, 2010, 5, 11));
		result.add(new SymbolDaily(symbol1, 2010, 9, 12));
		expect(sourceStorageMock.listDailySegments(symbol1, point1, 200)).andReturn(result);
		control.replay();
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol1, point1, 200);

		control.verify();
		assertEquals(result, actual);
	}
	
	public void testListDailySegments_3SIP() throws Exception {
		List<SymbolDaily> result = new ArrayList<>();
		result.add(new SymbolDaily(symbol2, 2017, 9, 11));
		result.add(new SymbolDaily(symbol2, 2017, 9, 12));
		expect(sourceStorageMock.listDailySegments(symbol2, 85, point1)).andReturn(result);
		control.replay();
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol2, 85, point1);

		control.verify();
		assertEquals(result, actual);
	}
	
	public void testListDailySegments_2SM() throws Exception {
		MonthPoint point = new MonthPoint(2017, Month.SEPTEMBER);
		List<SymbolDaily> result = new ArrayList<>();
		result.add(new SymbolDaily(symbol1, 2010, 5, 11));
		result.add(new SymbolDaily(symbol1, 2010, 9, 12));
		expect(sourceStorageMock.listDailySegments(symbol1, point)).andReturn(result);
		control.replay();
		
		List<SymbolDaily> actual = storage.listDailySegments(symbol1, point);

		control.verify();
		assertEquals(result, actual);
	}
	
	@SuppressWarnings("unchecked")
	public void testGetMetaData_IfNotCached() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<RecordType> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix))
			.andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("foo/bar.cache"))); // Non-existing file!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, tframe, aggregator))
			.andReturn(candleSeriesMock);
		sourceIteratorMock.close();
		expect(candleSeriesMock.getLength()).andReturn(212);
		expect(utilsMock.createWriter(new File("foo/bar.cache"))).andReturn(writerMock);
		utilsMock.writeHeader(writerMock, new CacheHeaderImpl()
				.setNumberOfElements(212)
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin"));
		utilsMock.writeSeries(writerMock, candleSeriesMock);
		candleSeriesMock.clear();
		writerMock.close();
		expect(utilsMock.getMetaData(new File("foo/bar.cache"), 212)).andReturn(resultMetaDataMock);
		control.replay();
		
		SegmentMetaData actual = storage.getMetaData(token);
		
		control.verify();
		assertSame(resultMetaDataMock, actual);
	}
	
	@SuppressWarnings("unchecked")
	public void testGetMetaData_IfNumberOfDescriptorsNe1() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<RecordType> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix)).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.readHeader(new File("fixture/dummy"))).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin")
				.addSourceDescriptor("xxxxxxxx zulu/bolgens.bin")); // The second descriptor!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, tframe, aggregator))
			.andReturn(candleSeriesMock);
		sourceIteratorMock.close();
		expect(candleSeriesMock.getLength()).andReturn(405);
		expect(utilsMock.createWriter(new File("fixture/dummy"))).andReturn(writerMock);
		utilsMock.writeHeader(writerMock, new CacheHeaderImpl()
				.setNumberOfElements(405)
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin"));
		utilsMock.writeSeries(writerMock, candleSeriesMock);
		candleSeriesMock.clear();
		writerMock.close();
		expect(utilsMock.getMetaData(new File("fixture/dummy"), 405)).andReturn(resultMetaDataMock);
		control.replay();
		
		SegmentMetaData actual = storage.getMetaData(token);
		
		control.verify();
		assertSame(resultMetaDataMock, actual);
	}
	
	@SuppressWarnings("unchecked")
	public void testGetMetaData_IfSourceHashCodeMismatch() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<RecordType> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix)).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.readHeader(new File("fixture/dummy"))).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zxcvbn12 zulu/canopus.bin")); // Hash code mismatch!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, tframe, aggregator))
			.andReturn(candleSeriesMock);
		sourceIteratorMock.close();
		expect(candleSeriesMock.getLength()).andReturn(201);
		expect(utilsMock.createWriter(new File("fixture/dummy"))).andReturn(writerMock);
		utilsMock.writeHeader(writerMock, new CacheHeaderImpl()
				.setNumberOfElements(201)
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin"));
		utilsMock.writeSeries(writerMock, candleSeriesMock);
		candleSeriesMock.clear();
		writerMock.close();
		expect(utilsMock.getMetaData(new File("fixture/dummy"), 201)).andReturn(resultMetaDataMock);
		control.replay();
		
		SegmentMetaData actual = storage.getMetaData(token);
		
		control.verify();
		assertSame(resultMetaDataMock, actual);
	}
	
	public void testGetMetaData_IfValidSegment() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix)).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.readHeader(new File("fixture/dummy"))).andReturn(new CacheHeaderImpl()
				.setNumberOfElements(1024)
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin")); // Same hash code!
		expect(utilsMock.getMetaData(new File("fixture/dummy"), 1024)).andReturn(resultMetaDataMock);
		control.replay();
		
		SegmentMetaData actual = storage.getMetaData(token);
		
		control.verify();
		assertSame(resultMetaDataMock, actual);
	}

	@SuppressWarnings("unchecked")
	public void testCreateReader_IfNotCached() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<RecordType> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix))
			.andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("foo/bar.cache"))); // Non-existing file!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, tframe, aggregator))
			.andReturn(candleSeriesMock);
		sourceIteratorMock.close();
		expect(candleSeriesMock.getLength()).andReturn(212);
		expect(utilsMock.createWriter(new File("foo/bar.cache"))).andReturn(writerMock);
		utilsMock.writeHeader(writerMock, new CacheHeaderImpl()
				.setNumberOfElements(212)
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin"));
		utilsMock.writeSeries(writerMock, candleSeriesMock);
		writerMock.close();
		expect(utilsMock.createIterator(candleSeriesMock)).andReturn(resultIteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(token);
		
		control.verify();
		assertSame(resultIteratorMock, actual);
	}
	
	@SuppressWarnings("unchecked")
	public void testCreateReader_IfNumberOfDescriptorsNe1() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CloseableIterator<RecordType> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix))
			.andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.createReader(new File("fixture/dummy"))).andReturn(readerMock);
		expect(utilsMock.readHeader(readerMock)).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin")
				.addSourceDescriptor("xxxxxxxx zulu/bolgens.bin")); // The second descriptor!
		readerMock.close();
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, tframe, aggregator))
			.andReturn(candleSeriesMock);
		sourceIteratorMock.close();
		expect(candleSeriesMock.getLength()).andReturn(212);
		expect(utilsMock.createWriter(new File("fixture/dummy"))).andReturn(writerMock);
		utilsMock.writeHeader(writerMock, new CacheHeaderImpl()
				.setNumberOfElements(212)
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin"));
		utilsMock.writeSeries(writerMock, candleSeriesMock);
		writerMock.close();
		expect(utilsMock.createIterator(candleSeriesMock)).andReturn(resultIteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(token);
		
		control.verify();
		assertSame(resultIteratorMock, actual);
	}
	
	@SuppressWarnings("unchecked")
	public void testCreateReader_IfSourceHashCodeMismatch() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CloseableIterator<RecordType> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix)).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.createReader(new File("fixture/dummy"))).andReturn(readerMock);
		expect(utilsMock.readHeader(readerMock)).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzz__ zulu/canopus.bin")); // Hash code mismatch
		readerMock.close();
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, tframe, aggregator))
			.andReturn(candleSeriesMock);
		sourceIteratorMock.close();
		expect(candleSeriesMock.getLength()).andReturn(212);
		expect(utilsMock.createWriter(new File("fixture/dummy"))).andReturn(writerMock);
		utilsMock.writeHeader(writerMock, new CacheHeaderImpl()
				.setNumberOfElements(212)
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin"));
		utilsMock.writeSeries(writerMock, candleSeriesMock);
		writerMock.close();
		expect(utilsMock.createIterator(candleSeriesMock)).andReturn(resultIteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(token);
		
		control.verify();
		assertSame(resultIteratorMock, actual);
	}

	@SuppressWarnings("unchecked")
	public void testCreateReader_IfValidSegment() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, cacheFileSuffix)).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.createReader(new File("fixture/dummy"))).andReturn(readerMock);
		expect(utilsMock.readHeader(readerMock)).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin")); // Hash code mismatch
		expect(utilsMock.createIterator(readerMock, tframe)).andReturn(resultIteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(token);
		
		control.verify();
		assertSame(resultIteratorMock, actual);
	}
	
}

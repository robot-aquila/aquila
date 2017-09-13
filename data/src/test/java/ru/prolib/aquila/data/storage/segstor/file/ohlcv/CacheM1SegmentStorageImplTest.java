package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.Writer;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.segstor.DatePoint;
import ru.prolib.aquila.data.storage.segstor.MonthPoint;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaData;
import ru.prolib.aquila.data.storage.segstor.SegmentMetaDataImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfoImpl;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

public class CacheM1SegmentStorageImplTest {
	private static final Symbol symbol1, symbol2, symbol3;
	
	static {
		symbol1 = new Symbol("XFER");
		symbol2 = new Symbol("BOLT");
		symbol3 = new Symbol("BUZZ");
	}
	
	private IMocksControl control;
	private SymbolDailySegmentStorage<L1Update> sourceStorageMock;
	private SegmentFileManager cacheManagerMock;
	private CacheUtils utilsMock;
	private CacheM1SegmentStorageImpl storage;
	private DatePoint point1, point2;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceStorageMock = control.createMock(SymbolDailySegmentStorage.class);
		cacheManagerMock = control.createMock(SegmentFileManager.class);
		utilsMock = control.createMock(CacheUtils.class);
		storage = new CacheM1SegmentStorageImpl(sourceStorageMock, cacheManagerMock, utilsMock);
		point1 = new DatePoint(2009, 1, 1);
		point2 = new DatePoint(2015, 12, 31);
	}
	
	@Test
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
	
	@Test
	public void testIsExists() {
		expect(sourceStorageMock.isExists(new SymbolDaily(symbol1, 2017, 9, 11)))
			.andReturn(true)
			.andReturn(false);
		control.replay();
		
		assertTrue(storage.isExists(new SymbolDaily(symbol1, 2017, 9, 11)));
		assertFalse(storage.isExists(new SymbolDaily(symbol1, 2017, 9, 11)));
		
		control.verify();
	}
	
	@Test
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
	
	@Test
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

	@Test
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

	@Test
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
	
	@Test
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
	
	@Test
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
	@Test
	public void testGetMetaData_IfNotCached() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<L1Update> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("foo/bar.cache"))); // Non-existing file!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, TimeFrame.M1)).andReturn(candleSeriesMock);
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
	@Test
	public void testGetMetaData_IfNumberOfDescriptorsNe1() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<L1Update> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.readHeader(new File("fixture/dummy"))).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin")
				.addSourceDescriptor("xxxxxxxx zulu/bolgens.bin")); // The second descriptor!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, TimeFrame.M1)).andReturn(candleSeriesMock);
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
	@Test
	public void testGetMetaData_IfSourceHashCodeMismatch() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<L1Update> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.readHeader(new File("fixture/dummy"))).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zxcvbn12 zulu/canopus.bin")); // Hash code mismatch!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, TimeFrame.M1)).andReturn(candleSeriesMock);
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
	
	@Test
	public void testGetMetaData_IfValidSegment() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		SegmentMetaData resultMetaDataMock = control.createMock(SegmentMetaData.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
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
	@Test
	public void testCreateReader_IfNotCached() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		CloseableIterator<L1Update> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("foo/bar.cache"))); // Non-existing file!
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, TimeFrame.M1)).andReturn(candleSeriesMock);
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
	@Test
	public void testCreateReader_IfNumberOfDescriptorsNe1() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CloseableIterator<L1Update> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.createReader(new File("fixture/dummy"))).andReturn(readerMock);
		expect(utilsMock.readHeader(readerMock)).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin")
				.addSourceDescriptor("xxxxxxxx zulu/bolgens.bin")); // The second descriptor!
		readerMock.close();
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, TimeFrame.M1)).andReturn(candleSeriesMock);
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
	@Test
	public void testCreateReader_IfSOurceHashCodeMismatch() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CloseableIterator<L1Update> sourceIteratorMock = control.createMock(CloseableIterator.class);
		EditableTSeries<Candle> candleSeriesMock = control.createMock(EditableTSeries.class);
		Writer writerMock = control.createMock(Writer.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.createReader(new File("fixture/dummy"))).andReturn(readerMock);
		expect(utilsMock.readHeader(readerMock)).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzz__ zulu/canopus.bin")); // Hash code mismatch
		readerMock.close();
		expect(sourceStorageMock.createReader(token)).andReturn(sourceIteratorMock);
		expect(utilsMock.buildUsingSourceData(sourceIteratorMock, TimeFrame.M1)).andReturn(candleSeriesMock);
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
	@Test
	public void testCreateReader_IfValidSegment() throws Exception {
		SymbolDaily token = new SymbolDaily(symbol1, 2017, 9, 12);
		BufferedReader readerMock = control.createMock(BufferedReader.class);
		CloseableIterator<Candle> resultIteratorMock = control.createMock(CloseableIterator.class);
		expect(sourceStorageMock.getMetaData(token)).andReturn(new SegmentMetaDataImpl()
				.setPath("zulu/canopus.bin")
				.setHashCode("zzzzzzzz"));
		expect(cacheManagerMock.getFileInfo(token, "-OHLCV-M1.cache")).andReturn(new SegmentFileInfoImpl()
				.setFullPath(new File("fixture/dummy"))); // Existing file
		expect(utilsMock.createReader(new File("fixture/dummy"))).andReturn(readerMock);
		expect(utilsMock.readHeader(readerMock)).andReturn(new CacheHeaderImpl()
				.addSourceDescriptor("zzzzzzzz zulu/canopus.bin")); // Hash code mismatch
		expect(utilsMock.createIterator(readerMock, TimeFrame.M1)).andReturn(resultIteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(token);
		
		control.verify();
		assertSame(resultIteratorMock, actual);
	}

}

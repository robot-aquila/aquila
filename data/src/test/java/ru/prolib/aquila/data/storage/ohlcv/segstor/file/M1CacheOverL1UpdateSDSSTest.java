package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesL1UpdateAggregator;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.CacheUtils;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.M1CacheOverL1UpdateSDSS;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

public class M1CacheOverL1UpdateSDSSTest {
	private Seg2SegCacheOverSDSSTestHelper<L1Update> testHelper;
	private IMocksControl control;
	private SymbolDailySegmentStorage<L1Update> sourceStorageMock;
	private SegmentFileManager cacheManagerMock;
	private CacheUtils utilsMock;
	private M1CacheOverL1UpdateSDSS storage;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceStorageMock = control.createMock(SymbolDailySegmentStorage.class);
		cacheManagerMock = control.createMock(SegmentFileManager.class);
		utilsMock = control.createMock(CacheUtils.class);
		storage = new M1CacheOverL1UpdateSDSS(sourceStorageMock, cacheManagerMock, utilsMock);
		testHelper = new Seg2SegCacheOverSDSSTestHelper<>(storage, control,
				sourceStorageMock, cacheManagerMock, utilsMock, TimeFrame.M1,
				CandleSeriesL1UpdateAggregator.getInstance());
	}
	
	@Test
	public void testListSymbols() {
		testHelper.testListSymbols();
	}
	
	@Test
	public void testIsExists() {
		testHelper.testIsExists();
	}
	
	@Test
	public void testListDailySegments_1S() throws Exception {
		testHelper.testListDailySegments_1S();
	}
	
	@Test
	public void testListDailySegments_3SPP() throws Exception {
		testHelper.testListDailySegments_3SPP();
	}

	@Test
	public void testListDailySegments_2SP() throws Exception {
		testHelper.testListDailySegments_2SP();
	}

	@Test
	public void testListDailySegments_3SPI() throws Exception {
		testHelper.testListDailySegments_3SPI();
	}
	
	@Test
	public void testListDailySegments_3SIP() throws Exception {
		testHelper.testListDailySegments_3SIP();
	}
	
	@Test
	public void testListDailySegments_2SM() throws Exception {
		testHelper.testListDailySegments_2SM();
	}
	
	@Test
	public void testGetMetaData_IfNotCached() throws Exception {
		testHelper.testGetMetaData_IfNotCached();
	}
	
	@Test
	public void testGetMetaData_IfNumberOfDescriptorsNe1() throws Exception {
		testHelper.testGetMetaData_IfNumberOfDescriptorsNe1();
	}
	
	@Test
	public void testGetMetaData_IfSourceHashCodeMismatch() throws Exception {
		testHelper.testGetMetaData_IfSourceHashCodeMismatch();
	}
	
	@Test
	public void testGetMetaData_IfValidSegment() throws Exception {
		testHelper.testGetMetaData_IfValidSegment();
	}
	
	@Test
	public void testCreateReader_IfNotCached() throws Exception {
		testHelper.testCreateReader_IfNotCached();
	}
	
	@Test
	public void testCreateReader_IfNumberOfDescriptorsNe1() throws Exception {
		testHelper.testCreateReader_IfNumberOfDescriptorsNe1();
	}
	
	@Test
	public void testCreateReader_IfSourceHashCodeMismatch() throws Exception {
		testHelper.testCreateReader_IfSourceHashCodeMismatch();
	}
	
	@Test
	public void testCreateReader_IfValidSegment() throws Exception {
		testHelper.testCreateReader_IfValidSegment();
	}

}

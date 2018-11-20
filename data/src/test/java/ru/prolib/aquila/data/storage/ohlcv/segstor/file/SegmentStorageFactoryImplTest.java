package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.ZoneId;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.timeframe.TFDays;
import ru.prolib.aquila.core.data.timeframe.TFHours;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;
import ru.prolib.aquila.core.data.timeframe.ZTFDays;
import ru.prolib.aquila.core.data.timeframe.ZTFHours;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesCandleAggregator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.IntradayCacheOverSDSS;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.SegmentStorageFactoryImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

public class SegmentStorageFactoryImplTest {
	private IMocksControl control;
	private SymbolDailySegmentStorage<L1Update> sdssMock;
	private SegmentFileManager cmMock;
	private SegmentStorageFactoryImpl service;
	private ZoneId MSK;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MSK = ZoneId.of("Europe/Moscow");
		control = createStrictControl();
		sdssMock = control.createMock(SymbolDailySegmentStorage.class);
		cmMock = control.createMock(SegmentFileManager.class);
		service = new SegmentStorageFactoryImpl(sdssMock, cmMock);
	}
	
	@Test
	public void testCreateSDSS_ForM1() throws Exception {
		expect(sdssMock.getZoneID()).andStubReturn(MSK);
		control.replay();

		SymbolDailySegmentStorage<Candle> actual = service.createSDSS(new TFMinutes(1));
		
		assertNotNull(actual);
		assertSame(actual, service.createSDSS(new TFMinutes(1)));
		assertSame(actual, service.getM1SDSS());
		assertEquals(M1CacheOverL1UpdateSDSS.class, actual.getClass());
		M1CacheOverL1UpdateSDSS x = (M1CacheOverL1UpdateSDSS) actual;
		assertEquals(sdssMock, x.getUnderlyingStorage());
		assertEquals(cmMock, x.getCacheManager());
	}
	
	@Test
	public void testCreateSDSS_ForMX() throws Exception {
		expect(sdssMock.getZoneID()).andStubReturn(MSK);
		control.replay();
		
		SymbolDailySegmentStorage<Candle> actual = service.createSDSS(new TFMinutes(15));
		
		assertNotNull(actual);
		assertEquals(IntradayCacheOverSDSS.class, actual.getClass());
		IntradayCacheOverSDSS<Candle> x = (IntradayCacheOverSDSS<Candle>) actual;
		assertEquals(ZTFrame.M15MSK, x.getTimeFrame());
		assertSame(service.getM1SDSS(), x.getUnderlyingStorage());
		assertSame(CandleSeriesCandleAggregator.getInstance(), x.getAggregator());
		assertSame(cmMock, x.getCacheManager());
	}
	
	@Test
	public void testCreateSDSS_ForH1() throws Exception {
		expect(sdssMock.getZoneID()).andStubReturn(MSK);
		control.replay();

		SymbolDailySegmentStorage<Candle> actual = service.createSDSS(new TFHours(1));
		
		assertNotNull(actual);
		assertEquals(IntradayCacheOverSDSS.class, actual.getClass());
		IntradayCacheOverSDSS<Candle> x = (IntradayCacheOverSDSS<Candle>) actual;
		assertEquals(ZTFrame.H1MSK, x.getTimeFrame());
		assertSame(service.getM1SDSS(), x.getUnderlyingStorage());
		assertSame(CandleSeriesCandleAggregator.getInstance(), x.getAggregator());
		assertSame(cmMock, x.getCacheManager());
	}
	
	@Test
	public void testCreateSDSS_ForHX() throws Exception {
		expect(sdssMock.getZoneID()).andStubReturn(MSK);
		control.replay();

		SymbolDailySegmentStorage<Candle> actual = service.createSDSS(new TFHours(4));
		
		assertNotNull(actual);
		assertEquals(IntradayCacheOverSDSS.class, actual.getClass());
		IntradayCacheOverSDSS<Candle> x = (IntradayCacheOverSDSS<Candle>) actual;
		assertEquals(new ZTFHours(4, MSK), x.getTimeFrame());
		assertSame(service.getH1SDSS(), x.getUnderlyingStorage());
		assertSame(CandleSeriesCandleAggregator.getInstance(), x.getAggregator());
		assertSame(cmMock, x.getCacheManager());
	}

	@Test (expected=DataStorageException.class)
	public void testCreateSDSS_ThrowsIfIsNotIntraday() throws Exception {
		service.createSDSS(new TFDays(1));
	}
	
	@Test
	public void testCreateSMSS_ForD1() throws Exception {
		expect(sdssMock.getZoneID()).andStubReturn(MSK);
		control.replay();

		SymbolMonthlySegmentStorage<Candle> actual = service.createSMSS(new TFDays(1));
		
		assertNotNull(actual);
		assertEquals(D1CacheOverCandleSDSS.class, actual.getClass());
		D1CacheOverCandleSDSS x = (D1CacheOverCandleSDSS) actual;
		assertEquals(ZTFrame.D1MSK, x.getTimeFrame());
		assertSame(service.getH1SDSS(), x.getSDSS());
		D1CacheOverCandleSDSS.TestHelper th = x.getTestHelper();
		assertSame(CandleSeriesCandleAggregator.getInstance(), th.getAggregator());
		assertSame(cmMock, th.getCacheManager());
	}
	
	@Test
	public void testCreateSMSS_ForDX() throws Exception {
		expect(sdssMock.getZoneID()).andStubReturn(MSK);
		control.replay();

		SymbolMonthlySegmentStorage<Candle> actual = service.createSMSS(new TFDays(7));
		
		assertNotNull(actual);
		assertEquals(D1CacheOverCandleSDSS.class, actual.getClass());
		D1CacheOverCandleSDSS x = (D1CacheOverCandleSDSS) actual;
		assertEquals(new ZTFDays(7, MSK), x.getTimeFrame());
		assertSame(service.getH1SDSS(), x.getSDSS());
		D1CacheOverCandleSDSS.TestHelper th = x.getTestHelper();
		assertSame(CandleSeriesCandleAggregator.getInstance(), th.getAggregator());
		assertSame(cmMock, th.getCacheManager());
	}
	
	@Ignore
	@Test
	public void testCreateSMSS_ThrowsIfUnsupportedTF() throws Exception {
		//service.createSMSS(???)
	}

}

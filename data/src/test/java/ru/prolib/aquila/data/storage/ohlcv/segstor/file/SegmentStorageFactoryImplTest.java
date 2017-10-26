package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.ZoneId;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.timeframe.TFDays;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;
import ru.prolib.aquila.core.data.timeframe.ZTFMinutes;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesCandleAggregator;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.IntradayCacheOverSDSS;
import ru.prolib.aquila.data.storage.ohlcv.segstor.file.SegmentStorageFactoryImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileManager;

public class SegmentStorageFactoryImplTest {
	private IMocksControl control;
	private SymbolDailySegmentStorage<L1Update> sourceSegmentsMock;
	private SegmentFileManager cacheManagerMock;
	private SegmentStorageFactoryImpl factory;
	private ZoneId MSK;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MSK = ZoneId.of("Europe/Moscow");
		control = createStrictControl();
		sourceSegmentsMock = control.createMock(SymbolDailySegmentStorage.class);
		cacheManagerMock = control.createMock(SegmentFileManager.class);
		factory = new SegmentStorageFactoryImpl(sourceSegmentsMock, cacheManagerMock);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateSDSS_ThrowsIfIsNotIntraday() throws Exception {
		factory.createSDSS(new TFDays(1));
	}
	
	@Test
	public void testCreateSDSS() throws Exception {
		expect(sourceSegmentsMock.getZoneID()).andStubReturn(MSK);
		control.replay();
		
		SymbolDailySegmentStorage<Candle> storage = factory.createSDSS(new TFMinutes(15));
		
		control.verify();
		assertNotNull(storage);
		assertTrue(storage instanceof IntradayCacheOverSDSS);
		IntradayCacheOverSDSS<Candle> x = (IntradayCacheOverSDSS<Candle>) storage;
		assertNotNull(x.getUnderlyingStorage());
		assertEquals(new ZTFMinutes(15, MSK), x.getTimeFrame());
		assertSame(cacheManagerMock, x.getCacheManager());
		assertSame(CandleSeriesCandleAggregator.getInstance(), x.getAggregator());
		SymbolDailySegmentStorage<Candle> m1SDSS = factory.createSDSS(new TFMinutes(1));
		assertSame(m1SDSS, x.getUnderlyingStorage());
	}

	@Test
	public void testCreateSDSS_ForM1() throws Exception {
		SymbolDailySegmentStorage<Candle> storage = factory.createSDSS(new TFMinutes(1));
		
		assertNotNull(storage);
		assertSame(storage, factory.createSDSS(new TFMinutes(1)));
	}

}

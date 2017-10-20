package ru.prolib.aquila.data.storage.ohlcv.segstor.file;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TimeFrame;
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

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceSegmentsMock = control.createMock(SymbolDailySegmentStorage.class);
		cacheManagerMock = control.createMock(SegmentFileManager.class);
		factory = new SegmentStorageFactoryImpl(sourceSegmentsMock, cacheManagerMock);
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateSDSS_ThrowsIfIsNotIntraday() throws Exception {
		factory.createSDSS(TimeFrame.D1);
	}
	
	@Test
	public void testCreateSDSS() throws Exception {
		SymbolDailySegmentStorage<Candle> storage = factory.createSDSS(TimeFrame.M15);
		
		assertNotNull(storage);
		assertTrue(storage instanceof IntradayCacheOverSDSS);
		IntradayCacheOverSDSS<Candle> x = (IntradayCacheOverSDSS<Candle>) storage;
		assertNotNull(x.getUnderlyingStorage());
		assertEquals(TimeFrame.M15, x.getTimeFrame());
		assertSame(cacheManagerMock, x.getCacheManager());
		assertSame(CandleSeriesCandleAggregator.getInstance(), x.getAggregator());
		SymbolDailySegmentStorage<Candle> m1SDSS = factory.createSDSS(TimeFrame.M1);
		assertSame(m1SDSS, x.getUnderlyingStorage());
	}

	@Test
	public void testCreateSDSS_ForM1() throws Exception {
		SymbolDailySegmentStorage<Candle> storage = factory.createSDSS(TimeFrame.M1);
		
		assertNotNull(storage);
		assertSame(storage, factory.createSDSS(TimeFrame.M1));
	}

}

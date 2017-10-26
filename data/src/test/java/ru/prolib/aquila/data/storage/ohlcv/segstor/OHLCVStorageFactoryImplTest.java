package ru.prolib.aquila.data.storage.ohlcv.segstor;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.segstor.OHLCVStorageFactoryImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public class OHLCVStorageFactoryImplTest {
	private IMocksControl control;
	private SegmentStorageRegistry registryMock;
	private SymbolDailySegmentStorage<Candle> sdssMock;
	private OHLCVStorageFactoryImpl factory;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		registryMock = control.createMock(SegmentStorageRegistry.class);
		sdssMock = control.createMock(SymbolDailySegmentStorage.class);
		factory = new OHLCVStorageFactoryImpl(registryMock);
	}
	
	@Test
	public void testCreateStorage_IntradayTimeFrame() throws Exception {
		expect(registryMock.getSDSS(new TFMinutes(15))).andReturn(sdssMock);
		control.replay();
		
		MDStorage<TFSymbol, Candle> storage = factory.createStorage(ZTFrame.M15);
		
		control.verify();
		assertNotNull(storage);
		assertTrue(storage instanceof IntradayMDStorageOverSDSS);
		IntradayMDStorageOverSDSS x = (IntradayMDStorageOverSDSS) storage;
		assertEquals(sdssMock, x.getSegmentStorage());
		assertEquals(ZTFrame.M15, x.getTimeFrame());
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateStorage_UnsupportedTimeFrame() throws Exception {
		factory.createStorage(ZTFrame.D1);
	}

}

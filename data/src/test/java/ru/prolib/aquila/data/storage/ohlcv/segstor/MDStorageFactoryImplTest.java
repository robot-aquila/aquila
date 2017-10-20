package ru.prolib.aquila.data.storage.ohlcv.segstor;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.ZoneId;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;
import ru.prolib.aquila.data.storage.ohlcv.segstor.OHLCVStorageFactoryImpl;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public class MDStorageFactoryImplTest {
	private IMocksControl control;
	private SegmentStorageRegistry registryMock;
	private SymbolDailySegmentStorage<Candle> sdssMock;
	private ZoneId zoneID;
	private OHLCVStorageFactoryImpl factory;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		registryMock = control.createMock(SegmentStorageRegistry.class);
		sdssMock = control.createMock(SymbolDailySegmentStorage.class);
		zoneID = ZoneId.of("Europe/Moscow");
		factory = new OHLCVStorageFactoryImpl(registryMock, zoneID);
	}
	
	@Test
	public void testCreateStorage_IntradayTimeFrame() throws Exception {
		expect(registryMock.getSDSS(TimeFrame.M15)).andReturn(sdssMock);
		control.replay();
		
		MDStorage<TFSymbol, Candle> storage = factory.createStorage(TimeFrame.M15);
		
		control.verify();
		assertNotNull(storage);
		assertTrue(storage instanceof IntradayMDStorageOverSDSS);
		IntradayMDStorageOverSDSS x = (IntradayMDStorageOverSDSS) storage;
		assertEquals(sdssMock, x.getSegmentStorage());
		assertEquals(TimeFrame.M15, x.getTimeFrame());
		assertEquals(zoneID, x.getZoneID());
	}
	
	@Test (expected=DataStorageException.class)
	public void testCreateStorage_UnsupportedTimeFrame() throws Exception {
		factory.createStorage(TimeFrame.D1);
	}

}

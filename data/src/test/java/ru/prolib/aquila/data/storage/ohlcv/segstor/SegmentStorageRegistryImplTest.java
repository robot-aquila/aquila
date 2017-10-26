package ru.prolib.aquila.data.storage.ohlcv.segstor;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFrame;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public class SegmentStorageRegistryImplTest {
	private IMocksControl control;
	private SymbolDailySegmentStorage<Candle> sdssMock1, sdssMock2;
	private SegmentStorageFactory sdssFactoryMock;
	private Map<TFrame, SymbolDailySegmentStorage<Candle>> sdssRegistryStub;
	private SegmentStorageRegistryImpl registry;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sdssMock1 = control.createMock(SymbolDailySegmentStorage.class);
		sdssMock2 = control.createMock(SymbolDailySegmentStorage.class);
		sdssFactoryMock = control.createMock(SegmentStorageFactory.class);
		sdssRegistryStub = new HashMap<>();
		registry = new SegmentStorageRegistryImpl(sdssFactoryMock, sdssRegistryStub);
	}
	
	@Test
	public void testGetSDSS_ExistsingStorage() throws Exception {
		sdssRegistryStub.put(new TFMinutes(1), sdssMock2);
		
		SymbolDailySegmentStorage<Candle> actual = registry.getSDSS(new TFMinutes(1));
		
		assertSame(sdssMock2, actual);
	}

	@Test
	public void testGetSDSS_NewStorage() throws Exception {
		expect(sdssFactoryMock.createSDSS(new TFMinutes(10))).andReturn(sdssMock1);
		control.replay();
		
		SymbolDailySegmentStorage<Candle> actual = registry.getSDSS(new TFMinutes(10));
		
		assertSame(sdssMock1, actual);
		assertSame(sdssMock1, sdssRegistryStub.get(new TFMinutes(10)));
	}

}

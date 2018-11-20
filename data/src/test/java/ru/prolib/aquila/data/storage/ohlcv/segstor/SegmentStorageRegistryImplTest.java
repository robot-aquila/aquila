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
import ru.prolib.aquila.core.data.timeframe.TFDays;
import ru.prolib.aquila.core.data.timeframe.TFMinutes;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;

public class SegmentStorageRegistryImplTest {
	private IMocksControl control;
	private SymbolDailySegmentStorage<Candle> sdssMock1, sdssMock2;
	private SymbolMonthlySegmentStorage<Candle> smssMock1;
	private SegmentStorageFactory factoryMock;
	private Map<TFrame, SymbolDailySegmentStorage<Candle>> sdssRegistryStub;
	private Map<TFrame, SymbolMonthlySegmentStorage<Candle>> smssRegistryStub;
	private SegmentStorageRegistryImpl registry;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sdssMock1 = control.createMock(SymbolDailySegmentStorage.class);
		sdssMock2 = control.createMock(SymbolDailySegmentStorage.class);
		smssMock1 = control.createMock(SymbolMonthlySegmentStorage.class);
		factoryMock = control.createMock(SegmentStorageFactory.class);
		sdssRegistryStub = new HashMap<>();
		smssRegistryStub = new HashMap<>();
		registry = new SegmentStorageRegistryImpl(factoryMock,
				sdssRegistryStub, smssRegistryStub);
	}
	
	@Test
	public void testGetSDSS_ExistsingStorage() throws Exception {
		sdssRegistryStub.put(new TFMinutes(1), sdssMock2);
		
		SymbolDailySegmentStorage<Candle> actual = registry.getSDSS(new TFMinutes(1));
		
		assertSame(sdssMock2, actual);
	}

	@Test
	public void testGetSDSS_NewStorage() throws Exception {
		expect(factoryMock.createSDSS(new TFMinutes(10))).andReturn(sdssMock1);
		control.replay();
		
		SymbolDailySegmentStorage<Candle> actual = registry.getSDSS(new TFMinutes(10));
		
		assertSame(sdssMock1, actual);
		assertSame(sdssMock1, sdssRegistryStub.get(new TFMinutes(10)));
	}
	
	@Test
	public void testGetSMSS_ExistingStorage() throws Exception {
		smssRegistryStub.put(new TFDays(5), smssMock1);
		
		assertSame(smssMock1, registry.getSMSS(new TFDays(5)));
	}
	
	@Test
	public void testGetSMSS_NewStorage() throws Exception {
		expect(factoryMock.createSMSS(new TFDays(1))).andReturn(smssMock1);
		control.replay();
		
		SymbolMonthlySegmentStorage<Candle> actual = registry.getSMSS(new TFDays(1));
		
		assertSame(smssMock1, actual);
		assertSame(smssMock1, smssRegistryStub.get(new TFDays(1)));
	}

}

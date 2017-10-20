package ru.prolib.aquila.data.storage.ohlcv;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.MDStorage;

public class OHLCVStorageRegistryImplTest {
	private IMocksControl control;
	private OHLCVStorageFactory factoryMock;
	private MDStorage<TFSymbol, Candle> storageMock1, storageMock2;
	private Map<TimeFrame, MDStorage<TFSymbol, Candle>> registryStub;
	private OHLCVStorageRegistryImpl registry;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		factoryMock = control.createMock(OHLCVStorageFactory.class);
		storageMock1 = control.createMock(MDStorage.class);
		storageMock2 = control.createMock(MDStorage.class);
		registryStub = new HashMap<>();
		registry = new OHLCVStorageRegistryImpl(factoryMock, registryStub);
	}
	
	@Test
	public void testGetStorage_ExistingInstance() throws Exception {
		registryStub.put(TimeFrame.D1, storageMock2);
		
		MDStorage<TFSymbol, Candle> actual = registry.getStorage(TimeFrame.D1);
		
		assertSame(storageMock2, actual);
	}

	@Test
	public void testGetStorage_NewInstance() throws Exception {
		expect(factoryMock.createStorage(TimeFrame.M3)).andReturn(storageMock1);
		control.replay();
		
		MDStorage<TFSymbol, Candle> actual = registry.getStorage(TimeFrame.M3);
		
		control.verify();
		assertSame(storageMock1, actual);
		assertSame(storageMock1, registryStub.get(TimeFrame.M3));
	}

}

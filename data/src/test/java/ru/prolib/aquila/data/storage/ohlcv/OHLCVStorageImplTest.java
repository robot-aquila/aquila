package ru.prolib.aquila.data.storage.ohlcv;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.data.storage.MDStorage;

public class OHLCVStorageImplTest {
	private static TFSymbol key1, key2, key3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		key1 = new TFSymbol(new Symbol("SPY"), TimeFrame.M30);
		key2 = new TFSymbol(new Symbol("XXL"), TimeFrame.M1);
		key3 = new TFSymbol(new Symbol("BAR"), TimeFrame.D1);
	}
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private OHLCVStorageRegistry registryMock;
	private MDStorage<TFSymbol, Candle> storageMock;
	private CloseableIterator<Candle> iteratorMock;
	private OHLCVStorageImpl storage;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		registryMock = control.createMock(OHLCVStorageRegistry.class);
		storageMock = control.createMock(MDStorage.class);
		iteratorMock = control.createMock(CloseableIterator.class);
		storage = new OHLCVStorageImpl(registryMock);
	}
	
	@Test
	public void testGetKeys() throws Exception {
		Set<TFSymbol> keys = new HashSet<>();
		keys.add(key1);
		keys.add(key2);
		keys.add(key3);
		expect(registryMock.getStorage(TimeFrame.M1)).andReturn(storageMock);
		expect(storageMock.getKeys()).andReturn(keys);
		control.replay();
		
		Set<TFSymbol> actual = storage.getKeys();
		
		control.verify();
		assertEquals(keys, actual);
	}
	
	@Test
	public void testCreateReader_1K() throws Exception {
		expect(registryMock.getStorage(TimeFrame.M30)).andReturn(storageMock);
		expect(storageMock.createReader(key1)).andReturn(iteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(key1);
		
		control.verify();
		assertSame(iteratorMock, actual);
	}

	@Test
	public void testCreateReaderFrom_2KT() throws Exception {
		expect(registryMock.getStorage(TimeFrame.D1)).andReturn(storageMock);
		expect(storageMock.createReaderFrom(key3, T("2017-10-20T18:20:00Z")))
			.andReturn(iteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReaderFrom(key3, T("2017-10-20T18:20:00Z"));
		
		control.verify();
		assertSame(iteratorMock, actual);
	}
	
	@Test
	public void testCreateReader_3KTI() throws Exception {
		expect(registryMock.getStorage(TimeFrame.M30)).andReturn(storageMock);
		expect(storageMock.createReader(key1, T("2017-10-20T18:15:00Z"), 25))
			.andReturn(iteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(key1, T("2017-10-20T18:15:00Z"), 25);
		
		control.verify();
		assertSame(iteratorMock, actual);
	}
	
	@Test
	public void testCreateReader_3KTT() throws Exception {
		expect(registryMock.getStorage(TimeFrame.M1)).andReturn(storageMock);
		expect(storageMock.createReader(key2, T("2017-10-20T18:00:00Z"), T("2017-10-20T18:15:00Z")))
			.andReturn(iteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(key2,
				T("2017-10-20T18:00:00Z"), T("2017-10-20T18:15:00Z"));
		
		control.verify();
		assertSame(iteratorMock, actual);
	}
	
	@Test
	public void testCreateReader_3KIT() throws Exception {
		expect(registryMock.getStorage(TimeFrame.M30)).andReturn(storageMock);
		expect(storageMock.createReader(key1, 10, T("2017-10-20T18:13:00Z")))
			.andReturn(iteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReader(key1, 10, T("2017-10-20T18:13:00Z"));
		
		control.verify();
		assertSame(iteratorMock, actual);
	}
	
	@Test
	public void testCreateReaderTo_2KT() throws Exception {
		expect(registryMock.getStorage(TimeFrame.D1)).andReturn(storageMock);
		expect(storageMock.createReaderTo(key3, T("2017-10-20T18:20:00Z")))
			.andReturn(iteratorMock);
		control.replay();
		
		CloseableIterator<Candle> actual = storage.createReaderTo(key3, T("2017-10-20T18:20:00Z"));
		
		control.verify();
		assertSame(iteratorMock, actual);
	}

}

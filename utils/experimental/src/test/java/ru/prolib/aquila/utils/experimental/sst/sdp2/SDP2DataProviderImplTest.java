package ru.prolib.aquila.utils.experimental.sst.sdp2;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.ZTFrame;

public class SDP2DataProviderImplTest {
	private static final Symbol symbol1 = new Symbol("MSFT"), symbol2 = new Symbol("AAPL");
	private IMocksControl control;
	private SDP2DataSlice<SDP2Key> sliceMock1, sliceMock2, sliceMock3, sliceMock4;
	private SDP2DataSliceFactory<SDP2Key> factoryMock1;
	private Map<SDP2Key, SDP2DataSlice<SDP2Key>> slicesStub;
	private SDP2DataProviderImpl<SDP2Key> provider;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sliceMock1 = control.createMock(SDP2DataSlice.class);
		sliceMock2 = control.createMock(SDP2DataSlice.class);
		sliceMock3 = control.createMock(SDP2DataSlice.class);
		sliceMock4 = control.createMock(SDP2DataSlice.class);
		factoryMock1 = control.createMock(SDP2DataSliceFactory.class);
		slicesStub = new LinkedHashMap<>();
		provider = new SDP2DataProviderImpl<SDP2Key>(factoryMock1, slicesStub);
	}
	
	@Test
	public void testCtor2() {
		assertSame(factoryMock1, provider.getFactory());
	}
	
	@Test
	public void testCtor1_Factory() {
		provider = new SDP2DataProviderImpl<SDP2Key>(factoryMock1);
		assertSame(factoryMock1, provider.getFactory());
	}
	
	@Test
	public void testCtor1_Queue() {
		EventQueue queueMock = control.createMock(EventQueue.class);
		provider = new SDP2DataProviderImpl<SDP2Key>(queueMock);
		
		SDP2DataSliceFactoryImpl<SDP2Key> actual =
				(SDP2DataSliceFactoryImpl<SDP2Key>) provider.getFactory();
		assertEquals(queueMock, actual.getEventQueue());
	}
	
	@Test
	public void testGetSlice() {
		slicesStub.put(new SDP2Key(ZTFrame.M15, symbol1), sliceMock1);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock3);
		
		assertSame(sliceMock1, provider.getSlice(new SDP2Key(ZTFrame.M15, symbol1)));
		assertSame(sliceMock2, provider.getSlice(new SDP2Key(ZTFrame.M10, symbol2)));
		assertSame(sliceMock3, provider.getSlice(new SDP2Key(ZTFrame.M1)));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetSlice_ThrowsIfSliceNotExist() {
		provider.getSlice(new SDP2Key(ZTFrame.M1));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateSlice_ThrowsIfSliceAlreadyExists() {
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock3);
		
		provider.createSlice(new SDP2Key(ZTFrame.M1));
	}
	
	@Test
	public void testCreateSlice() {
		SDP2Key key = new SDP2Key(ZTFrame.D1, symbol2);
		expect(factoryMock1.produce(key)).andReturn(sliceMock1);
		control.replay();
		
		assertSame(sliceMock1, provider.createSlice(key));
		
		control.verify();
		assertSame(sliceMock1, slicesStub.get(key));
	}

	@Test
	public void testPurgeSlice_SkipIfSliceNotExists() {
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol1), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		control.replay();

		provider.purgeSlice(new SDP2Key(ZTFrame.D1MSK));
		
		control.verify();
		Map<SDP2Key, SDP2DataSlice<SDP2Key>> expected = new LinkedHashMap<>();
		expected.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock2);
		expected.put(new SDP2Key(ZTFrame.M10, symbol1), sliceMock3);
		expected.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		assertEquals(expected, slicesStub);
	}
	
	@Test
	public void testPurgeSlice() {
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol1), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		sliceMock3.close();
		control.replay();

		provider.purgeSlice(new SDP2Key(ZTFrame.M10, symbol1));
		
		control.verify();
		Map<SDP2Key, SDP2DataSlice<SDP2Key>> expected = new LinkedHashMap<>();
		expected.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock2);
		expected.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		assertEquals(expected, slicesStub);
	}
	
	@Test
	public void testGetSlices1_Symbol() {
		slicesStub.put(new SDP2Key(ZTFrame.M15, symbol1), sliceMock1);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol1), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		
		List<SDP2DataSlice<SDP2Key>> expected = new ArrayList<>();
		expected.add(sliceMock1);
		expected.add(sliceMock3);
		assertEquals(expected, provider.getSlices(symbol1));
	}
	
	@Test
	public void testGetSlicesWoSymbol() {
		slicesStub.put(new SDP2Key(ZTFrame.M15, symbol1), sliceMock1);
		slicesStub.put(new SDP2Key(ZTFrame.M10), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol1), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		
		List<SDP2DataSlice<SDP2Key>> expected = new ArrayList<>();
		expected.add(sliceMock2);
		expected.add(sliceMock4);
		assertEquals(expected, provider.getSlicesWoSymbol());
	}
	
	@Test
	public void testGetSlices() {
		slicesStub.put(new SDP2Key(ZTFrame.M15, symbol1), sliceMock1);
		slicesStub.put(new SDP2Key(ZTFrame.M10), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol1), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		
		List<SDP2DataSlice<SDP2Key>> expected = new ArrayList<>();
		expected.add(sliceMock1);
		expected.add(sliceMock2);
		expected.add(sliceMock3);
		expected.add(sliceMock4);
		assertEquals(expected, provider.getSlices());
	}
	
	@Test
	public void testGetSymbols() {
		slicesStub.put(new SDP2Key(ZTFrame.M15, symbol1), sliceMock1);
		slicesStub.put(new SDP2Key(ZTFrame.M10), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);

		Set<Symbol> expected = new HashSet<>();
		expected.add(symbol1);
		expected.add(symbol2);
		assertEquals(expected, provider.getSymbols());
	}
	
	@Test
	public void testGetTimeFrames() {
		slicesStub.put(new SDP2Key(ZTFrame.M15, symbol1), sliceMock1);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol1), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);

		Set<ZTFrame> expected = new HashSet<>();
		expected.add(ZTFrame.M15);
		expected.add(ZTFrame.M10);
		assertEquals(expected, provider.getTimeFrames(symbol1));
	}

	@Test
	public void testGetTimeFramesWoSymbol() {
		slicesStub.put(new SDP2Key(ZTFrame.M15, symbol1), sliceMock1);
		slicesStub.put(new SDP2Key(ZTFrame.M10), sliceMock2);
		slicesStub.put(new SDP2Key(ZTFrame.M10, symbol2), sliceMock3);
		slicesStub.put(new SDP2Key(ZTFrame.M1), sliceMock4);
		
		Set<ZTFrame> expected = new HashSet<>();
		expected.add(ZTFrame.M10);
		expected.add(ZTFrame.M1);
		assertEquals(expected, provider.getTimeFramesWoSymbol());
	}

}

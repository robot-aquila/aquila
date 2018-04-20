package ru.prolib.aquila.utils.experimental.sst.sdp2;

import static org.junit.Assert.*;
import static ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataSliceImpl.Entry;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ObservableTSeriesImpl;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorageKeys;

public class SDP2DataSliceImplTest {
	private IMocksControl control;
	private EventQueue queueMock;
	private TSeriesNodeStorageKeys storageMock;
	private EditableTSeries<?> seriesMock1, seriesMock2, seriesMock3, seriesMock4;
	private ObservableTSeriesImpl<?> obsSeriesMock;
	private Symbol symbol;
	private Map<String, Entry> entriesStub;
	private SDP2DataSliceImpl<SDP2Key> slice;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(EventQueue.class);
		storageMock = control.createMock(TSeriesNodeStorageKeys.class);
		seriesMock1 = control.createMock(EditableTSeries.class);
		seriesMock2 = control.createMock(EditableTSeries.class);
		seriesMock3 = control.createMock(EditableTSeries.class);
		seriesMock4 = control.createMock(EditableTSeries.class);
		obsSeriesMock = control.createMock(ObservableTSeriesImpl.class);
		symbol = new Symbol("GAZP");
		entriesStub = new LinkedHashMap<>();
		slice = new SDP2DataSliceImpl<>(new SDP2Key(ZTFrame.M5, symbol),
				queueMock, storageMock, entriesStub);
	}
	
	@Test
	public void testCtor() {
		assertEquals(new SDP2Key(ZTFrame.M5, symbol), slice.getKey());
		assertEquals(symbol, slice.getSymbol());
		assertSame(ZTFrame.M5, slice.getTimeFrame());
		assertSame(queueMock, slice.getEventQueue());
		assertSame(storageMock, slice.getStorage());
		assertSame(storageMock, slice.getIntervalStartSeries());
	}
	
	@Test
	public void testCreateSeries() {
		EditableTSeries<Double> x = slice.createSeries("foobar", false);
		
		Entry actual = entriesStub.get("foobar");
		Entry expected = new Entry(x);
		assertEquals(expected, actual);
		assertTrue(expected.isEditable());
		assertFalse(expected.isObservable());
		assertTrue(expected.isCloseable());
	}
	
	@Test
	public void testCreateSeries_Observable() {
		EditableTSeries<Double> x = slice.createSeries("zulu24", true);
		
		Entry actual = entriesStub.get("zulu24");
		Entry expected = new Entry((ObservableTSeriesImpl<Double>) x);
		assertEquals(expected, actual);
		assertTrue(expected.isEditable());
		assertTrue(expected.isObservable());
		assertTrue(expected.isCloseable());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testCreateSeries_ThrowsIfSeriesExists() {
		entriesStub.put("aquila", new Entry(seriesMock1));
		
		slice.createSeries("aquila", false);
	}
	
	@Test
	public void testRegisterRawSeries_1R() {
		expect(seriesMock1.getId()).andReturn("kappa");
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		slice.registerRawSeries((TSeries<?>) seriesMock1);
		
		control.verify();
		Entry actual = entriesStub.get("kappa");
		Entry expected = new Entry((TSeries<?>) seriesMock1);
		assertEquals(expected, actual);
		assertFalse(expected.isEditable());
		assertFalse(expected.isObservable());
		assertFalse(expected.isCloseable());
	}
	
	@Test
	public void testRegisterRawSeries_1R_ThrowsIfBadTFrame() {
		expect(seriesMock1.getId()).andReturn("zulu24");
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M1);
		control.replay();
		
		try {
			slice.registerRawSeries((TSeries<?>)seriesMock1);
			fail("Expected exception");
		} catch ( IllegalArgumentException e ) {
			assertEquals("Timeframe mismatch: SeriesID: zulu24, Expected: M5[UTC], Actual: M1[UTC]", e.getMessage());
		}
		control.verify();
		assertNull(entriesStub.get("zulu24"));
	}
	
	@Test
	public void testRegisterRawSeries_1R_ThrowsIfSeriesExists() {
		Entry original = null;
		entriesStub.put("kappa", original = new Entry(seriesMock1));
		expect(seriesMock2.getId()).andReturn("kappa");
		expect(seriesMock2.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			slice.registerRawSeries((TSeries<?>) seriesMock2);
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Series already exists: kappa", e.getMessage());
		}
		control.verify();
		assertSame(original, entriesStub.get("kappa"));
	}
	
	@Test
	public void testRegisterRawSeries__2R() {
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		slice.registerRawSeries((TSeries<?>) seriesMock1, "zumba");
		
		control.verify();
		Entry actual = entriesStub.get("zumba");
		Entry expected = new Entry((TSeries<?>) seriesMock1);
		assertEquals(expected, actual);
		assertFalse(expected.isEditable());
		assertFalse(expected.isObservable());
		assertFalse(expected.isCloseable());
	}

	@Test
	public void testRegisterRawSeries__2R_ThrowsIfBadTFrame() {
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M1);
		control.replay();
		
		try {
			slice.registerRawSeries((TSeries<?>)seriesMock1, "boogie");
			fail("Expected exception");
		} catch ( IllegalArgumentException e ) {
			assertEquals("Timeframe mismatch: SeriesID: boogie, Expected: M5[UTC], Actual: M1[UTC]", e.getMessage());
		}
		control.verify();
		assertNull(entriesStub.get("boogie"));
	}
	
	@Test
	public void testRegisterRawSeries__2R_ThrowsIfSeriesExists() {
		Entry original = null;
		entriesStub.put("gizmo", original = new Entry(seriesMock1));
		expect(seriesMock2.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			slice.registerRawSeries((TSeries<?>) seriesMock2, "gizmo");
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Series already exists: gizmo", e.getMessage());
		}
		control.verify();
		assertSame(original, entriesStub.get("gizmo"));
	}

	@Test
	public void testRegisterRawSeries_1E() {
		expect(seriesMock1.getId()).andReturn("kappa");
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		slice.registerRawSeries((EditableTSeries<?>) seriesMock1);
		
		control.verify();
		Entry actual = entriesStub.get("kappa");
		Entry expected = new Entry((EditableTSeries<?>) seriesMock1);
		assertEquals(expected, actual);
		assertTrue(expected.isEditable());
		assertFalse(expected.isObservable());
		assertTrue(expected.isCloseable());
	}
	
	@Test
	public void testRegisterRawSeries_1E_ThrowsIfBadTFrame() {
		expect(seriesMock1.getId()).andReturn("zulu24");
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M1);
		control.replay();
		
		try {
			slice.registerRawSeries((EditableTSeries<?>)seriesMock1);
			fail("Expected exception");
		} catch ( IllegalArgumentException e ) {
			assertEquals("Timeframe mismatch: SeriesID: zulu24, Expected: M5[UTC], Actual: M1[UTC]", e.getMessage());
		}
		control.verify();
		assertNull(entriesStub.get("zulu24"));
	}

	@Test
	public void testRegisterRawSeries_1E_ThrowsIfSeriesExists() {
		Entry original = null;
		entriesStub.put("kappa", original = new Entry(seriesMock1));
		expect(seriesMock2.getId()).andReturn("kappa");
		expect(seriesMock2.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			slice.registerRawSeries((EditableTSeries<?>) seriesMock2);
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Series already exists: kappa", e.getMessage());
		}
		control.verify();
		assertSame(original, entriesStub.get("kappa"));
	}
	
	@Test
	public void testRegisterRawSeries__2E() {
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		slice.registerRawSeries((EditableTSeries<?>) seriesMock1, "zulu24");
		
		control.verify();
		Entry actual = entriesStub.get("zulu24");
		Entry expected = new Entry((EditableTSeries<?>) seriesMock1);
		assertEquals(expected, actual);
		assertTrue(expected.isEditable());
		assertFalse(expected.isObservable());
		assertTrue(expected.isCloseable());		
	}

	@Test
	public void testRegisterRawSeries__2E_ThrowsIfBadTFrame() {
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M1);
		control.replay();
		
		try {
			slice.registerRawSeries((EditableTSeries<?>)seriesMock1, "kappa");
			fail("Expected exception");
		} catch ( IllegalArgumentException e ) {
			assertEquals("Timeframe mismatch: SeriesID: kappa, Expected: M5[UTC], Actual: M1[UTC]", e.getMessage());
		}
		control.verify();
		assertNull(entriesStub.get("kappa"));
	}
	
	@Test
	public void testRegisterRawSeries__2E_ThrowsIfSeriesExists() {
		Entry original = null;
		entriesStub.put("kappa", original = new Entry(seriesMock1));
		expect(seriesMock2.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			slice.registerRawSeries((EditableTSeries<?>) seriesMock2, "kappa");
			fail("Expected exception");
		} catch ( IllegalStateException e ) {
			assertEquals("Series already exists: kappa", e.getMessage());
		}
		control.verify();
		assertSame(original, entriesStub.get("kappa"));
	}

	@Test
	public void testGetObservableSeries() {
		entriesStub.put("gamma", new Entry(obsSeriesMock));
		
		assertSame(obsSeriesMock, slice.getObservableSeries("gamma"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetObservableSeries_ThrowsIfSeriesNotExists() {
		slice.getObservableSeries("gamma");
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetObservableSeries_ThrowsIfSeriesNotObservable() {
		entriesStub.put("gamma", new Entry(seriesMock1));
		
		slice.getObservableSeries("gamma");
	}
	
	@Test
	public void testGetSeries() {
		entriesStub.put("foo", new Entry(seriesMock1));
		entriesStub.put("bar", new Entry(obsSeriesMock));
		
		assertSame(seriesMock1, slice.getSeries("foo"));
		assertSame(obsSeriesMock, slice.getSeries("bar"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetSeries_ThrowsIfSeriesNotExists() {
		slice.getSeries("foo");
	}
	
	@Test
	public void testIsExists() {
		entriesStub.put("foo", new Entry(seriesMock2));
		
		assertTrue(slice.isExists("foo"));
		assertFalse(slice.isExists("bar"));
	}
	
	@Test
	public void testIsObservable() {
		entriesStub.put("foo", new Entry(obsSeriesMock));
		entriesStub.put("bar", new Entry(seriesMock1));
		
		assertTrue(slice.isObservable("foo"));
		assertFalse(slice.isObservable("bar"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testIsObservable_ThrowsIfNotExists() {
		slice.isObservable("foo");
	}
	
	@Test
	public void testClose() {
		entriesStub.put("foo", new Entry((TSeries<?>) seriesMock1));
		entriesStub.put("zoo", new Entry((EditableTSeries<?>) seriesMock2));
		entriesStub.put("bar", new Entry((ObservableTSeriesImpl<?>) obsSeriesMock));
		entriesStub.put("buz", new Entry((TSeries<?>) seriesMock3));
		entriesStub.put("gaz", new Entry((EditableTSeries<?>) seriesMock4));
		seriesMock2.clear();
		obsSeriesMock.clear();
		seriesMock4.clear();
		control.replay();
		
		slice.close();
		
		control.verify();
		assertEquals(0, entriesStub.size());
	}

}

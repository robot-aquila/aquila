package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ObservableTSeriesImpl;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.STSeries.Entry;

public class STSeriesTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private EventQueue queueMock;
	private TSeriesNodeStorageKeys storageMock;
	private EditableTSeries<?> seriesMock1, seriesMock2;
	private ObservableTSeriesImpl<?> obsSeriesMock;
	private Map<String, Entry> entriesStub;
	private STSeries service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(EventQueue.class);
		storageMock = control.createMock(TSeriesNodeStorageKeys.class);
		seriesMock1 = control.createMock(EditableTSeries.class);
		seriesMock2 = control.createMock(EditableTSeries.class);
		obsSeriesMock = control.createMock(ObservableTSeriesImpl.class);
		entriesStub = new LinkedHashMap<>();
		service = new STSeries(queueMock, storageMock, entriesStub);
	}
	
	@Test
	public void testGetSharedStorage() {
		assertSame(storageMock, service.getSharedStorage());
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.H1);
		control.replay();
		
		assertEquals(ZTFrame.H1, service.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void testGet1_Key() {
		expect(storageMock.get(T("2018-11-12T10:00:00Z"))).andReturn(T("1995-01-01T00:00:00Z"));
		control.replay();
		
		assertEquals(T("1995-01-01T00:00:00Z"), service.get(T("2018-11-12T10:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testToIndex() {
		expect(storageMock.toIndex(T("2000-11-01T00:00:00Z"))).andReturn(500);
		control.replay();
		
		assertEquals(500, service.toIndex(T("2000-11-01T00:00:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testToKey() throws Exception {
		expect(storageMock.toKey(734)).andReturn(T("2018-12-15T12:22:58Z"));
		control.replay();
		
		assertEquals(T("2018-12-15T12:22:58Z"), service.toKey(734));
		
		control.verify();
	}
	
	@Test
	public void testGetId() {
		expect(storageMock.getId()).andReturn("foobar");
		control.replay();
		
		assertEquals("foobar", service.getId());
		
		control.verify();
	}
	
	@Test
	public void testGet0() throws Exception {
		expect(storageMock.get()).andReturn(T("1998-05-15T15:30:45Z"));
		control.replay();
		
		assertEquals(T("1998-05-15T15:30:45Z"), service.get());
		
		control.verify();
	}
	
	@Test
	public void testGet1_Int() throws Exception {
		expect(storageMock.get(5)).andReturn(T("2005-01-01T12:00:00Z"));
		control.replay();
		
		assertEquals(T("2005-01-01T12:00:00Z"), service.get(5));
		
		control.verify();
	}
	
	@Test
	public void testGetLength() {
		expect(storageMock.getLength()).andReturn(80);
		control.replay();
		
		assertEquals(80, service.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lidMock = control.createMock(LID.class);
		expect(storageMock.getLID()).andReturn(lidMock);
		control.replay();
		
		assertEquals(lidMock, service.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		storageMock.lock();
		control.replay();
		
		service.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		storageMock.unlock();
		control.replay();
		
		service.unlock();
		
		control.verify();
	}
	
	@Test
	public void testOnUpdate() {
		EventType typeMock = control.createMock(EventType.class);
		expect(storageMock.onUpdate()).andReturn(typeMock);
		control.replay();
		
		assertSame(typeMock, service.onUpdate());
		
		control.verify();
	}
	
	@Test
	public void testOnLengthUpdate() {
		EventType typeMock = control.createMock(EventType.class);
		expect(storageMock.onLengthUpdate()).andReturn(typeMock);
		control.replay();
		
		assertSame(typeMock, service.onLengthUpdate());
		
		control.verify();
	}
	
	@Test
	public void testCreateSeries() {
		EditableTSeries<Double> x = service.createSeries("foobar", false);
		
		Entry actual = entriesStub.get("foobar");
		Entry expected = new Entry(x);
		assertEquals(expected, actual);
		assertTrue(expected.isEditable());
		assertFalse(expected.isObservable());
		assertTrue(expected.isCloseable());
	}
	
	@Test
	public void testCreateSeries_Observable() {
		EditableTSeries<Double> x = service.createSeries("zulu24", true);
		
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

		service.createSeries("aquila", false);
	}

	@Test
	public void testRegisterRawSeries_1R() {
		expect(seriesMock1.getId()).andReturn("kappa");
		expect(seriesMock1.getTimeFrame()).andReturn(ZTFrame.M5);
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		service.registerRawSeries((TSeries<?>) seriesMock1);
		
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();

		try {
			service.registerRawSeries((TSeries<?>)seriesMock1);
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();

		try {
			service.registerRawSeries((TSeries<?>) seriesMock2);
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		service.registerRawSeries((TSeries<?>) seriesMock1, "zumba");
		
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			service.registerRawSeries((TSeries<?>)seriesMock1, "boogie");
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			service.registerRawSeries((TSeries<?>) seriesMock2, "gizmo");
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		service.registerRawSeries((EditableTSeries<?>) seriesMock1);
		
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			service.registerRawSeries((EditableTSeries<?>)seriesMock1);
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			service.registerRawSeries((EditableTSeries<?>) seriesMock2);
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		service.registerRawSeries((EditableTSeries<?>) seriesMock1, "zulu24");
		
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			service.registerRawSeries((EditableTSeries<?>)seriesMock1, "kappa");
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
		expect(storageMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		try {
			service.registerRawSeries((EditableTSeries<?>) seriesMock2, "kappa");
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
		
		assertSame(obsSeriesMock, service.getObservableSeries("gamma"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetObservableSeries_ThrowsIfSeriesNotExists() {
		service.getObservableSeries("gamma");
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetObservableSeries_ThrowsIfSeriesNotObservable() {
		entriesStub.put("gamma", new Entry(seriesMock1));
		
		service.getObservableSeries("gamma");
	}
	
	@Test
	public void testGetSeries() {
		entriesStub.put("foo", new Entry(seriesMock1));
		entriesStub.put("bar", new Entry(obsSeriesMock));
		
		assertSame(seriesMock1, service.getSeries("foo"));
		assertSame(obsSeriesMock, service.getSeries("bar"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetSeries_ThrowsIfSeriesNotExists() {
		service.getSeries("foo");
	}
	
	@Test
	public void testIsSeriesExists() {
		entriesStub.put("foo", new Entry(seriesMock2));
		
		assertTrue(service.isSeriesExists("foo"));
		assertFalse(service.isSeriesExists("bar"));
	}
	
	@Test
	public void testIsSeriesObservable() {
		entriesStub.put("foo", new Entry(obsSeriesMock));
		entriesStub.put("bar", new Entry(seriesMock1));
		
		assertTrue(service.isSeriesObservable("foo"));
		assertFalse(service.isSeriesObservable("bar"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testIsSeriesObservable_ThrowsIfNotExists() {
		service.isSeriesObservable("foo");
	}

}

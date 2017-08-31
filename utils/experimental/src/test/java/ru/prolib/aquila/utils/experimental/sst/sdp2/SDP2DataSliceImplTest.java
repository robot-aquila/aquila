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
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.tseries.TSeriesNodeStorage;

public class SDP2DataSliceImplTest {
	private IMocksControl control;
	private EventQueue queueMock;
	private TSeriesNodeStorage storageMock;
	private EditableTSeries<?> seriesMock1, seriesMock2;
	private ObservableTSeriesImpl<?> obsSeriesMock;
	private Symbol symbol;
	private Map<String, Entry> entriesStub;
	private SDP2DataSliceImpl<SDP2Key> slice;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		queueMock = control.createMock(EventQueue.class);
		storageMock = control.createMock(TSeriesNodeStorage.class);
		seriesMock1 = control.createMock(EditableTSeries.class);
		seriesMock2 = control.createMock(EditableTSeries.class);
		obsSeriesMock = control.createMock(ObservableTSeriesImpl.class);
		symbol = new Symbol("GAZP");
		entriesStub = new LinkedHashMap<>();
		slice = new SDP2DataSliceImpl<>(new SDP2Key(TimeFrame.M5, symbol),
				queueMock, storageMock, entriesStub);
	}
	
	@Test
	public void testCtor() {
		assertEquals(new SDP2Key(TimeFrame.M5, symbol), slice.getKey());
		assertEquals(symbol, slice.getSymbol());
		assertSame(TimeFrame.M5, slice.getTimeFrame());
		assertSame(queueMock, slice.getEventQueue());
		assertSame(storageMock, slice.getStorage());
	}
	
	@Test
	public void testCreateSeries() {
		EditableTSeries<Double> x = slice.createSeries("foobar", false);
		
		assertEquals(new Entry(x), entriesStub.get("foobar"));
	}
	
	@Test
	public void testCreateSeries_Observable() {
		EditableTSeries<Double> x = slice.createSeries("zulu24", true);
		
		assertEquals(new Entry((ObservableTSeriesImpl<Double>)x), entriesStub.get("zulu24"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testCreateSeries_ThrowsIfSeriesExists() {
		entriesStub.put("aquila", new Entry(seriesMock1));
		
		slice.createSeries("aquila", false);
	}
	
	@Test
	public void testRegisterRawSeries() {
		expect(seriesMock1.getId()).andReturn("kappa");
		control.replay();
		
		slice.registerRawSeries(seriesMock1);
		
		control.verify();
		assertEquals(new Entry(seriesMock1), entriesStub.get("kappa"));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testRegisterRawSeries_ThrowsIfSeriesExists() {
		entriesStub.put("kappa", new Entry(seriesMock1));
		expect(seriesMock2.getId()).andReturn("kappa");
		control.replay();
		
		slice.registerRawSeries(seriesMock2);
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

}

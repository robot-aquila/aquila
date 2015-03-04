package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.function.MAFunction;
import ru.prolib.aquila.core.utils.Variant;

public class ComplexIndicatorTest {
	private IMocksControl control;
	private ComplexFunction<Double, Double> fn;
	private IndicatorEventDispatcher dispatcher;
	private EditableDataSeries sourceSeries, ownSeries;
	private EventType type;
	private ComplexIndicator<Double, Double> indicator;
	private EventSystem es;
	
	class StubFunction implements ComplexFunction<Double, Double> {

		@Override
		public Double calculate(Series<Double> sourceSeries,
				Series<Double> ownSeries, int index) throws ValueException
		{
			return sourceSeries.get(index) + 5d;
		}

		@Override
		public String getDefaultId() {
			return "zoom";
		}
		
		@Override
		public boolean equals(Object other) {
			return other != null && other.getClass() == StubFunction.class;
		}
		
	}

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		control = createStrictControl();
		dispatcher = control.createMock(IndicatorEventDispatcher.class);
		type = control.createMock(EventType.class);
		fn = new StubFunction();
		sourceSeries = new DataSeriesImpl(es);
		ownSeries = new DataSeriesImpl(es);
		indicator = new ComplexIndicator<Double, Double>("foobar", fn,
				sourceSeries, ownSeries, dispatcher);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testGetId_IfSpecified() throws Exception {
		assertEquals("foobar", indicator.getId());
	}
	
	@Test
	public void testGetId_IfUnspecified() throws Exception {
		indicator = new ComplexIndicator<Double, Double>(null, fn, sourceSeries,
				ownSeries, dispatcher);
		assertEquals("zoom", indicator.getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		ownSeries.add(214d);
		
		assertEquals(214d, indicator.get(), 0.01d);
	}
	
	@Test
	public void testGet1() throws Exception {
		ownSeries.add(214d);
		ownSeries.add(421d);
		
		assertEquals(214d, indicator.get(0), 0.01d);
		assertEquals(421d, indicator.get(1), 0.01d);
	}
	
	@Test
	public void testGetLength() throws Exception {
		assertEquals(0, indicator.getLength());
		
		ownSeries.add(214d);
		assertEquals(1, indicator.getLength());
		
		ownSeries.add(421d);
		assertEquals(2, indicator.getLength());
	}
	
	@Test
	public void testOnAdded() throws Exception {
		expect(dispatcher.OnAdded()).andReturn(type);
		control.replay();
		
		assertSame(type, indicator.OnAdded());
		
		control.verify();
	}
	
	@Test
	public void testOnUpdated() throws Exception {
		expect(dispatcher.OnUpdated()).andReturn(type);
		control.replay();
		
		assertSame(type, indicator.OnUpdated());
		
		control.verify();
	}
	
	@Test
	public void testOnStarted() throws Exception {
		expect(dispatcher.OnStarted()).andReturn(type);
		control.replay();
		
		assertSame(type, indicator.OnStarted());
		
		control.verify();
	}
	
	@Test
	public void testOnStopped() throws Exception {
		expect(dispatcher.OnStopped()).andReturn(type);
		control.replay();
		
		assertSame(type, indicator.OnStopped());
		
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		// Результирующий ряд будет очищен
		ownSeries.add(12.34d);
		ownSeries.add(18.15d);
		
		// Будет осуществлен перерасчет для существующего исходного ряда
		sourceSeries.add(1d);
		sourceSeries.add(2d);
		sourceSeries.add(3d);
		
		dispatcher.startRelayFor(same(ownSeries));
		dispatcher.fireStarted();
		control.replay();
		
		indicator.start();
		
		control.verify();
		assertTrue(indicator.started());
		
		// Расчитанный ряд
		assertEquals(3, ownSeries.getLength());
		assertEquals(6d, ownSeries.get(0), 0.01d);
		assertEquals(7d, ownSeries.get(1), 0.01d);
		assertEquals(8d, ownSeries.get(2), 0.01d);
		
		// На исходный ряд должен быть подписан локальный обработчик
		assertTrue(sourceSeries.OnAdded().isListener(indicator));
		assertTrue(sourceSeries.OnUpdated().isListener(indicator));
	}
	
	@Test (expected=StarterException.class)
	public void testStart_ThrowsIfStarted() throws Exception {
		indicator.start();
		indicator.start();
	}
	
	@Test
	public void testStop() throws Exception {
		indicator.started = true;
		sourceSeries.OnAdded().addListener(indicator);
		sourceSeries.OnUpdated().addListener(indicator);
		
		dispatcher.stopRelay();
		dispatcher.fireStopped();
		control.replay();
		
		indicator.stop();
		
		control.verify();
		assertFalse(indicator.started());
		assertFalse(sourceSeries.OnAdded().isListener(indicator));
		assertFalse(sourceSeries.OnUpdated().isListener(indicator));
	}
	
	@Test
	public void testStop_SkipsIfNotStarted() throws Exception {
		indicator.stop();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSourceSeriesAdded() throws Exception {
		indicator = new ComplexIndicator<Double, Double>(null, fn, sourceSeries,
				ownSeries, new IndicatorEventDispatcher(es));
		indicator.start();
		
		final List<Event> actual = new Vector<Event>();
		EventListener listener = new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		};
		indicator.OnAdded().addSyncListener(listener);

		sourceSeries.add(10d);
		sourceSeries.add(20d);
		sourceSeries.add(30d);
		
		// test result
		assertEquals(15d, indicator.get(0), 0.001d);
		assertEquals(25d, indicator.get(1), 0.001d);
		assertEquals(35d, indicator.get(2), 0.001d);
		
		// test events
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) indicator.OnAdded(), 15d, 0));
		expected.add(new ValueEvent((EventTypeSI) indicator.OnAdded(), 25d, 1));
		expected.add(new ValueEvent((EventTypeSI) indicator.OnAdded(), 35d, 2));
		assertEquals(expected, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSourceSeriesUpdated() throws Exception {
		indicator = new ComplexIndicator<Double, Double>(null, fn, sourceSeries,
				ownSeries, new IndicatorEventDispatcher(es));
		indicator.start();
		
		final List<Event> actual = new Vector<Event>();
		EventListener listener = new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		};
		indicator.OnUpdated().addSyncListener(listener);
		
		sourceSeries.add(150d);
		sourceSeries.add(213d);
		sourceSeries.set(421d);
		sourceSeries.set(602d);
		
		// test result
		assertEquals(607d, indicator.get(), 0.001d);
		
		// test events
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) indicator.OnUpdated(), 218d, 426d, 1));
		expected.add(new ValueEvent((EventTypeSI) indicator.OnUpdated(), 426d, 607d, 1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(indicator.equals(indicator));
		assertFalse(indicator.equals(null));
		assertFalse(indicator.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("foobar")
			.add(null);
		Variant<ComplexFunction<Double, Double>> vFn =
				new Variant<ComplexFunction<Double, Double>>(vId)
			.add(fn)
			.add(control.createMock(MAFunction.class));
		Variant<DataSeries> vSrc = new Variant<DataSeries>(vFn)
			.add(sourceSeries)
			.add(control.createMock(DataSeries.class));
		Variant<EditableDataSeries> vOwn = new Variant<EditableDataSeries>(vSrc)
			.add(ownSeries)
			.add(control.createMock(EditableDataSeries.class));
		Variant<?> iterator = vOwn;
		int foundCnt = 0;
		ComplexIndicator<Double, Double> x, found = null;
		do {
			x = new ComplexIndicator<Double, Double>(vId.get(), vFn.get(),
					vSrc.get(), vOwn.get(), dispatcher);
			if ( indicator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(fn, found.getFunction());
		assertSame(sourceSeries, found.getSourceSeries());
		assertSame(ownSeries, found.getOwnSeries());
		assertEquals("foobar", found.getId());
	}

}

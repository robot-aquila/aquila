package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

public class SimpleIndicatorTest {
	private EventSystem es;
	private IMocksControl control;
	private SimpleFunction<Double, Double> fn;
	private IndicatorEventDispatcher dispatcher;
	private EditableDataSeries sourceSeries;
	private EventType type;
	private SimpleIndicator<Double, Double> indicator;
	
	class StubFunction implements SimpleFunction<Double, Double> {

		@Override
		public Double calculate(Series<Double> sourceSeries, int index)
			throws ValueException
		{
			return sourceSeries.get(index) * 2d;
		}

		@Override
		public String getDefaultId() {
			return "bam";
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
		indicator = new SimpleIndicator<Double, Double>("foobar", fn,
				sourceSeries, dispatcher);
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
		indicator = new SimpleIndicator<Double, Double>(null, fn, sourceSeries,
				dispatcher);
		assertEquals("bam", indicator.getId());
	}
	
	@Test
	public void testGet0() throws Exception {
		sourceSeries.add(80d);
		
		assertEquals(160d, indicator.get(), 0.01d);
	}

	@Test
	public void testGet1() throws Exception {
		sourceSeries.add(214d);
		sourceSeries.add(421d);
		
		assertEquals(428d, indicator.get(0), 0.01d);
		assertEquals(842d, indicator.get(1), 0.01d);
	}
	
	@Test
	public void testGetLength() throws Exception {
		assertEquals(0, indicator.getLength());
		
		sourceSeries.add(214d);
		assertEquals(1, indicator.getLength());
		
		sourceSeries.add(421d);
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
		dispatcher.fireStarted();
		control.replay();
		
		indicator.start();
		
		control.verify();
		assertTrue(indicator.started());
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
		indicator = new SimpleIndicator<Double, Double>(null, fn, sourceSeries,
				new IndicatorEventDispatcher(es));
		indicator.start();
		
		final List<Event> actual = new Vector<Event>();
		EventListener listener = new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		};
		indicator.OnAdded().addListener(listener);

		sourceSeries.add(10d);
		sourceSeries.add(20d);
		sourceSeries.add(30d);
		
		// test result
		assertEquals(20d, indicator.get(0), 0.001d);
		assertEquals(40d, indicator.get(1), 0.001d);
		assertEquals(60d, indicator.get(2), 0.001d);
		
		// test events
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) indicator.OnAdded(), 20d, 0));
		expected.add(new ValueEvent((EventTypeSI) indicator.OnAdded(), 40d, 1));
		expected.add(new ValueEvent((EventTypeSI) indicator.OnAdded(), 60d, 2));
		assertEquals(expected, actual);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSourceSeriesUpdated() throws Exception {
		indicator = new SimpleIndicator<Double, Double>(null, fn, sourceSeries,
				new IndicatorEventDispatcher(es));
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
		assertEquals(1204d, indicator.get(), 0.001d);
		
		// test events
		List<Event> expected = new Vector<Event>();
		expected.add(new ValueEvent((EventTypeSI) indicator.OnUpdated(),  842d, 1));
		expected.add(new ValueEvent((EventTypeSI) indicator.OnUpdated(), 1204d, 1));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(indicator.equals(indicator));
		assertFalse(indicator.equals(null));
		assertFalse(indicator.equals(this));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<String> vId = new Variant<String>()
			.add("foobar")
			.add(null);
		Variant<SimpleFunction<Double, Double>> vFn =
				new Variant<SimpleFunction<Double, Double>>(vId)
			.add(fn)
			.add(control.createMock(SimpleFunction.class));
		Variant<DataSeries> vSrc = new Variant<DataSeries>(vFn)
			.add(sourceSeries)
			.add(control.createMock(DataSeries.class));
		Variant<?> iterator = vSrc;
		int foundCnt = 0;
		SimpleIndicator<Double, Double> x, found = null;
		do {
			x = new SimpleIndicator<Double, Double>(vId.get(), vFn.get(),
					vSrc.get(), dispatcher);
			if ( indicator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(fn, found.getFunction());
		assertSame(sourceSeries, found.getSourceSeries());
		assertEquals("foobar", found.getId());
	}
	
}

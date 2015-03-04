package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.function.*;
import ru.prolib.aquila.core.utils.Variant;

public class EnvelopeBandTest {
	private EventSystem es;
	private IMocksControl control;
	private EnvelopeFunction fn;
	private DataSeries sourceSeries;
	private IndicatorEventDispatcher dispatcher;
	private EnvelopeBand indicator;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		control = createStrictControl();
		fn = control.createMock(EnvelopeFunction.class);
		sourceSeries = new DataSeriesImpl(es);
		dispatcher = new IndicatorEventDispatcher(es);
		indicator = new EnvelopeBand("foobar", fn, sourceSeries, dispatcher);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testBaseClass() throws Exception {
		assertTrue(indicator instanceof SimpleIndicator);
	}
	
	@Test
	public void testConstruct4Full() throws Exception {
		assertEquals("foobar", indicator.getId());
		assertSame(fn, indicator.getFunction());
		assertSame(sourceSeries, indicator.getSourceSeries());
		assertSame(dispatcher, indicator.getEventDispatcher());
	}
	
	@Test
	public void testConstruct4() throws Exception {
		indicator = new EnvelopeBand(es, "foobar", sourceSeries, true, 1.5d);
		assertEquals("foobar", indicator.getId());
		assertEquals(new EnvelopeFunction(true, 1.5d), indicator.getFunction());
		assertSame(sourceSeries, indicator.getSourceSeries());
		assertNotNull(indicator.getEventDispatcher());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		indicator = new EnvelopeBand(es, sourceSeries, false, 0.5d);
		assertEquals("EnvelopeLower(0.5)", indicator.getId());
		assertEquals(new EnvelopeFunction(false, 0.5d), indicator.getFunction());
		assertSame(sourceSeries, indicator.getSourceSeries());
		assertNotNull(indicator.getEventDispatcher());
	}

	@Test
	public void testSetOffset() throws Exception {
		fn.setOffset(eq(2.5d));
		control.replay();
		
		indicator.setOffset(2.5d);
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetOffset_ThrowsIfStarted() throws Exception {
		indicator.start();
		
		indicator.setOffset(2.5d);
	}
	
	@Test
	public void testGetOffset() throws Exception {
		expect(fn.getOffset()).andReturn(1.8d);
		control.replay();
		
		assertEquals(1.8d, indicator.getOffset(), 0.01d);
		
		control.verify();
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
			.add("zulu24");
		Variant<EnvelopeFunction> vFn = new Variant<EnvelopeFunction>()
			.add(fn)
			.add(control.createMock(EnvelopeFunction.class));
		Variant<DataSeries> vSrc = new Variant<DataSeries>(vFn)
			.add(sourceSeries)
			.add(new DataSeriesImpl(es, "zerg"));
		Variant<?> iterator = vSrc;
		int foundCnt = 0;
		EnvelopeBand x, found = null;
		do {
			x = new EnvelopeBand(vId.get(), vFn.get(), vSrc.get(), dispatcher);
			if ( indicator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foobar", found.getId());
		assertSame(fn, found.getFunction());
		assertSame(sourceSeries, found.getSourceSeries());
	}
	
}

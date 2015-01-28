package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.function.MAFunction;
import ru.prolib.aquila.core.utils.Variant;

public class MATest {
	private IMocksControl control;
	private MAFunction fn;
	private DataSeries sourceSeries;
	private EditableDataSeries ownSeries;
	private IndicatorEventDispatcher dispatcher;
	private MA indicator;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		fn = control.createMock(MAFunction.class);
		sourceSeries = new DataSeriesImpl();
		ownSeries = new DataSeriesImpl();
		dispatcher = new IndicatorEventDispatcher();
		indicator = new MA("zulu", fn, sourceSeries, ownSeries, dispatcher);
	}
	
	@Test
	public void testBaseClass() throws Exception {
		assertTrue(indicator instanceof ComplexIndicator);
	}
	
	@Test
	public void testConstruct5() throws Exception {
		assertSame(fn, indicator.getFunction());
		assertSame(sourceSeries, indicator.getSourceSeries());
		assertSame(ownSeries, indicator.getOwnSeries());
		assertSame(dispatcher, indicator.getEventDispatcher());
		assertEquals("zulu", indicator.getId());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		indicator = new MA("foobar", fn, sourceSeries);
		assertSame(fn, indicator.getFunction());
		assertSame(sourceSeries, indicator.getSourceSeries());
		assertEquals(new DataSeriesImpl(), indicator.getOwnSeries());
		assertNotNull(indicator.getEventDispatcher());
		assertEquals("foobar", indicator.getId());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		expect(fn.getDefaultId()).andReturn("zoom");
		control.replay();
		
		indicator = new MA(fn, sourceSeries);
		assertSame(fn, indicator.getFunction());
		assertSame(sourceSeries, indicator.getSourceSeries());
		assertEquals(new DataSeriesImpl(), indicator.getOwnSeries());
		assertNotNull(indicator.getEventDispatcher());
		assertEquals("zoom", indicator.getId());
		
		control.verify();
	}
	
	@Test
	public void testSetPeriod() throws Exception {
		fn.setPeriod(eq(213));
		control.replay();

		indicator.setPeriod(213);
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testSetPeriod_ThrowsIfStarted() throws Exception {
		indicator.start();
		
		indicator.setPeriod(22);
	}
	
	@Test
	public void testGetPeriod() throws Exception {
		expect(fn.getPeriod()).andReturn(91);
		control.replay();
		
		assertEquals(91, indicator.getPeriod());
		
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
			.add("zulu")
			.add("zoom");
		Variant<MAFunction> vFn = new Variant<MAFunction>(vId)
			.add(fn)
			.add(control.createMock(MAFunction.class));
		Variant<DataSeries> vSrc = new Variant<DataSeries>(vFn)
			.add(sourceSeries)
			.add(new DataSeriesImpl("gamma"));
		Variant<EditableDataSeries> vOwn = new Variant<EditableDataSeries>(vSrc)
			.add(ownSeries)
			.add(new DataSeriesImpl("beta"));
		Variant<?> iterator = vOwn;
		int foundCnt = 0;
		MA x, found = null;
		do {
			x = new MA(vId.get(), vFn.get(), vSrc.get(), vOwn.get(), dispatcher);
			if ( indicator.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("zulu", found.getId());
		assertSame(fn, found.getFunction());
		assertSame(sourceSeries, found.getSourceSeries());
		assertSame(ownSeries, found.getOwnSeries());
	}

}

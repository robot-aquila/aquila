package ru.prolib.aquila.core.indicator;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.indicator.function.*;

public class EnvelopesTest {
	private IMocksControl control;
	private DataSeries sourceSeries;
	private MA movingAverage;
	private EnvelopeBand upper, lower;
	private Envelopes indicator;

	@Before
	public void setUp() throws Exception {
		sourceSeries = new DataSeriesImpl();
		control = createStrictControl();
		movingAverage = control.createMock(MA.class);
		upper = control.createMock(EnvelopeBand.class);
		lower = control.createMock(EnvelopeBand.class);
		indicator = new Envelopes(movingAverage, upper, lower);
	}
	
	@Test
	public void testConstruct3_WithInstances() throws Exception {
		assertSame(movingAverage, indicator.getMovingAverageSeries());
		assertSame(upper, indicator.getUpperSeries());
		assertSame(lower, indicator.getLowerSeries());
	}
	
	@Test
	public void testConstruct4_WithFN() throws Exception {
		MAFunction fn = control.createMock(MAFunction.class);
		MA expectedMa = new MA("zulu", fn, sourceSeries);
		Envelopes expected = new Envelopes(expectedMa,
				new EnvelopeBand(expectedMa, true, 1.5d),
				new EnvelopeBand(expectedMa, false, 1.5d));
		assertEquals(expected, new Envelopes("zulu", sourceSeries, fn, 1.5d));
	}
	
	@Test
	public void testConstruct3_WithFN() throws Exception {
		MAFunction fn = control.createMock(MAFunction.class);
		MA expectedMa = new MA(fn, sourceSeries);
		Envelopes expected = new Envelopes(expectedMa,
				new EnvelopeBand(expectedMa, true, 0.5d),
				new EnvelopeBand(expectedMa, false, 0.5d));
		assertEquals(expected, new Envelopes(sourceSeries, fn, 0.5d));
	}
	
	@Test
	public void testConstruct4_WithPeriod() throws Exception {
		MA expectedMa = new MA("foo", new QuikEMAFunction(5), sourceSeries);
		Envelopes expected = new Envelopes(expectedMa,
				new EnvelopeBand(expectedMa, true, 1.0d),
				new EnvelopeBand(expectedMa, false, 1.0d));
		assertEquals(expected, new Envelopes("foo", sourceSeries, 5, 1.0d));
	}
	
	@Test
	public void testConstruct3_WithPeriod() throws Exception {
		MA expectedMa = new MA(new QuikEMAFunction(9), sourceSeries);
		Envelopes expected = new Envelopes(expectedMa,
				new EnvelopeBand(expectedMa, true, 0.3d),
				new EnvelopeBand(expectedMa, false, 0.3d));
		assertEquals(expected, new Envelopes(sourceSeries, 9, 0.3d));
	}
	
	@Test
	public void testConstruct2() throws Exception {
		MA expectedMa = new MA(new QuikEMAFunction(7), sourceSeries);
		Envelopes expected = new Envelopes(expectedMa,
				new EnvelopeBand(expectedMa, true, 2.0d),
				new EnvelopeBand(expectedMa, false, 2.0d));
		assertEquals(expected, new Envelopes(sourceSeries, 7));
	}
	
	@Test
	public void testGetMovingAverageSeries() throws Exception {
		assertSame(movingAverage, indicator.getMovingAverageSeries());
	}
	
	@Test
	public void testGetUpperSeries() throws Exception {
		assertSame(upper, indicator.getUpperSeries());
	}
	
	@Test
	public void testGetLowerSeries() throws Exception {
		assertSame(lower, indicator.getLowerSeries());
	}
	
	@Test
	public void testGetMovingAverage0() throws Exception {
		expect(movingAverage.get()).andReturn(215.34d);
		control.replay();
		
		assertEquals(215.34d, indicator.getMovingAverage(), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGetMovingAverage1() throws Exception {
		expect(movingAverage.get(eq(12))).andReturn(422.12d);
		control.replay();
		
		assertEquals(422.12d, indicator.getMovingAverage(12), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGetUpper0() throws Exception {
		expect(upper.get()).andReturn(320.91d);
		control.replay();
		
		assertEquals(320.91d, indicator.getUpper(), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGetUpper1() throws Exception {
		expect(upper.get(eq(62))).andReturn(291.14d);
		control.replay();
		
		assertEquals(291.14d, indicator.getUpper(62), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGetLower0() throws Exception {
		expect(lower.get()).andReturn(821.56d);
		control.replay();
		
		assertEquals(821.56d, indicator.getLower(), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGetLower1() throws Exception {
		expect(lower.get(eq(5))).andReturn(115.71d);
		control.replay();
		
		assertEquals(115.71d, indicator.getLower(5), 0.001d);
		
		control.verify();
	}
	
	@Test
	public void testGetLength() throws Exception {
		expect(movingAverage.getLength()).andReturn(415);
		control.replay();
		
		assertEquals(415, indicator.getLength());
		
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		upper.start();
		lower.start();
		movingAverage.start();
		control.replay();
		
		indicator.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		movingAverage.stop();
		upper.stop();
		lower.stop();
		control.replay();
		
		indicator.stop();
		
		control.verify();
	}
	
	@Test
	public void testStarted() throws Exception {
		expect(movingAverage.started()).andReturn(true);
		control.replay();
		
		assertTrue(indicator.started());
		
		control.verify();
	}
	
	@Test
	public void testSetPeriod() throws Exception {
		movingAverage.setPeriod(eq(20));
		control.replay();
		
		indicator.setPeriod(20);
		
		control.verify();
	}
	
	@Test
	public void testGetPeriod() throws Exception {
		expect(movingAverage.getPeriod()).andReturn(40);
		control.replay();
		
		assertEquals(40, indicator.getPeriod());
		
		control.verify();
	}
	
	@Test
	public void testSetOffset() throws Exception {
		upper.setOffset(eq(3.0d));
		lower.setOffset(eq(3.0d));
		control.replay();
		
		indicator.setOffset(3.0d);
		
		control.verify();
	}
	
	@Test
	public void testGetOffset() throws Exception {
		expect(upper.getOffset()).andReturn(2.5d);
		control.replay();
		
		assertEquals(2.5d, indicator.getOffset(), 0.001d);
		
		control.verify();
	}

}

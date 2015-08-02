package ru.prolib.aquila.core.indicator.function;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

public class EnvelopeFunctionTest {
	// фикстура для конверта с коэффициентом +/- 0.5
	private static final Double[][] fixture05 = {
		// value, upper, lower
		{ null,			  null,			  null			 },
		{ 149068.116109d, 149813.456689d, 148322.775528d },
		{ 149061.272587d, 149806.578950d, 148315.966224d },
		{ 149056.908946d, 149802.193491d, 148311.624401d },
	};
	private EditableSeries<Double> sourceSeries;
	private EnvelopeFunction upper, lower;
	private EventSystem es;

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		sourceSeries = new SeriesImpl<Double>(es);
		upper = new EnvelopeFunction(true, 0.5d);
		lower = new EnvelopeFunction(false, 0.5d);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testCalculate() throws Exception {
		for ( int i = 0; i < fixture05.length; i ++ ) {
			String msg = "At #" + i;
			sourceSeries.add(fixture05[i][0]);
			Double expectedUpper = fixture05[i][1],
				expectedLower = fixture05[i][2],
				actualUpper = upper.calculate(sourceSeries, i),
				actualLower = lower.calculate(sourceSeries, i);
			if ( expectedUpper == null ) {
				assertNull(msg, actualUpper);
			} else {
				assertEquals(msg, expectedUpper, actualUpper, 0.000001d);
			}
			if ( expectedLower == null ) {
				assertNull(msg, actualLower);
			} else {
				assertEquals(msg, expectedLower, actualLower, 0.000001d);
			}
		}
	}
	
	@Test
	public void testInternalState() throws Exception {
		assertTrue(upper.isUpper());
		assertTrue(lower.isLower());
		assertFalse(upper.isLower());
		assertFalse(lower.isUpper());
		assertEquals(0.5d, upper.getOffset(), 0.01d);
		assertEquals(0.5d, lower.getOffset(), 0.01d);
	}
	
	@Test
	public void testSetOffset() throws Exception {
		upper.setOffset(2.0d);
		assertEquals(2.0d, upper.getOffset(), 0.01d);
	}

	@Test
	public void testEquals() throws Exception {
		assertTrue(lower.equals(lower));
		assertFalse(lower.equals(upper));
		assertFalse(lower.equals(null));
		assertFalse(lower.equals(this));
		assertTrue(lower.equals(new EnvelopeFunction(false, 0.5d)));
		assertFalse(lower.equals(new EnvelopeFunction(true, 0.5d)));
	}
	
	@Test
	public void testGetDefaultId() throws Exception {
		assertEquals("EnvelopeUpper(0.5)", upper.getDefaultId());
		assertEquals("EnvelopeLower(0.5)", lower.getDefaultId());
	}

}

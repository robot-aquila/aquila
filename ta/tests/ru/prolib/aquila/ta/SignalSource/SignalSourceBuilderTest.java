package ru.prolib.aquila.ta.SignalSource;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.ChaosTheory.AssetImpl;
import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.SignalSource.PatternMatcher.*;

public class SignalSourceBuilderTest {
	private TestValue<Double> src1,src2,src3;
	private SignalSourceBuilder builder;
	private AssetImpl asset;

	@Before
	public void setUp() throws Exception {
		asset = new AssetImpl("RTS", "SPBFUT", 5, 0);
		src1 = new TestValue<Double>();
		src2 = new TestValue<Double>();
		src3 = new TestValue<Double>();
		builder = new SignalSourceBuilder(asset);
	}
	
	/**
	 * Проверить структуру источника сигналов на базе Awesome Oscillator.
	 * @param ss
	 * @param ao
	 * @param hi
	 * @param lo
	 */
	private void assertAwesomeOscillatorSource(ISignalSource ss,
			Value<?> ao, Value<?> hi, Value<?> lo)
	{
		assertNotNull(ss);
		// композитная форма
		CompositeSignalSource css = (CompositeSignalSource) ss;
		ISignalSource[] sslist = css.getSignalSources();
		// 4 сигнальных шаблона 
		assertEquals(4, sslist.length);
		
		PatternSignalSource ph = null;
		OffsetPriceCalculator pc = null;
		PatternMatcher1Value opm = null;
		
		// Первый шаблон - поиск блюдца на покупку
		ph = (PatternSignalSource) sslist[0];
		assertEquals("AO#BS", ph.getComment());
		assertEquals(Signal.BUY, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(1.0d, pc.getMul(), 0.01d);
		assertSame(hi, pc.getSourceValue());
		assertSame(asset, pc.getAsset());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ao, opm.getSourceValue());
		assertTrue(opm instanceof AO_BuySaucer);
		
		// Второй шаблон - поиск пересечения нулевой линии снизу
		ph = (PatternSignalSource) sslist[1];
		assertEquals("AO#BZC", ph.getComment());
		assertEquals(Signal.BUY, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(1.0, pc.getMul(), 0.01d);
		assertSame(hi, pc.getSourceValue());
		assertSame(asset, pc.getAsset());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ao, opm.getSourceValue());
		assertTrue(opm instanceof AO_BuyZeroCross);

		// Третий шаблон - блюдце на продажу
		ph = (PatternSignalSource) sslist[2];
		assertEquals("AO#SS", ph.getComment());
		assertEquals(Signal.SELL, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(-1.0d, pc.getMul(), 0.01d);
		assertSame(lo, pc.getSourceValue());
		assertSame(asset, pc.getAsset());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ao, opm.getSourceValue());
		assertTrue(opm instanceof AO_SellSaucer);
		
		// Четвертый шаблон - поиск пересечения нулевой линии сверху
		ph = (PatternSignalSource) sslist[3];
		assertEquals("AO#SZC", ph.getComment());
		assertEquals(Signal.SELL, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(-1.0d, pc.getMul(), 0.01d);
		assertSame(lo, pc.getSourceValue());
		assertSame(asset, pc.getAsset());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ao, opm.getSourceValue());
		assertTrue(opm instanceof AO_SellZeroCross);
	}
	
	private void assertFractalSource(ISignalSource ss,
			Value<Double> hi, Value<Double> lo,
			Value<Double> filter)
	{
		PatternSignalSource ph = null;
		FractalPriceCalculator pc = null;
		PatternMatcher2Value opm = null;
		
		assertNotNull(ss);
		// композитная форма
		CompositeSignalSource css = (CompositeSignalSource) ss;
		ISignalSource[] sslist = css.getSignalSources();
		// 2 сигнальных шаблона 
		assertEquals(2, sslist.length);
		
		// Первый шаблон - фрактал вверх
		ph = (PatternSignalSource) sslist[0];
		assertEquals("F#B", ph.getComment());
		assertEquals(Signal.BUY, ph.getType());
		pc = (FractalPriceCalculator) ph.getPriceCalculator();
		assertEquals(1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(hi, pc.getSourceValue());
		assertEquals(5, pc.getPeriods());
		
		opm = (PatternMatcher2Value) ph.getPatternMatcher();
		assertSame(hi, opm.getSourceValue1());
		assertSame(filter, opm.getSourceValue2());
		assertTrue(opm instanceof Fractal_BuyFiltered);
		
		// Второй шаблон - фрактал вниз
		ph = (PatternSignalSource) sslist[1];
		assertEquals("F#S", ph.getComment());
		assertEquals(Signal.SELL, ph.getType());
		pc = (FractalPriceCalculator) ph.getPriceCalculator();
		assertEquals(-1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(lo, pc.getSourceValue());
		assertEquals(5, pc.getPeriods());
		
		opm = (PatternMatcher2Value) ph.getPatternMatcher();
		assertSame(lo, opm.getSourceValue1());
		assertSame(filter, opm.getSourceValue2());
		assertTrue(opm instanceof Fractal_SellFiltered);
	}
	
	/**
	 * Проверить структуру источника сигналов на основе Acceleration Oscillator.
	 * @param ss
	 * @param ac
	 * @param hi
	 * @param lo
	 */
	private void assertAccelOscillatorSource(ISignalSource ss,
			Value<?> ac, Value<?> hi, Value<?> lo)
	{
		assertNotNull(ss);
		// композитная форма
		CompositeSignalSource css = (CompositeSignalSource) ss;
		ISignalSource[] sslist = css.getSignalSources();
		// 4 сигнальных шаблона 
		assertEquals(4, sslist.length);
		
		PatternSignalSource ph = null;
		OffsetPriceCalculator pc = null;
		PatternMatcher1Value opm = null;
		
		// Первый шаблон - покупка выше нулевой линии
		ph = (PatternSignalSource) sslist[0];
		assertEquals("AC#BAZ", ph.getComment());
		assertEquals(Signal.BUY, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(hi, pc.getSourceValue());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ac, opm.getSourceValue());
		assertTrue(opm instanceof AC_BuyAboveZero);

		// Второй шаблон - покупка ниже нулевой линии
		ph = (PatternSignalSource) sslist[1];
		assertEquals("AC#BBZ", ph.getComment());
		assertEquals(Signal.BUY, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(hi, pc.getSourceValue());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ac, opm.getSourceValue());
		assertTrue(opm instanceof AC_BuyBelowZero);
		
		// Третий шаблон - продажа выше нулевой линии
		ph = (PatternSignalSource) sslist[2];
		assertEquals("AC#SAZ", ph.getComment());
		assertEquals(Signal.SELL, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(-1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(lo, pc.getSourceValue());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ac, opm.getSourceValue());
		assertTrue(opm instanceof AC_SellAboveZero);

		// Третий шаблон - продажа ниже нулевой линии
		ph = (PatternSignalSource) sslist[3];
		assertEquals("AC#SBZ", ph.getComment());
		assertEquals(Signal.SELL, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(-1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(lo, pc.getSourceValue());
		opm = (PatternMatcher1Value) ph.getPatternMatcher();
		assertSame(ac, opm.getSourceValue());
		assertTrue(opm instanceof AC_SellBelowZero);
	}
	
	private void assertWilliamsZonesSource(ISignalSource ss,
			Value<Integer> wz, Value<Double> close,
			Value<Double> high, Value<Double> low)
	{
		assertNotNull(ss);
		// композитная форма
		CompositeSignalSource css = (CompositeSignalSource) ss;
		ISignalSource[] sslist = css.getSignalSources();
		// 2 сигнальных шаблона 
		assertEquals(2, sslist.length);
		
		PatternSignalSource ph = null;
		OffsetPriceCalculator pc = null;
		
		// Первый шаблон - покупка двух зеленых баров
		ph = (PatternSignalSource) sslist[0];
		assertEquals("WZ#BG", ph.getComment());
		assertEquals(Signal.BUY, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(high, pc.getSourceValue());
		WilliamsZones_Buy bm = (WilliamsZones_Buy) ph.getPatternMatcher();
		assertSame(wz, bm.getWilliamsZones());
		assertSame(close, bm.getSourceValue());

		// Второй шаблон - продажа двух красных баров
		ph = (PatternSignalSource) sslist[1];
		assertEquals("WZ#SR", ph.getComment());
		assertEquals(Signal.SELL, ph.getType());
		pc = (OffsetPriceCalculator) ph.getPriceCalculator();
		assertEquals(-1.0d, pc.getMul(), 0.01d);
		assertSame(asset, pc.getAsset());
		assertSame(low, pc.getSourceValue());
		WilliamsZones_Sell sm = (WilliamsZones_Sell) ph.getPatternMatcher();
		assertSame(wz, sm.getWilliamsZones());
		assertSame(close, sm.getSourceValue());
	}
	
	@Test
	public void testFromAwesomeOscillator() {
		ISignalSource ss = builder.fromAwesomeOscillator(src1, src2, src3);
		this.assertAwesomeOscillatorSource(ss, src1, src2, src3);
	}
	
	@Test
	public void testFromFractal() {
		ISignalSource ss = builder.fromFractal(src1, src2, src3);
		this.assertFractalSource(ss, src1, src2, src3);
	}
	
	@Test
	public void testFromAccelDecelOscillator() {
		ISignalSource ss = builder.fromAccelOscillator(src1, src2, src3);
		this.assertAccelOscillatorSource(ss, src1, src2, src3);
	}
	
	@Test
	public void testFromWilliamsZones() {
		TestValue<Integer> wz = new TestValue<Integer>();
		ISignalSource ss = builder.fromWilliamsZones(wz, src1, src2, src3);
		this.assertWilliamsZonesSource(ss, wz, src1, src2, src3);
	}

}

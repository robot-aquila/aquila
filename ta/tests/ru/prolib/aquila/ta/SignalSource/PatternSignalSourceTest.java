package ru.prolib.aquila.ta.SignalSource;

import org.junit.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import ru.prolib.aquila.ta.*;

public class PatternSignalSourceTest {
	private IPriceCalculator calculator;
	private IPatternMatcher matcher;
	private PatternSignalSource ss;
	private ISignalTranslator translator;

	@Before
	public void setUp() throws Exception {
		calculator = createMock(IPriceCalculator.class);
		matcher = createMock(IPatternMatcher.class);
		translator = createMock(ISignalTranslator.class);
	}
	
	@Test
	public void testAccessors() {
		ss = new PatternSignalSource(Signal.SELL, matcher, calculator, "zulu");
		assertEquals(Signal.SELL, ss.getType());
		assertSame(matcher, ss.getPatternMatcher());
		assertSame(calculator, ss.getPriceCalculator());
		assertEquals("zulu", ss.getComment());
	}
	
	@Test
	public void testAnalyze_NotMatched() throws Exception {
		ss = new PatternSignalSource(Signal.BUY, matcher, calculator, "foobar");
		expect(matcher.matches()).andReturn(false);
		replay(matcher);
		
		ss.analyze(translator);
		
		verify(matcher);
	}
	
	@Test
	public void testAnalyze_SignalToBuy() throws Exception {
		ss = new PatternSignalSource(Signal.BUY, matcher, calculator, "foobar");
		expect(matcher.matches()).andReturn(true);
		expect(calculator.getPrice()).andReturn(100.00d);
		translator.signalToBuy(100.00d, "foobar");
		replay(matcher);
		replay(calculator);
		replay(translator);
		
		ss.analyze(translator);
		
		verify(translator);
		verify(calculator);
		verify(matcher);
	}
	
	@Test
	public void testAnalyze_SignalToSell() throws Exception {
		ss = new PatternSignalSource(Signal.SELL, matcher, calculator, "zulu4");
		expect(matcher.matches()).andReturn(true);
		expect(calculator.getPrice()).andReturn(200.00d);
		translator.signalToSell(200.00d, "zulu4");
		replay(matcher);
		replay(calculator);
		replay(translator);
		
		ss.analyze(translator);
		
		verify(translator);
		verify(calculator);
		verify(matcher);
	}

}

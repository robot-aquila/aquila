package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class CSUtilsTest {
	private IMocksControl control;
	private SeriesImpl<Candle> series;
	private EditableTerminal terminal;
	private CSUtils utils;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		series = new SeriesImpl<>();
		utils = new CSUtils();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
	}

	@Test
	public void testAggregate3_Tick_FirstCandle() throws Exception {
		assertTrue(utils.aggregate(series, TimeFrame.M5, Tick.ofTrade(T("2017-05-02T11:36:53Z"), 86.12d, 1000L)));

		Interval expectedInt = Interval.of(T("2017-05-02T11:35:00Z"), T("2017-05-02T11:40:00Z"));
		Candle expected = new Candle(expectedInt, 86.12d, 1000L);
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate3_Tick_AppendToLastCandle() throws Exception {
		Interval expectedInt = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		series.add(new Candle(expectedInt, 100.02d, 500L));
		
		assertTrue(utils.aggregate(series, TimeFrame.M5, Tick.ofTrade(T("2017-05-02T11:52:00Z"), 98.13d, 100L)));
		
		Candle expected = new Candle(expectedInt, 100.02d, 100.02d, 98.13d, 98.13d, 600L);
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate3_Tick_PastTick() throws Exception {
		Interval expectedInt = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		series.add(new Candle(expectedInt, 100.02d, 500L));
		
		assertFalse(utils.aggregate(series, TimeFrame.M15, Tick.ofTrade(T("2017-05-02T11:49:59Z"), 98.13d, 100L)));
		
		Candle expected = new Candle(expectedInt, 100.02, 500L);
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate3_Tick_NewCandle() throws Exception {
		series.add(new Candle(Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z")), 100.02d, 500L));

		assertTrue(utils.aggregate(series, TimeFrame.M5, Tick.ofTrade(T("2017-05-02T11:56:02Z"), 98.13d, 100L)));

		assertEquals(2, series.getLength());
		Interval expectedInt = Interval.of(T("2017-05-02T11:55:00Z"), T("2017-05-02T12:00:00Z"));
		Candle expected = new Candle(expectedInt, 98.13d, 100L);
		assertEquals(expected, series.get());
		
		expectedInt = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		expected = new Candle(expectedInt, 100.02d, 500L);
		assertEquals(expected, series.get(0));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateFiller3_SecTfOs() {
		Security security = terminal.getEditableSecurity(new Symbol("SBER"));
		ObservableSeriesImpl<Candle> series = control.createMock(ObservableSeriesImpl.class);
		
		CSFiller actual = utils.createFiller(security, TimeFrame.M15, series);
		
		CSFiller expected = new CSLastTradeFiller(security, TimeFrame.M15, series, utils);
		assertEquals(expected, actual);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateFiller4_TrmSymTfOs() throws Exception {
		Symbol symbol = new Symbol("GAZP");
		terminal.getEditableSecurity(symbol);		
		ObservableSeriesImpl<Candle> series = control.createMock(ObservableSeriesImpl.class);
		
		CSFiller actual = utils.createFiller(terminal, symbol, TimeFrame.M5, series);
		
		CSFiller expected = new CSLastTradeFiller(terminal.getSecurity(symbol), TimeFrame.M5, series, utils);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateFiller3_TrmSymTf() throws Exception {
		Symbol symbol = new Symbol("LKOH");
		terminal.getEditableSecurity(symbol);		
		
		CSFiller actual = utils.createFiller(terminal, symbol, TimeFrame.M10);
		
		assertNotNull(actual.getSeries());
		CSFiller expected = new CSLastTradeFiller(terminal.getSecurity(symbol), TimeFrame.M10,
				(ObservableSeriesImpl<Candle>) actual.getSeries(), utils);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateCandleSeries1_Que() {
		ObservableSeriesImpl<Candle> actual = utils.createCandleSeries(terminal.getEventQueue());
		
		assertNotNull(actual);
		assertNotNull(actual.getUnderlyingSeries());
		assertSame(terminal.getEventQueue(), actual.getEventQueue());
	}
	
	@Test
	public void testCreateCandleSeries1_Trm() {
		ObservableSeriesImpl<Candle> actual = utils.createCandleSeries(terminal);
		
		assertNotNull(actual);
		assertNotNull(actual.getUnderlyingSeries());
		assertSame(terminal.getEventQueue(), actual.getEventQueue());
	}

}

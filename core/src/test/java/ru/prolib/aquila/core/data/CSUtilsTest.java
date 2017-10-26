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
	private TSeriesImpl<Candle> tseries;
	private EditableTerminal terminal;
	private CSUtils utils;
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		series = new SeriesImpl<>();
		tseries = new TSeriesImpl<>(ZTFrame.M5);
		utils = new CSUtils();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
	}

	@Test
	public void testAggregate3_Tick_FirstCandle() throws Exception {
		assertTrue(utils.aggregate(series, ZTFrame.M5, Tick.ofTrade(T("2017-05-02T11:36:53Z"), 86.12d, 1000L)));

		Interval expectedInt = Interval.of(T("2017-05-02T11:35:00Z"), T("2017-05-02T11:40:00Z"));
		Candle expected = new Candle(expectedInt, 86.12d, 1000L);
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate3_Tick_AppendToLastCandle() throws Exception {
		Interval expectedInt = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		series.add(new Candle(expectedInt, 100.02d, 500L));
		
		assertTrue(utils.aggregate(series, ZTFrame.M5, Tick.ofTrade(T("2017-05-02T11:52:00Z"), 98.13d, 100L)));
		
		Candle expected = new Candle(expectedInt, 100.02d, 100.02d, 98.13d, 98.13d, 600L);
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate3_Tick_PastTick() throws Exception {
		Interval expectedInt = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		series.add(new Candle(expectedInt, 100.02d, 500L));
		
		assertFalse(utils.aggregate(series, ZTFrame.M15, Tick.ofTrade(T("2017-05-02T11:49:59Z"), 98.13d, 100L)));
		
		Candle expected = new Candle(expectedInt, 100.02, 500L);
		assertEquals(1, series.getLength());
		assertEquals(expected, series.get());
	}
	
	@Test
	public void testAggregate3_Tick_NewCandle() throws Exception {
		series.add(new Candle(Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z")), 100.02d, 500L));

		assertTrue(utils.aggregate(series, ZTFrame.M5, Tick.ofTrade(T("2017-05-02T11:56:02Z"), 98.13d, 100L)));

		assertEquals(2, series.getLength());
		Interval expectedInt = Interval.of(T("2017-05-02T11:55:00Z"), T("2017-05-02T12:00:00Z"));
		Candle expected = new Candle(expectedInt, 98.13d, 100L);
		assertEquals(expected, series.get());
		
		expectedInt = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		expected = new Candle(expectedInt, 100.02d, 500L);
		assertEquals(expected, series.get(0));
	}
	
	@Test
	public void testAggregate2_FirstCandle() throws Exception {
		utils.aggregate(tseries, Tick.ofTrade(T("2017-05-02T11:36:53Z"), 86.12d, 1000L));

		Interval interval = Interval.of(T("2017-05-02T11:35:00Z"), T("2017-05-02T11:40:00Z"));
		Candle expected = new Candle(interval, 86.12d, 1000L);
		assertEquals(1, tseries.getLength());
		assertEquals(expected, tseries.get());
	}

	@Test
	public void testAggregate2_AppendToLastCandle() throws Exception {
		Interval interval = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		tseries.set(interval.getStart(), new Candle(interval, 100.02d, 500L));
		
		utils.aggregate(tseries, Tick.ofTrade(T("2017-05-02T11:52:00Z"), 98.13d, 100L));
		
		Candle expected = new Candle(interval, 100.02d, 100.02d, 98.13d, 98.13d, 600L);
		assertEquals(1, tseries.getLength());
		assertEquals(expected, tseries.get());
	}
	
	@Test
	public void testAggregate2_PastTick() throws Exception {
		Interval interval1 = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		tseries.set(interval1.getStart(), new Candle(interval1, 100.02d, 500L));
		
		utils.aggregate(tseries, Tick.ofTrade(T("2017-05-02T11:49:59Z"), 98.13d, 100L));
		
		Interval interval2 = Interval.of(T("2017-05-02T11:45:00Z"), T("2017-05-02T11:50:00Z"));
		Candle expected1 = new Candle(interval2, 98.13d, 100L),
				expected2 = new Candle(interval1, 100.02, 500L);

		assertEquals(2, tseries.getLength());
		assertEquals(expected1, tseries.get(0));
		assertEquals(expected2, tseries.get(1));
	}
	
	@Test
	public void testAggregate2_NewCandle() throws Exception {
		Interval interval1 = Interval.of(T("2017-05-02T11:50:00Z"), T("2017-05-02T11:55:00Z"));
		tseries.set(interval1.getStart(), new Candle(interval1, 100.02d, 500L));

		utils.aggregate(tseries, Tick.ofTrade(T("2017-05-02T11:56:02Z"), 98.13d, 100L));

		Interval interval2 = Interval.of(T("2017-05-02T11:55:00Z"), T("2017-05-02T12:00:00Z"));
		Candle expected2 = new Candle(interval2, 98.13d, 100L),
				expected1 = new Candle(interval1, 100.02d, 500L);
		assertEquals(2, tseries.getLength());
		assertEquals(expected1, tseries.get(0));
		assertEquals(expected2, tseries.get(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateFiller3_SecTfOs() {
		Security security = terminal.getEditableSecurity(new Symbol("SBER"));
		ObservableSeriesImpl<Candle> series = control.createMock(ObservableSeriesImpl.class);
		
		CSFiller actual = utils.createFiller(security, ZTFrame.M15, series);
		
		CSFiller expected = new CSLastTradeFiller(security, ZTFrame.M15, series, utils);
		assertEquals(expected, actual);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateFiller4_TrmSymTfOs() throws Exception {
		Symbol symbol = new Symbol("GAZP");
		terminal.getEditableSecurity(symbol);		
		ObservableSeriesImpl<Candle> series = control.createMock(ObservableSeriesImpl.class);
		
		CSFiller actual = utils.createFiller(terminal, symbol, ZTFrame.M5, series);
		
		CSFiller expected = new CSLastTradeFiller(terminal.getSecurity(symbol), ZTFrame.M5, series, utils);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateFiller3_TrmSymTf() throws Exception {
		Symbol symbol = new Symbol("LKOH");
		terminal.getEditableSecurity(symbol);		
		
		CSFiller actual = utils.createFiller(terminal, symbol, ZTFrame.M10);
		
		assertNotNull(actual.getSeries());
		CSFiller expected = new CSLastTradeFiller(terminal.getSecurity(symbol), ZTFrame.M10,
				(ObservableSeriesImpl<Candle>) actual.getSeries(), utils);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateCandleSeries1_Que() {
		ObservableSeriesImpl<Candle> actual = utils.createCandleSeries(terminal.getEventQueue());
		
		assertNotNull(actual);
		assertNotNull(actual.getUnderlyingSeries());
		assertSame(terminal.getEventQueue(), actual.getEventQueue());
		assertEquals(Series.DEFAULT_ID, actual.getId());
	}
	
	@Test
	public void testCreateCandleSeries2_QueStr() {
		ObservableSeriesImpl<Candle> actual = utils.createCandleSeries(terminal.getEventQueue(), "foobar");
		
		assertNotNull(actual);
		assertNotNull(actual.getUnderlyingSeries());
		assertSame(terminal.getEventQueue(), actual.getEventQueue());
		assertEquals("foobar", actual.getId());
	}
	
	@Test
	public void testCreateCandleSeries1_Trm() {
		ObservableSeriesImpl<Candle> actual = utils.createCandleSeries(terminal);
		
		assertNotNull(actual);
		assertNotNull(actual.getUnderlyingSeries());
		assertSame(terminal.getEventQueue(), actual.getEventQueue());
		assertEquals(Series.DEFAULT_ID, actual.getId());
	}
	
	@Test
	public void testCreateCandleSeris2_TrmStr() {
		ObservableSeriesImpl<Candle> actual = utils.createCandleSeries(terminal, "zulu");
		
		assertNotNull(actual);
		assertNotNull(actual.getUnderlyingSeries());
		assertSame(terminal.getEventQueue(), actual.getEventQueue());
		assertEquals("zulu", actual.getId());		
	}

}

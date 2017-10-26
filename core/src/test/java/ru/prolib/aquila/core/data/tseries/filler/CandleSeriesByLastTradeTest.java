package ru.prolib.aquila.core.data.tseries.filler;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class CandleSeriesByLastTradeTest {
	private static final Symbol symbol1 = new Symbol("AAPL"), symbol2 = new Symbol("SBER");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private CandleSeriesAggregator<Tick> aggregatorMock;
	private EditableTSeries<Candle> series;
	private EditableTerminal terminal;
	private CandleSeriesByLastTrade filler;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		aggregatorMock = control.createMock(CandleSeriesAggregator.class);
		series = new TSeriesImpl<Candle>(ZTFrame.M1);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		filler = new CandleSeriesByLastTrade(series, terminal, symbol1, aggregatorMock);
	}
	
	@Test
	public void testCtor4() {
		assertSame(series, filler.getSeries());
		assertSame(terminal, filler.getTerminal());
		assertEquals(symbol1, filler.getSymbol());
		assertNull(filler.getSecurity());
	}

	@Test
	public void testStart_SkipIfStarted() {
		filler.setStarted(true);
		
		assertSame(filler, filler.start());
		
		assertFalse(terminal.onSecurityAvailable().isListener(filler));
		assertTrue(filler.isStarted());
	}

	@Test
	public void testStart_IfSecurityAlreadyDefined() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		filler.setSecurity(security);
		
		assertSame(filler, filler.start());
		
		assertFalse(terminal.onSecurityAvailable().isListener(filler));
		assertTrue(filler.isStarted());
		assertSame(security, filler.getSecurity());
		assertTrue(security.onLastTrade().isListener(filler));
	}
	
	@Test
	public void testStart_IfSecurityNotAvailable() {
		
		assertSame(filler, filler.start());
		
		assertTrue(terminal.onSecurityAvailable().isListener(filler));
		assertTrue(filler.isStarted());
		assertNull(filler.getSecurity());
	}
	
	@Test
	public void testStart_IfSecurityAvailable() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		
		assertSame(filler, filler.start());
		
		assertFalse(terminal.onSecurityAvailable().isListener(filler));
		assertTrue(filler.isStarted());
		assertSame(security, filler.getSecurity());
		assertTrue(security.onLastTrade().isListener(filler));
	}
	
	@Test
	public void testStop_SkipIfNotStarted() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		security.onLastTrade().addListener(filler);
		filler.setSecurity(security);
		terminal.onSecurityAvailable().addListener(filler);
		
		assertSame(filler, filler.stop());
		
		assertTrue(security.onLastTrade().isListener(filler));
		assertTrue(terminal.onSecurityAvailable().isListener(filler));
		assertFalse(filler.isStarted());
	}
	
	@Test
	public void testStop_IfSecurityNotDefined() {
		terminal.onSecurityAvailable().addListener(filler);
		filler.setStarted(true);
		
		assertSame(filler, filler.stop());
		
		assertFalse(terminal.onSecurityAvailable().isListener(filler));
		assertFalse(filler.isStarted());
	}
	
	@Test
	public void testStop_IfSecurityDefined() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		security.onLastTrade().addListener(filler);
		filler.setSecurity(security);
		terminal.onSecurityAvailable().addListener(filler);
		filler.setStarted(true);
		
		assertSame(filler, filler.stop());
		
		assertFalse(terminal.onSecurityAvailable().isListener(filler));
		assertFalse(security.onLastTrade().isListener(filler));
		assertSame(security, filler.getSecurity());
		assertFalse(filler.isStarted());
	}
	
	@Test
	public void testOnEvent_OnSecurityAvailable_SkipIfNotStarted() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		terminal.onSecurityAvailable().addListener(filler);
		filler.setStarted(false);
		
		filler.onEvent(new SecurityEvent(terminal.onSecurityAvailable(), security, Instant.EPOCH));
		
		assertTrue(terminal.onSecurityAvailable().isListener(filler)); // still listener
		assertNull(filler.getSecurity());
	}
	
	@Test
	public void testOnEvent_OnSecurityAvailable_UncontrolledSecurity() {
		EditableSecurity security = terminal.getEditableSecurity(symbol2);
		terminal.onSecurityAvailable().addListener(filler);
		filler.setStarted(true);
		
		filler.onEvent(new SecurityEvent(terminal.onSecurityAvailable(), security, Instant.EPOCH));
		
		assertTrue(terminal.onSecurityAvailable().isListener(filler)); // still listener
		assertNull(filler.getSecurity());
	}
	
	@Test
	public void testOnEvent_OnSecurityAvailable_ControlledSecurity() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		terminal.onSecurityAvailable().addListener(filler);
		filler.setStarted(true);
		
		filler.onEvent(new SecurityEvent(terminal.onSecurityAvailable(), security, Instant.EPOCH));
		
		assertFalse(terminal.onSecurityAvailable().isListener(filler));
		assertSame(security, filler.getSecurity());
		assertTrue(security.onLastTrade().isListener(filler));
	}
	
	@Test
	public void testOnEvent_OnLastTrade_SkipIfNotStarted() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		SecurityTickEvent e = new SecurityTickEvent(security.onLastTrade(), security,
				Instant.EPOCH, Tick.ofTrade(T("2017-08-31T00:00:00Z"), 100.0d, 1000L));
		filler.setStarted(false);
		control.replay();
		
		filler.onEvent(e);
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnLastTrade() throws Exception {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Tick trade = Tick.ofTrade(T("2017-08-31T00:00:00Z"), 100.0d, 1000L);
		SecurityTickEvent e = new SecurityTickEvent(security.onLastTrade(), security, Instant.EPOCH, trade);
		filler.setStarted(true);
		filler.setSecurity(security);
		aggregatorMock.aggregate(series, trade);
		control.replay();
		
		filler.onEvent(e);
		
		control.verify();
	}

}

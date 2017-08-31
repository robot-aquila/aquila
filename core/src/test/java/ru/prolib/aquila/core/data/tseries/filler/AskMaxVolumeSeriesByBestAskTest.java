package ru.prolib.aquila.core.data.tseries.filler;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.TimeFrame;

public class AskMaxVolumeSeriesByBestAskTest {
	private static final Symbol symbol1 = new Symbol("AAPL"), symbol2 = new Symbol("SBER");
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private EditableTSeries<Long> series;
	private EditableTerminal terminal;
	private AskMaxVolumeSeriesByBestAsk filler;

	@Before
	public void setUp() throws Exception {
		series = new TSeriesImpl<>(TimeFrame.M5);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		filler = new AskMaxVolumeSeriesByBestAsk(series, terminal, symbol1);
	}
	
	@Test
	public void testCtor3() {
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
		assertTrue(security.onBestAsk().isListener(filler));
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
		assertTrue(security.onBestAsk().isListener(filler));
	}

	@Test
	public void testStop_SkipIfNotStarted() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		security.onBestAsk().addListener(filler);
		filler.setSecurity(security);
		terminal.onSecurityAvailable().addListener(filler);
		
		assertSame(filler, filler.stop());
		
		assertTrue(security.onBestAsk().isListener(filler));
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
		security.onBestAsk().addListener(filler);
		filler.setSecurity(security);
		terminal.onSecurityAvailable().addListener(filler);
		filler.setStarted(true);
		
		assertSame(filler, filler.stop());
		
		assertFalse(terminal.onSecurityAvailable().isListener(filler));
		assertFalse(security.onBestAsk().isListener(filler));
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
		assertTrue(security.onBestAsk().isListener(filler));
	}

	@Test
	public void testOnEvent_OnBestAsk_SkipIfNotStarted() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Tick tick = Tick.ofAsk(T("2017-08-31T00:00:00Z"), 120.0d, 1200L);
		SecurityTickEvent e = new SecurityTickEvent(security.onBestAsk(), security, Instant.EPOCH, tick);
		filler.setStarted(false);
		
		filler.onEvent(e);

		assertEquals(0, series.getLength());
	}
	
	@Test
	public void testOnEvent_OnBestAsk() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Tick tick = Tick.ofAsk(T("2017-08-31T00:00:00Z"), 120.0d, 1200L);
		SecurityTickEvent e = new SecurityTickEvent(security.onBestAsk(), security, Instant.EPOCH, tick);
		filler.setSecurity(security);
		filler.setStarted(true);

		filler.onEvent(e);
		
		assertEquals(1, series.getLength());
		assertEquals(1200L, (long)series.get(T("2017-08-31T00:00:00Z")));
	}
	
	@Test
	public void testOnEvent_OnBestAsk_NewMax() {
		series.set(T("2017-08-31T00:00:00Z"), 1000L);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Tick tick = Tick.ofAsk(T("2017-08-31T00:00:00Z"), 120.0d, 1200L);
		SecurityTickEvent e = new SecurityTickEvent(security.onBestAsk(), security, Instant.EPOCH, tick);
		filler.setSecurity(security);
		filler.setStarted(true);

		filler.onEvent(e);
		
		assertEquals(1, series.getLength());
		assertEquals(1200L, (long)series.get(T("2017-08-31T00:00:00Z")));
	}

	@Test
	public void testOnEvent_OnBestAsk_OldMax() {
		series.set(T("2017-08-31T00:00:00Z"), 2000L);
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		Tick tick = Tick.ofAsk(T("2017-08-31T00:00:00Z"), 120.0d, 1200L);
		SecurityTickEvent e = new SecurityTickEvent(security.onBestAsk(), security, Instant.EPOCH, tick);
		filler.setSecurity(security);
		filler.setStarted(true);

		filler.onEvent(e);
		
		assertEquals(1, series.getLength());
		assertEquals(2000L, (long)series.get(T("2017-08-31T00:00:00Z")));
	}
	
	@Test
	public void testOnEvent_OnBestAsk_SkipIfTickIsNull() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		SecurityTickEvent e = new SecurityTickEvent(security.onBestAsk(), security, Instant.EPOCH, null);
		filler.setSecurity(security);
		filler.setStarted(true);

		filler.onEvent(e);
		
		assertEquals(0, series.getLength());
	}
	
	@Test
	public void testOnEvent_OnBestAsk_SkipIfTickIsNullAsk() {
		EditableSecurity security = terminal.getEditableSecurity(symbol1);
		SecurityTickEvent e = new SecurityTickEvent(security.onBestAsk(), security, Instant.EPOCH, Tick.NULL_ASK);
		filler.setSecurity(security);
		filler.setStarted(true);

		filler.onEvent(e);
		
		assertEquals(0, series.getLength());
	}

}

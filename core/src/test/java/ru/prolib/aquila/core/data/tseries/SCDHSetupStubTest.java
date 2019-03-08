package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.ZTFrame;

public class SCDHSetupStubTest {
	private static Symbol SYMBOL = new Symbol("AQLA");
	private static ZTFrame TFRAME = ZTFrame.M5;
	
	private IMocksControl control;
	private EditableTerminal terminalMock;
	private EventQueue queueMock;
	private SCDHSetupStub service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SCDHSetupStub(SYMBOL, TFRAME, terminalMock);
	}
	
	@Test
	public void testGetters() {
		assertEquals(SYMBOL, service.getSymbol());
		assertEquals(TFRAME, service.getTimeFrame());
		assertSame(terminalMock, service.getTerminal());
		assertEquals("CHART", service.getSharedSeriesID());
		assertEquals("OHLC", service.getOhlcSeriesID());
		assertEquals("OHLC_MUTATOR", service.getOhlcMutatorSeriesID());
	}
	
	@Test
	public void testGetEventQueue() {
		expect(terminalMock.getEventQueue()).andReturn(queueMock);
		control.replay();
		
		assertSame(queueMock, service.getEventQueue());
		
		control.verify();
	}
	
	@Test
	public void testLoadInitialData_DoNothing() {
		control.replay();
		
		service.loadInitialData(null);
		
		control.verify();
	}
	
	@Test
	public void testCreateDerivedSeries_DoNothing_DoNothing() {
		control.replay();
		
		service.createDerivedSeries(null, null, null);
		
		control.verify();
	}
	
	@Test
	public void testOnStart_DoNothing() {
		control.replay();
		
		service.onStart();
		
		control.verify();
	}

	@Test
	public void testOnStop() {
		control.replay();
		
		service.onStop();
		
		control.verify();
	}

}

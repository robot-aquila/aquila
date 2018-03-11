package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionEvent;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionImpl;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class OEDataProviderImplTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private OEValidator validatorMock;
	private Terminal terminalMock1, terminalMock2;
	private TSeriesImpl<OEEntrySet> entries;
	private Set<Terminal> terminalsStub;
	private EventType eTypeStub1, eTypeStub2;
	private OEDataProviderImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		validatorMock = control.createMock(OEValidator.class);
		terminalMock1 = control.createMock(Terminal.class);
		terminalMock2 = control.createMock(Terminal.class);
		entries = new TSeriesImpl<>("foo", ZTFrame.M5);
		terminalsStub = new LinkedHashSet<>();
		eTypeStub1 = new EventTypeImpl();
		eTypeStub2 = new EventTypeImpl();
		service = new OEDataProviderImpl(entries, terminalsStub, validatorMock);
	}
	
	@Test
	public void testCtor3_Service() {
		assertSame(terminalsStub, service.getTrackedTerminals());
		assertSame(validatorMock, service.getValidator());
		assertSame(entries, service.getEntries());
	}
	
	@Test
	public void testCtor2() {
		service = new OEDataProviderImpl(entries, validatorMock);
		assertEquals(new HashSet<Terminal>(), service.getTrackedTerminals());
		assertSame(validatorMock, service.getValidator());
		assertSame(entries, service.getEntries());
	}
	
	@Test
	public void testCtor3() {
		expect(terminalMock1.onOrderExecution()).andStubReturn(eTypeStub1);
		control.replay();
		
		service = new OEDataProviderImpl(entries, validatorMock, terminalMock1);
		
		control.verify();
		assertTrue(service.getTrackedTerminals().contains(terminalMock1));
		assertTrue(eTypeStub1.isListener(service));
	}
	
	@Test
	public void testAddTerminal() {
		terminalsStub.add(terminalMock2);
		expect(terminalMock1.onOrderExecution()).andStubReturn(eTypeStub1);
		control.replay();
		
		service.addTerminal(terminalMock1);
		
		control.verify();
		Set<Terminal> expectedTerminals = new LinkedHashSet<>();
		expectedTerminals.add(terminalMock2);
		expectedTerminals.add(terminalMock1);
		assertEquals(expectedTerminals, service.getTrackedTerminals());
		assertEquals(expectedTerminals, terminalsStub);
		assertTrue(eTypeStub1.isListener(service));
	}
	
	@Test
	public void testAddTerminal_SkipIfAlreadyTracked() {
		terminalsStub.add(terminalMock1);
		terminalsStub.add(terminalMock2);
		control.replay();
		
		service.addTerminal(terminalMock1);
		
		control.verify();
		Set<Terminal> expectedTerminals = new LinkedHashSet<>();
		expectedTerminals.add(terminalMock1);
		expectedTerminals.add(terminalMock2);
		assertEquals(expectedTerminals, service.getTrackedTerminals());
		assertEquals(expectedTerminals, terminalsStub);
	}
	
	@Test
	public void testRemoveTerminal() {
		terminalsStub.add(terminalMock1);
		terminalsStub.add(terminalMock2);
		expect(terminalMock1.onOrderExecution()).andStubReturn(eTypeStub1);
		control.replay();
		eTypeStub1.addListener(service);
		
		service.removeTerminal(terminalMock1);
		
		control.verify();
		Set<Terminal> expectedTerminals = new LinkedHashSet<>();
		expectedTerminals.add(terminalMock2);
		assertEquals(expectedTerminals, service.getTrackedTerminals());
		assertEquals(expectedTerminals, terminalsStub);
		assertFalse(eTypeStub1.isListener(service));
	}
	
	@Test
	public void testRemoveTerminal_SkipIfNotTracked() {
		terminalsStub.add(terminalMock2);
		control.replay();
		
		service.removeTerminal(terminalMock1);
		
		control.verify();
		Set<Terminal> expectedTerminals = new LinkedHashSet<>();
		expectedTerminals.add(terminalMock2);
		assertEquals(expectedTerminals, service.getTrackedTerminals());
		assertEquals(expectedTerminals, terminalsStub);
	}
	
	@Test
	public void testOnEvent_NewEntrySet() throws Exception {
		OrderExecution oeStub = new OrderExecutionImpl(terminalMock1, 1000L, "FOO-1000", new Symbol("MSFT"),
			OrderAction.BUY, 250L, T("2018-03-10T22:12:00Z"), CDecimalBD.of("251.34"), CDecimalBD.of(10L),
			CDecimalBD.of("5.15"));
		terminalsStub.add(terminalMock1);
		terminalsStub.add(terminalMock2);
		expect(terminalMock1.onOrderExecution()).andStubReturn(eTypeStub1);
		expect(terminalMock2.onOrderExecution()).andStubReturn(eTypeStub2);
		expect(validatorMock.isValid(oeStub)).andReturn(true);
		control.replay();
		
		service.onEvent(new OrderExecutionEvent(eTypeStub2, null, T("2018-03-10T22:12:01Z"), oeStub));
		
		control.verify();
		OEEntrySet expectedSet = new OEEntrySetImpl();
		expectedSet.addEntry(new OEEntryImpl(true, CDecimalBD.of("251.34")));
		OEEntrySet actualSet = entries.get(0);
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void testOnEvent_ExistingEntrySet() throws Exception {
		OrderExecution oeStub = new OrderExecutionImpl(terminalMock2, 800L, "BAR-800", new Symbol("SBER"),
			OrderAction.SELL, 100L, T("2018-03-10T22:19:00Z"), CDecimalBD.of("85.69"), CDecimalBD.of(5L),
			CDecimalBD.of("1.13"));
		terminalsStub.add(terminalMock2);
		expect(terminalMock2.onOrderExecution()).andStubReturn(eTypeStub2);
		expect(validatorMock.isValid(oeStub)).andReturn(true);
		control.replay();
		OEEntrySet esetStub = new OEEntrySetImpl();
		esetStub.addEntry(new OEEntryImpl(true, CDecimalBD.of("56.41")));
		entries.set(T("2018-03-10T22:15:00Z"), esetStub);
		
		service.onEvent(new OrderExecutionEvent(eTypeStub2, null, T("2018-03-10T22:19:00Z"), oeStub));
		
		control.verify();
		OEEntrySet expectedSet = new OEEntrySetImpl();
		expectedSet.addEntry(new OEEntryImpl(true, CDecimalBD.of("56.41")));
		expectedSet.addEntry(new OEEntryImpl(false, CDecimalBD.of("85.69")));
		OEEntrySet actualSet = entries.get(0);
		assertEquals(expectedSet, actualSet);
	}
	
	@Test
	public void testOnEvent_SkipIfTerminalIsNotTracked() {
		OrderExecution oeStub = new OrderExecutionImpl(terminalMock1, 800L, "BAR-800", new Symbol("SBER"),
				OrderAction.SELL, 100L, T("2018-03-10T22:19:00Z"), CDecimalBD.of("85.69"), CDecimalBD.of(5L),
				CDecimalBD.of("1.13"));
		terminalsStub.add(terminalMock2);
		expect(terminalMock2.onOrderExecution()).andStubReturn(eTypeStub2);
		control.replay();
		
		service.onEvent(new OrderExecutionEvent(eTypeStub1, null, T("2018-03-10T22:10:00Z"), oeStub));
		
		control.verify();
		assertEquals(0, entries.getLength());
	}
	
	@Test
	public void testOnEvent_SkipIfOrderExecutionIsNotValid() throws Exception {
		OrderExecution oeStub = new OrderExecutionImpl(terminalMock2, 800L, "BAR-800", new Symbol("SBER"),
			OrderAction.SELL, 100L, T("2018-03-10T22:19:00Z"), CDecimalBD.of("85.69"), CDecimalBD.of(5L),
			CDecimalBD.of("1.13"));
		terminalsStub.add(terminalMock2);
		expect(terminalMock2.onOrderExecution()).andStubReturn(eTypeStub2);
		expect(validatorMock.isValid(oeStub)).andReturn(false);
		control.replay();
		OEEntrySet esetStub = new OEEntrySetImpl();
		esetStub.addEntry(new OEEntryImpl(true, CDecimalBD.of("56.41")));
		entries.set(T("2018-03-10T22:15:00Z"), esetStub);
		
		service.onEvent(new OrderExecutionEvent(eTypeStub2, null, T("2018-03-10T22:19:00Z"), oeStub));
		
		control.verify();
		OEEntrySet expectedSet = new OEEntrySetImpl();
		expectedSet.addEntry(new OEEntryImpl(true, CDecimalBD.of("56.41")));
		OEEntrySet actualSet = entries.get(0);
		assertEquals(expectedSet, actualSet);
	}

}

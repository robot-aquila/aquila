package ru.prolib.aquila.data.storage;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.TestEventQueueImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateImpl;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.data.storage.L1UpdateWriter;
import ru.prolib.aquila.data.storage.SimpleL1Recorder;
import ru.prolib.aquila.data.storage.file.SimpleCsvL1UpdateWriterFactory;

public class SimpleL1RecorderTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private L1UpdateWriterFactory writerFactoryMock;
	private L1UpdateWriter writerMock;
	private SimpleL1Recorder recorder;
	private File file = new File("foo/bar.csv");
	private EventQueue queue;
	private EventListenerStub listenerStub;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		queue = new TestEventQueueImpl();
		control = createStrictControl();
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		writerFactoryMock = control.createMock(L1UpdateWriterFactory.class);
		writerMock = control.createMock(L1UpdateWriter.class);
		recorder = new SimpleL1Recorder(queue, terminal, writerFactoryMock);
		listenerStub = new EventListenerStub();
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testCtor_WithWriterFactory() throws Exception {
		assertFalse(recorder.isStarted());
		assertSame(queue, recorder.getEventQueue());
		assertSame(terminal, recorder.getTerminal());
		assertSame(writerFactoryMock, recorder.getWriterFactory());
		assertEquals("STARTED", recorder.onStarted().getId());
		assertEquals("STOPPED", recorder.onStopped().getId());
	}
	
	@Test
	public void testCtor3_WithFile() throws Exception {
		recorder = new SimpleL1Recorder(queue, terminal, file);
		assertFalse(recorder.isStarted());
		assertSame(queue, recorder.getEventQueue());
		assertSame(terminal, recorder.getTerminal());
		SimpleCsvL1UpdateWriterFactory factory =
				(SimpleCsvL1UpdateWriterFactory) recorder.getWriterFactory();
		assertEquals(file, factory.getFile());
		assertEquals("STARTED", recorder.onStarted().getId());
		assertEquals("STOPPED", recorder.onStopped().getId());		
	}
	
	@Test
	public void testStartWritingUpdates() throws Exception {
		expect(writerFactoryMock.createWriter()).andReturn(writerMock);
		control.replay();
		recorder.onStarted().addListener(listenerStub);
		recorder.onStopped().addListener(listenerStub);
		
		recorder.startWritingUpdates();
		
		control.verify();
		assertTrue(recorder.isStarted());
		assertTrue(terminal.onSecurityBestAsk().isListener(recorder));
		assertTrue(terminal.onSecurityBestBid().isListener(recorder));
		assertTrue(terminal.onSecurityLastTrade().isListener(recorder));
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(recorder.onStarted()));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStartWritingUpdates_ThrowsIfStarted() throws Exception {
		expect(writerFactoryMock.createWriter()).andReturn(writerMock);
		control.replay();
		
		recorder.startWritingUpdates();
		recorder.startWritingUpdates();
	}
	
	@Test
	public void testClose() throws Exception {
		EventType type = new EventTypeImpl();
		expect(writerFactoryMock.createWriter()).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates();
		control.reset();
		writerMock.close();
		control.replay();
		recorder.onStarted().addListener(listenerStub);
		recorder.onStarted().addAlternateType(type);
		recorder.onStopped().addListener(listenerStub);
		recorder.onStopped().addAlternateType(type);
		
		recorder.close();
		
		control.verify();
		assertFalse(recorder.isStarted());
		assertFalse(terminal.onSecurityBestAsk().isListener(recorder));
		assertFalse(terminal.onSecurityBestBid().isListener(recorder));
		assertFalse(terminal.onSecurityLastTrade().isListener(recorder));
		assertFalse(recorder.onStarted().isListener(listenerStub));
		assertFalse(recorder.onStarted().isAlternateType(type));
		assertFalse(recorder.onStopped().isListener(listenerStub));
		assertFalse(recorder.onStopped().isAlternateType(type));
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(recorder.onStopped()));
	}
	
	@Test
	public void testStopWritingUpdates_DoNothingIfNotStarted() throws Exception {
		control.replay();
		
		recorder.stopWritingUpdates();
		
		control.verify();
	}
	
	@Test
	public void testStopWritingUpdates() throws Exception {
		EventType type = new EventTypeImpl();
		expect(writerFactoryMock.createWriter()).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates();
		control.reset();
		writerMock.close();
		control.replay();
		recorder.onStarted().addListener(listenerStub);
		recorder.onStarted().addAlternateType(type);
		recorder.onStopped().addListener(listenerStub);
		recorder.onStopped().addAlternateType(type);		
		
		recorder.stopWritingUpdates();
		
		control.verify();
		assertFalse(recorder.isStarted());
		assertFalse(terminal.onSecurityBestAsk().isListener(recorder));
		assertFalse(terminal.onSecurityBestBid().isListener(recorder));
		assertFalse(terminal.onSecurityLastTrade().isListener(recorder));
		// Should keep listeners/alternates
		assertTrue(recorder.onStarted().isListener(listenerStub));
		assertTrue(recorder.onStarted().isAlternateType(type));
		assertTrue(recorder.onStopped().isListener(listenerStub));
		assertTrue(recorder.onStopped().isAlternateType(type));
		assertEquals(1, listenerStub.getEventCount());
		assertTrue(listenerStub.getEvent(0).isType(recorder.onStopped()));
	}
	
	@Test
	public void testOnEvent() throws Exception {
		expect(writerFactoryMock.createWriter()).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates();
		control.reset();
		Symbol expectedSymbol = new Symbol("SBER");
		Tick expectedTick = Tick.ofAsk(Instant.now(),
				CDecimalBD.of("14.95"),
				CDecimalBD.of(1000L));
		L1Update expected = new L1UpdateImpl(expectedSymbol, expectedTick);
		writerMock.writeUpdate(expected);
		control.replay();
		
		recorder.onEvent(new SecurityTickEvent(terminal.onSecurityBestAsk(),
				terminal.getEditableSecurity(expectedSymbol), null, expectedTick));
		
		control.verify();
	}

	@Test
	public void testOnEvent_DoNothingIfNotStarted() throws Exception {
		control.replay();
		
		recorder.onEvent(new SecurityTickEvent(terminal.onSecurityBestAsk(),
				terminal.getEditableSecurity(new Symbol("GAZP")), null,
				Tick.ofAsk(Instant.now(), CDecimalBD.of("14.38"), CDecimalBD.of(85L))));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_IfWriterThrows() throws Exception {
		expect(writerFactoryMock.createWriter()).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates();
		control.reset();
		Symbol expectedSymbol = new Symbol("SBER");
		Tick expectedTick = Tick.ofAsk(Instant.now(), CDecimalBD.of("14.95"), CDecimalBD.of(1000L));
		L1Update expected = new L1UpdateImpl(expectedSymbol, expectedTick);
		writerMock.writeUpdate(expected);
		expectLastCall().andThrow(new IOException("test error"));
		writerMock.close();
		control.replay();

		recorder.onEvent(new SecurityTickEvent(terminal.onSecurityBestAsk(),
				terminal.getEditableSecurity(expectedSymbol), null,
				expectedTick));
		
		control.verify();
		assertFalse(recorder.isStarted());
		assertFalse(terminal.onSecurityBestAsk().isListener(recorder));
		assertFalse(terminal.onSecurityBestBid().isListener(recorder));
		assertFalse(terminal.onSecurityLastTrade().isListener(recorder));
	}

}

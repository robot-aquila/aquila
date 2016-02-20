package ru.prolib.aquila.datatools.tickdatabase;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.datatools.tickdatabase.SimpleL1Recorder.WriterFactory;

public class SimpleL1RecorderTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private WriterFactory writerFactoryMock;
	private L1UpdateWriter writerMock;
	private SimpleL1Recorder recorder;
	private File file = new File("foo/bar.csv");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		writerFactoryMock = control.createMock(WriterFactory.class);
		writerMock = control.createMock(L1UpdateWriter.class);
		recorder = new SimpleL1Recorder(terminal, writerFactoryMock);
	}
	
	@Test
	public void testCtor() throws Exception {
		assertFalse(recorder.isStarted());
	}
	
	@Test
	public void testStartWritingUpdates() throws Exception {
		expect(writerFactoryMock.createWriter(file)).andReturn(writerMock);
		control.replay();
		
		recorder.startWritingUpdates(file);
		
		control.verify();
		assertTrue(recorder.isStarted());
		assertTrue(terminal.onSecurityBestAsk().isListener(recorder));
		assertTrue(terminal.onSecurityBestBid().isListener(recorder));
		assertTrue(terminal.onSecurityLastTrade().isListener(recorder));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStartWritingUpdates_ThrowsIfStarted() throws Exception {
		expect(writerFactoryMock.createWriter(file)).andReturn(writerMock);
		control.replay();
		
		recorder.startWritingUpdates(file);
		recorder.startWritingUpdates(file);
	}
	
	@Test
	public void testClose() throws Exception {
		expect(writerFactoryMock.createWriter(file)).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates(file);
		control.reset();
		writerMock.close();
		control.replay();
		
		recorder.close();
		
		control.verify();
		assertFalse(recorder.isStarted());
		assertFalse(terminal.onSecurityBestAsk().isListener(recorder));
		assertFalse(terminal.onSecurityBestBid().isListener(recorder));
		assertFalse(terminal.onSecurityLastTrade().isListener(recorder));
	}
	
	@Test
	public void testStopWritingUpdates_DoNothingIfNotStarted() throws Exception {
		control.replay();
		
		recorder.stopWritingUpdates();
		
		control.verify();
	}
	
	@Test
	public void testStopWritingUpdates() throws Exception {
		expect(writerFactoryMock.createWriter(file)).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates(file);
		control.reset();
		writerMock.close();
		control.replay();
		
		recorder.stopWritingUpdates();
		
		control.verify();
		assertFalse(recorder.isStarted());
		assertFalse(terminal.onSecurityBestAsk().isListener(recorder));
		assertFalse(terminal.onSecurityBestBid().isListener(recorder));
		assertFalse(terminal.onSecurityLastTrade().isListener(recorder));
	}
	
	@Test
	public void testOnEvent() throws Exception {
		expect(writerFactoryMock.createWriter(file)).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates(file);
		control.reset();
		Symbol expectedSymbol = new Symbol("SBER");
		Tick expectedTick = Tick.of(TickType.ASK, Instant.now(), 14.95d, 1000L);
		L1Update expected = new L1UpdateImpl(expectedSymbol, expectedTick);
		writerMock.writeUpdate(expected);
		control.replay();
		
		recorder.onEvent(new SecurityTickEvent(terminal.onSecurityBestAsk(),
				terminal.getEditableSecurity(expectedSymbol), expectedTick));
		
		control.verify();
	}

	@Test
	public void testOnEvent_DoNothingIfNotStarted() throws Exception {
		control.replay();
		
		recorder.onEvent(new SecurityTickEvent(terminal.onSecurityBestAsk(),
				terminal.getEditableSecurity(new Symbol("GAZP")),
				Tick.of(TickType.ASK, Instant.now(), 14.38d, 85L)));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_IfWriterThrows() throws Exception {
		expect(writerFactoryMock.createWriter(file)).andReturn(writerMock);
		control.replay();
		recorder.startWritingUpdates(file);
		control.reset();
		Symbol expectedSymbol = new Symbol("SBER");
		Tick expectedTick = Tick.of(TickType.ASK, Instant.now(), 14.95d, 1000L);
		L1Update expected = new L1UpdateImpl(expectedSymbol, expectedTick);
		writerMock.writeUpdate(expected);
		expectLastCall().andThrow(new IOException("test error"));
		writerMock.close();
		control.replay();

		recorder.onEvent(new SecurityTickEvent(terminal.onSecurityBestAsk(),
				terminal.getEditableSecurity(expectedSymbol), expectedTick));
		
		control.verify();
		assertFalse(recorder.isStarted());
		assertFalse(terminal.onSecurityBestAsk().isListener(recorder));
		assertFalse(terminal.onSecurityBestBid().isListener(recorder));
		assertFalse(terminal.onSecurityLastTrade().isListener(recorder));
	}

}

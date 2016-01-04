package ru.prolib.aquila.datatools.tickdatabase.util;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickWriter;

public class SmartFlushTickWriterTest {
	private IMocksControl control;
	private TickWriter writer;
	private Scheduler scheduler;
	private SmartFlushSetup setup;
	private SmartFlushTickWriter flusher;
	private Instant time;
	private TaskHandler taskHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		writer = control.createMock(TickWriter.class);
		scheduler = control.createMock(Scheduler.class);
		taskHandler = control.createMock(TaskHandler.class);
		setup = new SmartFlushSetup();
		setup.setExecutionPeriod(10);
		setup.setFlushPeriod(50);
		flusher = new SmartFlushTickWriter(writer, scheduler, "ZUMBA", setup);
		time = Instant.parse("2015-12-29T18:39:16Z");
	}
	
	@Test
	public void testCtor5() {
		assertSame(writer, flusher.getTickWriter());
		assertSame(scheduler, flusher.getScheduler());
		assertEquals("ZUMBA", flusher.getStreamId());
		assertSame(setup, flusher.getSetup());
		assertNull(flusher.getLastFlushTime());
		assertFalse(flusher.hasUpdate());
	}
	
	@Test
	public void testClose() throws Exception {
		writer.close();
		control.replay();
		
		flusher.close();
		
		control.verify();
	}
	
	@Test
	public void testFlush() throws Exception {
		writer.flush();
		control.replay();
		
		flusher.flush();
		
		control.verify();
	}
	
	@Test
	public void testWrite_FirstTime() throws Exception {
		Tick tick = Tick.of(time.plus(21502, ChronoUnit.MILLIS), 215.12d);
		expect(scheduler.schedule(flusher, 10, 10)).andReturn(taskHandler);
		writer.write(tick);
		expect(scheduler.getCurrentTime()).andReturn(time);
		control.replay();
		
		flusher.write(tick);
		
		control.verify();
		assertTrue(flusher.hasUpdate());
		assertEquals(time, flusher.getLastFlushTime());
	}
	
	@Test
	public void testWrite_NextTime() throws Exception {
		Tick tick = Tick.of(time.plus(88712, ChronoUnit.MILLIS), 112.54d);
		writer.write(tick);
		expect(scheduler.getCurrentTime()).andReturn(time);
		control.replay();
		flusher.setLastFlushTime(time.minus(77812, ChronoUnit.MILLIS));
		
		flusher.write(tick);
		
		control.verify();
		assertTrue(flusher.hasUpdate());
		assertEquals(time, flusher.getLastFlushTime());
	}
	
	@Test
	public void testRun_NoUpdates() throws Exception {
		control.replay();
		flusher.setHasUpdate(false);
		
		flusher.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_SkipIfLessThanPeriodSpecified() throws Exception {
		flusher.setHasUpdate(true);
		flusher.setLastFlushTime(time);
		expect(scheduler.getCurrentTime()).andReturn(time.plus(50, ChronoUnit.MILLIS)); // limit
		control.replay();
		
		flusher.run();
		
		control.verify();
		assertTrue(flusher.hasUpdate());
	}
	
	@Test
	public void testRun_FlushSuccess() throws Exception {
		flusher.setHasUpdate(true);
		flusher.setLastFlushTime(time);
		expect(scheduler.getCurrentTime()).andReturn(time.plus(51, ChronoUnit.MILLIS));
		writer.flush();
		control.replay();
		
		flusher.run();
		
		control.verify();
		assertFalse(flusher.hasUpdate());
		assertEquals(time.plus(51, ChronoUnit.MILLIS), flusher.getLastFlushTime());
	}
	
	@Test
	public void testRun_FlushError() throws Exception {
		flusher.setHasUpdate(true);
		flusher.setLastFlushTime(time);
		expect(scheduler.getCurrentTime()).andReturn(time.plus(51, ChronoUnit.MILLIS));
		writer.flush();
		expectLastCall().andThrow(new IOException("Test error"));
		control.replay();
		
		flusher.run();
		
		control.verify();
		assertTrue(flusher.hasUpdate());
		assertEquals(time, flusher.getLastFlushTime());
	}

}

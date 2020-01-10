package ru.prolib.aquila.core.eqs;

import static org.junit.Assert.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueueStats;
import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.core.eqs.legacy.EventQueueServiceLegacy;
import ru.prolib.aquila.core.eqs.legacy.EventQueueStatsLegacy;
import ru.prolib.aquila.core.eqs.legacy.FlushControl;

public class CmdProcessorDelegateTest {
	private FlushControl flush_control;
	private EventQueueStatsLegacy stats;
	private LinkedBlockingQueue<Cmd> cmd_queue;
	private EventQueueServiceLegacy target;
	private CmdSenderService sender;
	private CmdProcessorDelegate service;
	private Thread thread;

	@Before
	public void setUp() throws Exception {
		flush_control = new FlushControl();
		stats = new EventQueueStatsLegacy();
		cmd_queue = new LinkedBlockingQueue<Cmd>();
		target = new EventQueueServiceLegacy(flush_control, stats);
		sender = new CmdSenderService(cmd_queue);
		service = new CmdProcessorDelegate(cmd_queue, target);
		thread = new Thread(service);
		thread.setDaemon(true);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testHowItWorks() throws Exception {
		thread.start();
		
		sender.eventEnqueued();
		sender.eventEnqueued();
		sender.eventSent();
		sender.eventDispatched();
		sender.addPreparingTime(80L);
		sender.addDispatchingTime(55L);
		sender.addDeliveryTime(20L);
		
		EventQueueStats stats = sender.getStats();
		
		assertEquals(1, stats.getTotalEventsSent());
		assertEquals(1, stats.getTotalEventsDispatched());
		assertEquals(80L, stats.getPreparingTime());
		assertEquals(55L, stats.getDispatchingTime());
		assertEquals(20L, stats.getDeliveryTime());
		
		FlushIndicator indicator = sender.createIndicator();
		indicator.start();
		sender.eventEnqueued();
		sender.eventSent();
		sender.eventDispatched();
		indicator.waitForFlushing(1, TimeUnit.SECONDS);
		
		stats = sender.getStats();
		
		assertEquals(2, stats.getTotalEventsSent());
		assertEquals(2, stats.getTotalEventsDispatched());
		
		cmd_queue.put(new CmdShutdown());
		thread.join(1000L);
		assertFalse(thread.isAlive());
	}

}

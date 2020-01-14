package ru.prolib.aquila.core.eqs.v4;

import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueueStats;
import ru.prolib.aquila.core.FlushIndicator;

public class V4QueueServiceTest {
	private IMocksControl control;
	private V4Queue queueMock;
	private V4QueueService service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service = new V4QueueService(queueMock);
	}
	
	@Test
	public void testGetStats() {
		service.eventEnqueued();
		service.eventEnqueued();
		service.eventEnqueued();
		service.eventSent();
		service.eventSent();
		service.eventDispatched();
		service.addPreparingTime(800L);
		service.addPreparingTime(100L);
		service.addDispatchingTime(250L);
		service.addDispatchingTime(150L);
		service.addDeliveryTime(330L);
		service.addDeliveryTime(170L);
		
		EventQueueStats stats = service.getStats();
		
		assertEquals(3L, stats.getTotalEventsEnqueued());
		assertEquals(2L, stats.getTotalEventsSent());
		assertEquals(1L, stats.getTotalEventsDispatched());
		assertEquals(900L, stats.getPreparingTime());
		assertEquals(400L, stats.getDispatchingTime());
		assertEquals(500L, stats.getDeliveryTime());
	}

	@Test
	public void testCreateIndicator() {
		
		FlushIndicator actual = service.createIndicator();
		
		assertNotNull(actual);
		assertThat(actual, IsInstanceOf.instanceOf(V4FlushIndicator.class));
	}
	
	@Test
	public void testShutdown() {
		control.replay();
		
		service.shutdown();
		
		control.verify();
	}
	
	@Test
	public void testEventDispatched2() {
		service.eventDispatched(820L, 290L);
		service.eventDispatched( 30L,  10L);
		
		EventQueueStats stats = service.getStats();
		
		assertEquals(0L, stats.getTotalEventsEnqueued());
		assertEquals(0L, stats.getTotalEventsSent());
		assertEquals(2L, stats.getTotalEventsDispatched());
		assertEquals(850L, stats.getPreparingTime());
		assertEquals(300L, stats.getDispatchingTime());
	}

}

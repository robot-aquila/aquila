package ru.prolib.aquila.core.eqs;

import static org.junit.Assert.*;

import java.util.concurrent.CompletionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.EventQueueStats;
import ru.prolib.aquila.core.FlushIndicator;

public class CmdSenderServiceTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private LinkedBlockingQueue<Cmd> cmdQueue, cmdQueueMock;
	private CmdSenderService service_wm, service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		cmdQueueMock = control.createMock(LinkedBlockingQueue.class);
		cmdQueue = new LinkedBlockingQueue<>();
		service_wm = new CmdSenderService(cmdQueueMock, 2500L);
		service = new CmdSenderService(cmdQueue, 50L);
	}
	
	@Test
	public void testCtor2() {
		assertEquals(2500L, service_wm.getTimeout());
	}
	
	@Test
	public void testCtor1() {
		service_wm = new CmdSenderService(cmdQueueMock);
		assertEquals(5000L, service_wm.getTimeout());
	}
	
	@Test
	public void testCreateIndicator() throws Exception {
		FlushIndicator resultMock = control.createMock(FlushIndicator.class);
		cmdQueueMock.put(anyObject());
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				((CmdRequestIndicator)getCurrentArguments()[0]).getResult().complete(resultMock);
				return null;
			}
		});
		control.replay();
		
		FlushIndicator actual = service_wm.createIndicator();
		
		control.verify();
		assertSame(resultMock, actual);
	}
	
	@Test
	public void testCreateIndicator_ThrowsOnTimeout() throws Exception {
		eex.expect(CompletionException.class);
		eex.expectMessage("Unexpected exception");
		eex.expectCause(IsInstanceOf.instanceOf(TimeoutException.class));
		control.replay();
		
		service.createIndicator();
	}
	
	@Test
	public void testGetStats() throws Exception {
		EventQueueStats resultMock = control.createMock(EventQueueStats.class);
		cmdQueueMock.put(anyObject());
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				((CmdRequestStats)getCurrentArguments()[0]).getResult().complete(resultMock);
				return null;
			}
		});
		control.replay();
		
		EventQueueStats actual = service_wm.getStats();
		
		control.verify();
		assertSame(resultMock, actual);
	}
	
	@Test
	public void testGetStats_ThrowsOnTimeout() throws Exception {
		eex.expect(CompletionException.class);
		eex.expectMessage("Unexpected exception");
		eex.expectCause(IsInstanceOf.instanceOf(TimeoutException.class));
		control.replay();
		
		service.getStats();
	}
	
	@Test
	public void testEventEnqueued() throws Exception {
		cmdQueueMock.put(new CmdAddCount(1L, null, null));
		control.replay();
		
		service_wm.eventEnqueued();
		
		control.verify();
	}
	
	@Test
	public void testEventSent() throws Exception {
		cmdQueueMock.put(new CmdAddCount(null, 1L, null));
		control.replay();
		
		service_wm.eventSent();
		
		control.verify();
	}
	
	@Test
	public void testEventDispatched() throws Exception {
		cmdQueueMock.put(new CmdAddCount(null, null, 1L));
		control.replay();
		
		service_wm.eventDispatched();
		
		control.verify();
	}
	
	@Test
	public void testAddPreparingTime() throws Exception {
		cmdQueueMock.put(new CmdAddTime(852L, null, null));
		control.replay();
		
		service_wm.addPreparingTime(852L);
		
		control.verify();
	}
	
	@Test
	public void testAddDispatchingTime() throws Exception {
		cmdQueueMock.put(new CmdAddTime(null, 326L, null));
		control.replay();
		
		service_wm.addDispatchingTime(326L);
		
		control.verify();
	}
	
	@Test
	public void testAddDeliveryTime() throws Exception {
		cmdQueueMock.put(new CmdAddTime(null, null, 527L));
		control.replay();
		
		service_wm.addDeliveryTime(527L);
		
		control.verify();
	}
	
	@Test
	public void testShutdown() throws Exception {
		cmdQueueMock.put(new CmdShutdown());
		control.replay();
		
		service_wm.shutdown();
		
		control.verify();
	}
	
	@Test
	public void testEventDispatched2() throws Exception {
		cmdQueueMock.put(new CmdAddCount(null, null, 1L));
		cmdQueueMock.put(new CmdAddTime(225L, 831L, null));
		control.replay();
		
		service_wm.eventDispatched(225L, 831L);
		
		control.verify();
	}

}

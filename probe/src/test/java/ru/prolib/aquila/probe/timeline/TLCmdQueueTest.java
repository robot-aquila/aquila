package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.probe.timeline.TLCmd;
import ru.prolib.aquila.probe.timeline.TLCmdQueue;

public class TLCmdQueueTest {
	private IMocksControl control;
	private TLCmd c1, c2;
	private BlockingQueue<TLCmd> subQueue;
	private TLCmdQueue queue;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		LocalDateTime time = LocalDateTime.of(1998, 1, 7, 23, 15, 40, 999);
		c1 = new TLCmd(time);
		c2 = new TLCmd(time.plus(10, ChronoUnit.MILLIS));
		control = createStrictControl();
		subQueue = control.createMock(BlockingQueue.class);
		queue = new TLCmdQueue(subQueue);
	}
	
	@Test
	public void testPut() throws Exception {
		subQueue.put(same(c1));
		control.replay();
		
		queue.put(c1);
		
		control.verify();
	}
	
	@Test
	public void testTell() throws Exception {
		expect(subQueue.poll()).andReturn(null);
		expect(subQueue.poll()).andReturn(c1);
		expect(subQueue.poll()).andReturn(c2);
		control.replay();
		
		assertNull(queue.tell());
		assertSame(c1, queue.tell());
		assertSame(c1, queue.tell());
		queue.pull(); // remove c1 from queue
		assertSame(c2, queue.tell());
		
		control.verify();
	}

	@Test
	public void testTellb() throws Exception {
		expect(subQueue.take()).andReturn(c1);
		expect(subQueue.take()).andReturn(c2);
		control.replay();
		
		assertSame(c1, queue.tellb());
		assertSame(c1, queue.tellb());
		queue.pull(); // remove c1 from queue
		assertSame(c2, queue.tellb());
		assertSame(c2, queue.tellb());
		
		control.verify();
	}
	
	@Test
	public void testPull() throws Exception {
		expect(subQueue.poll()).andReturn(null);
		expect(subQueue.poll()).andReturn(c1);
		expect(subQueue.poll()).andReturn(c2);
		expect(subQueue.poll()).andReturn(null);
		control.replay();
		
		assertNull(queue.pull());
		assertSame(c1, queue.pull());
		assertSame(c2, queue.pull());
		assertNull(queue.tell());
		
		control.verify();
	}
	
	@Test
	public void testPullb() throws Exception {
		expect(subQueue.take()).andReturn(c1);
		expect(subQueue.take()).andReturn(c2);
		expect(subQueue.poll()).andReturn(null);
		control.replay();
		
		assertSame(c1, queue.pullb());
		assertSame(c2, queue.pullb());
		assertNull(queue.tell());
		
		control.verify();
	}
	
	@Test
	public void testClear() throws Exception {
		subQueue.clear();
		control.replay();
		
		queue.clear();
		
		control.verify();
	}

}

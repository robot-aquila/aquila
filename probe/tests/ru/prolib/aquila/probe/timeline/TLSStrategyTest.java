package ru.prolib.aquila.probe.timeline;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

public class TLSStrategyTest {
	private IMocksControl control;
	private TLSInterrogationStrategy helper;
	private TLEventQueue eventQueue;
	private TLSStrategy simulation;
	private TLEventSource src1, src2, src3, src4;
	private List<TLEventSource> list1, list2, list3;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		helper = control.createMock(TLSInterrogationStrategy.class);
		eventQueue = control.createMock(TLEventQueue.class);
		simulation = new TLSStrategy(helper, eventQueue);
		src1 = control.createMock(TLEventSource.class);
		src2 = control.createMock(TLEventSource.class);
		src3 = control.createMock(TLEventSource.class);
		src4 = control.createMock(TLEventSource.class);
		list1 = new Vector<TLEventSource>();
		list2 = new Vector<TLEventSource>();
		list3 = new Vector<TLEventSource>();
	}
	
	@Test
	public void testExecute_HasNoStack() throws Exception {
		list1.add(src1);
		list2.add(src2);
		list2.add(src3);
		expect(helper.getForInterrogating()).andReturn(list1);
		helper.interrogate(same(src1));
		expect(helper.getForInterrogating()).andReturn(list2);
		helper.interrogate(same(src2));
		helper.interrogate(same(src3));
		expect(helper.getForInterrogating()).andReturn(list3);
		expect(eventQueue.pullStack()).andReturn(null);
		control.replay();
		
		assertFalse(simulation.execute());
		
		control.verify();
	}
	
	@Test
	public void testExecute_HasStack() throws Exception {
		list1.add(src1);
		list1.add(src2);
		list1.add(src3);
		list2.add(src4);
		expect(helper.getForInterrogating()).andReturn(list1);
		helper.interrogate(same(src1));
		helper.interrogate(same(src2));
		helper.interrogate(same(src3));
		expect(helper.getForInterrogating()).andReturn(list2);
		helper.interrogate(same(src4));
		expect(helper.getForInterrogating()).andReturn(list3);
		TLEventStack stack = control.createMock(TLEventStack.class);
		expect(eventQueue.pullStack()).andReturn(stack);
		stack.execute();
		control.replay();
		
		assertTrue(simulation.execute());
		
		control.verify();
	}

}

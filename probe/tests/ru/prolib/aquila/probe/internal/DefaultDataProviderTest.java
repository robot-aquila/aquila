package ru.prolib.aquila.probe.internal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DefaultDataProviderTest {
	private DefaultDataProvider dataProvider;

	@Before
	public void setUp() throws Exception {
		dataProvider = new DefaultDataProvider(null);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	/*
	@Test
	public void testRegisterTimelineEvents() throws Exception {
		TLEventSource source = control.createMock(TLEventSource.class);
		timeline.registerSource(source);
		control.replay();
		locator.setTimeline(timeline);
		
		locator.registerTimelineEvents(source);
		
		control.verify();
	}

	//locator.registerTimelineEvents(new TickDataDispatcher(
	//		locator.getDataIterator(descr, getCurrentTime()),
	//		new CommonTickHandler(getEditableSecurity(descr))));

	
	@SuppressWarnings("unchecked")
	@Test
	public void testRequestSecurity() throws Exception {
		SecurityDescriptor descr = new SecurityDescriptor("foo", "bar", "RUR");
		locator = control.createMock(PROBEServiceLocator.class);
		
		Aqiterator<Tick> it = control.createMock(Aqiterator.class);
		terminal.setServiceLocator(locator);
		
		
		expect(locator.getDataIterator(descr, start)).andReturn(it);
		locator.registerTimelineEvents(new TickDataDispatcher(it,
				new CommonTickHandler(terminal.getEditableSecurity(descr))));
		control.replay();
		
		terminal.requestSecurity(descr);
		terminal.requestSecurity(descr); // ignore repeated requests

		control.verify();
	}
	*/
	
}

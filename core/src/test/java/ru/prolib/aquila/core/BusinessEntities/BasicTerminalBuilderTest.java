package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.DataProvider;

public class BasicTerminalBuilderTest {
	private IMocksControl control;
	private BasicTerminalBuilder builder;
	private TerminalImpl terminal;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		builder = new BasicTerminalBuilder();
	}
	
	@Test
	public void testWithTerminalID() {
		terminal = (TerminalImpl) builder.withTerminalID("foobar").buildTerminal();
		
		assertEquals("foobar", terminal.getTerminalID());
	}
	
	@Test
	public void testWithEventQueue() {
		EventQueue queue = new EventQueueImpl();
		terminal = (TerminalImpl) builder.withEventQueue(queue).buildTerminal();
		
		assertSame(queue, terminal.getEventQueue());
	}
	
	@Test
	public void testWithScheduler() {
		Scheduler scheduler = control.createMock(Scheduler.class);
		terminal = (TerminalImpl) builder.withScheduler(scheduler).buildTerminal();
		
		assertSame(scheduler, terminal.getScheduler());
	}
	
	@Test
	public void testWithObjectFactory() {
		ObjectFactory factory = control.createMock(ObjectFactory.class);
		terminal = (TerminalImpl) builder.withObjectFactory(factory).buildTerminal();
		
		assertSame(factory, terminal.getObjectFactory());
	}
	
	@Test
	public void testWithDataProvider() {
		DataProvider dataProvider = control.createMock(DataProvider.class);
		terminal = (TerminalImpl) builder.withDataProvider(dataProvider).buildTerminal();
				
		assertSame(dataProvider, terminal.getDataProvider());
	}

}

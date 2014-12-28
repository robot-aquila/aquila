package ru.prolib.aquila.probe;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueueStarter;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.Aqiterator;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.probe.internal.DataProvider;
import ru.prolib.aquila.probe.internal.PROBEDataStorage;
import ru.prolib.aquila.probe.internal.PROBEServiceLocator;
import ru.prolib.aquila.probe.internal.XFactory;
import ru.prolib.aquila.probe.timeline.TLSTimeline;

public class PROBEFactoryTest {
	private IMocksControl control;
	private XFactory x;
	private PROBEFactory factory;
	private Properties props;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		x = control.createMock(XFactory.class);
		factory = new PROBEFactory(x);
		props = new Properties();
		props.setProperty(PROBEFactory.DATA_STORAGE_PATH, "fixture");
		props.setProperty(PROBEFactory.RUN_INTERVAL_START, "2014-12-12 00:00:00.000");
		props.setProperty(PROBEFactory.RUN_INTERVAL_END, "2014-12-31 23:59:59.999");
	}
	
	@Test
	public void testCreateTerminal() throws Exception {
		PROBETerminal terminal = new PROBETerminal("xxx");
		TLSTimeline timeline = control.createMock(TLSTimeline.class);
		Scheduler scheduler = control.createMock(Scheduler.class);
		DataProvider dataProvider = control.createMock(DataProvider.class);
		PROBEDataStorage dataStorage = control.createMock(PROBEDataStorage.class);
		EventQueueStarter eventQueueStarter = control.createMock(EventQueueStarter.class);
		StarterQueue starter = control.createMock(StarterQueue.class);
		terminal.setStarter(starter);
		
		expect(x.newTerminal("Probe" + (factory.getInstancesCount() + 1)))
			.andReturn(terminal);
		expect(x.newTimeline(terminal.getEventSystem(), new Interval(
				new DateTime(2014, 12, 12, 0, 0, 0, 0),
				new DateTime(2014, 12, 31, 23, 59, 59, 999))))
			.andReturn(timeline);
		expect(x.newScheduler(same(timeline))).andReturn(scheduler);
		expect(x.newDataProvider(terminal)).andReturn(dataProvider);
		expect(x.newDataStorage(new File("fixture"))).andReturn(dataStorage);
		expect(x.newQueueStarter(terminal.getEventSystem().getEventQueue(), 3000L))
			.andReturn(eventQueueStarter);
		expect(starter.add(eventQueueStarter)).andReturn(starter);
		control.replay();
		
		PROBETerminal actual = (PROBETerminal)factory.createTerminal(props);
		
		control.verify();
		assertNotNull(actual);
		PROBEServiceLocator locator = terminal.getServiceLocator();
		assertSame(dataProvider, locator.getDataProvider());
		assertSame(timeline, locator.getTimeline());
		assertSame(scheduler, terminal.getScheduler());
		assertSame(dataStorage, locator.getDataStorage());
	}
	
	@Test
	public void testDataStorage_IntegrationTest()
			throws Exception
	{
		PROBETerminal terminal = (PROBETerminal)
				new PROBEFactory().createTerminal(props);
		
		SecurityDescriptor descr = new SecurityDescriptor("Si-3.15", "SPBFUT",
				"RUR", SecurityType.FUT);
		Aqiterator<Tick> it = terminal.getServiceLocator().getDataStorage()
				.getIterator(descr, new DateTime(2014, 12, 12, 10, 0, 0, 0));
		assertTrue(it.next());
		Tick first = it.item(), last = null, expected;
		while ( it.next() ) {
			last = it.item();
		}
		expected = new Tick(new DateTime(2014, 12, 12, 10,  0,  0, 0), 58300d, 1d);
		assertEquals(expected, first);
		expected = new Tick(new DateTime(2014, 12, 12, 23, 49, 51, 0), 60650d, 1d);
		assertEquals(expected, last);
	}

}

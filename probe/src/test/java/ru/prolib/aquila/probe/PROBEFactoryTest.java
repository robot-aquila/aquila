package ru.prolib.aquila.probe;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Properties;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.*;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

public class PROBEFactoryTest {
	private IMocksControl control;
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
		factory = new PROBEFactory();
		props = new Properties();
		props.setProperty(PROBEFactory.DATA_STORAGE_PATH, "fixture");
		props.setProperty(PROBEFactory.RUN_INTERVAL_START, "2014-12-12 00:00:00.000");
		props.setProperty(PROBEFactory.RUN_INTERVAL_END, "2014-12-31 23:59:59.999");
	}
	
	@Test
	public void testCreateTerminal1() throws Exception {
		PROBETerminal terminal = control.createMock(PROBETerminal.class);
		PROBETerminalBuilder builder = control.createMock(PROBETerminalBuilder.class);
		factory = new PROBEFactory(builder);
		String id = "Probe" + (factory.getInstancesCount() + 1);
		expect(builder.withCommonEventSystemAndQueueId(id)).andReturn(builder);
		expect(builder.withCommonTimelineAndTimeInterval(new Interval(
				new DateTime(2014, 12, 12, 0, 0, 0, 0),
				new DateTime(2014, 12, 31, 23, 59, 59, 999))))
			.andReturn(builder);
		expect(builder.withCommonDataStorageAndPath("fixture"))
			.andReturn(builder);
		expect(builder.buildTerminal()).andReturn(terminal);
		control.replay();
		
		assertSame(terminal, factory.createTerminal(props));
		
		control.verify();
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

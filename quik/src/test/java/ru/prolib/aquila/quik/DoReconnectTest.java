package ru.prolib.aquila.quik;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.t2q.T2QException;

public class DoReconnectTest {
	private static QUIKConfigImpl config;
	private IMocksControl control;
	private QUIKTerminal terminal;
	private QUIKClient client;
	private DoReconnect task;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		config = new QUIKConfigImpl();
		config.quikPath = "C:/work/quik";
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(QUIKTerminal.class);
		client = control.createMock(QUIKClient.class);
		expect(terminal.getClient()).andStubReturn(client);
		task = new DoReconnect(terminal, config);
	}
	
	@Test
	public void testRun_Stopped() throws Exception {
		expect(terminal.started()).andReturn(false);
		control.replay();

		task.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_StartedConnected() throws Exception {
		expect(terminal.started()).andReturn(true);
		expect(terminal.connected()).andReturn(true);
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_ConnectOk() throws Exception {
		expect(terminal.started()).andReturn(true);
		expect(terminal.connected()).andReturn(false);
		client.connect("C:/work/quik");
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_ConnectThrows() throws Exception {
		expect(terminal.started()).andReturn(true);
		expect(terminal.connected()).andReturn(false);
		client.connect("C:/work/quik");
		expectLastCall().andThrow(new T2QException("test error"));
		expect(terminal.schedule(task, 5000L)).andReturn(null);
		control.replay();
		
		task.run();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(task.equals(task));
		assertFalse(task.equals(null));
		assertFalse(task.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKTerminal> vTerm = new Variant<QUIKTerminal>()
			.add(terminal)
			.add(control.createMock(QUIKTerminal.class));
		Variant<QUIKConfig> vConf = new Variant<QUIKConfig>(vTerm)
			.add(config)
			.add(new QUIKConfigImpl());
		Variant<?> iterator = vConf;
		int foundCnt = 0;
		DoReconnect x, found = null;
		do {
			x = new DoReconnect(vTerm.get(), vConf.get());
			if ( task.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertEquals(config, found.getConfig());
	}

}

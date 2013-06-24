package ru.prolib.aquila.ib;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.api.*;

public class DoReconnectTest {
	private static IBConfig config;
	private static Timer timer;
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private IBClient client;
	private DoReconnect task;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		timer = new Timer(true);
		config = new IBConfig("127.0.0.1", 4001, 0);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		timer.cancel();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		client = control.createMock(IBClient.class);
		expect(terminal.getClient()).andStubReturn(client);
		task = new DoReconnect(terminal, config);
	}
	
	@Test
	public void testRun_CancelIfTerminalStopped() throws Exception {
		expect(terminal.started()).andReturn(false);
		control.replay();

		timer.schedule(task, 0L, 100L);
		
		new CountDownLatch(1).await(200L, TimeUnit.MILLISECONDS);
		control.verify();
	}
	
	@Test
	public void testRun_CancelAfterConnect() throws Exception {
		expect(terminal.started()).andReturn(true);
		client.connect(eq(config));
		expect(client.connected()).andReturn(false); // first - failed
		expect(terminal.started()).andReturn(true);
		client.connect(eq(config));
		expect(client.connected()).andReturn(true); // second - ok
		control.replay();
		
		timer.schedule(task, 0L, 100L);
		
		new CountDownLatch(1).await(250L, TimeUnit.MILLISECONDS);
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
		TerminalBuilder tb = new IBTerminalBuilder();
		IBEditableTerminal t1 = (IBEditableTerminal) tb.createTerminal("foo");
		IBEditableTerminal t2 = (IBEditableTerminal) tb.createTerminal("foo");
		task = new DoReconnect(t1, config);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<IBConfig> vConf = new Variant<IBConfig>(vTerm)
			.add(config)
			.add(new IBConfig(null, 4001));
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
		assertSame(t1, found.getTerminal());
		assertEquals(config, found.getConfig());
	}

}

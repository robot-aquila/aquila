package ru.prolib.aquila.ib.subsys.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBClientStarter;
import ru.prolib.aquila.ib.subsys.api.IBConfig;

/**
 * 2012-11-25<br>
 * $Id: IBClientStarterTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBClientStarterTest {
	private static IMocksControl control;
	private static IBClient client;
	private static IBConfig config;
	private static IBClientStarter starter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		client = control.createMock(IBClient.class);
		config = new IBConfig("192.168.1.10", 4001, 5);
		starter = new IBClientStarter(client, config);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(client, starter.getClient());
		assertSame(config, starter.getConfig());
	}
	
	@Test
	public void testStart() throws Exception {
		client.eConnect(eq("192.168.1.10"), eq(4001), eq(5));
		control.replay();
		starter.start();
		control.verify();
	}

	@Test
	public void testStart_DefaultClient() throws Exception {
		config = new IBConfig("192.168.1.10", 4001, 0);
		starter = new IBClientStarter(client, config);
		client.eConnect(eq("192.168.1.10"), eq(4001), eq(0));
		client.reqAutoOpenOrders(eq(true));
		control.replay();
		starter.start();
		control.verify();
	}

	@Test
	public void testStop() throws Exception {
		client.eDisconnect();
		control.replay();
		starter.stop();
		control.verify();
	}

}

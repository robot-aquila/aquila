package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDEServer;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.Assembler;

public class QUIKDDEStarterTest {
	private IMocksControl control;
	private QUIKConfigImpl config;
	private Assembler asm;
	private DDEServer server;
	private QUIKEditableTerminal terminal;
	private QUIKDDEStarter starter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		config = new QUIKConfigImpl();
		config.serviceName = "AQUILA";
		config.dateFormat = "yyyy-MM-dd";
		config.timeFormat = "HH:mm:ss";
		config.allDeals = "trades";
		config.portfoliosFUT = "portfolios";
		config.positionsFUT = "positions";
		config.securities = "securities";
		asm = control.createMock(Assembler.class);
		server = control.createMock(DDEServer.class);
		terminal = control.createMock(QUIKEditableTerminal.class);
		starter = new QUIKDDEStarter(config, asm, server);
		expect(asm.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testStart() throws Exception {
		RowDataConverter conv = new RowDataConverter("yyyy-MM-dd", "HH:mm:ss");
		QUIKDDEService expected = new QUIKDDEService("AQUILA", terminal);
		expected.setHandler("securities",
				new TableHandler(new SecuritiesGateway(conv, null)));
		expected.setHandler("trades",
				new TableHandler(new TradesGateway(conv, asm)));
		expected.setHandler("portfolios",
				new TableHandler(new FortsPortfoliosGateway(conv, asm)));
		expected.setHandler("positions",
				new TableHandler(new FortsPositionsGateway(conv, asm)));
		server.registerService(eq(expected));
		control.replay();
		
		starter.start();
		
		control.verify();
		assertEquals("AQUILA", starter.getName());
	}
	
	@Test
	public void testStop() throws Exception {
		starter.setName("foobar");
		server.unregisterService("foobar");
		control.replay();
		
		starter.stop();
		
		control.verify();
		assertNull(starter.getName());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(starter.equals(starter));
		assertFalse(starter.equals(null));
		assertFalse(starter.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<QUIKConfig> vConf = new Variant<QUIKConfig>()
			.add(config)
			.add(new QUIKConfigImpl());
		Variant<Assembler> vAsm = new Variant<Assembler>(vConf)
			.add(asm)
			.add(control.createMock(Assembler.class));
		Variant<DDEServer> vSrv = new Variant<DDEServer>(vAsm)
			.add(server)
			.add(control.createMock(DDEServer.class));
		Variant<?> iterator = vSrv;
		int foundCnt = 0;
		QUIKDDEStarter x, found = null;
		do {
			x = new QUIKDDEStarter(vConf.get(), vAsm.get(), vSrv.get());
			if ( starter.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(config, found.getConfig());
		assertSame(asm, found.getAssembler());
		assertSame(server, found.getServer());
	}

}

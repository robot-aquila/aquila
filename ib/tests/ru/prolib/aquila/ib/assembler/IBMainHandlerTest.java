package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import com.ib.client.*;

import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.*;

public class IBMainHandlerTest {
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private IBClient client;
	private Counter requestId;
	private Assembler assembler;
	private IBMainHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		client = control.createMock(IBClient.class);
		requestId = control.createMock(Counter.class);
		assembler = control.createMock(Assembler.class);
		handler = new IBMainHandler(terminal, assembler);
		
		expect(terminal.getClient()).andStubReturn(client);
		expect(terminal.getOrderNumerator()).andStubReturn(requestId);
	}
	
	/**
	 * Создать экземпляр состояния заявки.
	 * <p>
	 * Конструктор класса статуса защищенный. Данный метод создает экземпляр
	 * используя рефлекшн API.
	 * <p>
	 * @return новый экземпляр состояния
	 * @throws Exception
	 */
	private OrderState createOrderState() throws Exception {
		Constructor<OrderState> con = OrderState.class.getDeclaredConstructor();
		con.setAccessible(true);
		con.newInstance();
		return con.newInstance();		
	}
	
	@Test
	public void testError() throws Exception {
		control.replay();
		
		handler.error(1, 100, "you should see this message");
		
		control.verify();
	}
	
	@Test
	public void testConnectionOpened() throws Exception {
		terminal.fireTerminalConnectedEvent();
		control.replay();
		
		handler.connectionOpened();
		
		control.verify();
	}
	
	@Test
	public void testConnectionClosed() throws Exception {
		terminal.fireTerminalDisconnectedEvent();
		control.replay();
		
		handler.connectionClosed();
		
		control.verify();
	}
	
	@Test
	public void testAccountDownloadEnd() throws Exception {
		control.replay();
		
		handler.accountDownloadEnd("foobar");
		
		control.verify();
	}
	
	@Test
	public void testCommissionReport() throws Exception {
		control.replay();
		
		handler.commissionReport(new CommissionReport());
		
		control.verify();
	}
	
	@Test
	public void testCurrentTime() throws Exception {
		control.replay();
		
		handler.currentTime(128372367265L);
		
		control.verify();
	}
	
	@Test
	public void testManagedAccounts() throws Exception {
		client.reqAccountUpdates(eq(true), eq("foo"));
		client.reqAccountUpdates(eq(true), eq("bar"));
		control.replay();
		
		handler.managedAccounts("foo,bar");
		
		control.verify();
	}
	
	@Test
	public void testNextValidId_ChangeIfGreaterOffered() throws Exception {
		expect(requestId.get()).andReturn(821);
		requestId.set(950);
		control.replay();
		
		handler.nextValidId(950);
		
		control.verify();
	}
	
	@Test
	public void testNextValidId_SkipIfLessOrEquals() throws Exception {
		expect(requestId.get()).andReturn(821);
		control.replay();
		
		handler.nextValidId(821);
		
		control.verify();
	}
	
	@Test
	public void testUpdateAccount() throws Exception {
		assembler.update(eq(new PortfolioValueEntry("TEST",
				"Cash", "USD", "24.15")));
		control.replay();
		
		handler.updateAccount("Cash", "24.15", "USD", "TEST");
		
		control.verify();
	}
	
	@Test
	public void testUpdatePortfolio() throws Exception {
		Contract contract = new Contract();
		contract.m_conId = 815;
		assembler.update(eq(new PositionEntry(contract, 10, 1d, 2d, 3d, "AC")));
		control.replay();
		
		handler.updatePortfolio(contract, 10, 0d, 1d, 2d, 0d, 3d, "AC");
		
		control.verify();
	}
	
	@Test
	public void testContractDetails() throws Exception {
		ContractDetails details = new ContractDetails();
		details.m_summary = new Contract();
		details.m_summary.m_conId = 348;
		assembler.update(eq(new ContractEntry(details)));
		control.replay();
		
		handler.contractDetails(8, details);
		
		control.verify();
	}
	
	@Test
	public void testBondContractDetails() throws Exception {
		ContractDetails details = new ContractDetails();
		details.m_summary = new Contract();
		details.m_summary.m_conId = 712;
		assembler.update(eq(new ContractEntry(details)));
		control.replay();
		
		handler.bondContractDetails(5, details);
		
		control.verify();
	}
	
	@Test
	public void testContractDetailsEnd() throws Exception {
		control.replay();
		
		handler.contractDetailsEnd(824);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice() throws Exception {
		control.replay();
		
		handler.tickPrice(8, 0, 15.20d);
		
		control.verify();
	}
	
	@Test
	public void testTickSize() throws Exception {
		control.replay();
		
		handler.tickSize(15, 1, 24);
		
		control.verify();
	}
	
	@Test
	public void testOpenOrder() throws Exception {
		Contract contract = new Contract();
		contract.m_conId = 7612;
		Order order = new Order();
		OrderState state = createOrderState();
		control.replay();
		
		handler.openOrder(81, contract, order, state);
		
		control.verify();
	}
	
	@Test
	public void testOrderStatus() throws Exception {
		control.replay();
		
		handler.orderStatus(928, "foobar", 20, 10, 1d, 0, 5, 2d, 1, "why");
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(handler.equals(handler));
		assertFalse(handler.equals(null));
		assertFalse(handler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		IBTerminalBuilder tb = new IBTerminalBuilder();
		IBEditableTerminal t1 = tb.createTerminal("foo");
		IBEditableTerminal t2 = tb.createTerminal("foo");
		handler = new IBMainHandler(t1, assembler);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<Assembler> vAsm = new Variant<Assembler>(vTerm)
			.add(assembler)
			.add(control.createMock(Assembler.class));
		Variant<?> iterator = vAsm;
		int foundCnt = 0;
		IBMainHandler x = null, found = null;
		do {
			x = new IBMainHandler(vTerm.get(), vAsm.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(assembler, found.getAssembler());
	}

}

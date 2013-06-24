package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.api.MainHandler;

public class IBRequestSecurityHandlerTest {
	private static SecurityDescriptor descr1, descr2;
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private MainHandler hMain;
	private IBClient client;
	private IBRequestSecurityHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr1 = new SecurityDescriptor("SBR", "EQBR", "SUR", SecurityType.STK);
		descr2 = new SecurityDescriptor("RTS", "SFUT", "USD", SecurityType.OPT);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		hMain = control.createMock(MainHandler.class);
		client = control.createMock(IBClient.class);
		handler = new IBRequestSecurityHandler(terminal, 815, descr1);
		
		expect(terminal.getClient()).andStubReturn(client);
		expect(client.getMainHandler()).andStubReturn(hMain);
	}
	
	@Test
	public void testError() throws Exception {
		hMain.error(815, 400, "test error message");
		terminal.fireSecurityRequestError(descr1, 400, "test error message");
		client.removeHandler(815);
		control.replay();
		
		handler.error(815, 400, "test error message");
		
		control.verify();
	}
	
	@Test
	public void testConnectionOpened() throws Exception {
		Object fix[][] = {
				// local type, IB type
				{ SecurityType.BOND, "STK" },
				{ SecurityType.CASH, "CASH" },
				{ SecurityType.FUT, "FUT" },
				{ SecurityType.OPT, "OPT" },
				{ SecurityType.STK, "STK" },
				{ SecurityType.UNK, "STK" },
		};
		SecurityDescriptor descr;
		Contract expected = new Contract();
		expected.m_symbol = "SBR";
		expected.m_exchange = "EQBR";
		expected.m_currency = "SUR";
		for ( int i = 0; i < fix.length; i ++ ) {
			descr = new SecurityDescriptor("SBR", "EQBR", "SUR",
					(SecurityType) fix[i][0]);
			expected.m_secType = (String) fix[i][1];
			setUp();
			handler = new IBRequestSecurityHandler(terminal, 815, descr);
			client.reqContractDetails(eq(815), eq(expected));
			control.replay();
			
			handler.connectionOpened();
			
			control.verify();
		}
	}
	
	@Test
	public void testConnectionClosed() throws Exception {
		control.replay();
		
		handler.connectionClosed();
		
		control.verify();
	}
	
	@Test
	public void testContractDetails() throws Exception {
		ContractDetails details = new ContractDetails();
		hMain.contractDetails(eq(815), same(details));
		control.replay();
		
		handler.contractDetails(815, details);
		
		control.verify();
	}
	
	@Test
	public void testBondContractDetails() throws Exception {
		ContractDetails details = new ContractDetails();
		hMain.bondContractDetails(eq(815), same(details));
		control.replay();
		
		handler.bondContractDetails(815, details);
		
		control.verify();
	}
	
	@Test
	public void testContractDetailsEnd() throws Exception {
		client.removeHandler(eq(815));
		control.replay();
		
		handler.contractDetailsEnd(815);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice() throws Exception {
		control.replay();
		
		handler.tickPrice(815, 0, 12.34d);
		
		control.verify();
	}
	
	@Test
	public void testTickSize() throws Exception {
		control.replay();
		
		handler.tickSize(815, 25, 15);
		
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
		IBEditableTerminal t1 = (IBEditableTerminal) tb.createTerminal("foo");
		IBEditableTerminal t2 = (IBEditableTerminal) tb.createTerminal("foo");
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<Integer> vId = new Variant<Integer>(vTerm)
			.add(815)
			.add(245);
		Variant<SecurityDescriptor> vDesc = new Variant<SecurityDescriptor>(vId)
			.add(descr1)
			.add(descr2);
		Variant<?> iterator = vDesc;
		int foundCnt = 0;
		IBRequestSecurityHandler x, found = null;
		handler = new IBRequestSecurityHandler(t1, 815, descr1);
		do {
			x = new IBRequestSecurityHandler(vTerm.get(),vId.get(),vDesc.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertEquals(815, found.getRequestId());
		assertEquals(descr1, found.getSecurityDescriptor());
	}

}

package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.api.*;

public class IBRequestContractHandlerTest {
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private IBClient client;
	private MainHandler hMain;
	private IBRequestContractHandler handler;

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
		hMain = control.createMock(MainHandler.class);
		handler = new IBRequestContractHandler(terminal, 816, 241928);
		
		expect(terminal.getClient()).andStubReturn(client);
		expect(client.getMainHandler()).andStubReturn(hMain);
	}
	
	@Test
	public void testError() throws Exception {
		hMain.error(816, 200, "test error message");
		client.removeHandler(816);
		control.replay();
		
		handler.error(816, 200, "test error message");
		
		control.verify();
	}
	
	@Test
	public void testConnectionOpened() throws Exception {
		Contract expected = new Contract();
		expected.m_conId = 241928;
		client.reqContractDetails(eq(816), eq(expected));
		control.replay();
		
		handler.connectionOpened();
		
		control.verify();
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
		hMain.contractDetails(eq(816), same(details));
		control.replay();
		
		handler.contractDetails(816, details);
		
		control.verify();
	}
	
	@Test
	public void testBondContractDetails() throws Exception {
		ContractDetails details = new ContractDetails();
		hMain.bondContractDetails(eq(816), same(details));
		control.replay();
		
		handler.bondContractDetails(816, details);
		
		control.verify();
	}
	
	@Test
	public void testContractDetailsEnd() throws Exception {
		client.removeHandler(eq(816));
		control.replay();
		
		handler.contractDetailsEnd(816);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice() throws Exception {
		control.replay();
		
		handler.tickPrice(816, 0, 12.34d);
		
		control.verify();
	}
	
	@Test
	public void testTickSize() throws Exception {
		control.replay();
		
		handler.tickSize(816, 25, 15);
		
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
			.add(816)
			.add(245);
		Variant<Integer> vConId = new Variant<Integer>(vId)
			.add(241928)
			.add(117215);
		Variant<?> iterator = vConId;
		int foundCnt = 0;
		IBRequestContractHandler x, found = null;
		handler = new IBRequestContractHandler(t1, 816, 241928);
		do {
			x= new IBRequestContractHandler(vTerm.get(),vId.get(),vConId.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertEquals(816, found.getRequestId());
		assertEquals(241928, found.getContractId());
	}

}

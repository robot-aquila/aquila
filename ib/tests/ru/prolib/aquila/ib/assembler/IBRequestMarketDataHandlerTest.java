package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.TickType;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.*;
import ru.prolib.aquila.ib.api.*;
import ru.prolib.aquila.ib.assembler.cache.ContractEntry;

public class IBRequestMarketDataHandlerTest {
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private EditableSecurity security;
	private IBClient client;
	private MainHandler hMain;
	private ContractEntry entry;
	private IBRequestMarketDataHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		security = control.createMock(EditableSecurity.class);
		client = control.createMock(IBClient.class);
		hMain = control.createMock(MainHandler.class);
		entry = control.createMock(ContractEntry.class);
		handler =new IBRequestMarketDataHandler(terminal, security, 815, entry);
		
		expect(terminal.getClient()).andStubReturn(client);
		expect(client.getMainHandler()).andStubReturn(hMain);
	}
	
	@Test
	public void testError() throws Exception {
		hMain.error(eq(815), eq(200), eq("test error message"));
		client.removeHandler(eq(815));
		control.replay();
		
		handler.error(815, 200, "test error message");
		
		control.verify();
	}
	
	@Test
	public void testConnectionOpened() throws Exception {
		expect(entry.getContractId()).andReturn(34);
		expect(entry.getDefaultExchange()).andReturn("merlin");
		Contract expected = new Contract();
		expected.m_conId = 34;
		expected.m_exchange = "merlin";
		client.reqMktData(eq(815), eq(expected), (String) isNull(), eq(false));
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
		control.replay();
		
		handler.contractDetails(815, new ContractDetails());
		
		control.verify();
	}
	
	@Test
	public void testBondContractDetails() throws Exception {
		control.replay();
		
		handler.bondContractDetails(815, new ContractDetails());
		
		control.verify();
	}
	
	@Test
	public void testContractDetailsEnd() throws Exception {
		control.replay();
		
		handler.contractDetailsEnd(815);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice_Ask() throws Exception {
		security.setAskPrice(eq(824.15d));
		control.replay();
		
		handler.tickPrice(815, TickType.ASK, 824.15d);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice_Bid() throws Exception {
		security.setBidPrice(eq(827.24d));
		control.replay();
		
		handler.tickPrice(815, TickType.BID, 827.24d);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice_Last() throws Exception {
		security.setLastPrice(eq(912.84d));
		control.replay();
		
		handler.tickPrice(815, TickType.LAST, 912.84d);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice_Open() throws Exception {
		security.setOpenPrice(eq(112.82d));
		control.replay();
		
		handler.tickPrice(815, TickType.OPEN, 112.82);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice_High() throws Exception {
		security.setHighPrice(eq(519.33d));
		control.replay();
		
		handler.tickPrice(815, TickType.HIGH, 519.33d);
		
		control.verify();
	}
	
	@Test
	public void testTickPrice_Low() throws Exception {
		security.setLowPrice(eq(1824.15d));
		control.replay();
		
		handler.tickPrice(815, TickType.LOW, 1824.15d);
		
		control.verify();
	}

	@Test
	public void testTickPrice_Close() throws Exception {
		security.setClosePrice(eq(4.15d));
		control.replay();
		
		handler.tickPrice(815, TickType.CLOSE, 4.15d);
		
		control.verify();
	}
	
	@Test
	public void testTickSize_AskSize() throws Exception {
		security.setAskSize(eq(new Long(1000L)));
		control.replay();
		
		handler.tickSize(815, TickType.ASK_SIZE, 1000);
		
		control.verify();
	}
	
	@Test
	public void testTickSize_BidSize() throws Exception {
		security.setBidSize(eq(new Long(2000L)));
		control.replay();
		
		handler.tickSize(815, TickType.BID_SIZE, 2000);
		
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
		IBEditableTerminal t1 = tb.createTerminal("foo"),
			t2 = tb.createTerminal("foo");
		handler = new IBRequestMarketDataHandler(t1, security, 815, entry);
		Variant<IBEditableTerminal> vTerm = new Variant<IBEditableTerminal>()
			.add(t1)
			.add(t2);
		Variant<EditableSecurity> vSec = new Variant<EditableSecurity>(vTerm)
			.add(security)
			.add(control.createMock(EditableSecurity.class));
		Variant<Integer> vReqId = new Variant<Integer>(vSec)
			.add(815)
			.add(251);
		Variant<ContractEntry> vEntry = new Variant<ContractEntry>(vReqId)
			.add(entry)
			.add(control.createMock(ContractEntry.class));
		Variant<?> iterator = vEntry;
		int foundCnt = 0;
		IBRequestMarketDataHandler x, found = null;
		do {
			x = new IBRequestMarketDataHandler(vTerm.get(), vSec.get(),
					vReqId.get(), vEntry.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(security, found.getSecurity());
		assertEquals(815, found.getRequestId());
		assertSame(entry, found.getContractEntry());
	}

}

package ru.prolib.aquila.quik.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.quik.assembler.Assembler;
import ru.prolib.aquila.quik.assembler.MainHandler;
import ru.prolib.aquila.t2q.T2QConnStatus;
import ru.prolib.aquila.t2q.T2QOrder;
import ru.prolib.aquila.t2q.T2QTrade;

public class MainHandlerTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private Assembler asm;
	private MainHandler handler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		asm = control.createMock(Assembler.class);
		handler = new MainHandler(terminal, asm);
	}
	
	@Test
	public void testConnectionStatus_Connected() throws Exception {
		terminal.fireTerminalConnectedEvent();
		control.replay();
		
		handler.connectionStatus(T2QConnStatus.DLL_CONN);
		
		control.verify();
	}
	
	@Test
	public void testConnectionStatus_Disconected() throws Exception {
		terminal.fireTerminalDisconnectedEvent();
		control.replay();
		
		handler.connectionStatus(T2QConnStatus.DLL_DISC);
		
		control.verify();
	}
	
	@Test
	public void testOrderStatus() throws Exception {
		T2QOrder order = control.createMock(T2QOrder.class);
		asm.assemble(same(order));
		control.replay();
		
		handler.orderStatus(order);
		
		control.verify();
	}
	
	@Test
	public void testTradeStatus() throws Exception {
		T2QTrade trade = control.createMock(T2QTrade.class);
		asm.assemble(same(trade));
		control.replay();
		
		handler.tradeStatus(trade);
		
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
		EditableTerminal t1 = new BasicTerminalBuilder().buildTerminal();
		handler = new MainHandler(t1, asm);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(t1)
			.add(new BasicTerminalBuilder().buildTerminal());
		Variant<Assembler> vAsm = new Variant<Assembler>(vTerm)
			.add(asm)
			.add(control.createMock(Assembler.class));
		Variant<?> iterator = vAsm;
		int foundCnt = 0;
		MainHandler x, found = null;
		do {
			x = new MainHandler(vTerm.get(), vAsm.get());
			if ( handler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(t1, found.getTerminal());
		assertSame(asm, found.getAssembler());
	}

}

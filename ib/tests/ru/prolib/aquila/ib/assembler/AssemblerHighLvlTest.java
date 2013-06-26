package ru.prolib.aquila.ib.assembler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.*;

public class AssemblerHighLvlTest {
	private IMocksControl control;
	private IBEditableTerminal terminal;
	private EditablePortfolio port;
	private AssemblerHighLvl asm;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(IBEditableTerminal.class);
		port = control.createMock(EditablePortfolio.class);
		asm = new AssemblerHighLvl(terminal);
	}
	
	@Test
	public void testGetPortfolio_CreateNew() throws Exception {
		Account acc = new Account("TEST");
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(false);
		expect(terminal.createPortfolio(eq(acc))).andReturn(port);
		control.replay();
		
		assertSame(port, asm.getPortfolio("TEST"));
		
		control.verify();
	}
	
	@Test
	public void testGetPortfolio_ReturnExisting() throws Exception {
		Account acc = new Account("BEST");
		expect(terminal.isPortfolioAvailable(eq(acc))).andReturn(true);
		expect(terminal.getEditablePortfolio(eq(acc))).andReturn(port);
		control.replay();
		
		assertSame(port, asm.getPortfolio("BEST"));
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio_SkipNoChanges() throws Exception {
		expect(port.hasChanged()).andReturn(false);
		control.replay();
		
		asm.fireEvents(port);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio_ForAvailable() throws Exception {
		expect(port.hasChanged()).andReturn(true);
		expect(port.isAvailable()).andReturn(true);
		port.fireChangedEvent();
		port.resetChanges();
		control.replay();
		
		asm.fireEvents(port);
		
		control.verify();
	}
	
	@Test
	public void testFireEvents_Portfolio_ForNew() throws Exception {
		expect(port.hasChanged()).andReturn(true);
		expect(port.isAvailable()).andReturn(false);
		port.setAvailable(true);
		terminal.firePortfolioAvailableEvent(same(port));
		port.resetChanges();
		control.replay();
		
		asm.fireEvents(port);
		
		control.verify();
	}
	
	@Test
	public void testX() throws Exception {
		fail("TODO: incomplete");
	}

}

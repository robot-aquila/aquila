package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;

public class ObjectFactoryImplTest {
	private Symbol symbol = new Symbol("SBER");
	private Account account = new Account("PORT-001");
	private IMocksControl control;
	private EventQueue queue;
	private EditableTerminal terminal;
	private ObjectFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueImpl();
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		expect(terminal.getTerminalID()).andStubReturn("foobar");
		expect(terminal.getEventQueue()).andStubReturn(queue);
		control.replay();
		factory = new ObjectFactoryImpl();
	}
	
	@Test
	public void testCreateSecurity() {
		EditableSecurity security = factory.createSecurity(terminal, symbol);
		assertNotNull(security);
		assertEquals(symbol, security.getSymbol());
		assertSame(terminal, security.getTerminal());
		assertEquals("foobar.SBER.SECURITY", security.getContainerID());
	}
	
	@Test
	public void testCreatePortfolio() {
		EditablePortfolio portfolio = factory.createPortfolio(terminal, account);
		assertNotNull(portfolio);
		assertEquals(account, portfolio.getAccount());
		assertSame(terminal, portfolio.getTerminal());
		assertEquals("foobar.PORT-001.PORTFOLIO", portfolio.getContainerID());
	}
	
	@Test
	public void testCreateOrder() {
		EditableOrder order = factory.createOrder(terminal, account, symbol, 800L);
		assertNotNull(order);
		assertEquals(account, order.getAccount());
		assertEquals(symbol, order.getSymbol());
		assertSame(terminal, order.getTerminal());
		assertEquals("foobar.PORT-001[SBER].ORDER#800", order.getContainerID());
	}

}

package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.DataProviderStub;

public class ObjectFactoryImplTest {
	private Symbol symbol = new Symbol("SBER");
	private Account account = new Account("PORT-001");
	private EventQueue queue;
	private EditableTerminal terminal;
	private ObjectFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		factory = new ObjectFactoryImpl();
		queue = new EventQueueImpl();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.withEventQueue(queue)
				.withObjectFactory(factory)
				.withTerminalID("foobar")
				.buildTerminal();
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
	
	@Test
	public void testCreatePosition() throws Exception {
		PositionImpl position = (PositionImpl) factory.createPosition(terminal, account, symbol);
		assertNotNull(position);
		assertEquals(account, position.getAccount());
		assertEquals(symbol, position.getSymbol());
		assertSame(terminal, position.getTerminal());
		PortfolioImpl portfolio = (PortfolioImpl) terminal.getPortfolio(account);
		assertSame(position.lock, portfolio.lock);
		assertEquals("foobar.PORT-001[SBER].POSITION", position.getContainerID());
	}

}

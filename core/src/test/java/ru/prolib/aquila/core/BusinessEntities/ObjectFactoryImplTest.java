package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueFactory;
import ru.prolib.aquila.core.data.DataProviderStub;

public class ObjectFactoryImplTest {
	private IMocksControl control;
	private Lock lockMock1, lockMock2, lock;
	private Symbol symbol = new Symbol("SBER");
	private Account account = new Account("PORT-001");
	private EventQueue queue;
	private EditableTerminal terminal;
	private ObjectFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		lockMock1 = control.createMock(Lock.class);
		lockMock2 = control.createMock(Lock.class);
		lock = new ReentrantLock();
		factory = new ObjectFactoryImpl(lock);
		queue = new EventQueueFactory().createDefault();
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.withEventQueue(queue)
				.withObjectFactory(factory)
				.withTerminalID("foobar")
				.buildTerminal();
	}
	
	@Test
	public void testCreateSecurity() {
		SecurityImpl security = (SecurityImpl) factory.createSecurity(terminal, symbol);
		
		assertNotNull(security);
		assertEquals(symbol, security.getSymbol());
		assertSame(terminal, security.getTerminal());
		assertEquals("foobar.SBER.SECURITY", security.getContainerID());
		assertSame(lock, security.getLock());
	}
	
	@Test
	public void testCreatePortfolio() {
		PortfolioImpl portfolio = (PortfolioImpl) factory.createPortfolio(terminal, account);
		assertNotNull(portfolio);
		assertEquals(account, portfolio.getAccount());
		assertSame(terminal, portfolio.getTerminal());
		assertEquals("foobar.PORT-001.PORTFOLIO", portfolio.getContainerID());
		assertSame(lock, portfolio.getLock());
	}
	
	@Test
	public void testCreateOrder() {
		terminal.getEditableSecurity(symbol);
		terminal.getEditablePortfolio(account);
		
		OrderImpl order = (OrderImpl) factory.createOrder(terminal, account, symbol, 800L);
		
		assertNotNull(order);
		assertEquals(account, order.getAccount());
		assertEquals(symbol, order.getSymbol());
		assertSame(terminal, order.getTerminal());
		assertEquals("foobar.PORT-001[SBER].ORDER#800", order.getContainerID());
		assertSame(lock, order.getLock());
	}
	
	@Test
	public void testCreatePosition() throws Exception {
		terminal.getEditableSecurity(symbol);
		// Do not create a portfolio. The factory must create portfolio if needed.
		//terminal.getEditablePortfolio(account);
		
		PositionImpl position = (PositionImpl) factory.createPosition(terminal, account, symbol);
		assertNotNull(position);
		assertEquals(account, position.getAccount());
		assertEquals(symbol, position.getSymbol());
		assertSame(terminal, position.getTerminal());
		PortfolioImpl portfolio = (PortfolioImpl) terminal.getPortfolio(account);
		assertSame(position.lock, portfolio.lock);
		assertEquals("foobar.PORT-001[SBER].POSITION", position.getContainerID());
		assertSame(lock, position.getLock());
	}
	
	@Test
	public void testHashCode() {
		factory = new ObjectFactoryImpl(lockMock1);
		int expected = new HashCodeBuilder()
				.append(lockMock1)
				.build();
		
		assertEquals(expected, factory.hashCode());
	}
	
	@Test
	public void testEquals() {
		factory = new ObjectFactoryImpl(lockMock1);
		assertTrue(factory.equals(factory));
		assertTrue(factory.equals(new ObjectFactoryImpl(lockMock1)));
		assertFalse(factory.equals(new ObjectFactoryImpl(lockMock2)));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

}

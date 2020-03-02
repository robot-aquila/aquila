package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.*;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.FeedStatus;

public class QFSymbolAutosubscrTest {
	static Symbol symbol1, symbol2, symbol3;
	static Account account1, account2, account3;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("AAPL");
		symbol2 = new Symbol("MSFT");
		symbol3 = new Symbol("SBER");
		account1 = new Account("TEST1");
		account2 = new Account("TEST2");
		account3 = new Account("TEST3");
	}
	
	@Rule public ExpectedException eex = ExpectedException.none();
	IMocksControl control;
	Action nodeActionMock;
	Set<Account> nodeOpenPositions;
	ANode node;
	EditableTerminal terminal;
	EditablePortfolio port1, port2, port3;
	Order order_p1_s1, order_p2_s1, order_p3_s1;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		nodeActionMock = control.createMock(Action.class);
		nodeOpenPositions = new LinkedHashSet<>();
		node = new ANode(symbol1, nodeActionMock, nodeOpenPositions);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol3);
		port1 = terminal.getEditablePortfolio(account1);
		port1.getEditablePosition(symbol1).consume(new DeltaUpdateBuilder()
				.withToken(PositionField.CURRENT_VOLUME, of(10L))
				.buildUpdate());
		port2 = terminal.getEditablePortfolio(account2);
		port3 = terminal.getEditablePortfolio(account3);
		order_p1_s1 = terminal.createOrder(account1, symbol1, OrderAction.BUY, of(50L));
		order_p2_s1 = terminal.createOrder(account2, symbol1, OrderAction.BUY, of(25L));
		order_p3_s1 = terminal.createOrder(account3, symbol1, OrderAction.BUY, of(10L));
	}
	
	@Test
	public void testNode_Increment_FromNotRequiredToMaxDetails() {
		nodeActionMock.change(symbol1, FeedStatus.NOT_REQUIRED, FeedStatus.MAX_DETAILS);
		control.replay();
		
		node.increment(order_p1_s1);
		node.increment(order_p2_s1);
		node.increment(order_p3_s1);
		
		control.verify();
		assertEquals(FeedStatus.MAX_DETAILS, node.currentStatus());
		assertEquals(Collections.EMPTY_SET, nodeOpenPositions);
	}
	
	@Test
	public void testNode_Increment_FromLessDetailsToMaxDetails() {
		nodeActionMock.change(symbol1, FeedStatus.NOT_REQUIRED, FeedStatus.MAX_DETAILS);
		nodeActionMock.change(symbol1, FeedStatus.MAX_DETAILS, FeedStatus.LESS_DETAILS);
		control.replay();
		node.increment(order_p1_s1);
		node.decrement(order_p1_s1);
		control.resetToStrict();
		nodeActionMock.change(symbol1, FeedStatus.LESS_DETAILS, FeedStatus.MAX_DETAILS);
		control.replay();
		
		node.increment(order_p1_s1);
		
		control.verify();
		assertEquals(FeedStatus.MAX_DETAILS, node.currentStatus());
	}
	
	@Test
	public void testNode_Decrement_ThrowsIfNotRequired() {
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Unexpected call in status: NOT_REQUIRED");
		
		node.decrement(order_p1_s1);
	}
	
	@Test
	public void testNode_Decrement_ThrowsIfLessDetails() {
		nodeActionMock.change(symbol1, FeedStatus.NOT_REQUIRED, FeedStatus.MAX_DETAILS);
		nodeActionMock.change(symbol1, FeedStatus.MAX_DETAILS, FeedStatus.LESS_DETAILS);
		control.replay();
		node.increment(order_p1_s1);
		node.decrement(order_p1_s1);
		control.resetToStrict();
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Unexpected call in status: LESS_DETAILS");

		
		node.decrement(order_p1_s1);
	}
	
	@Test
	public void testNode_Decrement_FromMaxDetailsToLessDetails() {
		nodeActionMock.change(symbol1, FeedStatus.NOT_REQUIRED, FeedStatus.MAX_DETAILS);
		control.replay();
		node.increment(order_p1_s1);
		node.increment(order_p1_s1);
		control.resetToStrict();
		nodeActionMock.change(symbol1, FeedStatus.MAX_DETAILS, FeedStatus.LESS_DETAILS);
		control.replay();
		
		assertFalse(node.decrement(order_p1_s1));
		assertEquals(FeedStatus.MAX_DETAILS, node.currentStatus());
		
		assertFalse(node.decrement(order_p1_s1));
		assertEquals(FeedStatus.LESS_DETAILS, node.currentStatus());
		
		control.verify();
	}
	
	@Test
	public void testNode_Decrement_FromMaxDetailsToNotRequired() {
		nodeActionMock.change(symbol1, FeedStatus.NOT_REQUIRED, FeedStatus.MAX_DETAILS);
		control.replay();
		node.increment(order_p3_s1);
		control.resetToStrict();
		nodeActionMock.change(symbol1, FeedStatus.MAX_DETAILS, FeedStatus.NOT_REQUIRED);
		control.replay();
		
		assertTrue(node.decrement(order_p3_s1));
		assertEquals(FeedStatus.NOT_REQUIRED, node.currentStatus());
		
		control.verify();
	}
	
}

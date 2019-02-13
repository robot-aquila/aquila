package ru.prolib.aquila.ui.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.text.Messages;

public class PositionListTableModelTest {
	private static Account ACCOUNT1 = new Account("foo@bar");
	private static Symbol SYMBOL1 = new Symbol("AAPL"), SYMBOL2 = new Symbol("MSFT"), SYMBOL3 = new Symbol("SPY");
	private EditableTerminal terminal;
	private PositionListTableModel service;

	@Before
	public void setUp() throws Exception {
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		terminal.getEditableSecurity(SYMBOL1);
		terminal.getEditableSecurity(SYMBOL2);
		terminal.getEditableSecurity(SYMBOL3);
		service = new PositionListTableModel(new Messages());
	}
	
	@After
	public void tearDown() {
		service.stopListeningUpdates();
	}
	
	@Test
	public void testGetColumnID() {
		assertEquals(PositionListTableModel.CID_TERMINAL_ID, service.getColumnID(0));
		assertEquals(PositionListTableModel.CID_ACCOUNT, service.getColumnID(1));
		assertEquals(PositionListTableModel.CID_SYMBOL, service.getColumnID(2));
		assertEquals(PositionListTableModel.CID_CURRENT_VOLUME, service.getColumnID(3));
		assertEquals(PositionListTableModel.CID_CURRENT_PRICE, service.getColumnID(4));
		assertEquals(PositionListTableModel.CID_OPEN_PRICE, service.getColumnID(5));
		assertEquals(PositionListTableModel.CID_USED_MARGIN, service.getColumnID(6));
		assertEquals(PositionListTableModel.CID_PROFIT_AND_LOSS, service.getColumnID(7));
	}

	@Test
	public void testGetValueAt_RowOutOfBoundsBugfix() {
		EditablePortfolio portfolio = terminal.getEditablePortfolio(ACCOUNT1);
		portfolio.getEditablePosition(SYMBOL1);
		portfolio.getEditablePosition(SYMBOL2);
		portfolio.getEditablePosition(SYMBOL3);
		service.add(portfolio);
		service.startListeningUpdates();
		
		assertNotNull(service.getValueAt(0, 1));
		assertNotNull(service.getValueAt(1, 1));
		assertNotNull(service.getValueAt(2, 1));
		assertNull(service.getValueAt(3, 1));
	}

}

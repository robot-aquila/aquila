package ru.prolib.aquila.ui.form;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateType;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.form.MarketDepthTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class MarketDepthTableModelTest {
	private static Symbol symbol = new Symbol("AAPL");
	
	static class FullTableRefreshListener implements TableModelListener {
		final CountDownLatch signal;
		
		FullTableRefreshListener() {
			signal = new CountDownLatch(1);
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			assertEquals(TableModelEvent.ALL_COLUMNS, e.getColumn());
			assertEquals(0, e.getFirstRow());
			assertEquals(Integer.MAX_VALUE, e.getLastRow());
			assertEquals(TableModelEvent.UPDATE, e.getType());
			signal.countDown();
		}
		
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private EditableSecurity security;
	private IMocksControl control;
	private IMessages messagesMock;
	private MarketDepthTableModel model;
	private FullTableRefreshListener refreshListener;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		messagesMock = control.createMock(IMessages.class);
		security = new BasicTerminalBuilder()
			.buildTerminal()
			.getEditableSecurity(symbol);
		security.update(SecurityField.TICK_SIZE, new FDecimal("0.01"));
		model = new MarketDepthTableModel(messagesMock, security, 5);
		refreshListener = new FullTableRefreshListener();
	}
	
	private void assertRowEquals(int rowIndex, Long bid, String price, Long ask) {
		BigDecimal p = null;
		if ( price != null ) {
			p = new BigDecimal(price);
		}
		assertEquals(bid, model.getValueAt(rowIndex, 0));
		assertEquals(p, model.getValueAt(rowIndex, 1));
		assertEquals(ask, model.getValueAt(rowIndex, 2));
	}
	
	private void assignTestMarketDepth() {
		security.update(SecurityField.TICK_SIZE, new FDecimal("0.0001"));
		security.consume(new MDUpdateBuilder(symbol)
			.withTime("2016-09-12T00:00:00Z")
			.withType(MDUpdateType.REFRESH)
			.addAsk(101.85, 2878)
			.addAsk(101.84, 3497)
			.buildMDUpdate());
	}

	@Test
	public void testGetColumnIDList() {
		List<Integer> expected = new ArrayList<>();
		expected.add(MarketDepthTableModel.CID_BID_SIZE);
		expected.add(MarketDepthTableModel.CID_PRICE);
		expected.add(MarketDepthTableModel.CID_ASK_SIZE);
		
		List<Integer> actual = model.getColumnIDList();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetColumnIDToHeaderMap() {
		Map<Integer, MsgID> expected = new HashMap<>();
		expected.put(MarketDepthTableModel.CID_ASK_SIZE, CommonMsg.MDD_ASK_SIZE);
		expected.put(MarketDepthTableModel.CID_BID_SIZE, CommonMsg.MDD_BID_SIZE);
		expected.put(MarketDepthTableModel.CID_PRICE, CommonMsg.MDD_PRICE);
		
		Map<Integer, MsgID> actual = model.getColumnIDToHeaderMap();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(10, model.getRowCount());
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(3, model.getColumnCount());
	}

	@Test
	public void testGetColumnIndex() {
		assertEquals(0, model.getColumnIndex(MarketDepthTableModel.CID_BID_SIZE));
		assertEquals(1, model.getColumnIndex(MarketDepthTableModel.CID_PRICE));
		assertEquals(2, model.getColumnIndex(MarketDepthTableModel.CID_ASK_SIZE));
	}
	
	@Test
	public void testGetColumnID() {
		assertEquals(MarketDepthTableModel.CID_BID_SIZE, model.getColumnID(0));
		assertEquals(MarketDepthTableModel.CID_PRICE, model.getColumnID(1));
		assertEquals(MarketDepthTableModel.CID_ASK_SIZE, model.getColumnID(2));
	}
	
	@Test
	public void testGetColumnName() {
		expect(messagesMock.get(CommonMsg.MDD_BID_SIZE)).andReturn("Bid");
		expect(messagesMock.get(CommonMsg.MDD_PRICE)).andReturn("Price");
		expect(messagesMock.get(CommonMsg.MDD_ASK_SIZE)).andReturn("Ask");
		control.replay();
		
		assertEquals("Bid", model.getColumnName(0));
		assertEquals("Price", model.getColumnName(1));
		assertEquals("Ask", model.getColumnName(2));
		
		control.verify();
	}
	
	@Test
	public void testGetColumnClass() {
		assertEquals(Long.class, model.getColumnClass(0));
		assertEquals(BigDecimal.class, model.getColumnClass(1));
		assertEquals(Long.class, model.getColumnClass(2));
	}
	
	@Test
	public void testStartListeningUpdates() throws Exception {
		assignTestMarketDepth();
		model.addTableModelListener(refreshListener);
		
		model.startListeningUpdates();
		
		assertTrue(refreshListener.signal.await(1, TimeUnit.SECONDS));
		assertTrue(model.isListeningUpdates());
		assertTrue(security.onMarketDepthUpdate().isListener(model));
		assertRowEquals(0,  null,       null,  null);
		assertRowEquals(1,  null,       null,  null);
		assertRowEquals(2,  null,       null,  null);
		assertRowEquals(3,  null, "101.8500", 2878L);
		assertRowEquals(4,  null, "101.8400", 3497L);
		assertRowEquals(5,  null,       null,  null);
		assertRowEquals(6,  null,       null,  null);
		assertRowEquals(7,  null,       null,  null);
		assertRowEquals(8,  null,       null,  null);
		assertRowEquals(9,  null,       null,  null);
	}
	
	@Test
	public void testStopListeningUpdates() throws Exception {
		assignTestMarketDepth();
		model.startListeningUpdates();
		model.addTableModelListener(refreshListener);
		
		model.stopListeningUpdates();
		
		assertTrue(refreshListener.signal.await(1, TimeUnit.SECONDS));
		assertFalse(model.isListeningUpdates());
		assertFalse(security.onMarketDepthUpdate().isListener(model));
		for ( int i = 0; i < 10; i ++ ) {
			assertRowEquals(i, null, null, null);
		}
	}
	
	@Test
	public void testClose() throws Exception {
		assignTestMarketDepth();
		model.startListeningUpdates();
		model.addTableModelListener(refreshListener);
		
		model.close();
		
		assertTrue(refreshListener.signal.await(1, TimeUnit.SECONDS));
		assertFalse(model.isListeningUpdates());
		assertFalse(security.onMarketDepthUpdate().isListener(model));
		for ( int i = 0; i < 10; i ++ ) {
			assertRowEquals(i, null, null, null);
		}
	}
	
	@Test
	public void testGetValueAt_NotStarted() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			assertRowEquals(i, null, null, null);
		}
	}
	
	@Test
	public void testGetValueAt_HasEmptyRows() throws Exception {
		security.update(SecurityField.TICK_SIZE, new FDecimal("0.001"));
		security.consume(new MDUpdateBuilder(symbol)
			.withTime("2016-09-12T00:00:00Z")
			.withType(MDUpdateType.REFRESH)
			.addAsk(101.85, 2878)
			.addAsk(101.84, 3497)
			.addAsk(101.83, 1704)
			.addAsk(101.82,  750)
			.addBid(101.80, 2293)
			.addBid(101.79, 1475)
			.addBid(101.78, 2130)
			.addBid(101.77, 3513)
			.buildMDUpdate());
		model.startListeningUpdates();
		model.addTableModelListener(refreshListener);
		
		model.onEvent(null);
		
		assertTrue(refreshListener.signal.await(1, TimeUnit.SECONDS));
		assertRowEquals(0,  null,      null,  null);
		assertRowEquals(1,  null, "101.850", 2878L);
		assertRowEquals(2,  null, "101.840", 3497L);
		assertRowEquals(3,  null, "101.830", 1704L);
		assertRowEquals(4,  null, "101.820",  750L);
		assertRowEquals(5, 2293L, "101.800",  null);
		assertRowEquals(6, 1475L, "101.790",  null);
		assertRowEquals(7, 2130L, "101.780",  null);
		assertRowEquals(8, 3513L, "101.770",  null);
		assertRowEquals(9,  null,      null,  null);
	}
	
	@Test
	public void testGetValueAt_MoreThanMaxDepth() throws Exception {
		security.update(SecurityField.TICK_SIZE, new FDecimal("0.01"));
		security.consume(new MDUpdateBuilder(symbol)
			.withTime("2016-09-12T00:00:00Z")
			.withType(MDUpdateType.REFRESH)
			.addAsk(101.87, 1200)
			.addAsk(101.86, 1000)
			.addAsk(101.85, 2878)
			.addAsk(101.84, 3497)
			.addAsk(101.83, 1704)
			.addAsk(101.82,  750)
			.addBid(101.80, 2293)
			.addBid(101.79, 1475)
			.addBid(101.78, 2130)
			.addBid(101.77, 3513)
			.addBid(101.76, 2500)
			.addBid(101.75, 1300)
			.buildMDUpdate());
		model.startListeningUpdates();
		model.addTableModelListener(refreshListener);
		
		model.onEvent(null);
		
		assertTrue(refreshListener.signal.await(1, TimeUnit.SECONDS));
		assertRowEquals(0,  null, "101.86", 1000L);
		assertRowEquals(1,  null, "101.85", 2878L);
		assertRowEquals(2,  null, "101.84", 3497L);
		assertRowEquals(3,  null, "101.83", 1704L);
		assertRowEquals(4,  null, "101.82",  750L);
		assertRowEquals(5, 2293L, "101.80",  null);
		assertRowEquals(6, 1475L, "101.79",  null);
		assertRowEquals(7, 2130L, "101.78",  null);
		assertRowEquals(8, 3513L, "101.77",  null);
		assertRowEquals(9, 2500L, "101.76",  null);
	}
	
}

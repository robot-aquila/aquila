package ru.prolib.aquila.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class SecurityListTableModel extends AbstractTableModel
	implements ITableModel, EventListener
{
	private static final long serialVersionUID = 1L;
	// primary attributes
	public static final int CID_DISPLAY_NAME = 1;
	public static final int CID_SYMBOL = 2;
	public static final int CID_SYMBOL_CODE = 3;
	public static final int CID_SYMBOL_EXCHANGE = 4;
	public static final int CID_SYMBOL_TYPE = 5;
	public static final int CID_SYMBOL_CURRENCY = 6;
	public static final int CID_TERMINAL_ID = 7;
	public static final int CID_LOT_SIZE = 8;
	public static final int CID_SCALE = 9;
	// session attributes
	public static final int CID_TICK_SIZE = 10;
	public static final int CID_TICK_VALUE = 11;
	public static final int CID_INITIAL_MARGIN = 12;
	public static final int CID_SETTLEMENT_PRICE = 13;
	public static final int CID_LOWER_PRICE_LIMIT = 14;
	public static final int CID_UPPER_PRICE_LIMIT = 15;
	// operative attributes
	public static final int CID_LAST_PRICE = 16;
	public static final int CID_LAST_SIZE = 17;
	public static final int CID_ASK_PRICE = 18;
	public static final int CID_ASK_SIZE = 19;
	public static final int CID_BID_PRICE = 20;
	public static final int CID_BID_SIZE = 21;
	public static final int CID_OPEN_PRICE = 22;
	public static final int CID_HIGH_PRICE = 23;
	public static final int CID_LOW_PRICE = 24;
	public static final int CID_CLOSE_PRICE = 25;
	
	private final List<Integer> columnIndexToColumnID;
	private final Map<Integer, MsgID> columnIDToColumnHeader;
	private final IMessages messages;
	private final List<Security> securities;
	private final Map<Security, Integer> securityMap;
	private final Set<Terminal> terminalSet;
	private boolean subscribed = false;
	
	public SecurityListTableModel(IMessages messages) {
		super();
		columnIndexToColumnID = getColumnIDList();
		columnIDToColumnHeader = getColumnIDToHeaderMap();
		this.messages = messages;
		this.securities = new ArrayList<Security>();
		this.securityMap = new HashMap<Security, Integer>();
		this.terminalSet = new HashSet<Terminal>();
	}
	
	/**
	 * Get list of columns to display.
	 * <p>
	 * Not all security attributes are displayed by default.
	 * Override this method to add or modify column list. 
	 * <p>
	 * @return list of columns
	 */
	protected List<Integer> getColumnIDList() {
		List<Integer> cols = new ArrayList<>();
		cols.add(CID_DISPLAY_NAME);
		cols.add(CID_SYMBOL);
		cols.add(CID_LOT_SIZE);
		cols.add(CID_SCALE);
		cols.add(CID_TICK_SIZE);
		cols.add(CID_TICK_VALUE);
		cols.add(CID_INITIAL_MARGIN);
		cols.add(CID_SETTLEMENT_PRICE);
		cols.add(CID_LOWER_PRICE_LIMIT);
		cols.add(CID_UPPER_PRICE_LIMIT);
		cols.add(CID_ASK_PRICE);
		cols.add(CID_ASK_SIZE);
		cols.add(CID_BID_PRICE);
		cols.add(CID_BID_SIZE);
		cols.add(CID_LAST_PRICE);
		cols.add(CID_LAST_SIZE);
		return cols;
	}
	
	/**
	 * Get map of columns mapped to its titles.
	 * <p>
	 * Override this method to add or modify column titles.
	 * <p>
	 * @return map of column titles
	 */
	protected Map<Integer, MsgID> getColumnIDToHeaderMap() {
		Map<Integer, MsgID> head = new HashMap<>();
		head.put(CID_DISPLAY_NAME, SecurityMsg.NAME);
		head.put(CID_SYMBOL, SecurityMsg.SYMBOL);
		head.put(CID_SYMBOL_CODE, SecurityMsg.SYMBOL);
		head.put(CID_SYMBOL_EXCHANGE, SecurityMsg.EXCHANGE);
		head.put(CID_SYMBOL_TYPE, SecurityMsg.TYPE);
		head.put(CID_SYMBOL_CURRENCY, SecurityMsg.CURRENCY);
		head.put(CID_TERMINAL_ID, SecurityMsg.TERMINAL_ID);
		head.put(CID_LOT_SIZE, SecurityMsg.LOT_SIZE);
		head.put(CID_SCALE, SecurityMsg.SCALE);
		head.put(CID_TICK_SIZE, SecurityMsg.TICK_SIZE);
		head.put(CID_TICK_VALUE, SecurityMsg.TICK_VALUE);
		head.put(CID_INITIAL_MARGIN, SecurityMsg.INITIAL_MARGIN);
		head.put(CID_SETTLEMENT_PRICE, SecurityMsg.SETTLEMENT_PRICE);
		head.put(CID_LOWER_PRICE_LIMIT, SecurityMsg.LOWER_PRICE);
		head.put(CID_UPPER_PRICE_LIMIT, SecurityMsg.UPPER_PRICE);
		head.put(CID_LAST_PRICE, SecurityMsg.LAST_PRICE);
		head.put(CID_LAST_SIZE, SecurityMsg.LAST_SIZE);
		head.put(CID_ASK_PRICE, SecurityMsg.ASK_PRICE);
		head.put(CID_ASK_SIZE, SecurityMsg.ASK_SIZE);
		head.put(CID_BID_PRICE, SecurityMsg.BID_PRICE);
		head.put(CID_BID_SIZE, SecurityMsg.BID_SIZE);
		head.put(CID_OPEN_PRICE, SecurityMsg.OPEN_PRICE);
		head.put(CID_HIGH_PRICE, SecurityMsg.HIGH_PRICE);
		head.put(CID_LOW_PRICE, SecurityMsg.LOW_PRICE);
		head.put(CID_CLOSE_PRICE, SecurityMsg.CLOSE_PRICE);
		return head;
	}
	
	@Override
	public int getColumnCount() {
		return columnIndexToColumnID.size();
	}

	@Override
	public int getRowCount() {
		return securities.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if ( row > securities.size() ) {
			return null;
		}
		return getColumnValue(securities.get(row), getColumnID(col));
	}
	
	protected Object getColumnValue(Security security, int columnID) {
		Tick tick = null;
		Symbol symbol = security.getSymbol();
		switch ( columnID ) {
		case CID_DISPLAY_NAME:
			return security.getDisplayName();
		case CID_SYMBOL:
			return symbol.toString();
		case CID_SYMBOL_CODE:
			return symbol.getCode();
		case CID_SYMBOL_EXCHANGE:
			return symbol.getExchangeID();
		case CID_SYMBOL_TYPE:
			return symbol.getTypeCode();
		case CID_SYMBOL_CURRENCY:
			return symbol.getCurrencyCode();
		case CID_TERMINAL_ID:
			return security.getTerminal().getTerminalID();
		case CID_LOT_SIZE:
			return security.getLotSize();
		case CID_SCALE:
			return security.getScale();
		case CID_TICK_SIZE:
			return security.getTickSize();
		case CID_TICK_VALUE:
			return security.getTickValue();
		case CID_INITIAL_MARGIN:
			return security.getInitialMargin();
		case CID_SETTLEMENT_PRICE:
			return security.getSettlementPrice();
		case CID_LOWER_PRICE_LIMIT:
			return security.getLowerPriceLimit();
		case CID_UPPER_PRICE_LIMIT:
			return security.getUpperPriceLimit();
		case CID_LAST_PRICE:
			tick = security.getLastTrade();
			return tick == null ? null : tick.getPrice();
		case CID_LAST_SIZE:
			tick = security.getLastTrade();
			return tick == null ? null : tick.getSize();
		case CID_ASK_PRICE:
			tick = security.getBestAsk();
			return tick == null ? null : tick.getPrice();
		case CID_ASK_SIZE:
			tick = security.getBestAsk();
			return tick == null ? null : tick.getSize();
		case CID_BID_PRICE:
			tick = security.getBestBid();
			return tick == null ? null : tick.getPrice();
		case CID_BID_SIZE:
			tick = security.getBestBid();
			return tick == null ? null : tick.getSize();
		case CID_OPEN_PRICE:
			return security.getOpenPrice();
		case CID_HIGH_PRICE:
			return security.getHighPrice();
		case CID_LOW_PRICE:
			return security.getLowPrice();
		case CID_CLOSE_PRICE:
			return security.getClosePrice();
		default:
				return null;
		}
	}
	
	@Override
	public int getColumnIndex(int columnID) {
		return columnIndexToColumnID.indexOf(columnID);
	}

	@Override
	public int getColumnID(int columnIndex) {
		return columnIndexToColumnID.get(columnIndex);
	}
	
	@Override
	public String getColumnName(int col) {
		MsgID id = columnIDToColumnHeader.get(columnIndexToColumnID.get(col));
		if ( id == null ) {
			return "NULL_ID#" + col; 
		}
		return messages.get(id);
	}
	
	/**
	 * Clear all cached data.
	 */
	public void clear() {
		stopListeningUpdates();
		terminalSet.clear();
	}
	
	/**
	 * Add terminal to show its securities.
	 * <p>
	 * @param terminal - terminal to add
	 */
	public void add(Terminal terminal) {
		if ( terminal == null ) {
			throw new IllegalArgumentException("Terminal cannot be null");
		}
		if ( terminalSet.contains(terminal) ) {
			return;
		}
		terminalSet.add(terminal);
		if ( subscribed ) {
			cacheDataAndSubscribeEvents(terminal);
		}
	}
	
	@Override
	public void startListeningUpdates() {
		if ( subscribed ) {
			return;
		}
		for ( Terminal terminal : terminalSet ) {
			cacheDataAndSubscribeEvents(terminal);
		}
		subscribed = true;
	}

	@Override
	public void stopListeningUpdates() {
		if ( ! subscribed ) {
			return;
		}
		for ( Terminal terminal : terminalSet ) {
			terminal.lock();
			try {
				unsubscribe(terminal);
			} finally {
				terminal.unlock();
			}
		}
		securities.clear();
		securityMap.clear();
		fireTableDataChanged();
		subscribed = false;
	}

	@Override
	public void onEvent(final Event event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				processEvent(event);
			}
		});
	}
	
	private void processEvent(Event event) {
		if ( ! subscribed ) {
			return;
		}
		for ( Terminal terminal : terminalSet ) {
			if ( event.isType(terminal.onSecurityUpdate())
			  || event.isType(terminal.onSecurityBestAsk())
			  || event.isType(terminal.onSecurityBestBid())
			  || event.isType(terminal.onSecurityLastTrade()))
			{
				int firstRow = securities.size();
				Security security = ((SecurityEvent) event).getSecurity();
				if ( securityMap.containsKey(security) ) {
					Integer row = securityMap.get(security);
					fireTableRowsUpdated(row, row);						
				} else {
					securities.add(security);
					securityMap.put(security, firstRow);
					fireTableRowsInserted(firstRow, firstRow);
				}
			}
		}
	}
	
	/**
	 * Return security instance by row index.
	 * <p>
	 * @param rowIndex - the row index
	 * @return security
	 */
	public Security getSecurity(int rowIndex) {
		return securities.get(rowIndex);
	}

	@Override
	public void close() {
		clear();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch ( getColumnID(col) ) {
		case CID_SCALE:
			return Integer.class;
		case CID_LOT_SIZE:
		case CID_TICK_VALUE:
		case CID_INITIAL_MARGIN:
		case CID_TICK_SIZE:
		case CID_SETTLEMENT_PRICE:
		case CID_LOWER_PRICE_LIMIT:
		case CID_UPPER_PRICE_LIMIT:
		case CID_OPEN_PRICE:
		case CID_HIGH_PRICE:
		case CID_LOW_PRICE:
		case CID_CLOSE_PRICE:
			return CDecimal.class;
		case CID_LAST_SIZE:
		case CID_ASK_SIZE:
		case CID_BID_SIZE:
			return Long.class;
		case CID_LAST_PRICE:
		case CID_ASK_PRICE:
		case CID_BID_PRICE:
			return Double.class;
		default:
			return super.getColumnClass(col);
		}
	}

	private void cacheDataAndSubscribeEvents(Terminal terminal) {
		terminal.lock();
		try {
			subscribe(terminal);
			int countAdded = 0, firstRow = securities.size();
			for ( Security security : terminal.getSecurities() ) {
				if ( ! securityMap.containsKey(security) ) {
					securities.add(security);
					securityMap.put(security, firstRow + countAdded);
					countAdded ++;
				}
			}
			if ( countAdded > 0 ) {
				fireTableRowsInserted(firstRow, firstRow + countAdded - 1);
			}
		} finally {
			terminal.unlock();
		}
	}
	
	private void subscribe(Terminal terminal) {
		terminal.onSecurityUpdate().addListener(this);
		terminal.onSecurityBestAsk().addListener(this);
		terminal.onSecurityBestBid().addListener(this);
		terminal.onSecurityLastTrade().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onSecurityUpdate().removeListener(this);
		terminal.onSecurityBestAsk().removeListener(this);
		terminal.onSecurityBestBid().removeListener(this);
		terminal.onSecurityLastTrade().removeListener(this);	
	}

}

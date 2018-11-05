package ru.prolib.aquila.ui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class SecurityListTableModel extends AbstractTableModel
	implements ITableModel, EventListener, ActionListener
{
	
	public static class UpdateRange {
		private final int startIndex, endIndex;
		private final boolean inserted;
		
		public UpdateRange(int startIndex, int endIndex, boolean inserted) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.inserted = inserted;
		}
		
		public int getStartIndex() {
			return startIndex;
		}
		
		public int getEndIndex() {
			return endIndex;
		}
		
		public boolean isInserted() {
			return inserted;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != UpdateRange.class ) {
				return false;
			}
			UpdateRange o = (UpdateRange) other;
			return new EqualsBuilder()
					.append(o.startIndex, startIndex)
					.append(o.endIndex, endIndex)
					.append(o.inserted, inserted)
					.build();
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("UpdateRange[startIndex=")
					.append(startIndex)
					.append(",endIndex=")
					.append(endIndex)
					.append(",inserted=")
					.append(inserted)
					.append("]")
					.toString();
		}

	}
	
	public static class CacheEntry {
		private final Security security;
		private final AtomicInteger counter;
		private final AtomicBoolean inserted;
		
		public CacheEntry(Security security,
				AtomicInteger counter,
				AtomicBoolean inserted)
		{
			this.security = security;
			this.counter = counter;
			this.inserted = inserted;
		}
		
		public CacheEntry(Security security) {
			this(security, new AtomicInteger(1), new AtomicBoolean(true));
		}
		
		public CacheEntry(Security security, int counter, boolean inserted) {
			this(security, new AtomicInteger(counter), new AtomicBoolean(inserted));
		}
		
		public Security getSecurity() {
			return security;
		}
		
		public boolean isUpdated() {
			return counter.get() > 0;
		}
		
		public boolean isInserted() {
			return inserted.get();
		}
		
		public void reset() {
			counter.set(0);
			inserted.set(false);
		}
		
		public void addUpdate() {
			counter.incrementAndGet();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != CacheEntry.class ) {
				return false;
			}
			CacheEntry o = (CacheEntry) other;
			return new EqualsBuilder()
					.append(o.security, security)
					.append(o.counter.get(), counter.get())
					.append(o.inserted.get(), inserted.get())
					.build();
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("CacheEntry[security=")
					.append(security)
					.append(",updateCount=")
					.append(counter.get())
					.append(",inserted=")
					.append(inserted.get())
					.append("]")
					.toString();
		}
		
	}
	
	public static class Cache {
		private final List<CacheEntry> entries;
		private final Map<Security, Integer> mapSecurityToIndex;
		
		public Cache(List<CacheEntry> entries,
				Map<Security, Integer> mapSecurityToIndex)
		{
			this.entries = entries;
			this.mapSecurityToIndex = mapSecurityToIndex;
		}
		
		public Cache() {
			this(new ArrayList<>(), new HashMap<>());
		}
		
		public synchronized void clear() {
			entries.clear();
			mapSecurityToIndex.clear();
		}
		
		public synchronized int getSecuritiesCount() {
			return entries.size();
		}
		
		public synchronized Security getSecurity(int index) {
			return entries.get(index).getSecurity();
		}
		
		public synchronized void addUpdate(Security security) {
			Integer index = mapSecurityToIndex.get(security);
			if ( index == null ) {
				index = entries.size();
				entries.add(new CacheEntry(security));
				mapSecurityToIndex.put(security, index);
			} else {
				entries.get(index).addUpdate();
			}
		}
		
		public synchronized List<UpdateRange> getUpdatesAndReset() {
			List<UpdateRange> result = new ArrayList<>();
			int count = entries.size();
			Integer firstInSeries = null;
			Boolean inserted = null;
			for ( int i = 0; i < count; i ++ ) {
				CacheEntry entry = entries.get(i);
				if ( entry.isUpdated() ) {
					if ( firstInSeries == null ) {
						// We aren't in active range.
						// Should open new range.
						firstInSeries = i;
						inserted = entry.isInserted();
					} else {
						// We're in active range.
						// There is possible that here's change
						// updated to inserted and vice versa
						if ( inserted != entry.isInserted() ) {
							// Yep, there is change of inserted or
							// updated mark. We have to close previously
							// opened range and open a new one.
							result.add(new UpdateRange(firstInSeries, i - 1, inserted));
							firstInSeries = i;
							inserted = entry.isInserted();
						} else {
							// No change of inserted or updated mark.
							// Nothing to do, just continue with this range.
						}
					}
				} else {
					if ( firstInSeries == null ) {
						// We aren't in active range.
						// Just proceed to the next.
					} else {
						// We were in active range.
						// But here we meet a first record which wasn't updated.
						// We have to close previously opened range.
						result.add(new UpdateRange(firstInSeries, i - 1, inserted));
						firstInSeries = null;
						inserted = null;
					}
				}
				entry.reset();
			}
			// There is possible that the last range still open.
			if ( firstInSeries != null ) {
				result.add(new UpdateRange(firstInSeries, count - 1, inserted));
			}
			return result;
		}

	}
	
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
		case CID_LAST_SIZE:
		case CID_ASK_SIZE:
		case CID_BID_SIZE:
		case CID_LAST_PRICE:
		case CID_ASK_PRICE:
		case CID_BID_PRICE:
			return CDecimal.class;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}

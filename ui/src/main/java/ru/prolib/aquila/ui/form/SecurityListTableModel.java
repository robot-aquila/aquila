package ru.prolib.aquila.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class SecurityListTableModel extends AbstractTableModel
	implements ITableModel, EventListener
{
	private static final long serialVersionUID = 1L;
	private static final List<MsgID> mapIndexToID;
	
	static {
		mapIndexToID = new Vector<MsgID>();
		mapIndexToID.add(SecurityMsg.NAME);
		mapIndexToID.add(SecurityMsg.SYMBOL);
		mapIndexToID.add(SecurityMsg.EXCHANGE);
		mapIndexToID.add(SecurityMsg.TYPE);
		mapIndexToID.add(SecurityMsg.CURRENCY);
		mapIndexToID.add(SecurityMsg.TERMINAL_ID);
		mapIndexToID.add(SecurityMsg.LOT_SIZE);
		mapIndexToID.add(SecurityMsg.SCALE);
		mapIndexToID.add(SecurityMsg.TICK_SIZE);
		mapIndexToID.add(SecurityMsg.TICK_VALUE);
		mapIndexToID.add(SecurityMsg.LAST_PRICE);
		mapIndexToID.add(SecurityMsg.LAST_SIZE);
		mapIndexToID.add(SecurityMsg.ASK_PRICE);
		mapIndexToID.add(SecurityMsg.ASK_SIZE);
		mapIndexToID.add(SecurityMsg.BID_PRICE);
		mapIndexToID.add(SecurityMsg.BID_SIZE);
		mapIndexToID.add(SecurityMsg.LOWER_PRICE);
		mapIndexToID.add(SecurityMsg.UPPER_PRICE);
		mapIndexToID.add(SecurityMsg.OPEN_PRICE);
		mapIndexToID.add(SecurityMsg.HIGH_PRICE);
		mapIndexToID.add(SecurityMsg.LOW_PRICE);
		mapIndexToID.add(SecurityMsg.CLOSE_PRICE);
		mapIndexToID.add(SecurityMsg.INITIAL_MARGIN);
		mapIndexToID.add(SecurityMsg.INITIAL_PRICE);
	}
	
	private final IMessages messages;
	private final List<Security> securities;
	private final Map<Security, Integer> securityMap;
	private final Set<Terminal> terminalSet;
	private boolean subscribed = false;
	
	public SecurityListTableModel(IMessages messages) {
		super();
		this.messages = messages;
		this.securities = new ArrayList<Security>();
		this.securityMap = new HashMap<Security, Integer>();
		this.terminalSet = new HashSet<Terminal>();
	}

	@Override
	public int getRowCount() {
		return securities.size();
	}

	@Override
	public int getColumnCount() {
		return mapIndexToID.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Security security = null;
		if ( rowIndex >= securities.size() || columnIndex >= mapIndexToID.size() ) {
			return null;
		}
		security = securities.get(rowIndex);
		Tick tick = null;
		Symbol symbol = security.getSymbol();
		MsgID id = mapIndexToID.get(columnIndex); 
		if ( id == SecurityMsg.NAME ) {
			return security.getDisplayName();
		} else if ( id == SecurityMsg.SYMBOL ) {
			return symbol.getCode();
		} else if ( id == SecurityMsg.EXCHANGE ) {
			return symbol.getExchangeID();
		} else if ( id == SecurityMsg.TYPE ) {
			return symbol.getType();
		} else if ( id == SecurityMsg.CURRENCY ) {
			return symbol.getCurrencyCode();
		} else if ( id == SecurityMsg.LOT_SIZE ) {
			return security.getLotSize();
		} else if ( id == SecurityMsg.SCALE ) {
			return security.getScale();
		} else if ( id == SecurityMsg.TICK_SIZE ) {
			return security.getTickSize();
		} else if ( id == SecurityMsg.TICK_VALUE ) {
			return security.getTickValue();
		} else if ( id == SecurityMsg.LAST_PRICE ) {
			tick = security.getLastTrade();
			return tick == null ? null : tick.getPrice();
		} else if ( id == SecurityMsg.LAST_SIZE ) {
			tick = security.getLastTrade();
			return tick == null ? null : tick.getSize();
		} else if ( id == SecurityMsg.ASK_PRICE ) {
			tick = security.getBestAsk();
			return tick == null ? null : tick.getPrice();
		} else if ( id == SecurityMsg.ASK_SIZE ) {
			tick = security.getBestAsk();
			return tick == null ? null : tick.getSize();
		} else if ( id == SecurityMsg.BID_PRICE ) {
			tick = security.getBestBid();
			return tick == null ? null : tick.getPrice();
		} else if ( id == SecurityMsg.BID_SIZE ) {
			tick = security.getBestBid();
			return tick == null ? null : tick.getSize();
		} else if ( id == SecurityMsg.LOWER_PRICE ) {
			return security.getLowerPriceLimit();
		} else if ( id == SecurityMsg.UPPER_PRICE ) {
			return security.getUpperPriceLimit();
		} else if ( id == SecurityMsg.OPEN_PRICE ) {
			return security.getOpenPrice();
		} else if ( id == SecurityMsg.HIGH_PRICE ) {
			return security.getHighPrice();
		} else if ( id == SecurityMsg.LOW_PRICE ) {
			return security.getLowPrice();
		} else if ( id == SecurityMsg.CLOSE_PRICE ) {
			return security.getClosePrice();
		} else if ( id == SecurityMsg.INITIAL_MARGIN ) {
			return security.getInitialMargin();
		} else if ( id == SecurityMsg.INITIAL_PRICE ) {
			return security.getInitialPrice();
		} else if ( id == SecurityMsg.TERMINAL_ID ) {
			return security.getTerminal().getTerminalID();
		} else {
			return null;			
		}
	}

	@Override
	public int getColumnIndex(MsgID columnId) {
		return mapIndexToID.indexOf(columnId);
	}
	
	@Override
	public String getColumnName(int c) {
		return messages.get(mapIndexToID.get(c));
	}
	
	/**
	 * Clear all cached data.
	 */
	public void clear() {
		stopListeningUpdates();
		terminalSet.clear();
		fireTableDataChanged();
	}
	
	/**
	 * Add terminal to show its securities.
	 * <p>
	 * @param terminal - terminal to add
	 */
	public void add(Terminal terminal) {
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
		if ( ! subscribed  ) {
			return;
		}
		for ( Terminal terminal : terminalSet ) {
			if ( event.isType(terminal.onSecurityAvailable()) ) {
				int firstRow = securities.size();
				Security security = ((SecurityEvent) event).getSecurity();
				if ( ! securityMap.containsKey(security) ) {
					securities.add(security);
					securityMap.put(security, firstRow);
					fireTableRowsInserted(firstRow, firstRow);
				}
			} else if ( event.isType(terminal.onSecurityUpdate())
					|| event.isType(terminal.onSecurityBestAsk())
					|| event.isType(terminal.onSecurityBestBid())
					|| event.isType(terminal.onSecurityLastTrade()) )
			{
				Security security = ((SecurityEvent) event).getSecurity();
				Integer row = securityMap.get(security);
				if ( row != null ) {
					fireTableRowsUpdated(row, row);	
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
		terminal.onSecurityAvailable().addListener(this);
		terminal.onSecurityUpdate().addListener(this);
		terminal.onSecurityBestAsk().addListener(this);
		terminal.onSecurityBestBid().addListener(this);
		terminal.onSecurityLastTrade().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onSecurityAvailable().removeListener(this);
		terminal.onSecurityUpdate().removeListener(this);
		terminal.onSecurityBestAsk().removeListener(this);
		terminal.onSecurityBestBid().removeListener(this);
		terminal.onSecurityLastTrade().removeListener(this);	
	}

}

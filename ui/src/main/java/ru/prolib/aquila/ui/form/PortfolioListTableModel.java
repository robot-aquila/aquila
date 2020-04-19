package ru.prolib.aquila.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

/**
 * 2012-12-09<br>
 * $Id: PortfoliosTableModel.java 491 2013-02-05 20:31:41Z huan.kaktus $
 */
public class PortfolioListTableModel extends AbstractTableModel
	implements EventListener, ITableModel
{
	private static final long serialVersionUID = 1L;
	public static final int CID_ACCOUNT = 1;
	public static final int CID_CURRENCY = 2;
	public static final int CID_TERMINAL_ID = 3;
	public static final int CID_BALANCE = 4;
	public static final int CID_EQUITY = 5;
	public static final int CID_LEVERAGE = 6;
	public static final int CID_FREE_MARGIN = 7;
	public static final int CID_USED_MARGIN = 8;
	public static final int CID_PROFIT_AND_LOSS = 9;
	
	private final List<Integer> columnIndexToColumnID;
	private final Map<Integer, MsgID> columnIDToColumnHeader;
	private final IMessages messages;
	private final List<Portfolio> portfolios;
	private final Map<Portfolio, Integer> portfolioMap;
	private final Set<Terminal> terminalSet;
	private boolean subscribed = false;
	
	public PortfolioListTableModel(IMessages messages) {
		columnIndexToColumnID = getColumnIDList();
		columnIDToColumnHeader = getColumnIDToHeaderMap();
		this.messages = messages;
		this.terminalSet = new HashSet<Terminal>();
		this.portfolioMap = new HashMap<Portfolio, Integer>();
		this.portfolios = new ArrayList<Portfolio>();
	}
	
	/**
	 * Get list of columns to display.
	 * <p>
	 * Not all of portfolio attributes are displayed by default.
	 * Override this method to add or modify column list. 
	 * <p>
	 * @return list of columns
	 */
	protected List<Integer> getColumnIDList() {
		List<Integer> cols = new ArrayList<>();
		cols.add(CID_ACCOUNT);
		cols.add(CID_CURRENCY);
		cols.add(CID_TERMINAL_ID);
		cols.add(CID_BALANCE);
		cols.add(CID_EQUITY);
		cols.add(CID_LEVERAGE);
		cols.add(CID_FREE_MARGIN);
		cols.add(CID_USED_MARGIN);
		cols.add(CID_PROFIT_AND_LOSS);
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
		head.put(CID_ACCOUNT, CommonMsg.ACCOUNT);
		head.put(CID_CURRENCY, CommonMsg.CURRENCY);
		head.put(CID_TERMINAL_ID, CommonMsg.TERMINAL);
		head.put(CID_BALANCE, CommonMsg.BALANCE);
		head.put(CID_EQUITY, CommonMsg.EQUITY);
		head.put(CID_LEVERAGE, CommonMsg.LEVERAGE);
		head.put(CID_FREE_MARGIN, CommonMsg.FREE_MARGIN);
		head.put(CID_USED_MARGIN, CommonMsg.USED_MARGIN);
		head.put(CID_PROFIT_AND_LOSS, CommonMsg.PROFIT_AND_LOSS);
		return head;
	}
	

	@Override
	public int getColumnCount() {
		return columnIndexToColumnID.size();
	}
	
	@Override
	public int getRowCount() {
		return portfolios.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		if ( row > portfolios.size() ) {
			return null;
		}
		return getColumnValue(portfolios.get(row), getColumnID(col));
	}
	
	protected Object getColumnValue(Portfolio p, int columnID) {
		switch ( columnID ) {
		case CID_TERMINAL_ID:
			return p.getTerminal().getTerminalID();
		case CID_CURRENCY:
			return p.getCurrency();
		case CID_ACCOUNT:
			return p.getAccount();
		case CID_BALANCE:
			return p.getBalance();
		case CID_EQUITY:
			return p.getEquity();
		case CID_FREE_MARGIN:
			return p.getFreeMargin();
		case CID_USED_MARGIN:
			return p.getUsedMargin();
		case CID_PROFIT_AND_LOSS:
			return p.getProfitAndLoss();
		case CID_LEVERAGE:
			return p.getLeverage();
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
	 * Add all portfolios of terminal.
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
		portfolios.clear();
		portfolioMap.clear();
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
			if ( event.isType(terminal.onPortfolioUpdate()) ) {
				int firstRow = portfolios.size();
				Portfolio portfolio = ((PortfolioUpdateEvent) event).getPortfolio();
				if ( portfolioMap.containsKey(portfolio) ) {
					Integer row = portfolioMap.get(portfolio);
					fireTableRowsUpdated(row, row);
				} else {					
					portfolios.add(portfolio);
					portfolioMap.put(portfolio, firstRow);
					fireTableRowsInserted(firstRow, firstRow);
				}
			}
		}
	}

	/**
	 * Get portfolio associated with the row.
	 * <p>
	 * @param rowIndex - row index
	 * @return portfolio instance
	 */
	public Portfolio getPortfolio(int rowIndex) {
		return portfolios.get(rowIndex);
	}

	@Override
	public void close() {
		clear();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch ( getColumnID(col) ) {
		case CID_BALANCE:
		case CID_EQUITY:
		case CID_FREE_MARGIN:
		case CID_USED_MARGIN:
		case CID_PROFIT_AND_LOSS:
			return CDecimal.class;
		default:
			return super.getColumnClass(col);
		}
	}
	
	private void cacheDataAndSubscribeEvents(Terminal terminal) {
		terminal.lock();
		try {
			subscribe(terminal);
			int countAdded = 0, firstRow = portfolios.size();
			for ( Portfolio portfolio : terminal.getPortfolios() ) {
				if ( ! portfolioMap.containsKey(portfolio) ) {
					portfolios.add(portfolio);
					portfolioMap.put(portfolio, firstRow + countAdded);
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
		terminal.onPortfolioUpdate().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onPortfolioUpdate().removeListener(this);
	}

}

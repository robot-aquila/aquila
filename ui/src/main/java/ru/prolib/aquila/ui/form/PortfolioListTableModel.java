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
	private static final List<MsgID> mapIndexToID;
	
	static {
		mapIndexToID = new Vector<MsgID>();
		mapIndexToID.add(CommonMsg.ACCOUNT);
		mapIndexToID.add(CommonMsg.CURRENCY);
		mapIndexToID.add(CommonMsg.TERMINAL);
		mapIndexToID.add(CommonMsg.BALANCE);
		mapIndexToID.add(CommonMsg.EQUITY);
		mapIndexToID.add(CommonMsg.LEVERAGE);
		mapIndexToID.add(CommonMsg.FREE_MARGIN);
		mapIndexToID.add(CommonMsg.USED_MARGIN);
		mapIndexToID.add(CommonMsg.PROFIT_AND_LOSS);
	}
	
	private boolean subscribed = false;
	private final IMessages messages;
	private final Set<Terminal> terminalSet;
	private final Map<Portfolio, Integer> portfolioMap;
	private final List<Portfolio> portfolios;
	
	public PortfolioListTableModel(IMessages messages) {
		super();
		this.messages = messages;
		this.terminalSet = new HashSet<Terminal>();
		this.portfolioMap = new HashMap<Portfolio, Integer>();
		this.portfolios = new ArrayList<Portfolio>();
	}

	@Override
	public int getColumnCount() {
		return mapIndexToID.size();
	}
	
	@Override
	public int getRowCount() {
		return portfolios.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Portfolio p = null;
		if ( row >= portfolios.size() ) {
			return null;
		}
		p = portfolios.get(row);
		
		MsgID id = mapIndexToID.get(col);
		if ( id == CommonMsg.TERMINAL ) {
			return p.getTerminal().getTerminalID();
		} else if ( id == CommonMsg.CURRENCY ) {
			return p.getCurrency();
		} else if ( id == CommonMsg.ACCOUNT ) {
			return p.getAccount();
		} else if ( id == CommonMsg.BALANCE ) {
			return p.getBalance();
		} else if ( id == CommonMsg.EQUITY ) {
			return p.getEquity();
		} else if ( id == CommonMsg.FREE_MARGIN ) {
			return p.getFreeMargin();
		} else if ( id == CommonMsg.USED_MARGIN ) {
			return p.getUsedMargin();
		} else if ( id == CommonMsg.PROFIT_AND_LOSS ) {
			return p.getProfitAndLoss();
		} else if ( id == CommonMsg.LEVERAGE ) {
			return p.getLeverage();
		} else {
			return null;
		}
	}
	
	@Override
	public String getColumnName(int col) {
		return messages.get(mapIndexToID.get(col));
	}
	
	@Override
	public int getColumnID(int columnIndex) {
		throw new UnsupportedOperationException();
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
				Portfolio portfolio = ((PortfolioEvent) event).getPortfolio();
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

	@Override
	public void close() {
		clear();
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
		terminal.onPortfolioAvailable().addListener(this);
		terminal.onPortfolioUpdate().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onPortfolioAvailable().removeListener(this);
		terminal.onPortfolioUpdate().removeListener(this);
	}

	@Override
	public int getColumnIndex(int columnID) {
		throw new RuntimeException("Not implemented");
	}

}

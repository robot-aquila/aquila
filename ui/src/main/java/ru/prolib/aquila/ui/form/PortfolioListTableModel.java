package ru.prolib.aquila.ui.form;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.msg.CommonMsg;

/**
 * 2012-12-09<br>
 * $Id: PortfoliosTableModel.java 491 2013-02-05 20:31:41Z huan.kaktus $
 */
public class PortfolioListTableModel extends AbstractTableModel
	implements EventListener, Starter
{
	private static final long serialVersionUID = 1L;
	private static final List<MsgID> mapIndexToID;
	
	static {
		mapIndexToID = new Vector<MsgID>();
		mapIndexToID.add(CommonMsg.CODE);
		mapIndexToID.add(CommonMsg.SUBCODE);
		mapIndexToID.add(CommonMsg.SUBCODE2);
		mapIndexToID.add(CommonMsg.CASH);
		mapIndexToID.add(CommonMsg.CURRENCY);
		mapIndexToID.add(CommonMsg.TERMINAL);
	}
	
	private boolean started = false;
	private final ReentrantLock lock;
	private final IMessages messages;
	private final List<Terminal> terminals;
	private final List<Portfolio> portfolios;
	
	public PortfolioListTableModel(IMessages messages) {
		super();
		this.lock = new ReentrantLock();
		this.messages = messages;
		this.terminals = new Vector<Terminal>();
		this.portfolios = new Vector<Portfolio>();
	}

	@Override
	public int getColumnCount() {
		return mapIndexToID.size();
	}
	
	@Override
	public String getColumnName(int col) {
		return messages.get(mapIndexToID.get(col));
	}

	@Override
	public int getRowCount() {
		lock.lock();
		try {
			return portfolios.size();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		Portfolio p = null;
		lock.lock();
		try {
			if ( row >= portfolios.size() ) {
				return null;
			}
			p = portfolios.get(row);
		} finally {
			lock.unlock();
		}
		
		MsgID id = mapIndexToID.get(col);
		if ( id == CommonMsg.TERMINAL ) {
			return "TODO";
		} else if ( id == CommonMsg.CURRENCY ) {
			return "TODO";
		} else if ( id == CommonMsg.CASH ) {
			return p.getFreeMargin();
		} else if ( id == CommonMsg.SUBCODE2 ) {
			return p.getAccount().getSubCode2();
		} else if ( id == CommonMsg.SUBCODE ) {
			return p.getAccount().getSubCode();
		} else if ( id == CommonMsg.CODE ) {
			return p.getAccount().getCode();
		} else {
			return null;
		}
	}

	@Override
	public void start() {
		lock.lock();
		try {
			portfolios.clear();
			for ( Terminal terminal : terminals ) {
				subscribe(terminal);
				addPortfolios(terminal);
			}
			fireTableDataChanged();
			started = true;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void stop() {
		lock.lock();
		try {
			for ( Terminal terminal : terminals ) {
				unsubscribe(terminal);
			}
			portfolios.clear();
			started = false;
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Add all portfolios of terminal.
	 * <p>
	 * @param terminal - terminal to add
	 */
	public void add(Terminal terminal) {
		lock.lock();
		try {
			if ( ! isExists(terminal) ) {
				terminals.add(terminal);
				if ( started ) {
					subscribe(terminal);
					addPortfolios(terminal);
					fireTableDataChanged();
				}
			}
			
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Return column index by ID.
	 * <p> 
	 * @param columnId - column ID.
	 * @return return index of specified column
	 */
	public int getColumnIndex(MsgID columnId) {
		return mapIndexToID.indexOf(columnId);
	}
	
	private void subscribe(Terminal terminal) {
		terminal.onPortfolioAvailable().addListener(this);
		terminal.onPortfolioUpdate().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onPortfolioAvailable().removeListener(this);
		terminal.onPortfolioUpdate().removeListener(this);
	}
	
	private void addPortfolios(Terminal terminal) {
		for ( Portfolio portfolio : terminal.getPortfolios() ) {
			if ( ! isExists(portfolio) ) {
				portfolios.add(portfolio);
			}
		}
	}
	
	private boolean isExists(Portfolio portfolio) {
		return portfolios.indexOf(portfolio) >= 0 ? true : false;
	}
	
	private boolean isExists(Terminal terminal) {
		return terminals.indexOf(terminal) >= 0 ? true : false;
	}

	@Override
	public void onEvent(Event event) {
		lock.lock();
		try {
			final PortfolioEvent e = (PortfolioEvent) event;
			if ( isPortfolioAvailableEvent(event) ) {
				insertNewRow(e.getPortfolio());
			} else if ( isPortfolioChangedEvent(event) ) {
				updateRow(e.getPortfolio());
			}
		} finally {
			lock.unlock();
		}
	}
	
	private void updateRow(Portfolio portfolio) {
		int rowIndex = portfolios.indexOf(portfolio);
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	private void insertNewRow(Portfolio portfolio) {
		int rowIndex = portfolios.indexOf(portfolio);
		if ( ! isExists(portfolio) ) {
			rowIndex = portfolios.size();
			portfolios.add(portfolio);
			fireTableRowsInserted(rowIndex, rowIndex);
		}
	}
	
	private boolean isPortfolioAvailableEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.onPortfolioAvailable()) ) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isPortfolioChangedEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.onPortfolioUpdate()) ) {
				return true;
			}
		}
		return false;
	}

}

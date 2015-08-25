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
 * $Id: PositionsTableModel.java 544 2013-02-25 14:31:32Z huan.kaktus $
 */
public class PositionListTableModel extends AbstractTableModel implements
		EventListener, Starter 
{
	private static final long serialVersionUID = 1;
	private static final List<MsgID> mapIndexToID;
	
	static {
		mapIndexToID = new Vector<MsgID>();
		mapIndexToID.add(CommonMsg.TERMINAL);
		mapIndexToID.add(CommonMsg.ACCOUNT);
		mapIndexToID.add(CommonMsg.SECURITY);
		mapIndexToID.add(CommonMsg.TYPE);
		mapIndexToID.add(CommonMsg.CURR_VAL);
		mapIndexToID.add(CommonMsg.MARKET_VAL);
		mapIndexToID.add(CommonMsg.LOCKED_VAL);
		mapIndexToID.add(CommonMsg.VMARGIN);
		mapIndexToID.add(CommonMsg.OPEN_VAL);
		mapIndexToID.add(CommonMsg.BALANCE_VAL);
	}

	private boolean started = false;
	private final ReentrantLock lock;
	private final IMessages messages;
	private final List<Terminal> terminals;
	private final List<Position> positions;
	
	public PositionListTableModel(IMessages messages) {
		super();
		this.lock = new ReentrantLock();
		this.messages = messages;
		this.terminals = new Vector<Terminal>();
		this.positions = new Vector<Position>();
	}
	
	private void subscribe(Terminal terminal) {
		terminal.OnPositionAvailable().addListener(this);
		terminal.OnPositionChanged().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.OnPositionAvailable().removeListener(this);
		terminal.OnPositionChanged().removeListener(this);
	}
	
	private void addPositions(Terminal terminal) {
		for ( Portfolio portfolio : terminal.getPortfolios() ) {
			for ( Position position : portfolio.getPositions() ) {
				if ( ! isExists(position) ) {
					positions.add(position);
				}
			}
		}
	}
	
	@Override
	public void start() {
		lock.lock();
		try {
			positions.clear();
			for ( Terminal terminal : terminals ) {
				subscribe(terminal);
				addPositions(terminal);
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
			positions.clear();
			started = false;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		Position p = null;
		lock.lock();
		try {
			if ( row >= positions.size() ) {
				return null;
			}
			p = positions.get(row);
		} finally {
			lock.unlock();
		} 
			
		MsgID id = mapIndexToID.get(col);
		if ( id == CommonMsg.ACCOUNT ) {
			return p.getAccount();
		} else if ( id == CommonMsg.TERMINAL ) {
			return "TODO";
		} else if ( id == CommonMsg.SECURITY ) {			
			return p.getSecurity().getDescriptor();
		} else if ( id == CommonMsg.VMARGIN ) {
			return p.getVarMargin();
		} else if ( id == CommonMsg.OPEN_VAL ) {
			return p.getOpenQty();
		} else if ( id == CommonMsg.LOCKED_VAL ) {
			return p.getLockQty();
		} else if ( id == CommonMsg.CURR_VAL ) {
			return p.getCurrQty();
		} else if ( id == CommonMsg.TYPE ) {
			return p.getType();
		} else if ( id == CommonMsg.MARKET_VAL ) {
			return p.getMarketValue();
		} else if ( id == CommonMsg.BALANCE_VAL ) {
			return p.getBookValue();
		} else {
			return null;
		}
	}
	
	private boolean isPositionAvailableEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.OnPositionAvailable()) ) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isPositionChangedEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.OnPositionChanged()) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onEvent(Event event) {
		lock.lock();
		try {
			final PositionEvent e = (PositionEvent) event;
			if ( isPositionAvailableEvent(event) ) {
				insertNewRow(e.getPosition());			
			} else if ( isPositionChangedEvent(event) ) {
				updateRow(e.getPosition());
			}
		} finally {
			lock.unlock();
		}
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
			return positions.size();
		} finally {
			lock.unlock();
		}
	}
	
	private void updateRow(Position position) {
		int rowIndex = positions.indexOf(position);
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	private void insertNewRow(Position position) {
		int rowIndex = positions.indexOf(position);
		if ( ! isExists(position) ) {
			rowIndex = positions.size();
			positions.add(position);
			fireTableRowsInserted(rowIndex, rowIndex);
		}
	}
	
	private boolean isExists(Position position) {
		return positions.indexOf(position) >= 0 ? true : false; 
	}
	
	private boolean isExists(Terminal terminal) {
		return terminals.indexOf(terminal) >= 0 ? true : false;
	}
	
	/**
	 * Add all positions of terminal.
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
					addPositions(terminal);
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
	
}

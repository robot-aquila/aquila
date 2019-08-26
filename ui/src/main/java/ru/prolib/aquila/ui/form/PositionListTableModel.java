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
 * $Id: PositionsTableModel.java 544 2013-02-25 14:31:32Z huan.kaktus $
 */
public class PositionListTableModel extends AbstractTableModel implements
		EventListener, ITableModel 
{
	private static final long serialVersionUID = 1;
	public static final int CID_TERMINAL_ID = 1;
	public static final int CID_ACCOUNT = 2;
	public static final int CID_SYMBOL = 3;
	public static final int CID_CURRENT_VOLUME = 4;
	public static final int CID_CURRENT_PRICE = 5;
	public static final int CID_OPEN_PRICE = 6;
	public static final int CID_USED_MARGIN = 7;
	public static final int CID_PROFIT_AND_LOSS = 8;
	
	private final List<Integer> columnIndexToColumnID;
	private final Map<Integer, MsgID> columnIDToColumnHeader;
	private final IMessages messages;
	private final List<Position> positions;
	private final Map<Position, Integer> positionMap;
	private final Set<Terminal> terminalSet;
	private boolean subscribed = false;

	public PositionListTableModel(IMessages messages) {
		super();
		columnIndexToColumnID = getColumnIDList();
		columnIDToColumnHeader = getColumnIDToHeaderMap();
		this.messages = messages;
		this.positions = new ArrayList<Position>();
		this.positionMap = new HashMap<Position, Integer>();
		this.terminalSet = new HashSet<>();
	}

	/**
	 * Get list of columns to display.
	 * <p>
	 * Not all of attributes are displayed by default.
	 * Override this method to add or modify column list. 
	 * <p>
	 * @return list of columns
	 */
	protected List<Integer> getColumnIDList() {
		List<Integer> cols = new ArrayList<>();
		cols.add(CID_TERMINAL_ID);
		cols.add(CID_ACCOUNT);
		cols.add(CID_SYMBOL);
		cols.add(CID_CURRENT_VOLUME);
		cols.add(CID_CURRENT_PRICE);
		cols.add(CID_OPEN_PRICE);
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
		head.put(CID_TERMINAL_ID, CommonMsg.TERMINAL);
		head.put(CID_ACCOUNT, CommonMsg.ACCOUNT);
		head.put(CID_SYMBOL, CommonMsg.SYMBOL);
		head.put(CID_CURRENT_VOLUME, CommonMsg.CURR_VOL);
		head.put(CID_CURRENT_PRICE, CommonMsg.CURR_PR);
		head.put(CID_OPEN_PRICE, CommonMsg.OPEN_PR);
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
		return positions.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		if ( row >= positions.size() ) {
			return null;
		}
		return getColumnValue(positions.get(row), getColumnID(col));
	}
	
	protected Object getColumnValue(Position p, int columnID) {
		switch ( columnID ) {
		case CID_ACCOUNT:
			return p.getAccount();
		case CID_TERMINAL_ID:
			return p.getTerminal().getTerminalID();
		case CID_SYMBOL:		
			return p.getSymbol().toString();
		case CID_USED_MARGIN:
			return p.getUsedMargin();
		case CID_OPEN_PRICE:
			return p.getOpenPrice();
		case CID_CURRENT_VOLUME:
			return p.getCurrentVolume();
		case CID_CURRENT_PRICE:
			return p.getCurrentPrice();
		case CID_PROFIT_AND_LOSS:
			return p.getProfitAndLoss();
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
	 * Add terminal to show all its positions.
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
		positions.clear();
		positionMap.clear();
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
			if ( event.isType(terminal.onPositionUpdate()) ) {
				int firstRow = positions.size();
				Position position = ((PositionEvent) event).getPosition();
				if ( positionMap.containsKey(position) ) {
					Integer row = positionMap.get(position);
					fireTableRowsUpdated(row, row);
				} else {
					positions.add(position);
					positionMap.put(position, firstRow);
					fireTableRowsInserted(firstRow, firstRow);
				}
			}
		}
	}
	
	/**
	 * Get position associated with the row.
	 * <p>
	 * @param rowIndex - row index
	 * @return position instance
	 */
	public Position getPosition(int rowIndex) {
		return positions.get(rowIndex);
	}
	
	@Override
	public void close() {
		clear();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch ( getColumnID(col) ) {
		case CID_CURRENT_VOLUME:
		case CID_CURRENT_PRICE:
		case CID_OPEN_PRICE:
		case CID_USED_MARGIN:
		case CID_PROFIT_AND_LOSS:
			return CDecimal.class;
		default:
			return super.getColumnClass(col);
		}
	}
	
	private void cacheDataAndSubscribeEvents(Terminal terminal) {
		subscribe(terminal);
		for ( Portfolio portfolio : terminal.getPortfolios() ) {
			cacheDataAndSubscribeEvents(portfolio);
		}
	}
	
	private void cacheDataAndSubscribeEvents(Portfolio portfolio) {
		portfolio.lock();
		try {
			int countAdded = 0, firstRow = positions.size();
			for ( Position position : portfolio.getPositions() ) {
				if ( ! positionMap.containsKey(position) ) {
					positions.add(position);
					positionMap.put(position, firstRow + countAdded);
					countAdded ++;
				}
			}
			if ( countAdded > 0 ) {
				fireTableRowsInserted(firstRow, firstRow + countAdded - 1);
			}
		} finally {
			portfolio.unlock();
		}
	}
	
	private void subscribe(Terminal terminal) {
		terminal.onPositionUpdate().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onPositionUpdate().removeListener(this);
	}
	
}

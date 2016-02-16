package ru.prolib.aquila.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private static final List<MsgID> mapIndexToID;
	
	static {
		mapIndexToID = new ArrayList<MsgID>();
		mapIndexToID.add(CommonMsg.TERMINAL);
		mapIndexToID.add(CommonMsg.ACCOUNT);
		mapIndexToID.add(CommonMsg.SYMBOL);
		mapIndexToID.add(CommonMsg.CURR_VOL);
		mapIndexToID.add(CommonMsg.CURR_PR);
		mapIndexToID.add(CommonMsg.OPEN_PR);
		mapIndexToID.add(CommonMsg.USED_MARGIN);
		mapIndexToID.add(CommonMsg.PROFIT_AND_LOSS);
	}

	private final IMessages messages;
	private final List<Position> positions;
	private final Map<Position, Integer> positionMap;
	private final Set<Portfolio> portfolioSet;
	private boolean subscribed = false;

	
	public PositionListTableModel(IMessages messages) {
		super();
		this.messages = messages;
		this.positions = new ArrayList<Position>();
		this.positionMap = new HashMap<Position, Integer>();
		this.portfolioSet = new HashSet<Portfolio>();
	}
	
	@Override
	public int getRowCount() {
		return positions.size();
	}
	
	@Override
	public int getColumnCount() {
		return mapIndexToID.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Position p = null;
		if ( row >= positions.size() ) {
			return null;
		}
		p = positions.get(row);
		
		MsgID id = mapIndexToID.get(col);
		if ( id == CommonMsg.ACCOUNT ) {
			return p.getAccount();
		} else if ( id == CommonMsg.TERMINAL ) {
			return p.getTerminal().getTerminalID();
		} else if ( id == CommonMsg.SYMBOL ) {		
			return p.getSymbol();
		} else if ( id == CommonMsg.USED_MARGIN ) {
			return p.getUsedMargin();
		} else if ( id == CommonMsg.OPEN_PR ) {
			return p.getOpenPrice();
		} else if ( id == CommonMsg.CURR_VOL ) {
			return p.getCurrentVolume();
		} else if ( id == CommonMsg.CURR_PR ) {
			return p.getCurrentPrice();
		} else if ( id == CommonMsg.PROFIT_AND_LOSS ) {
			return p.getProfitAndLoss();
		} else {
			return null;
		}
	}

	@Override
	public int getColumnIndex(MsgID columnId) {
		return mapIndexToID.indexOf(columnId);
	}

	@Override
	public String getColumnName(int col) {
		return messages.get(mapIndexToID.get(col));
	}
	
	/**
	 * Clear all cached data.
	 */
	public void clear() {
		stopListeningUpdates();
		portfolioSet.clear();
	}
	
	/**
	 * Add portfolio to show its positions.
	 * <p>
	 * @param portfolio - portfolio to add
	 */
	public void add(Portfolio portfolio) {
		if ( portfolio == null ) {
			throw new IllegalArgumentException("Portfolio cannot be null");
		}
		if ( portfolioSet.contains(portfolio) ) {
			return;
		}
		portfolioSet.add(portfolio);
		if ( subscribed ) {
			cacheDataAndSubscribeEvents(portfolio);
		}
	}
	
	@Override
	public void startListeningUpdates() {
		if ( subscribed ) {
			return;
		}
		for ( Portfolio portfolio : portfolioSet ) {
			cacheDataAndSubscribeEvents(portfolio);
		}
		subscribed = true;
	}

	@Override
	public void stopListeningUpdates() {
		if ( ! subscribed ) {
			return;
		}
		for ( Portfolio portfolio : portfolioSet ) {
			portfolio.lock();
			try {
				unsubscribe(portfolio);
			} finally {
				portfolio.unlock();
			}
		}
		positions.clear();
		positionMap.clear();
		fireTableDataChanged();
		subscribed = false;
	}

	@Override
	public void onEvent(Event event) {
		if ( ! subscribed ) {
			return;
		}
		for ( Portfolio portfolio : portfolioSet ) {
			if ( event.isType(portfolio.onPositionAvailable()) ) {
				int firstRow = positions.size();
				Position position = ((PositionEvent) event).getPosition();
				if ( ! positionMap.containsKey(position) ) {
					positions.add(position);
					positionMap.put(position, firstRow);
					fireTableRowsInserted(firstRow, firstRow);
				}
			} else if ( event.isType(portfolio.onPositionUpdate()) ) {
				Position position = ((PositionEvent) event).getPosition();
				Integer row = positionMap.get(position);
				if ( row != null ) {
					fireTableRowsUpdated(row, row);	
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
	
	private void cacheDataAndSubscribeEvents(Portfolio portfolio) {
		portfolio.lock();
		try {
			subscribe(portfolio);
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
	
	private void subscribe(Portfolio portfolio) {
		portfolio.onPositionAvailable().addListener(this);
		portfolio.onPositionUpdate().addListener(this);
	}
	
	private void unsubscribe(Portfolio portfolio) {
		portfolio.onPositionAvailable().removeListener(this);
		portfolio.onPositionUpdate().removeListener(this);
	}
	
}

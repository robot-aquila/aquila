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

public class OrderListTableModel extends AbstractTableModel implements
		EventListener, ITableModel
{
	private static final long serialVersionUID = 1L;
	private static final List<MsgID> mapIndexToID;
	
	static {
		mapIndexToID = new Vector<MsgID>();
		mapIndexToID.add(CommonMsg.ID);
		mapIndexToID.add(CommonMsg.TIME);
		mapIndexToID.add(CommonMsg.CHNG_TIME);
		mapIndexToID.add(CommonMsg.ACCOUNT);
		mapIndexToID.add(CommonMsg.DIR);
		mapIndexToID.add(CommonMsg.SECURITY);
		mapIndexToID.add(CommonMsg.TYPE);
		mapIndexToID.add(CommonMsg.STATUS);
		mapIndexToID.add(CommonMsg.PRICE);
		mapIndexToID.add(CommonMsg.QTY);
		mapIndexToID.add(CommonMsg.QTY_REST);
		mapIndexToID.add(CommonMsg.EXEC_VOL);
		mapIndexToID.add(CommonMsg.AVG_EXEC_PRICE);
		mapIndexToID.add(CommonMsg.ACTIVATOR);
		mapIndexToID.add(CommonMsg.COMMENT);
	}

	private boolean subscribed = false;
	private final IMessages messages;
	private final List<Order> orders;
	private final Map<Order, Integer> orderMap;
	private final Set<Terminal> terminalSet;
	
	public OrderListTableModel(IMessages messages) {
		super();
		this.messages = messages;
		this.orders = new ArrayList<Order>();
		this.terminalSet = new HashSet<Terminal>(); 
		this.orderMap = new HashMap<Order, Integer>();
	}
	
	@Override
	public int getColumnCount() {
		return mapIndexToID.size();
	}

	@Override
	public int getRowCount() {
		return orders.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if ( row >= orders.size() ) {
			return null;
		}
		Order order = orders.get(row);
		
		MsgID id = mapIndexToID.get(col);
		if ( id == CommonMsg.ID ) {
			return order.getID();
		} else if ( id == CommonMsg.DIR) {
			return order.getAction();
		} else if ( id == CommonMsg.TYPE) {
			return order.getType();
		} else if ( id == CommonMsg.SECURITY) {
			return order.getSymbol();
		} else if ( id == CommonMsg.QTY) {
			return order.getInitialVolume();
		} else if ( id == CommonMsg.STATUS) {
			return order.getStatus();
		} else if ( id == CommonMsg.QTY_REST) {
			return order.getCurrentVolume();
		} else if ( id == CommonMsg.PRICE) {
			return order.getPrice() == null? 0.0 : order.getPrice();
		} else if (  id == CommonMsg.EXEC_VOL ) {
			return order.getExecutedValue();
		} else if (  id == CommonMsg.ACCOUNT) {
			return order.getAccount();
		} else if (  id == CommonMsg.TIME) {
			return order.getTime();
		} else if (  id == CommonMsg.CHNG_TIME ) {
			return null;
		} else if (  id == CommonMsg.AVG_EXEC_PRICE ) {
			return null;
		} else if (  id == CommonMsg.ACTIVATOR ) {
			return null;
		} else if (  id == CommonMsg.COMMENT ) {
			return order.getComment();
		} else {
			return null;
		}
	}
	
	/**
	 * Return column index by ID.
	 * <p> 
	 * @param columnId - column ID.
	 * @return return index of specified column
	 */
	@Override
	public int getColumnIndex(MsgID columnId) {
		return mapIndexToID.indexOf(columnId);
	}

	@Override
	public String getColumnName(int col) {
		return messages.get(mapIndexToID.get(col));
	}

	public void clear() {
		stopListeningUpdates();
		terminalSet.clear();
	}
	
	/**
	 * Add all orders of terminal.
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
			unsubscribe(terminal);
		}
		orders.clear();
		orderMap.clear();
		fireTableDataChanged();
		subscribed = false;
	}

	@Override
	public void onEvent(Event event) {
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
			if ( event.isType(terminal.onOrderAvailable()) ) {
				int firstRow = orders.size();
				Order order = ((OrderEvent) event).getOrder();
				if ( ! orderMap.containsKey(order) ) {
					orders.add(order);
					orderMap.put(order, firstRow);
					fireTableRowsInserted(firstRow, firstRow);
				}
			} else if ( event.isType(terminal.onOrderUpdate()) ) {
				Order order = ((OrderEvent) event).getOrder();
				Integer row = orderMap.get(order);
				if ( row != null ) {
					fireTableRowsUpdated(row, row);
				}
			}
		}
	}
	
	/**
	 * Get order by its position.
	 * <p>
	 * @param rowIndex - row index
	 * @return order instance or null if no order exists
	 */
	public Order getOrder(int rowIndex) {
		return orders.get(rowIndex);
	}
	
	@Override
	public void close() {
		clear();
	}
	
	private void cacheDataAndSubscribeEvents(Terminal terminal) {
		terminal.lock();
		try {
			subscribe(terminal);
			int countAdded = 0, firstRow = orders.size();
			for ( Order order : terminal.getOrders() ) {
				if ( ! orderMap.containsKey(order) ) {
					orders.add(order);
					orderMap.put(order, firstRow + countAdded);
				}
				countAdded ++;
			}
			if ( countAdded > 0 ) {
				fireTableRowsInserted(firstRow, firstRow + countAdded - 1);
			}
		} finally {
			terminal.unlock();
		}
	}
	
	private void subscribe(Terminal terminal) {
		terminal.onOrderAvailable().addListener(this);
		terminal.onOrderUpdate().addListener(this);		
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onOrderUpdate().removeListener(this);
		terminal.onOrderAvailable().removeListener(this);
	}
	
}

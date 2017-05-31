package ru.prolib.aquila.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
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
	/**
	 * Column ID: Order ID.
	 */
	public static final int CID_ID = 1;
	
	/**
	 * Column ID: Order time.
	 */
	public static final int CID_TIME = 2;
	
	/**
	 * Column ID: Order done time.
	 */
	public static final int CID_TIME_DONE = 3;
	public static final int CID_ACCOUNT = 4;
	public static final int CID_ACTION = 5;
	public static final int CID_SYMBOL = 6;
	public static final int CID_TYPE = 7;
	public static final int CID_STATUS = 8;
	public static final int CID_PRICE = 9;
	public static final int CID_INITIAL_VOLUME = 10;
	public static final int CID_CURRENT_VOLUME = 11;
	public static final int CID_EXECUTED_VALUE = 12;
	public static final int CID_COMMENT = 13;
	public static final int CID_SYSTEM_MESSAGE = 14;
	public static final int CID_USER_DEFINED_LONG = 15;
	public static final int CID_USER_DEFINED_STRING = 16;
	
	private final List<Integer> columnIndexToColumnID;
	private final Map<Integer, MsgID> columnIDToColumnHeader;
	private boolean subscribed = false;
	private final IMessages messages;
	private final List<Order> orders;
	private final Set<Terminal> terminalSet;
	
	public OrderListTableModel(IMessages messages) {
		super();
		columnIndexToColumnID = getColumnIDList();
		columnIDToColumnHeader = getColumnIDToHeaderMap();
		this.messages = messages;
		this.orders = new ArrayList<Order>();
		this.terminalSet = new HashSet<Terminal>(); 
	}
	
	protected List<Integer> getColumnIDList() {
		List<Integer> cols = new ArrayList<>();
		cols.add(CID_ID);
		cols.add(CID_TIME);
		cols.add(CID_TIME_DONE);
		cols.add(CID_ACCOUNT);
		cols.add(CID_ACTION);
		cols.add(CID_SYMBOL);
		cols.add(CID_TYPE);
		cols.add(CID_STATUS);
		cols.add(CID_PRICE);
		cols.add(CID_INITIAL_VOLUME);
		cols.add(CID_CURRENT_VOLUME);
		cols.add(CID_EXECUTED_VALUE);
		cols.add(CID_COMMENT);
		cols.add(CID_SYSTEM_MESSAGE);
		return cols;
	}

	protected Map<Integer, MsgID> getColumnIDToHeaderMap() {
		Map<Integer, MsgID> head = new HashMap<>();
		head.put(CID_ID, CommonMsg.ID);
		head.put(CID_TIME, CommonMsg.TIME);
		head.put(CID_TIME_DONE, CommonMsg.TIME_DONE);
		head.put(CID_ACCOUNT, CommonMsg.ACCOUNT);
		head.put(CID_ACTION, CommonMsg.ACTION);
		head.put(CID_SYMBOL, CommonMsg.SYMBOL);
		head.put(CID_TYPE, CommonMsg.TYPE);
		head.put(CID_STATUS, CommonMsg.STATUS);
		head.put(CID_PRICE, CommonMsg.PRICE);
		head.put(CID_INITIAL_VOLUME, CommonMsg.INITIAL_VOLUME);
		head.put(CID_CURRENT_VOLUME, CommonMsg.CURRENT_VOLUME);
		head.put(CID_EXECUTED_VALUE, CommonMsg.EXECUTED_VALUE);
		head.put(CID_COMMENT, CommonMsg.COMMENT);
		head.put(CID_SYSTEM_MESSAGE, CommonMsg.SYSTEM_MESSAGE);
		head.put(CID_USER_DEFINED_LONG, CommonMsg.USER_DEFINED_LONG);
		head.put(CID_USER_DEFINED_STRING, CommonMsg.USER_DEFINED_STRING);
		return head;
	}
	
	@Override
	public int getColumnCount() {
		return columnIndexToColumnID.size();
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
		return getColumnValue(orders.get(row), getColumnID(col));
	}
	
	protected Object getColumnValue(Order order, int columnID) {
		switch ( columnID ) {
		case CID_ID:				return order.getID();
		case CID_TIME:				return order.getTime();
		case CID_TIME_DONE:			return order.getTimeDone();
		case CID_ACCOUNT:			return order.getAccount();
		case CID_ACTION:			return order.getAction();
		case CID_SYMBOL:			return order.getSymbol();
		case CID_TYPE:				return order.getType();
		case CID_STATUS:			return order.getStatus();
		case CID_PRICE:				return order.getPrice();
		case CID_INITIAL_VOLUME:	return order.getInitialVolume();
		case CID_CURRENT_VOLUME:	return order.getCurrentVolume();
		case CID_EXECUTED_VALUE:	return order.getExecutedValue();
		case CID_COMMENT:			return order.getComment();
		case CID_SYSTEM_MESSAGE:	return order.getSystemMessage();
		case CID_USER_DEFINED_LONG:	return order.getUserDefinedLong();
		case CID_USER_DEFINED_STRING:	return order.getUserDefinedString();
		default:					return null;
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
			if ( event.isType(terminal.onOrderUpdate()) ) {
				Integer row = getIndexOfOrder(((OrderEvent) event).getOrder());
				if ( row == null ) {
					row = addOrder(((OrderEvent) event).getOrder());
					if ( row != null ) {
						fireTableRowsInserted(row, row);
					}
				} else {
					fireTableRowsUpdated(row, row);
				}
			} else if ( event.isType(terminal.onOrderArchived()) ) {
				Integer row = removeOrder(((OrderEvent) event).getOrder());
				if ( row != null ) {
					fireTableRowsDeleted(row, row);
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

	@Override
	public Class<?> getColumnClass(int col) {
		switch (getColumnID(col)) {
			case CID_PRICE:
			case CID_EXECUTED_VALUE:
				return FMoney.class;
			case CID_ID:
			case CID_INITIAL_VOLUME:
			case CID_CURRENT_VOLUME:
				return Long.class;
			default:
				return super.getColumnClass(col);
		}
	}

	private void cacheDataAndSubscribeEvents(Terminal terminal) {
		terminal.lock();
		try {
			subscribe(terminal);
			int countAdded = 0, firstRow = orders.size();
			for ( Order order : terminal.getOrders() ) {
				if ( addOrder(order) != null ) {
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
		terminal.onOrderAvailable().addListener(this);
		terminal.onOrderUpdate().addListener(this);		
		terminal.onOrderArchived().addListener(this);
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.onOrderArchived().removeListener(this);
		terminal.onOrderUpdate().removeListener(this);
		terminal.onOrderAvailable().removeListener(this);
	}
	
	private Integer getIndexOfOrder(Order order) {
		int index = orders.indexOf(order);
		return index == -1 ? null : index;
	}
	
	/**
	 * Add order to local cache.
	 * <p>
	 * @param order - the order instance to add
	 * @return index of order in local cache or null if order was not added
	 * (in case if it is already in cache)
	 */
	private Integer addOrder(Order order) {
		if ( ! orders.contains(order) ) {
			orders.add(order);
			return orders.size() - 1;
		} else {
			return null;
		}
	}
	
	/**
	 * Remove order from local cache.
	 * <p>
	 * @param order - the order instance to remove
	 * @return index of removed order of null if order was not found
	 */
	private Integer removeOrder(Order order) {
		Integer row = getIndexOfOrder(order);
		if ( row != null ) {
			orders.remove((int) row);
			return row;
		} else {
			return null;
		}
	}
	
}

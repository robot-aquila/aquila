package ru.prolib.aquila.ui.form;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class OrderListTableModel extends AbstractTableModel implements
		EventListener, Starter
{
	private static final Logger logger;
	private static final long serialVersionUID = 1L;
	private static final List<MsgID> mapIndexToID;
	
	static {
		logger = LoggerFactory.getLogger(OrderListTableModel.class);
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

	private boolean started = false;
	private final ReentrantLock lock;
	private final IMessages messages;
	private final List<Terminal> terminals;
	private final List<Order> orders;
	
	public OrderListTableModel(IMessages messages) {
		super();
		this.lock = new ReentrantLock();
		this.messages = messages;
		this.terminals = new Vector<Terminal>(); 
		this.orders = new Vector<Order>();
	}
	
	@Override
	public String getColumnName(int col) {
		return messages.get(mapIndexToID.get(col));
	}

	@Override
	public int getColumnCount() {
		return mapIndexToID.size();
	}

	@Override
	public int getRowCount() {
		lock.lock();
		try {
			return orders.size();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		Order order = null;
		lock.lock();
		try {
			if ( row >= orders.size() ) {
				return null;
			}
			order = orders.get(row);
		} finally {
			lock.unlock();
		}
		
		MsgID id = mapIndexToID.get(col);
		if ( id == CommonMsg.ID ) {
			return order.getId();
		} else if ( id == CommonMsg.DIR) {
			return order.getDirection();
		} else if ( id == CommonMsg.TYPE) {
			return order.getType();
		} else if ( id == CommonMsg.SECURITY) {
			try {
				return order.getSecurity().getDescriptor();
			} catch ( SecurityException e ) {
				logger.error("Unexpected exception: ", e);
				return null;
			}
		} else if ( id == CommonMsg.QTY) {
			return order.getQty();
		} else if ( id == CommonMsg.STATUS) {
			return order.getStatus();
		} else if ( id == CommonMsg.QTY_REST) {
			return order.getQtyRest();
		} else if ( id == CommonMsg.PRICE) {
			return order.getPrice() == null? 0.0 : order.getPrice();
		} else if (  id == CommonMsg.EXEC_VOL ) {
			return order.getExecutedVolume();
		} else if (  id == CommonMsg.ACCOUNT) {
			return order.getAccount();
		} else if (  id == CommonMsg.TIME) {
			return order.getTime();
		} else if (  id == CommonMsg.CHNG_TIME ) {
			return order.getLastChangeTime();
		} else if (  id == CommonMsg.AVG_EXEC_PRICE ) {
			return order.getAvgExecutedPrice();
		} else if (  id == CommonMsg.ACTIVATOR ) {
			return order.getActivator();
		} else if (  id == CommonMsg.COMMENT ) {
			return order.getComment();
		} else {
			return null;
		}
	}
	
	private void subscribe(Terminal terminal) {
		terminal.OnOrderAvailable().addListener(this);
		terminal.OnOrderChanged().addListener(this);		
	}
	
	private void unsubscribe(Terminal terminal) {
		terminal.OnOrderChanged().removeListener(this);
		terminal.OnOrderAvailable().removeListener(this);
	}
	
	private boolean isExists(Order order) {
		return orders.contains(order);
	}
	
	private boolean isExists(Terminal terminal) {
		return terminals.contains(terminal);
	}
	
	private void addOrders(Terminal terminal) {
		for ( Order order : terminal.getOrders() ) {
			if ( ! isExists(order) ) {
				orders.add(order);
			}
		}
	}
	
	private boolean isOrderAvailableEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.OnOrderAvailable()) ) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isOrderChangedEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.OnOrderChanged()) ) {
				return true;
			}
		}
		return false;
	}
	
	private void insertNewRow(Order order) {
		if ( ! isExists(order) ) {
			int rowIndex = orders.size();
			orders.add(order);
			fireTableRowsInserted(rowIndex, rowIndex);
		}
	}
	
	private void updateRow(Order order) {
		int rowIndex = orders.indexOf(order);
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	@Override
	public void start() {
		lock.lock();
		try {
			orders.clear();
			for ( Terminal terminal : terminals ) {
				subscribe(terminal);
				addOrders(terminal);
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
			orders.clear();
			started = false;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void onEvent(Event event) {
		lock.lock();
		try {
			final OrderEvent e = (OrderEvent) event;
			if ( isOrderAvailableEvent(event) ) {
				insertNewRow(e.getOrder());			
			} else if ( isOrderChangedEvent(event) ) {
				updateRow(e.getOrder());
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Add all orders of terminal.
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
					addOrders(terminal);
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

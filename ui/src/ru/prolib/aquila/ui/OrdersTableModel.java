package ru.prolib.aquila.ui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;

public class OrdersTableModel extends AbstractTableModel implements
		EventListener, Starter
{
	private static final long serialVersionUID = 2523179020342012340L;
	private static Logger logger = LoggerFactory.getLogger(OrdersTableModel.class);
	private static final String COL_ID = "COL_ID";
	private static final String COL_DIR = "COL_DIR";
	private static final String COL_TIME = "COL_TIME";
	private static final String COL_CHNG_TIME = "COL_CHNG_TIME";
	private static final String COL_ACCOUNT = "COL_ACCOUNT";
	private static final String COL_TYPE = "COL_TYPE";
	private static final String COL_SEC = "COL_SEC";
	private static final String COL_QTY = "COL_QTY";
	private static final String COL_STATUS = "COL_STATUS";
	private static final String COL_QTY_REST = "COL_QTY_REST";
	private static final String COL_PRICE = "COL_PRICE";
	private static final String COL_EXEC_VOL = "COL_EXEC_VOL";
	private static final String COL_AVG_EXEC_PRICE = "COL_AVG_EXEC_PRICE";
	private static final String COL_ACTIVATOR = "COL_ACTIVATOR";
	private static final String COL_COMMENT = "COL_COMMENT";
	private static final String[] header = {
		COL_ID,
		COL_TIME,
		COL_CHNG_TIME,
		COL_ACCOUNT,
		COL_DIR,
		COL_SEC,
		COL_TYPE,		
		COL_STATUS,
		COL_PRICE,
		COL_QTY,
		COL_QTY_REST,
		COL_EXEC_VOL,
		COL_AVG_EXEC_PRICE,
		COL_ACTIVATOR,
		COL_COMMENT,
	};
	private ClassLabels uiLabels;
	private final Terminal orders;
	private final List<Order> list;
	
	public OrdersTableModel(Terminal orders, UiTexts uiTexts) {
		super();
		uiLabels = uiTexts.get("OrdersTableModel");
		this.orders = orders;
		this.list = orders.getOrders();
	}
	
	@Override
	public String getColumnName(int col) {
		return uiLabels.get(header[col]);
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Order order = list.get(row);
		try {
			Security sec = order.getSecurity();
			if (header[col] == COL_ID) {
				return order.getId();
			} else if (header[col] == COL_DIR) {
				return order.getDirection();
			} else if (header[col] == COL_TYPE) {
				return order.getType();
			} else if (header[col] == COL_SEC) {
				return sec.getDescriptor();
			} else if (header[col] == COL_QTY) {
				return order.getQty();
			} else if (header[col] == COL_STATUS) {
				return order.getStatus();
			} else if (header[col] == COL_QTY_REST) {
				return order.getQtyRest();
			} else if (header[col] == COL_PRICE) {
				return order.getPrice() == null? 0.0 : order.getPrice();
			} else if ( header[col] == COL_EXEC_VOL ) {
				return order.getExecutedVolume();
			} else if ( header[col] == COL_ACCOUNT) {
				return order.getAccount();
			} else if ( header[col] == COL_TIME) {
				return order.getTime();
			} else if ( header[col] == COL_CHNG_TIME ) {
				return order.getLastChangeTime();
			} else if ( header[col] == COL_AVG_EXEC_PRICE ) {
				return order.getAvgExecutedPrice();
			} else if ( header[col] == COL_ACTIVATOR ) {
				return order.getActivator();
			} else if ( header[col] == COL_COMMENT ) {
				return order.getComment();
			} else {
				return null;
			}
		} catch (SecurityException e) {
			logger.error("SecurityException: ", e);
			return null;
		}
	}

	@Override
	public void start() {
		orders.OnOrderAvailable().addListener(this);
		orders.OnOrderChanged().addListener(this);
	}

	@Override
	public void stop() {
		orders.OnOrderChanged().removeListener(this);
		orders.OnOrderAvailable().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		if( event.isType(orders.OnOrderAvailable()) ) {
			OrderEvent e = (OrderEvent) event;
			Order order = e.getOrder();
			int rowIndex = list.size();
			list.add(order);
			order.OnChanged().addListener(this);
			fireTableRowsInserted(rowIndex, rowIndex);
		} else if( event.isType(orders.OnOrderChanged()) ) {
			OrderEvent e = (OrderEvent) event;
			int rowIndex = list.indexOf(e.getOrder());
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

}

package ru.prolib.aquila.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderEvent;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.StopOrders;

public class StopOrdersTableModel extends AbstractTableModel implements
		EventListener, Starter
{
	private static final SimpleDateFormat format;
	private static final long serialVersionUID = -2214955310406288648L;
	private static Logger logger = LoggerFactory.getLogger(StopOrdersTableModel.class);
	private static final String COL_ID = "COL_ID";
	private static final String COL_DIR = "COL_DIR";
	private static final String COL_TIME = "COL_TIME";
	private static final String COL_ACCOUNT = "COL_ACCOUNT";
	private static final String COL_TYPE = "COL_TYPE";
	private static final String COL_SEC = "COL_SEC";
	private static final String COL_QTY = "COL_QTY";
	private static final String COL_STATUS = "COL_STATUS";
	private static final String COL_TRN = "COL_TRN";
	private static final String COL_PRICE = "COL_PRICE";
	private static final String COL_LNK_ORDER = "COL_LNK_ORDER";
	private static final String COL_STOP_LMT = "COL_STOP_LMT";
	private static final String COL_TAKE_PRICE = "COL_TAKE_PRICE";
	private static final String COL_OFFS = "COL_OFFS";
	private static final String COL_SPREAD = "COL_SPREAD";
	private static final String COL_CHNG_TIME = "COL_CHNG_TIME";
	private static final String[] header = {
		COL_ID,
		COL_TRN,
		COL_TIME,
		COL_CHNG_TIME,
		COL_ACCOUNT,
		COL_DIR,
		COL_SEC,
		COL_TYPE,		
		COL_STATUS,
		COL_PRICE,
		COL_QTY,
		COL_LNK_ORDER,
		COL_STOP_LMT,
		COL_TAKE_PRICE,
		COL_OFFS,
		COL_SPREAD
	};
	private ClassLabels uiLabels;
	private final StopOrders orders;
	private final List<Order> list;
	
	static {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public StopOrdersTableModel(StopOrders orders, UiTexts uiTexts) {
		super();
		uiLabels = uiTexts.get("StopOrdersTableModel");
		this.orders = orders;
		this.list = orders.getStopOrders();
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
				//return order.getSecurityDescriptor();
				// Что бы формировать загрузку инструмента 
				return sec.getDescriptor();
			} else if (header[col] == COL_QTY) {
				return order.getQty();
			} else if (header[col] == COL_STATUS) {
				return order.getStatus();
			} else if (header[col] == COL_TRN) {
				return order.getTransactionId();
			} else if (header[col] == COL_LNK_ORDER) {
				return order.getLinkedOrderId();
			} else if (header[col] == COL_PRICE) {
				return order.getPrice() == null? 0.0 : order.getPrice();
			} else if (header[col] == COL_STOP_LMT) {
				return order.getStopLimitPrice() == null? 0.0 : order.getStopLimitPrice();
			} else if (header[col] == COL_TAKE_PRICE) {
				return order.getTakeProfitPrice() == null? 0.0 : order.getTakeProfitPrice();
			} else if (header[col] == COL_OFFS) {
				return order.getOffset();
			} else if ( header[col] == COL_ACCOUNT) {
				return order.getAccount();
			} else if ( header[col] == COL_TIME) {
				return formatTime(order.getTime());
			} else if ( header[col] == COL_CHNG_TIME ) {
				return formatTime(order.getLastChangeTime());
			} else {
				return null;
			}
		} catch (SecurityException e) {
			logger.error("SecurityException: ", e);
			return null;
		}
	}
	
	/**
	 * Форматировать время.
	 * <p>
	 * @param time время
	 * @return строка времени или null, если время не определено
	 */
	private String formatTime(Date time) {
		return time == null ? null : format.format(time);
	}

	@Override
	public void start() throws StarterException {
		orders.OnStopOrderAvailable().addListener(this);
		orders.OnStopOrderChanged().addListener(this);
		
	}

	@Override
	public void stop() throws StarterException {
		orders.OnStopOrderAvailable().removeListener(this);
		orders.OnStopOrderChanged().removeListener(this);
		
	}

	@Override
	public void onEvent(Event event) {
		if( event.isType(orders.OnStopOrderAvailable()) ) {
			OrderEvent e = (OrderEvent) event;
			Order order = e.getOrder();
			int rowIndex = list.size();
			list.add(order);
			order.OnChanged().addListener(this);
			fireTableRowsInserted(rowIndex, rowIndex);
		} else if( event.isType(orders.OnStopOrderChanged()) ) {
			OrderEvent e = (OrderEvent) event;
			int rowIndex = list.indexOf(e.getOrder());
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
		
	}

}

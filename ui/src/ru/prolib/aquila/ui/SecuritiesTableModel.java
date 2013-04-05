package ru.prolib.aquila.ui;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.BusinessEntities.Securities;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;

/**
 * 2012-12-08<br>
 * $Id: SecuritiesTableModel.java 544 2013-02-25 14:31:32Z huan.kaktus $
 */
public class SecuritiesTableModel extends AbstractTableModel
	implements EventListener, Starter
{
	private static final String COL_SYMBOL = "COL_SYMBOL";
	private static final String COL_CLASS = "COL_CLASS";
	private static final String COL_NAME = "COL_NAME";
	private static final String COL_LOT = "COL_LOT";
	private static final String COL_TICK = "COL_TICK";
	private static final String COL_PREC = "COL_PREC";
	private static final String COL_CURR = "COL_CURR";
	private static final String COL_TYPE = "COL_TYPE";
	private static final String COL_LAST = "COL_LAST";
	private static final String COL_OPEN = "COL_OPEN";
	private static final String COL_HIGH = "COL_HIGH";
	private static final String COL_LOW = "COL_LOW";
	private static final String COL_CLOSE = "COL_CLOSE";
	private static final String COL_ASK = "COL_ASK";
	private static final String COL_ASK_SIZE = "COL_ASK_SIZE";
	private static final String COL_BID = "COL_BID";
	private static final String COL_BID_SIZE = "COL_BID_SIZE";
	private static final String COL_STATUS = "COL_STATUS";
	private static final String[] header = {
		COL_NAME,
		COL_TYPE,
		COL_STATUS,
		COL_CURR,
		COL_SYMBOL,
		COL_CLASS,
		COL_LAST,
		COL_OPEN,
		COL_HIGH,
		COL_LOW,
		COL_CLOSE,
		COL_ASK,
		COL_ASK_SIZE,
		COL_BID,
		COL_BID_SIZE,
		COL_LOT,
		COL_TICK,
		COL_PREC
	};
	
	private static final long serialVersionUID = -4408315560673296066L;
	private static Logger logger = LoggerFactory.getLogger(SecuritiesTableModel.class);
	private final ClassLabels uiLabels;
	private final Securities securities;
	private final List<SecurityDescriptor> list;
	
	public SecuritiesTableModel(Securities securities, UiTexts uiTexts) {
		super();
		uiLabels = uiTexts.get("SecuritiesTableModel");
		this.securities = securities;
		this.list = new Vector<SecurityDescriptor>();
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
		SecurityDescriptor descr = list.get(row);
		try {
			Security security = securities.getSecurity(descr);
			if ( header[col] == COL_SYMBOL ) {
				return descr.getCode();
			} else if ( header[col] == COL_CLASS ) {
				return descr.getClassCode();
			} else if ( header[col] == COL_LOT ) {
				return security.getLotSize();
			} else if ( header[col] == COL_PREC ) {
				return security.getPrecision();
			} else if ( header[col] == COL_TICK ) {
				return security.getMinStepSize();
			} else if ( header[col] == COL_NAME ) {
				return security.getDisplayName();
			} else if (header[col] == COL_CURR) {
				return descr.getCurrency();
			} else if ( header[col] == COL_TYPE ) {
				return descr.getType().getName();
			} else if ( header[col] == COL_ASK ) {
				return security.getAskPrice() == null? 0.0 : security.getAskPrice();
			} else if ( header[col] == COL_ASK_SIZE ) {
				return security.getAskSize();
			} else if ( header[col] == COL_BID ) {
				return security.getBidPrice() == null? 0.0 : security.getBidPrice();
			} else if ( header[col] == COL_BID_SIZE ) {
				return security.getBidSize();
			} else if ( header[col] == COL_LAST ) {
				return security.getLastPrice() == null? 0.0 : security.getLastPrice();
			} else if ( header[col] == COL_STATUS ) {
				return security.getStatus();
			} else if ( header[col] == COL_OPEN ) {
				return security.getOpenPrice() == null? 0.0 : security.getOpenPrice();
			} else if ( header[col] == COL_HIGH ) {
				return security.getHighPrice() == null? 0.0 : security.getHighPrice();
			} else if ( header[col] == COL_LOW ) {
				return security.getLowPrice() == null? 0.0 : security.getLowPrice();
			} else if ( header[col] == COL_CLOSE ) {
				return security.getClosePrice() == null? 0.0 : security.getClosePrice();
			} else {
				return null;
			}
		} catch (SecurityException e) {
			logger.error("SecurityException: ", e);
			return null;
		}
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(securities.OnSecurityAvailable()) ) {
			SecurityEvent e = (SecurityEvent) event;
			Security security = e.getSecurity();
			int rowIndex = list.size();
			list.add(security.getDescriptor());
			fireTableRowsInserted(rowIndex, rowIndex);
		} else if ( event.isType(securities.OnSecurityChanged()) ) {
			SecurityEvent e = (SecurityEvent) event;
			int rowIndex = list.indexOf(e.getSecurity().getDescriptor());
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	@Override
	public void start() {
		securities.OnSecurityAvailable().addListener(this);
		securities.OnSecurityChanged().addListener(this);
	}

	@Override
	public void stop() {
		securities.OnSecurityChanged().removeListener(this);
		securities.OnSecurityAvailable().removeListener(this);
	}

}

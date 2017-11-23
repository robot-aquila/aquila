package ru.prolib.aquila.ui.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.MarketDepth;
import ru.prolib.aquila.core.BusinessEntities.MarketDepthException;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class MarketDepthTableModel extends AbstractTableModel
	implements ITableModel, EventListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MarketDepthTableModel.class);
	}
	
	public static final int CID_BID_SIZE = 1;
	public static final int CID_ASK_SIZE = 2;
	public static final int CID_PRICE = 3;

	private final List<Integer> columnIndexToColumnID;
	private final Map<Integer, MsgID> columnIDToColumnHeader;
	private final IMessages messages;
	private final Security security;
	private final int maxDepth;
	private boolean subscribed = false;
	private MarketDepth marketDepth;
	//private int priceScale;
	
	public MarketDepthTableModel(IMessages messages, Security security, int maxDepth) {
		this.messages = messages;
		this.security = security;
		this.maxDepth = maxDepth;
		columnIndexToColumnID = getColumnIDList();
		columnIDToColumnHeader = getColumnIDToHeaderMap();
	}
	
	/**
	 * Override this method to define a set and order of columns.
	 * <p>
	 * @return the set of expected columns
	 */
	protected List<Integer> getColumnIDList() {
		List<Integer> cols = new ArrayList<>();
		cols.add(CID_BID_SIZE);
		cols.add(CID_PRICE);
		cols.add(CID_ASK_SIZE);
		return cols;
	}
	
	/**
	 * Override this method to define a map a column header text.
	 * <p>
	 * @return the map of header messages
	 */
	protected Map<Integer, MsgID> getColumnIDToHeaderMap() {
		Map<Integer, MsgID> head = new HashMap<>();
		head.put(CID_BID_SIZE, CommonMsg.MDD_BID_SIZE);
		head.put(CID_ASK_SIZE, CommonMsg.MDD_ASK_SIZE);
		head.put(CID_PRICE, CommonMsg.MDD_PRICE);
		return head;
	}
	
	@Override
	public int getRowCount() {
		return maxDepth * 2;
	}

	@Override
	public int getColumnCount() {
		return columnIndexToColumnID.size();
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
		MsgID id = columnIDToColumnHeader.get(getColumnID(col));
		if ( id == null ) {
			return "NULL_ID#" + col; 
		}
		return messages.get(id);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch ( getColumnID(columnIndex) ) {
		case CID_BID_SIZE:
		case CID_ASK_SIZE:
			return Long.class;
		case CID_PRICE:
			return BigDecimal.class;
		default:
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if ( marketDepth == null ) {
			return null;
		}
		try {
			return getColumnValue(rowIndex, getColumnID(columnIndex));
		} catch ( MarketDepthException e ) {
			logger.error("Unexpected exception: ", e);
		}
		return null;
	}
	
	protected Object getColumnValue(int rowIndex, int columnID) throws MarketDepthException {
		if ( rowIndex >=0 && rowIndex < maxDepth ) {
			// Ask
			int quoteOffset = maxDepth - rowIndex - 1;
			if ( marketDepth.hasAskAtOffset(quoteOffset) ) {
				switch ( columnID ) {
				case CID_ASK_SIZE:
					return marketDepth.getAskAtOffset(quoteOffset).getSize();
				case CID_PRICE:
					return marketDepth.getAskAtOffset(quoteOffset).getPrice();
				case CID_BID_SIZE:
				default:
					break;
				}
			}
		} else if ( rowIndex >= maxDepth && rowIndex < maxDepth * 2 ) {
			// Bid
			int quoteOffset = rowIndex - maxDepth;
			if ( marketDepth.hasBidAtOffset(quoteOffset) ) {
				switch ( columnID ) {
				case CID_BID_SIZE:
					return marketDepth.getBidAtOffset(quoteOffset).getSize();
				case CID_PRICE:
					return marketDepth.getBidAtOffset(quoteOffset).getPrice();
				case CID_ASK_SIZE:
				default:
					break;
				}
			}
		}
		return null;
	}

	@Override
	public void close() {
		stopListeningUpdates();
	}

	@Override
	public void startListeningUpdates() {
		if ( subscribed || security.isClosed() ) {
			return;
		}
		marketDepth = security.getMarketDepth();
		//priceScale = security.getScale();
		security.onMarketDepthUpdate().addListener(this);
		subscribed = true;
		fireTableDataChanged();
	}

	@Override
	public void stopListeningUpdates() {
		if ( ! subscribed ) {
			return;
		}
		security.onMarketDepthUpdate().removeListener(this);
		marketDepth = null;
		subscribed = false;
		fireTableDataChanged();
	}
	
	public boolean isListeningUpdates() {
		return subscribed;
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
	
	void processEvent(Event event) {
		if ( ! subscribed || security.isClosed() ) {
			return;
		}
		marketDepth = security.getMarketDepth();
		//priceScale = security.getScale();
		fireTableDataChanged();
	}
	
	//private BigDecimal toPrice(double price) {
	//	return new BigDecimal(price).setScale(priceScale, RoundingMode.HALF_UP);
	//}

}

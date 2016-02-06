package ru.prolib.aquila.ui.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class SecurityListTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final List<MsgID> mapIndexToID;
	private static final Comparator<Security> compareSecuritiesByName;
	
	static {
		compareSecuritiesByName = new Comparator<Security>() {
			@Override public int compare(Security o1, Security o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		};
		mapIndexToID = new Vector<MsgID>();
		mapIndexToID.add(SecurityMsg.NAME);
		mapIndexToID.add(SecurityMsg.SYMBOL);
		mapIndexToID.add(SecurityMsg.EXCHANGE);
		mapIndexToID.add(SecurityMsg.TYPE);
		mapIndexToID.add(SecurityMsg.CURRENCY);
		mapIndexToID.add(SecurityMsg.LOT_SIZE);
		mapIndexToID.add(SecurityMsg.SCALE);
		mapIndexToID.add(SecurityMsg.TICK_SIZE);
		mapIndexToID.add(SecurityMsg.TICK_VALUE);
		mapIndexToID.add(SecurityMsg.LAST_PRICE);
		mapIndexToID.add(SecurityMsg.LAST_SIZE);
		mapIndexToID.add(SecurityMsg.ASK_PRICE);
		mapIndexToID.add(SecurityMsg.ASK_SIZE);
		mapIndexToID.add(SecurityMsg.BID_PRICE);
		mapIndexToID.add(SecurityMsg.BID_SIZE);
		mapIndexToID.add(SecurityMsg.LOWER_PRICE);
		mapIndexToID.add(SecurityMsg.UPPER_PRICE);
		mapIndexToID.add(SecurityMsg.OPEN_PRICE);
		mapIndexToID.add(SecurityMsg.HIGH_PRICE);
		mapIndexToID.add(SecurityMsg.LOW_PRICE);
		mapIndexToID.add(SecurityMsg.CLOSE_PRICE);
		mapIndexToID.add(SecurityMsg.INITIAL_MARGIN);
		mapIndexToID.add(SecurityMsg.INITIAL_PRICE);
	}
	
	private final IMessages messages;
	private final List<Security> data;
	
	public SecurityListTableModel(IMessages messages) {
		super();
		this.messages = messages;
		this.data = new ArrayList<Security>();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return mapIndexToID.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if ( rowIndex >= data.size() || columnIndex >= mapIndexToID.size() ) {
			return null;
		}
		Tick tick = null;
		Security security = data.get(rowIndex);
		Symbol symbol = security.getSymbol();
		MsgID id = mapIndexToID.get(columnIndex); 
		if ( id == SecurityMsg.NAME ) {
			return security.getDisplayName();
		} else if ( id == SecurityMsg.SYMBOL ) {
			return symbol.getCode();
		} else if ( id == SecurityMsg.EXCHANGE ) {
			return symbol.getExchangeID();
		} else if ( id == SecurityMsg.TYPE ) {
			return symbol.getType();
		} else if ( id == SecurityMsg.CURRENCY ) {
			return symbol.getCurrencyCode();
		} else if ( id == SecurityMsg.LOT_SIZE ) {
			return security.getLotSize();
		} else if ( id == SecurityMsg.SCALE ) {
			return security.getScale();
		} else if ( id == SecurityMsg.TICK_SIZE ) {
			return security.getTickSize();
		} else if ( id == SecurityMsg.TICK_VALUE ) {
			return security.getTickValue();
		} else if ( id == SecurityMsg.LAST_PRICE ) {
			tick = security.getLastTrade();
			return tick == null ? null : tick.getPrice();
		} else if ( id == SecurityMsg.LAST_SIZE ) {
			tick = security.getLastTrade();
			return tick == null ? null : tick.getSize();
		} else if ( id == SecurityMsg.ASK_PRICE ) {
			tick = security.getBestAsk();
			return tick == null ? null : tick.getPrice();
		} else if ( id == SecurityMsg.ASK_SIZE ) {
			tick = security.getBestAsk();
			return tick == null ? null : tick.getSize();
		} else if ( id == SecurityMsg.BID_PRICE ) {
			tick = security.getBestBid();
			return tick == null ? null : tick.getPrice();
		} else if ( id == SecurityMsg.BID_SIZE ) {
			tick = security.getBestBid();
			return tick == null ? null : tick.getSize();
		} else if ( id == SecurityMsg.LOWER_PRICE ) {
			return security.getLowerPriceLimit();
		} else if ( id == SecurityMsg.UPPER_PRICE ) {
			return security.getUpperPriceLimit();
		} else if ( id == SecurityMsg.OPEN_PRICE ) {
			return security.getOpenPrice();
		} else if ( id == SecurityMsg.HIGH_PRICE ) {
			return security.getHighPrice();
		} else if ( id == SecurityMsg.LOW_PRICE ) {
			return security.getLowPrice();
		} else if ( id == SecurityMsg.CLOSE_PRICE ) {
			return security.getClosePrice();
		} else if ( id == SecurityMsg.INITIAL_MARGIN ) {
			return security.getInitialMargin();
		} else if ( id == SecurityMsg.INITIAL_PRICE ) {
			return security.getInitialPrice();			
		} else {
			return null;			
		}
	}
	
	/**
	 * Return column index by ID.
	 * <p> 
	 * @param columnId - one of {@link SecurityMsg} constants.
	 * @return return index of specified column
	 */
	public int getColumnIndex(MsgID columnId) {
		return mapIndexToID.indexOf(columnId);
	}
	
	@Override
	public String getColumnName(int c) {
		return messages.get(mapIndexToID.get(c));
	}
	
	/**
	 * Clear all cached data.
	 */
	public void clear() {
		data.clear();
	}
	
	/**
	 * Add all securities owned to terminal.
	 * <p>
	 * @param terminal - terminal to scan
	 */
	public void add(Terminal terminal) {
		for ( Security security : terminal.getSecurities() ) {
			if ( ! data.contains(security) ) {
				data.add(security);
			}
		}
		Collections.sort(data, compareSecuritiesByName);
		fireTableDataChanged();
	}
	
	/**
	 * Return security instance by row index.
	 * <p>
	 * @param rowIndex - the row index
	 * @return security
	 */
	public Security getSecurity(int rowIndex) {
		return data.get(rowIndex);
	}

}

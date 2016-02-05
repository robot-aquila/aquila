package ru.prolib.aquila.ui.form;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
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
		mapIndexToID.add(SecurityMsg.CLASS);
		mapIndexToID.add(SecurityMsg.TYPE);
		mapIndexToID.add(SecurityMsg.CURRENCY);
	}
	
	private final IMessages messages;
	private final List<Security> data;
	
	public SecurityListTableModel(IMessages messages) {
		super();
		this.messages = messages;
		this.data = new Vector<Security>();
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
		Security security = data.get(rowIndex);
		Symbol symbol = security.getSymbol();
		MsgID id = mapIndexToID.get(columnIndex); 
		if ( id == SecurityMsg.NAME ) {
			return security.getDisplayName();
		} else if ( id == SecurityMsg.SYMBOL ) {
			return symbol.getCode();
		} else if ( id == SecurityMsg.CLASS ) {
			return symbol.getExchangeID();
		} else if ( id == SecurityMsg.TYPE ) {
			return symbol.getType();
		} else if ( id == SecurityMsg.CURRENCY ) {
			return symbol.getCurrencyCode();
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

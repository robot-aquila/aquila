package ru.prolib.aquila.ui.form;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class SelectSecurityTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final List<String> mapIndexToName;
	private static final Comparator<Security> compareSecuritiesByName;
	
	static {
		compareSecuritiesByName = new Comparator<Security>() {
			@Override public int compare(Security o1, Security o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		};
		mapIndexToName = new Vector<String>();
		mapIndexToName.add(SecurityMsg.NAME);
		mapIndexToName.add(SecurityMsg.SYMBOL);
		mapIndexToName.add(SecurityMsg.CLASS);
		mapIndexToName.add(SecurityMsg.TYPE);
		mapIndexToName.add(SecurityMsg.CURRENCY);
	}
	
	private final IMessages messages;
	private final List<Security> data;
	
	public SelectSecurityTableModel(IMessages messages) {
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
		return mapIndexToName.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if ( rowIndex >= data.size() || columnIndex >= mapIndexToName.size() ) {
			return null;
		}
		Security security = data.get(rowIndex);
		switch ( mapIndexToName.get(columnIndex) ) {
		case SecurityMsg.NAME:
			return security.getDisplayName();
		case SecurityMsg.SYMBOL:
			return security.getCode();
		case SecurityMsg.CLASS:
			return security.getClassCode();
		case SecurityMsg.TYPE:
			return security.getDescriptor().getType();
		case SecurityMsg.CURRENCY:
			return security.getDescriptor().getCurrencyCode();
		default:
			return null;			
		}
	}
	
	/**
	 * Return column index by ID.
	 * <p> 
	 * @param columnId - one of {@link SecurityMsg} constants.
	 * @return return index of specified column
	 */
	public int getColumnIndex(String columnId) {
		return mapIndexToName.indexOf(columnId);
	}
	
	@Override
	public String getColumnName(int c) {
		return messages.get(mapIndexToName.get(c));
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
	public Security getSecutity(int rowIndex) {
		return data.get(rowIndex);
	}

}

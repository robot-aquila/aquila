package ru.prolib.aquila.ui.form;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JTable;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.text.IMessages;

/**
 * This class opens a market depth dialog by double-click on the row of security
 * list table. To use just add an instance of the class as mouse listener to
 * security list table.
 */
public class SecurityListMarketDepthActivator extends MouseAdapter {
	private final Map<Security, JDialog> dialogs;
	private final IMessages messages;
	
	public SecurityListMarketDepthActivator(IMessages messages) {
		super();
		dialogs = new Hashtable<Security, JDialog>();
		this.messages = messages;
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if ( event.getClickCount() == 2 ) {
			JTable table = (JTable)event.getSource();
			int rowIndex = table.convertRowIndexToModel(table.getSelectedRow());
			SecurityListTableModel tableModel = (SecurityListTableModel)table.getModel();
			Security security = tableModel.getSecurity(rowIndex);
			JDialog dialog = dialogs.get(security);
			if ( dialog == null ) {
				dialog = new MarketDepthDialog(messages, security);
				dialogs.put(security, dialog);
			}
			if ( ! dialog.isShowing() ) {
				dialog.setVisible(true);
			}
		}
	}

}

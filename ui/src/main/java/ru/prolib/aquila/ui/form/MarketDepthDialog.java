package ru.prolib.aquila.ui.form;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.TableModelController;

public class MarketDepthDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_DEPTH = 10;
	
	public MarketDepthDialog(IMessages messages, Security security, int maxDepth) {
		final MarketDepthTableModel tableModel = new MarketDepthTableModel(messages, security, maxDepth);
		addComponentListener(new TableModelController(tableModel, this));
		final JTable table = new JTable(tableModel);
		table.setRowSelectionAllowed(false);
		getContentPane().add(new JScrollPane(table));
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(180, 390);
		setTitle(security.getSymbol().toString());
	}
	
	public MarketDepthDialog(IMessages messages, Security security) {
		this(messages, security, DEFAULT_DEPTH);
	}

}

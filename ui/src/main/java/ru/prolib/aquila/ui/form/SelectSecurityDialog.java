package ru.prolib.aquila.ui.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.MessageRegistry;
import ru.prolib.aquila.ui.msg.CommonMsg;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class SelectSecurityDialog extends JDialog
	implements ActionListener, ListSelectionListener
{
	private static final long serialVersionUID = 1L;
	public static final String SELECT = "SELECT";
	public static final String CANCEL = "CANCEL";
	private final JPanel panel;
	private final SelectSecurityTableModel tableModel;
	private final JTable table;
	private final JButton buttonSelect, buttonCancel;
	private Security selectedSecurity;
	
	public SelectSecurityDialog(JFrame frame, MessageRegistry messageRegistry) {
		super(frame);
		panel = new JPanel(new BorderLayout());
		IMessages commonMsg = messageRegistry.getMessages(CommonMsg.SECTION_ID);
		IMessages securityMsg = messageRegistry.getMessages(SecurityMsg.SECTION_ID);
		
		JPanel filterPanel = new JPanel();
		
		tableModel = new SelectSecurityTableModel(securityMsg);
		table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		setColumnWidth(SecurityMsg.NAME, 160);
		setColumnWidth(SecurityMsg.SYMBOL, 80);
		setColumnWidth(SecurityMsg.CLASS, 50);
		setColumnWidth(SecurityMsg.TYPE, 50);
		setColumnWidth(SecurityMsg.CURRENCY, 50);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2) {
					onSelect();
				}
			}
		});
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.addListSelectionListener(this);
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane tablePanel = new JScrollPane(table);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonCancel = new JButton(commonMsg.get(CommonMsg.CANCEL));
		buttonCancel.setActionCommand(CANCEL);
		buttonCancel.addActionListener(this);
		buttonPanel.add(buttonCancel);
		buttonSelect = new JButton(commonMsg.get(CommonMsg.SELECT));
		buttonSelect.setActionCommand(SELECT);
		buttonSelect.addActionListener(this);
		buttonSelect.setEnabled(false); // Initially disabled
		buttonPanel.add(buttonSelect);
		
		panel.add(filterPanel, BorderLayout.NORTH);
		panel.add(tablePanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(panel);
		setTitle(securityMsg.get(SecurityMsg.SELECT_SECURITY));
		setPreferredSize(new Dimension(800, 600));
	}
	
	private void setColumnWidth(String columnId, int width) {
		table.getColumnModel().getColumn(tableModel.getColumnIndex(columnId))
			.setPreferredWidth(width);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( SELECT.equals(e.getActionCommand()) && isSelected() ) {
			onSelect();
		} else {
			onCancel();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if ( ! e.getValueIsAdjusting() ) {
			buttonSelect.setEnabled(isSelected());
		}
	}
	
	/**
	 * Clear table contents and reset currently selected security.
	 */
	public void clear() {
		selectedSecurity = null;
		tableModel.clear();
	}
	
	/**
	 * Add all securities owned by terminal.
	 * <p>
	 * @param terminal - the owner of securities
	 */
	public void add(Terminal terminal) {
		tableModel.add(terminal);
	}
	
	/**
	 * Get previously selected security.
	 * <p>
	 * @return security instance or null if not selected
	 */
	public Security getSelectedSecurity() {
		return selectedSecurity;
	}
	
	private boolean isSelected() {
		return table.getSelectedRowCount() > 0;
	}
	
	private void onSelect() {
		selectedSecurity = tableModel.getSecutity(table.getSelectedRow());
		dispose();
	}
	
	private void onCancel() {
		selectedSecurity = null;
		dispose();
	}

}

package ru.prolib.aquila.ui.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.msg.CommonMsg;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class SecurityListDialog extends JDialog
	implements ActionListener, ListSelectionListener
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SecurityListDialog.class);
	}
	
	/**
	 * Just show the security table. 
	 */
	public static final int TYPE_SHOW = 0;
	/**
	 * Show table for security selection.
	 */
	public static final int TYPE_SELECT = 1;
	private static final long serialVersionUID = 1L;
	public static final String SELECT = "SELECT";
	public static final String CANCEL = "CANCEL";
	private final JPanel panel;
	private final SecurityListTableModel tableModel;
	private final JTable table;
	private final JButton buttonSelect, buttonCancel, buttonClose;
	private final int type;
	private Security selectedSecurity;
	
	public SecurityListDialog(JFrame frame, IMessages messages) {
		this(frame, TYPE_SHOW, messages);
	}
	
	public SecurityListDialog(JFrame frame, final int type,
			IMessages messages)
	{
		super(frame);
		this.type = type;
		panel = new JPanel(new BorderLayout());
		
		JPanel filterPanel = new JPanel();
		
		tableModel = new SecurityListTableModel(messages);
		table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(tableModel));
		//setColumnWidth(SecurityListTableModel.CID_DISPLAY_NAME, 160);
		//setColumnWidth(SecurityListTableModel.CID_SYMBOL, 80);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2) {
					if ( type == TYPE_SELECT ) {
						onSelect();	
					} else {
						onCancel();
					}
				}
			}
		});
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.addListSelectionListener(this);
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane tablePanel = new JScrollPane(table);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonClose = new JButton(messages.get(CommonMsg.CLOSE));
		buttonCancel = new JButton(messages.get(CommonMsg.CANCEL));
		buttonSelect = new JButton(messages.get(CommonMsg.SELECT));
		if ( type == TYPE_SHOW ) {
			buttonClose.setActionCommand(CANCEL);
			buttonClose.addActionListener(this);
			buttonPanel.add(buttonClose);
		} else {
			buttonCancel.setActionCommand(CANCEL);
			buttonCancel.addActionListener(this);
			buttonPanel.add(buttonCancel);
			buttonSelect.setActionCommand(SELECT);
			buttonSelect.addActionListener(this);
			buttonSelect.setEnabled(false); // Initially disabled
			buttonPanel.add(buttonSelect);			
		}
		panel.add(filterPanel, BorderLayout.NORTH);
		panel.add(tablePanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		getContentPane().add(panel);
		setTitle(messages.get(type == TYPE_SELECT ?
				SecurityMsg.SELECT_SECURITY : SecurityMsg.SHOW_SECURITIES));
		setPreferredSize(new Dimension(800, 600));
		new TableModelController(tableModel, this);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				onShown();
			}
		});
		pack();
	}
	
	protected void setColumnWidth(int columnId, int width) {
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
		//logger.debug("valueChanged called: {}", e);
		if ( ! e.getValueIsAdjusting() && type == TYPE_SELECT ) {
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
	
	public void setSelectedSecurity(Security security) {
		selectedSecurity = security;
	}
	
	private boolean isSelected() {
		return table.getSelectedRowCount() > 0;
	}
	
	private void onSelect() {
		int selected_index = table.getSelectedRow();
		//logger.debug("onSelect: index={}", selected_index);
		if ( selected_index != -1 ) {
			selectedSecurity = tableModel.getSecurity(table.convertRowIndexToModel(selected_index));
		}
		dispose();
	}
	
	private void onCancel() {
		//logger.debug("onCancel: reset selected security");
		selectedSecurity = null;
		dispose();
	}
	
	private void onShown() {
		// TODO: this doesn't work
		//if ( selectedSecurity != null ) {
		//	int index = tableModel.getSecurityIndex(selectedSecurity);
		//	logger.debug("Security {} index is {}", selectedSecurity.getSymbol(), index);
		//	if ( index != -1 ) {
		//		index = table.convertRowIndexToView(index);
		//		table.getSelectionModel().setSelectionInterval(index, index);
		//		table.scrollRectToVisible(new Rectangle(table.getCellRect(index, 0, true)));
		//	}
		//}
	}

}

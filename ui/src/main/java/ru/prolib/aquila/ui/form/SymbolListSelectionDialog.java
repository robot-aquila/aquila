package ru.prolib.aquila.ui.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class SymbolListSelectionDialog extends JDialog implements SymbolListSelectionDialogView, ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private static final String ACTION_CANCEL = "CANCEL";
	private static final String ACTION_OK = "OK";
	private static final String ACTION_ADD = "ADD";
	private static final String ACTION_REMOVE = "REMOVE";
	
	private final IMessages messages;
	private final DefaultListModel<Symbol> listModel;
	private final JList<Symbol> list;
	private List<Symbol> selectedSymbols;
	private final JPanel contentPanel = new JPanel();
	private final JButton btnOK, btnCancel, btnAdd, btnRemove;
	
	public SymbolListSelectionDialog(IMessages messages, MsgID title) {
		this.messages = messages;
		setResizable(false);
		setAlwaysOnTop(true);
		setModal(true);
		setTitle(messages.get(title));
		setPreferredSize(new Dimension(200, 300));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.NORTH);
			btnAdd = new JButton(messages.get(CommonMsg.SLD_ADD_SYMBOL));
			btnAdd.setToolTipText(messages.get(CommonMsg.SLD_ADD_SYMBOL_TOOLTIP));
			btnAdd.setActionCommand(ACTION_ADD);
			btnAdd.addActionListener(this);
			btnRemove = new JButton(messages.get(CommonMsg.SLD_REMOVE_SYMBOL));
			btnRemove.setToolTipText(messages.get(CommonMsg.SLD_REMOVE_SYMBOL_TOOLTIP));
			btnRemove.setActionCommand(ACTION_REMOVE);
			btnRemove.addActionListener(this);
			panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			panel.add(btnAdd);
			panel.add(btnRemove);
		}
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			contentPanel.add(panel);
			panel.setBorder(BorderFactory.createTitledBorder(messages.get(CommonMsg.SLD_SELECTED_SYMBOLS)));
			listModel = new DefaultListModel<>();
			list = new JList<>(listModel);			
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			list.addListSelectionListener(this);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			list.setVisibleRowCount(20);
			panel.add(new JScrollPane(list), BorderLayout.CENTER);
		}
		contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			btnCancel = new JButton(messages.get(CommonMsg.CANCEL));
			btnCancel.setActionCommand(ACTION_CANCEL);
			btnCancel.addActionListener(this);
			btnOK = new JButton(messages.get(CommonMsg.OK));
			btnOK.setActionCommand(ACTION_OK);
			btnOK.addActionListener(this);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			panel.add(btnCancel);
			panel.add(btnOK);
		}
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(btnOK);
		pack();
		setLocationRelativeTo(null);
	}
	
	public SymbolListSelectionDialog(IMessages messages) {
		this(messages, CommonMsg.SLD_DEFAULT_TITLE);
	}

	@Override
	public List<Symbol> showDialog(List<Symbol> initialList) {
		selectedSymbols = null;
		listModel.clear();
		for ( Symbol symbol : initialList ) {
			listModel.addElement(symbol);
		}
		btnRemove.setEnabled(false);
		setVisible(true);
		return selectedSymbols;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch ( event.getActionCommand() ) {
		case ACTION_OK:
			selectedSymbols = new ArrayList<>();
			for ( int i = 0; i < listModel.size(); i ++ ) {
				selectedSymbols.add(listModel.get(i));
			}
			setVisible(false);
			return;
		case ACTION_CANCEL:
			setVisible(false);
			return;
		case ACTION_ADD:
			String x = JOptionPane.showInputDialog(null, messages.get(CommonMsg.SLD_INPUT_SYMBOL));
			if ( x != null ) {
				try {
					Symbol symbol = new Symbol(x);
					if ( ! listModel.contains(symbol) ) {
						listModel.addElement(symbol);
					}
				} catch ( IllegalArgumentException e ) {
					JOptionPane.showMessageDialog(null, messages.format(CommonMsg.SLD_BAD_INPUT_SYMBOL, e.getMessage()));
				}
			}
			return;
		case ACTION_REMOVE:
			int index = list.getSelectedIndex();
            listModel.remove(index);
			return;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if ( ! e.getValueIsAdjusting() ) {
			btnRemove.setEnabled(list.getSelectedIndex() >=- 0);
		}
	}

}

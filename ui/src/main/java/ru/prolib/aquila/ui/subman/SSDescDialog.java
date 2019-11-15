package ru.prolib.aquila.ui.subman;

import static javax.swing.JScrollPane.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TerminalRegistry;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.OSCRepositoryTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class SSDescDialog extends JDialog implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;
	private static final String BTN_SUBSCR = "SUBSCRIBE";
	private static final String BTN_UNSUBSCR = "UNSUBSCRIBE";
	private static final String BTN_CLOSE = "CLOSE";
	
	private final Frame frame;
	private final IMessages messages;
	private final SSDescRepo repository;
	private SSDescFormPanel formPanel;
	private JTable subscrTable;
	private OSCRepositoryTableModel<SSDesc> subscrTableModel;
	private JButton btnSubscr, btnClose, btnUnsubscr;

	public SSDescDialog(Frame frame, IMessages messages, TerminalRegistry registry, SSDescRepo repository) {
		super(frame);
		this.frame = frame;
		this.messages = messages;
		this.repository = repository;
		
		JPanel main_panel = new JPanel(new BorderLayout());
		main_panel.add(createTopPanel(), BorderLayout.PAGE_START);
		main_panel.add(createMidPanel(), BorderLayout.CENTER);
		main_panel.add(createBotPanel(), BorderLayout.PAGE_END);
		getContentPane().add(main_panel);

		formPanel.fillTerminalList(registry);
		onSelectedRowChanged();
		getRootPane().setDefaultButton(btnClose);
		setPreferredSize(new Dimension(800, 600));
		setTitle(messages.get(CommonMsg.MANUAL_SYMBOL_SUBSCRIPTIONS));
		pack();
	}
	
	private JPanel createTopPanel() {
		JPanel top_btn_panel = new JPanel();
		top_btn_panel.add(btnSubscr = new JButton(messages.get(CommonMsg.SUBSCRIBE)));
		btnSubscr.setActionCommand(BTN_SUBSCR);
		btnSubscr.addActionListener(this);
		
		JPanel top_panel = new JPanel(new MigLayout());
		top_panel.add(formPanel = new SSDescFormPanel(true, messages));
		top_panel.add(top_btn_panel);

		return top_panel;
	}
	
	private JPanel createMidPanel() {
		subscrTableModel = createTableModel(repository);
		subscrTable = createTable(subscrTableModel);
		subscrTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		subscrTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane scroll = new JScrollPane(subscrTable, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		JPanel mid_panel = new JPanel(new BorderLayout());
		mid_panel.add(scroll, BorderLayout.CENTER);
		new TableModelController(subscrTableModel, this);
		return mid_panel;
	}
	
	private JPanel createBotPanel() {
		JPanel bot_btn_panel = new JPanel(new MigLayout());
		bot_btn_panel.add(btnUnsubscr = new JButton(messages.get(CommonMsg.UNSUBSCRIBE)));
		btnUnsubscr.setActionCommand(BTN_UNSUBSCR);
		btnUnsubscr.addActionListener(this);
		bot_btn_panel.add(btnClose = new JButton(messages.get(CommonMsg.CLOSE)));
		btnClose.setActionCommand(BTN_CLOSE);
		btnClose.addActionListener(this);
		
		JPanel bot_panel = new JPanel();
		bot_panel.add(bot_btn_panel);
		return bot_panel;
	}
	
	private OSCRepositoryTableModel<SSDesc> createTableModel(OSCRepository<Integer, SSDesc> repository) {
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(SSDesc.ID);
		column_id_list.add(SSDesc.TERM_ID);
		column_id_list.add(SSDesc.SYMBOL);
		column_id_list.add(SSDesc.MD_LEVEL);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(SSDesc.ID, CommonMsg.ID);
		column_id_to_header.put(SSDesc.TERM_ID, CommonMsg.TERMINAL);
		column_id_to_header.put(SSDesc.SYMBOL, CommonMsg.SYMBOL);
		column_id_to_header.put(SSDesc.MD_LEVEL, CommonMsg.MD_LEVEL);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	private JTable createTable(ITableModel table_model) {
		JTable table = new JTable(table_model);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(table_model));
		return table;	
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		switch ( event.getActionCommand() ) {
		case BTN_SUBSCR:
			subscribe();
			break;
		case BTN_CLOSE:
			dispose();
			break;
		case BTN_UNSUBSCR:
			unsubscribe();
			break;
		}
	}
	
	private void subscribe() {
		String term_id = formPanel.getSelectedTermID();
		String str_symbol = formPanel.getSelectedSymbol();
		MDLevel level = formPanel.getSelectedLevel();
		Symbol symbol = null;
		try {
			symbol = new Symbol(str_symbol);
		} catch ( IllegalArgumentException e ) {
			JOptionPane.showMessageDialog(frame,
					messages.format(CommonMsg.MSG_ERR_ILLEGAL_SYMBOL, str_symbol),
					messages.get(CommonMsg.MSG_ERR_SUBSCR_FAILED),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		repository.subscribe(term_id, symbol, level);
	}
	
	private void unsubscribe() {
		int index = subscrTable.getSelectedRow();
		if( index < 0 ) {
			return;
		}
		index = subscrTable.getRowSorter().convertRowIndexToModel(index);
		SSDesc desc = subscrTableModel.getEntity(index);
		repository.unsibscribe(desc.getID());
	}
	
	private void onSelectedRowChanged() {
		btnUnsubscr.setEnabled(subscrTable.getSelectedRow() >= 0);
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		onSelectedRowChanged();
	}

}

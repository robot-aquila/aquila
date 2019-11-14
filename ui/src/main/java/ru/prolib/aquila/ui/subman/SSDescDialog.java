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

public class SSDescDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String BTN_SUBSCR = "SUBSCRIBE";
	private static final String BTN_CANCEL = "CANCEL";
	
	private final Frame frame;
	private final IMessages messages;
	private final SSDescRepo repository;
	private final SSDescFormPanel formPanel;

	public SSDescDialog(Frame frame, IMessages messages, TerminalRegistry registry, SSDescRepo repository) {
		super(frame);
		this.frame = frame;
		this.messages = messages;
		this.repository = repository;
				
		formPanel = new SSDescFormPanel(true, messages);
		formPanel.fillTerminalList(registry);

		JButton btn_subscr, btn_cancel;
		JPanel btn_panel = new JPanel();
		btn_panel.add(btn_subscr = new JButton(messages.get(CommonMsg.SUBSCRIBE)));
		btn_subscr.setActionCommand(BTN_SUBSCR);
		btn_subscr.addActionListener(this);
		btn_panel.add(btn_cancel = new JButton(messages.get(CommonMsg.CANCEL)));
		btn_cancel.setActionCommand(BTN_CANCEL);
		btn_cancel.addActionListener(this);
		
		JPanel top_panel = new JPanel(new MigLayout());
		top_panel.add(formPanel);
		top_panel.add(btn_panel);
		
		OSCRepositoryTableModel<SSDesc> table_model = createTableModel(repository);
		JTable table = createTable(table_model);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scroll_panel = new JScrollPane(table, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		JPanel mid_panel = new JPanel(new BorderLayout());
		mid_panel.add(scroll_panel, BorderLayout.CENTER);
		new TableModelController(table_model, this);
		
		JPanel main_panel = new JPanel(new BorderLayout());
		main_panel.add(top_panel, BorderLayout.PAGE_START);
		main_panel.add(mid_panel, BorderLayout.CENTER);
		
		getContentPane().add(main_panel);
		getRootPane().setDefaultButton(btn_cancel);
		setPreferredSize(new Dimension(800, 600));
		setTitle(messages.get(CommonMsg.MANUAL_SYMBOL_SUBSCRIPTIONS));
		pack();
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
		case BTN_CANCEL:
			dispose();
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

}

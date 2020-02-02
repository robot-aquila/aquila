package ru.prolib.aquila.ui.ssrview;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.OSCRepositoryTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class SSRViewDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String BTN_CLOSE = "CLOSE";
	private final IMessages messages;
	private final SymbolSubscrRepository repository;
	private JTable table;
	private OSCRepositoryTableModel<SymbolSubscrCounter> tableModel;
	private JButton btnClose;
	
	public SSRViewDialog(Frame frame, IMessages messages, SymbolSubscrRepository repository) {
		super(frame);
		this.messages = messages;
		this.repository = repository;
		
		JPanel main_panel = new JPanel(new BorderLayout());
		main_panel.add(createMidPanel(), BorderLayout.CENTER);
		main_panel.add(createBotPanel(), BorderLayout.PAGE_END);
		getContentPane().add(main_panel);

		getRootPane().setDefaultButton(btnClose);
		setPreferredSize(new Dimension(800, 600));
		setTitle(messages.get(CommonMsg.SSRV_DEFAULT_TITLE));
		pack();
	}
	
	private JTable createTable(ITableModel table_model) {
		JTable table = new JTable(table_model);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(table_model));
		return table;
	}
	
	private OSCRepositoryTableModel<SymbolSubscrCounter>
		createTableModel(OSCRepository<Symbol, SymbolSubscrCounter> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(Field.SYMBOL);
		column_id_list.add(Field.NUM_L0);
		column_id_list.add(Field.NUM_L1_BBO);
		column_id_list.add(Field.NUM_L1);
		column_id_list.add(Field.NUM_L2);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(Field.SYMBOL, CommonMsg.SSRV_SYMBOL);
		column_id_to_header.put(Field.NUM_L0, CommonMsg.SSRV_L0);
		column_id_to_header.put(Field.NUM_L1_BBO, CommonMsg.SSRV_L1_BBO);
		column_id_to_header.put(Field.NUM_L1, CommonMsg.SSRV_L1);
		column_id_to_header.put(Field.NUM_L2, CommonMsg.SSRV_L2);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	private JPanel createMidPanel() {
		tableModel = createTableModel(repository);
		table = createTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(table, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		JPanel mid_panel = new JPanel(new BorderLayout());
		mid_panel.add(scroll, BorderLayout.CENTER);
		new TableModelController(tableModel, this);
		return mid_panel;
	}
	
	private JPanel createBotPanel() {
		JPanel bot_btn_panel = new JPanel(new MigLayout());
		bot_btn_panel.add(btnClose = new JButton(messages.get(CommonMsg.CLOSE)));
		btnClose.setActionCommand(BTN_CLOSE);
		btnClose.addActionListener(this);
		
		JPanel bot_panel = new JPanel();
		bot_panel.add(bot_btn_panel);
		return bot_panel;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch ( event.getActionCommand() ) {
		case BTN_CLOSE:
			dispose();
			break;
		}
	}

}

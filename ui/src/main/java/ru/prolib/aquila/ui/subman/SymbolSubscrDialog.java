package ru.prolib.aquila.ui.subman;

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

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.TerminalRegistry;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.form.OSCRepositoryTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class SymbolSubscrDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String BTN_SUBSCR = "SUBSCRIBE";
	private static final String BTN_CANCEL = "CANCEL";
	
	private final IMessages messages;
	private final TerminalRegistry registry;
	private final SymbolSubscrPanel panel;
	private final JButton btnSubscr;
	private final JButton btnCancel;

	public SymbolSubscrDialog(Frame frame, IMessages messages, TerminalRegistry registry) {
		super(frame);
		this.messages = messages;
		this.registry = registry;
		panel = new SymbolSubscrPanel(true, messages);
		panel.fillTerminalList(registry);
		
		panel.add(btnSubscr = new JButton(messages.get(CommonMsg.SUBSCRIBE)));
		btnSubscr.setActionCommand(BTN_SUBSCR);
		btnSubscr.addActionListener(this);
		panel.add(btnCancel = new JButton(messages.get(CommonMsg.CANCEL)));
		btnCancel.setActionCommand(BTN_CANCEL);
		btnCancel.addActionListener(this);
		
		getRootPane().setDefaultButton(btnSubscr);
		getContentPane().add(panel);
		setTitle(messages.get(CommonMsg.SUBSCRIBE_FOR_SYMBOL));
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
		String term_id = panel.getSelectedTermID();
		String str_symbol = panel.getSelectedSymbol();
		MDLevel level = panel.getSelectedLevel();
		Symbol symbol = null;
		try {
			symbol = new Symbol(str_symbol);
		} catch ( IllegalArgumentException e ) {
			JOptionPane.showMessageDialog(null,
					messages.format(CommonMsg.MSG_ERR_ILLEGAL_SYMBOL, str_symbol),
					messages.get(CommonMsg.MSG_ERR_SUBSCR_FAILED),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Terminal term = registry.get(term_id);
		SubscrHandler handler = term.subscribe(symbol, level); // TODO: register handler somewhere
	}

}

package ru.prolib.aquila.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;

public class AbstractServiceMenu implements ActionListener {
	protected final IMessages messages;
	protected final Map<String, DialogFactory> actionCmdToDialogMap = new HashMap<>();

	public AbstractServiceMenu(IMessages messages) {
		super();
		this.messages = messages;
	}

	protected JMenuItem addMenuItem(JMenu menu, MsgID msg_id, String action_command, DialogFactory factory) {
		JMenuItem item = null;
		menu.add(item = new JMenuItem(messages.get(msg_id)));
		item.setActionCommand(action_command);
		item.addActionListener(this);
		actionCmdToDialogMap.put(action_command, new LazyDialogInitializer(factory));
		return item;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DialogFactory factory = actionCmdToDialogMap.get(e.getActionCommand());
		if ( factory != null ) {
			factory.produce().setVisible(true);
		}
	}

}

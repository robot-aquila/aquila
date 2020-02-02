package ru.prolib.aquila.qforts.ui;

import javax.swing.JFrame;
import javax.swing.JMenu;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.TerminalImpl;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.qforts.impl.QFReactor;
import ru.prolib.aquila.ui.AbstractServiceMenu;
import ru.prolib.aquila.ui.ssrview.SSRViewDialog;

public class QFServiceMenu extends AbstractServiceMenu {
	private final JFrame frame;
	private final IMessages messages;
	
	public QFServiceMenu(JFrame frame, IMessages messages) {
		super(messages);
		this.frame = frame;
		this.messages = messages;
	}
	
	private QFReactor extractReactor(EditableTerminal terminal) {
		if ( (terminal instanceof TerminalImpl) == false ) {
			throw new IllegalArgumentException("Unexpected terminal instance");
		}
		DataProvider data_provider = ((TerminalImpl) terminal).getDataProvider();
		if ( (data_provider instanceof QFReactor) == false ) {
			throw new IllegalArgumentException("Unexpected data provider instance");
		}
		return (QFReactor) data_provider;
	}

	public JMenu create(EditableTerminal terminal) {
		QFReactor reactor = extractReactor(terminal);
		JMenu menu = new JMenu(messages.get(QFortsMsg.SERVICE_MENU));
		addMenuItem(menu, QFortsMsg.SYMBOL_SUBSCRIBERS, "SYMBOL_SUBSCRIBERS_???", () -> {
			return new SSRViewDialog(frame, messages, reactor.getSymbolSubscrRepository());
		});
		return menu;
	}

}

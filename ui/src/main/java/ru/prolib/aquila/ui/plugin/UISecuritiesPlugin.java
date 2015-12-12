package ru.prolib.aquila.ui.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.form.SecurityListDialog;
import ru.prolib.aquila.ui.msg.SecurityMsg;
import ru.prolib.aquila.ui.plugin.getters.GSecurity;
import ru.prolib.aquila.ui.wrapper.*;

/**
 * Плагин отображающий доступные инструменты в виде таблицы на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UISecuritiesPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UISecuritiesPlugin implements AquilaPlugin {
	private Terminal terminal;
	private SecuritiesTableCols cols = new SecuritiesTableCols();
	private TableModel model = new TableModelImpl(new GSecurity());
	private Table tb;
	private JPanel panel = new JPanel(new BorderLayout());
	
	public UISecuritiesPlugin() {
		super();		
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public SecuritiesTableCols getTableCols() {
		return cols;
	}
	
	public void setTableCols(SecuritiesTableCols cols) {
		this.cols = cols;
	}
	
	public TableModel getModel() {
		return model;
	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String arg)
	{
		this.terminal = terminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public void createUI(final AquilaUI facade) throws Exception {
		EventSystem ev = ((ServiceLocator) facade).getEventSystem();
		EventDispatcher dispatcher = ev.createEventDispatcher();
		final JFrame frame = facade.getMainFrame();
		final IMessages messages = facade.getTexts();
		
		cols.addColumnsToModel(model, messages);

		DataSourceEventTranslator onRowAvailableListener =
			new DataSourceEventTranslator(dispatcher, dispatcher.createType());
		model.setOnRowAvailableListener(onRowAvailableListener);
		
		DataSourceEventTranslator onRowChangedListener =
			new DataSourceEventTranslator(dispatcher, dispatcher.createType());
		model.setOnRowChangedListener(onRowChangedListener);		
		
		tb = new TableImpl(model, dispatcher, dispatcher.createType());		
		tb.start();
		
		facade.addTab(messages.get(SecurityMsg.SECURITIES_TITLE), panel);
        panel.add(new JScrollPane(tb.getUnderlayed()));
		
        String menuID = SecurityMsg.SECURITIES_MENU.toString();
        facade.getMainMenu().addMenu(menuID, messages.get(SecurityMsg.SECURITIES_MENU));
        facade.getMainMenu().getMenu(menuID)
        	.addBottomItem(SecurityMsg.SHOW_SECURITIES.toString(),
        		messages.get(SecurityMsg.SHOW_SECURITIES))
        	.getUnderlyingObject().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SecurityListDialog dialog = new SecurityListDialog(frame,
							SecurityListDialog.TYPE_SELECT, messages);
					dialog.add(terminal);
					dialog.pack();
					dialog.setModal(true);
					dialog.setVisible(true);
					Security security = dialog.getSelectedSecurity();
					JOptionPane.showMessageDialog(null, "Selected: "
						+ (security == null ? null : security.getSymbol()));
				}
        });
	}
	
	@Override
	public void start() throws StarterException {
		terminal.OnSecurityAvailable()
			.addListener(model.getOnRowAvailableListener());
		terminal.OnSecurityChanged()
			.addListener(model.getOnRowChangedListener());
	}
	
	@Override
	public void stop() throws StarterException {
		terminal.OnSecurityAvailable()
			.removeListener(model.getOnRowAvailableListener());
		terminal.OnSecurityChanged()
			.removeListener(model.getOnRowChangedListener());
	}

}

package ru.prolib.aquila.ui.plugin;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.*;

/**
 * Плагин отображающий доступные инструменты в виде таблицы на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UISecuritiesPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UISecuritiesPlugin implements AquilaPlugin {
	public static final String TEXT_SECT = "MainFrame";
	public static final String TITLE = "TAB_SECURITIES";
	public static final String MENU_SECURITY = "MENU_SEC";
	
	private Terminal terminal;
	private SecuritiesTableModel model;
	
	public UISecuritiesPlugin() {
		super();
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		model = new SecuritiesTableModel(terminal, facade.getTexts());
		model.start();
		JTable table = new JTable(model);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		
		ClassLabels text = facade.getTexts().get(TEXT_SECT);
        JPanel panel = new JPanel(new BorderLayout());
		facade.addTab(text.get(TITLE), panel);
        panel.add(new JScrollPane(table));
        
        facade.getMainMenu().addMenu(MENU_SECURITY, text.get(MENU_SECURITY));
	}
	
	@Override
	public void start() throws StarterException {
		
	}
	
	@Override
	public void stop() throws StarterException {
		
	}

}

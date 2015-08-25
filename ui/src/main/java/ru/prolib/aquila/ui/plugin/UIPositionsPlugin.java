package ru.prolib.aquila.ui.plugin;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.form.PositionListTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

/**
 * Плагин отображающий открытые позиции в виде таблицы на вкладке.
 * <p>
 * 2013-02-28<br>
 * $Id: UIPositionsPlugin.java 558 2013-03-04 17:21:48Z whirlwind $
 */
public class UIPositionsPlugin implements AquilaPlugin {
	private Terminal terminal;
	private PositionListTableModel model;
	
	public UIPositionsPlugin() {
		super();
	}

	@Override
	public void start() throws StarterException {

	}

	@Override
	public void stop() throws StarterException {

	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String arg)
	{
		this.terminal = terminal;
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		model = new PositionListTableModel(facade.getTexts());
		model.add(terminal);
		model.start();
		
		JTable table = new JTable(model);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JPanel panel = new JPanel(new BorderLayout());
		facade.addTab(facade.getTexts().get(CommonMsg.POSITIONS), panel);
        panel.add(new JScrollPane(table));
	}

}

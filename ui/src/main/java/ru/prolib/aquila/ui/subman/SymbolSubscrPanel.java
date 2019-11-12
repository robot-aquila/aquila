package ru.prolib.aquila.ui.subman;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.TerminalRegistry;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class SymbolSubscrPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected final JComboBox<String> cmbTerminalList = new JComboBox<>();
	protected final JTextField txtSymbol = new JTextField(16);
	protected final JComboBox<MDLevel> cmbLevel = new JComboBox<>();
	
	public SymbolSubscrPanel(boolean horizontal, IMessages messages) {
		JLabel lblTerminalList = new JLabel(messages.get(CommonMsg.TERMINAL));
		JLabel lblSymbol = new JLabel(messages.get(CommonMsg.SYMBOL));
		JLabel lblLevel = new JLabel(messages.get(CommonMsg.MD_LEVEL));
		setLayout(new MigLayout());
		if ( horizontal ) {
			add(lblTerminalList);
			add(cmbTerminalList);
			add(lblSymbol, "gap 30");
			add(txtSymbol);
			add(lblLevel, "gap 30");
			add(cmbLevel);
		} else {
			add(lblTerminalList);
			add(cmbTerminalList, "wrap");
			add(lblSymbol);
			add(txtSymbol,"wrap");
			add(lblLevel);
			add(cmbLevel, "wrap");
		}
		cmbLevel.addItem(MDLevel.L0);
		cmbLevel.addItem(MDLevel.L1_BBO);
		cmbLevel.addItem(MDLevel.L1);
		cmbLevel.addItem(MDLevel.L2);
	}
	
	public void fillTerminalList(TerminalRegistry registry) {
		cmbTerminalList.removeAllItems();
		for ( String term_id : registry.getListIDs() ) {
			cmbTerminalList.addItem(term_id);
		}
	}
	
	public String getSelectedTermID() {
		return (String) cmbTerminalList.getSelectedItem();
	}
	
	public String getSelectedSymbol() {
		return txtSymbol.getText();
	}
	
	public MDLevel getSelectedLevel() {
		return (MDLevel) cmbLevel.getSelectedItem();
	}

}

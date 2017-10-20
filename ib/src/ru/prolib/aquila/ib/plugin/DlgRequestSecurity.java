package ru.prolib.aquila.ib.plugin;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;

/**
 * Диалог выбора торгового инструмента.
 * <p>
 * 2012-12-08<br>
 * $Id: DlgRequestSecurity.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public class DlgRequestSecurity extends JDialog {
	private static final long serialVersionUID = -749221400621590178L;
	
	private final String title = "Security";
	private final JTextField secCode = new JTextField(8);
	private final JTextField secClass = new JTextField("SMART", 8);
	private final JTextField secCurr = new JTextField("USD", 3);
	private final JComboBox secType = new JComboBox();
	private SecurityDescriptor descr = null;
	private final SecurityType secTypeList[] = {
			SecurityType.STK,
			SecurityType.BOND,
			SecurityType.CASH,
			SecurityType.FUT,
			SecurityType.OPT,
			SecurityType.UNK,
	};

	public DlgRequestSecurity(Frame owner) {
		super(owner, true);
		
		JPanel fields = new JPanel(); 
		fields.setLayout(new GridLayout(0, 2));
		fields.add(new JLabel("Symbol"));
		fields.add(secCode);
		fields.add(new JLabel("Exchange"));
		fields.add(secClass);
		fields.add(new JLabel("Currency"));
		fields.add(secCurr);
		fields.add(new JLabel("Type"));
		fields.add(secType);
		for ( int i = 0; i < secTypeList.length; i ++ ) {
			secType.addItem(secTypeList[i].getName());
		}
		secType.setSelectedIndex(0);
		
		JPanel buttons = new JPanel();
		JButton ok = new JButton("Ok"), cancel = new JButton("Cancel");
		buttons.add(ok);
		buttons.add(cancel);
		ok.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				closeDialog(true);
			} });
		cancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				closeDialog(false);
			} });
		
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		main.add(fields);
		main.add(buttons);
		
		JPanel border = new JPanel(new BorderLayout());
		border.setBorder(BorderFactory.createTitledBorder(title));
		border.add(main);
		
		getRootPane().setDefaultButton(ok);
		getContentPane().add(border);
		setResizable(false);
		setLocationRelativeTo(owner);
		setTitle(title);
		pack();
	}
	
	private void closeDialog(boolean ok) {
		if ( ok ) {
			descr = new SecurityDescriptor(secCode.getText(),
					secClass.getText(), secCurr.getText(),
					secTypeList[secType.getSelectedIndex()]);
		} else {
			descr = null;
		}
		dispose();
	}
	
	public SecurityDescriptor getDescriptor() {
		setVisible(true);
		return descr;
	}
	
}

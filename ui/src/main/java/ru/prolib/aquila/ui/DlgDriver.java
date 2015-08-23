package ru.prolib.aquila.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Диалог выбора драйвера.
 * <p>
 * $Id: DlgDriver.java 554 2013-03-01 13:43:04Z whirlwind $
 * 2013-02-28<br>
 */
public class DlgDriver extends JDialog {
	private static final long serialVersionUID = -6489802215113323182L;
	private final JComboBox drvSelect = new JComboBox();
	private String selectedDriver = null;
	
	public DlgDriver(MessageRegistry uiTexts, Set<String> drivers) {
		super((Frame) null, true);
		ClassLabels uiLabels = uiTexts.get("DlgDriver");
		
		JPanel fields = new JPanel();
		fields.setLayout(new GridLayout(0, 2));

		for ( String driver : drivers ) {
			drvSelect.addItem(driver);
		}
		drvSelect.setSelectedIndex(0);
		
		fields.add(new JLabel(uiLabels.get("LB_DRIVER")));
		fields.add(drvSelect);
		
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
		String title = uiLabels.get("T_SELECT_DRIVER");
		border.setBorder(BorderFactory.createTitledBorder(title));
		border.add(main);
		
		getRootPane().setDefaultButton(ok);
		getContentPane().add(border);
		setResizable(false);
		setTitle(title);
		pack();
	}
	
	private void closeDialog(boolean ok) {
		if ( ok ) {
			selectedDriver = (String) drvSelect.getSelectedItem();
		} else {
			selectedDriver = null;
		}
		dispose();
	}
	
	public String selectDriver() {
		Dimension desktop = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = this.getSize();
		setLocation(desktop.width / 2 - size.width / 2,
				desktop.height / 2 - size.height / 2);
		setVisible(true);
		return selectedDriver;
	}

}

package ru.prolib.aquila.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * $Id: DlgCancelOrders.java 483 2013-02-03 21:37:15Z whirlwind $
 */
public class DlgCancelOrders extends JDialog {

	private static final long serialVersionUID = 2535968713675244970L;
	private final String title = "Cancel orders";
	private boolean confirm = false;
	
	private DlgCancelOrders(Frame owner, int count) {
		super(owner, true);
		
		JPanel mssg = new JPanel();
		mssg.add(new JLabel(
				String.format("Are You sure want to cancel %d orders?", count)));
		JPanel buttons = new JPanel();
		JButton ok = new JButton("Ok"), cancel = new JButton("Cancel");
		buttons.add(ok);
		buttons.add(cancel);
		ok.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				confirm = true;
				dispose();
			} });
		cancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				confirm = false;
				dispose();
			} });
		
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		main.add(mssg);
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

	
	public static boolean confirmCancel(Frame owner, int count) {
		DlgCancelOrders dialog = new DlgCancelOrders(owner, count);
		dialog.setVisible(true);
		return dialog.confirm;		
	}
}

package ru.prolib.aquila.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Диалог ввода параметров подключеняи к IB API.
 * <p>
 * 2012-12-07<br>
 * $Id: ConfigDialog.java 481 2013-02-03 15:14:28Z whirlwind $
 */
public class ConfigDialog extends JDialog {
	private static final long serialVersionUID = -1580435448067860869L;
	
	private final JTextField textIp = new JTextField();
	private final JTextField textPort = new JTextField("4001");
	private final JTextField textClientId = new JTextField("0");
	
	public ConfigDialog(Frame owner) {
		super(owner, true);
		
		JPanel buttonsPanel = new JPanel();
		JButton buttonOk = new JButton("OK");
		buttonsPanel.add(buttonOk);
		buttonOk.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				onOk();
			}
		});
		JButton buttonCancel = new JButton("Cancel");
		buttonsPanel.add(buttonCancel);
		buttonCancel.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		
		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridLayout(0, 2));
		fieldsPanel.add(new JLabel("IP"));
		fieldsPanel.add(textIp);
		fieldsPanel.add(new JLabel("Port"));
		fieldsPanel.add(textPort);
		fieldsPanel.add(new JLabel("Client ID"));
		fieldsPanel.add(textClientId);
		
		getContentPane().add(fieldsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		setTitle("IB API connection settings");
		pack();
	}
	
	public void onOk() {
		closeDialog();
	}
	
	public void onCancel() {
		closeDialog();
	}
	
	public void closeDialog() {
		setVisible(false);
	}

}

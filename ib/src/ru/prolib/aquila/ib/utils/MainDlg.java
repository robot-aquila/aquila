package ru.prolib.aquila.ib.utils;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ib.client.Contract;

/**
 * $Id$
 */
public class MainDlg extends JPanel {

	private static final long serialVersionUID = 7916855556992776889L;
	private final Main owner;
	
	private JTextField exchange = new JTextField("SMART");
	private JTextField primaryExch = new JTextField("NYSE");
	private JTextField secType = new JTextField("STK");
	private JTextField currency = new JTextField("USD");
	private JTextField symbol = new JTextField();
	private JTextField endDateTime = new JTextField();
	private JTextField duration = new JTextField("2 D");
	private JTextField barSize = new JTextField("1 min");
	private JTextField whatToShow = new JTextField("TRADES");
	private JTextField useRTH = new JTextField("1");
	private JTextField saveDir = new JTextField();
	private JButton selectDir = new JButton("Browse");
	private JButton doImport = new JButton("Import");
	
	
	public MainDlg(Main owner) {
		super(new BorderLayout());
		this.owner = owner;
	}
	
	public void createUI() {
		JPanel fields = new JPanel(new GridLayout(0, 2));
		fields.add(new JLabel("Exchange:"));
		fields.add(exchange);
		fields.add(new JLabel("Primary exchange:"));
		fields.add(primaryExch);
		fields.add(new JLabel("Security type:"));
		fields.add(secType);
		fields.add(new JLabel("Symbol:"));
		fields.add(symbol);
		fields.add(new JLabel("Currency:"));
		fields.add(currency);
		fields.add(new JLabel("End datetime:"));
		fields.add(endDateTime);
		fields.add(new JLabel("Duration:"));
		fields.add(duration);
		fields.add(new JLabel("Bar size:"));
		fields.add(barSize);
		fields.add(new JLabel("What to show:"));
		fields.add(whatToShow);
		fields.add(new JLabel("Use RTH:"));
		fields.add(useRTH);
		fields.add(saveDir);
		fields.add(selectDir);
		add(fields, BorderLayout.CENTER);
		add(doImport, BorderLayout.SOUTH);
		
		selectDir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectDir();				
			}
			
		});
		
		doImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				doImport();				
			}			
		});
	}
	
	private void selectDir() {
		JFileChooser chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Select destination folder");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    chooser.setAcceptAllFileFilterUsed(false);
	    //    
	    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
	    	saveDir.setText(chooser.getSelectedFile().getPath());
	    }
	}
	
	private void doImport() {
		Contract contract = new Contract();
		contract.m_exchange = this.exchange.getText();
		contract.m_primaryExch = this.primaryExch.getText();
		contract.m_currency = this.currency.getText();
		contract.m_symbol = this.symbol.getText();
		contract.m_secType = this.secType.getText();
		
		String destFile = saveDir.getText() + 
				System.getProperty("file.separator") +
				this.symbol.getText() + ".csv";
		owner.doImport(contract, endDateTime.getText(), duration.getText(), 
				barSize.getText(), whatToShow.getText(), 
				useRTH.getText(), destFile);
	}
}

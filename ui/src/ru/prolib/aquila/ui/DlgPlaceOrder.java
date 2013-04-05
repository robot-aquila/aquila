package ru.prolib.aquila.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class DlgPlaceOrder extends JDialog {

	/**
	 * $Id: DlgPlaceOrder.java 491 2013-02-05 20:31:41Z huan.kaktus $
	 */
	private static final long serialVersionUID = -8795220966301347088L;
	private static Logger logger = LoggerFactory.getLogger(DlgPlaceOrder.class);
	private final String title = "Place order";
	private final Terminal terminal;
	private Order order = null;
	private final JComboBox secName = new JComboBox();
	private final JTextField qty = new JTextField("1", 4);
	private final JComboBox ordDirect = new JComboBox();
	
	private final List<Security> list;
	private final OrderDirection ordDirList[] = {
		OrderDirection.BUY,
		OrderDirection.SELL
	};
	
	private DlgPlaceOrder(Frame owner, Terminal terminal) {
		super(owner, true);
		this.terminal = terminal;
		list = terminal.getSecurities();
		
		for(int i = 0; i < list.size(); i++) {
			secName.addItem(list.get(i).getDescriptor());
		}
		if(list.size() > 0) { 
			secName.setSelectedIndex(0); 
		}
		
		for(int i = 0; i < ordDirList.length; i++) {
			ordDirect.addItem(ordDirList[i]);
		}
		ordDirect.setSelectedIndex(0);
		
		JPanel fields = new JPanel(); 
		fields.setLayout(new GridLayout(0, 2));
		fields.add(new JLabel("Security"));
		fields.add(secName);
		fields.add(new JLabel("Quantity"));
		fields.add(qty);
		fields.add(new JLabel("Direction"));
		fields.add(ordDirect);
		
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

	protected void closeDialog(boolean ok) {
		if(list.size() > 0 && ok ) {
			try {
				Security sec = list.get(secName.getSelectedIndex());
				if(ordDirList[ordDirect.getSelectedIndex()] == OrderDirection.BUY) {
					order = terminal.createMarketOrderB(
						terminal.getDefaultPortfolio().getAccount(), sec, 
						new Long(qty.getText()));
				} else {
					order = terminal.createMarketOrderS(
							terminal.getDefaultPortfolio().getAccount(), sec, 
							new Long(qty.getText()));
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		} else {
			order = null;
		}
		dispose();		
	}
	
	public static Order getOrder(Frame owner, Terminal terminal) {
		DlgPlaceOrder dialog = new DlgPlaceOrder(owner, terminal);
		dialog.setVisible(true);
		return dialog.order;
	}
}

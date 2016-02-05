package ru.prolib.aquila.ui.FastOrder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Панель быстрого выставления заявок.
 * <p>
 * Account: [^] Security: [^] Type: [^] Qty: [ ] Slippage: [ ] Place [ BUY ] or [ SELL ] order 
 */
public class FastOrderPanel extends JPanel implements Starter {
	private static final long serialVersionUID = 1L;
	private static final Logger logger;
	private static final Account ACCOUNT_PROTO;
	private static final Symbol SECURITY_PROTO;
	
	static {
		logger = LoggerFactory.getLogger(FastOrderPanel.class);
		ACCOUNT_PROTO = new Account("SPBFUT", "SPBFUTXXXXX", "SPBFUTXXXXX__");// "SPBFUT#SPBFUTXXXXX#SPBFUTXXXXX__";
		SECURITY_PROTO = new Symbol("RIU3", "SPBFUT", ISO4217.USD, SymbolType.FUTURE);// "RIU3@SPBFUT(FUT/USD)__";
	}
	
	private final Terminal terminal;
	private final AccountCombo accountCombo;
	private final SecurityCombo securityCombo;
	private final TypeCombo typeCombo;
	private final JFormattedTextField qtyField;
	private final JFormattedTextField slippageField;
	
	public FastOrderPanel(Terminal terminal) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.terminal = terminal;
		accountCombo = new AccountCombo(terminal);
		accountCombo.setPrototypeDisplayValue(ACCOUNT_PROTO);
		securityCombo = new SecurityCombo(terminal);
		securityCombo.setPrototypeDisplayValue(SECURITY_PROTO);
		typeCombo = new TypeCombo();
		qtyField = new JFormattedTextField();
		qtyField.setValue(new Integer(1));
		qtyField.setColumns(8);
		qtyField.setMinimumSize(new Dimension(50, 10));
		NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
		format.setMinimumFractionDigits(4);
		slippageField = new JFormattedTextField(format);
		slippageField.setValue(new Double(0.0d));
		slippageField.setColumns(8);
		slippageField.setMinimumSize(new Dimension(50, 10));
		
		addLabel("  Account: ");
		add(accountCombo);
		addLabel("  Security: ");
		add(securityCombo);
		addLabel("  Type: ");
		add(typeCombo);
		addLabel("  Qty.: ");
		add(qtyField);
		addLabel("  Slippage: ");
		add(slippageField);
		addLabel("   Place ");
		JButton button;
		add(button = new JButton("buy"));
		button.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				placeOrder(OrderAction.BUY);
			}
		});
		addLabel(" or ", JLabel.CENTER);
		add(button = new JButton("sell"));
		button.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				placeOrder(OrderAction.SELL);
			}
		});
		addLabel(" order");
		add(Box.createHorizontalGlue());
	}
	
	private void addLabel(String textId, int horizontalPosition) {
		JLabel label = new JLabel(textId);
		label.setVerticalTextPosition(JLabel.CENTER);
		label.setHorizontalTextPosition(horizontalPosition);
		add(label);		
	}
	
	private void addLabel(String textId) {
		addLabel(textId, JLabel.RIGHT);
	}

	@Override
	public void start() {
		accountCombo.start();
		securityCombo.start();
	}

	@Override
	public void stop() {
		securityCombo.stop();
		accountCombo.stop();
	}
	
	/**
	 * Разместить заявку.
	 * <p>
	 * @param dir направление заявки
	 */
	private void placeOrder(OrderAction dir) {
		Order order; Integer qty; Double slippage; Object value = null;
		Account account = accountCombo.getSelectedAccount();
		Security security = securityCombo.getSelectedSecurity();
		if ( account == null ) {
			logger.warn("Account was not selected");
			return;
		}
		if ( security == null ) {
			logger.warn("Security not selected");
			return;
		}
		try {
			value = qtyField.getValue();
			qty = ((Number) value).intValue();
		} catch ( Exception e ) {
			Object args[] = { value, e };
			logger.warn("Bad order qty value: {}", args);
			return;
		}
		try {
			value = slippageField.getValue();
			slippage = ((Number) value).doubleValue();
		} catch ( Exception e ) {
			Object args[] = { value, e };
			logger.warn("Bad order slippage value: {}", args);
			return;
		}

		Symbol symbol = security.getSymbol();
		if ( typeCombo.getSelectedType() == OrderType.LIMIT ) {
			Tick last = security.getLastTrade();
			if ( last == null ) {
				logger.warn("Last trade not available");
				return;
			}
			double price = last.getPrice() +
				(dir == OrderAction.BUY ? slippage : -slippage);
			order = terminal.createOrder(account, symbol, dir, qty, price);
		} else {
			order = terminal.createOrder(account, symbol, dir, qty);
		}
		try {
			terminal.placeOrder(order);
		} catch ( OrderException e ) {
			logger.error("Error place order: ", e);
		}
	}

}

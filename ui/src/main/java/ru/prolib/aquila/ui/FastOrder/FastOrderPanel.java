package ru.prolib.aquila.ui.FastOrder;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

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
import ru.prolib.aquila.ui.form.SecurityListDialog;

/**
 * Панель быстрого выставления заявок.
 * <p>
 * Account: [^] Security: [^] Type: [^] Qty: [ ] Slippage: [ ] Place [ BUY ] or [ SELL ] order 
 * <p>
 * NOTE: Untested!
 */
public class FastOrderPanel extends JPanel implements Starter {
	private static final long serialVersionUID = 1L;
	private static final Logger logger;
	private static final Account ACCOUNT_PROTO;
	private static final Symbol SECURITY_PROTO;
	
	static {
		logger = LoggerFactory.getLogger(FastOrderPanel.class);
		ACCOUNT_PROTO = new Account("SPBFUT", "SPBFUTXXXXX", "SPBFUTXXXXX__");// "SPBFUT#SPBFUTXXXXX#SPBFUTXXXXX__";
		SECURITY_PROTO = new Symbol("EUR/USD", "SPBFUT", ISO4217.USD, SymbolType.FUTURES);
	}
	
	private final Terminal terminal;
	private final SecurityListDialog securityListDialog;
	private final AccountCombo accountCombo;
	private final SecurityCombo securityCombo;
	private final JTextField securityText;
	private final TypeCombo typeCombo;
	private final JFormattedTextField qtyField;
	private final JFormattedTextField slippageField;
	private final JFormattedTextField priceField;
	private final NumberFormat priceNumberFormat;
	private boolean securityListDialogFirstTime = true;
	private Security selectedSecurity;
	
	public FastOrderPanel(Terminal terminal, SecurityListDialog securityListDialog) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.terminal = terminal;
		this.securityListDialog = securityListDialog;
		accountCombo = new AccountCombo(terminal);
		accountCombo.setPrototypeDisplayValue(ACCOUNT_PROTO);
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
		priceNumberFormat = NumberFormat.getInstance(Locale.ENGLISH);
		priceNumberFormat.setMinimumFractionDigits(5);
		priceField = new JFormattedTextField(priceNumberFormat);
		priceField.setValue(new Double(0d));
		priceField.setColumns(12);
		priceField.setMinimumSize(new Dimension(50, 10));
		JButton button;
		addLabel("  Account: ");
		add(accountCombo);
		addLabel("  Security: ");
		if ( securityListDialog != null ) {
			add(securityText = new JTextField("", SECURITY_PROTO.toString().length()));
			add(button = new JButton("..."));
			button.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent arg0) {
					showSecurityListDialog();
				}
			});
			securityText.setEnabled(false);
			securityCombo = null;
		} else {
			securityCombo = new SecurityCombo(terminal);
			securityCombo.setPrototypeDisplayValue(SECURITY_PROTO);
			securityText = null;
			add(securityCombo);
		}
		
		addLabel("  Type: ");
		add(typeCombo);
		addLabel("  Qty.: ");
		add(qtyField);
		addLabel("  Price: ");
		add(priceField);
		addLabel("  Slippage: ");
		add(slippageField);
		addLabel("   Place ");
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
	
	public FastOrderPanel(Terminal terminal) {
		this(terminal, null);
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
		if ( securityCombo != null ) {
			securityCombo.start();
		}
	}

	@Override
	public void stop() {
		if ( securityCombo != null ) {
			securityCombo.stop();
		}
		accountCombo.stop();
	}
	
	/**
	 * Get selected security.
	 * <p>
	 * @return currently selected security instance
	 */
	private Security getSecurity() {
		return securityCombo == null ? selectedSecurity : securityCombo.getSelectedSecurity();
	}
	
	/**
	 * Get selected slippage.
	 * <p>
	 * @return currently selected slippage (in price points) or null if slippage not selected or invalid value
	 */
	private CDecimal getSlippage() {
		Object value = null;
		try {
			value = slippageField.getValue();
			return of(Double.toString(((Number) value).doubleValue())).withScale(getSecurity().getScale());
		} catch ( Exception e ) {
			Object args[] = { value, e };
			logger.warn("Bad price slippage value: {}", args);
			return null;
		}
	}
	
	private CDecimal getQty() {
		Object value = null;
		try {
			value = qtyField.getValue();
			return of(((Number) value).longValue());
		} catch ( Exception e ) {
			Object args[] = { value, e };
			logger.warn("Bad qty value: {}", args);
			return null;
		}
	}
	
	private CDecimal getPrice() {
		Object value = null;
		try {
			value = priceField.getValue();
			return of(Double.toString(((Number) value).doubleValue())).withScale(getSecurity().getScale());
		} catch ( Exception e ) {
			Object args[] = { value, e };
			logger.warn("Bad price value: {}", args);
			return null;
		} 
	}
	
	/**
	 * Разместить заявку.
	 * <p>
	 * @param dir направление заявки
	 */
	private void placeOrder(OrderAction dir) {
		Order order; CDecimal qty;
		Account account = accountCombo.getSelectedAccount();
		Security security = getSecurity();
		if ( account == null ) {
			logger.warn("Account was not selected");
			return;
		}
		if ( security == null ) {
			logger.warn("Security not selected");
			return;
		}
		qty = getQty();
		Symbol symbol = security.getSymbol();
		if ( typeCombo.getSelectedType() == OrderType.LMT ) {
			CDecimal price = getPrice();
			CDecimal slippage = getSlippage();
			if ( price == null ) {
				price = security.getLastPrice();
				if ( price == null ) {
					logger.warn("Last trade not available. Cannot determine order price.");
					return;					
				}
			}
			if ( dir == OrderAction.BUY ) {
				price = price.add(slippage);
			} else {
				price = price.subtract(slippage);
			}
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
	
	private void showSecurityListDialog() {
		try {
			if ( securityListDialogFirstTime ) {
				securityListDialog.add(terminal);
				securityListDialog.setSize(1024, 768);
				securityListDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			}
			securityListDialog.setSelectedSecurity(selectedSecurity);
			securityListDialog.setModal(true);
			securityListDialog.setVisible(true);
			selectedSecurity = securityListDialog.getSelectedSecurity();
			securityText.setText(selectedSecurity == null ? "" : selectedSecurity.getSymbol().toString());
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

}

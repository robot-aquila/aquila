package ru.prolib.aquila.ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.msg.CommonMsg;

/**
 * $Id: PortfolioDataPanel.java 544 2013-02-25 14:31:32Z huan.kaktus $
 */
public class PortfolioStatusBar extends JPanel implements PortfolioStatusBarView {
	private static final long serialVersionUID = -5773959478808068827L;
	public static final String TEXT_SECT = "PortfolioDataPanel";
	private LabeledTextValue cashVal;
	private LabeledTextValue balanceVal;
	private LabeledTextValue varMargin;
	private LabeledTextValue accountVal;
	
	public PortfolioStatusBar(IMessages messages) {
		super();
		accountVal = new LabeledTextValue(messages.get(CommonMsg.ACCOUNT));
		cashVal = new LabeledTextValue(messages.get(CommonMsg.CASH));
		balanceVal = new LabeledTextValue(messages.get(CommonMsg.BALANCE));
		varMargin = new LabeledTextValue(messages.get(CommonMsg.VMARGIN));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(accountVal);
		add(cashVal);
		add(balanceVal);
		add(varMargin);
	}
	
	@Override
	public void updateDisplayData(final Portfolio portfolio) {
		if ( SwingUtilities.isEventDispatchThread() ) {
			accountVal.setValue(String.format("%-40s", portfolio.getAccount()));
			cashVal.setValue(portfolio.getFreeMargin().toString());
			balanceVal.setValue(portfolio.getBalance().toString());
			varMargin.setValue(portfolio.getProfitAndLoss().toString());
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					updateDisplayData(portfolio);
				}
			});
		}
	}

	public LabeledTextValue getAccount() {
		return accountVal;
	}
	
	public LabeledTextValue getVarMargin() {
		return varMargin;
	}
	
	public LabeledTextValue getBalanceVal() {
		return balanceVal;
	}
	
	public LabeledTextValue getCashVal() {
		return cashVal;
	}

}

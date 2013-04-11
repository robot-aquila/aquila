package ru.prolib.aquila.ui;

import java.awt.FlowLayout;
import javax.swing.JPanel;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioEvent;

public class PortfolioDataPanel extends JPanel implements EventListener, Starter {

	/**
	 * $Id: PortfolioDataPanel.java 544 2013-02-25 14:31:32Z huan.kaktus $
	 */
	private static final long serialVersionUID = -5773959478808068827L;
	public static final String TEXT_SECT = "PortfolioDataPanel";
	private LabeledTextValue cashVal;
	private LabeledTextValue balanceVal;
	private LabeledTextValue varMargin;
	private LabeledTextValue accountVal;
	
	private final CurrentPortfolio currPortfolio;
	
	
	public PortfolioDataPanel(CurrentPortfolio currPrt, UiTexts uiTexts) {
		super();
		currPortfolio = currPrt;
		accountVal = new LabeledTextValue(uiTexts.get(TEXT_SECT).get("LB_ACCOUNT"));
		cashVal = new LabeledTextValue(uiTexts.get(TEXT_SECT).get("LB_CASH"));
		balanceVal = new LabeledTextValue(uiTexts.get(TEXT_SECT).get("LB_BALANCE"));
		varMargin = new LabeledTextValue(uiTexts.get(TEXT_SECT).get("LB_VAR_MARGIN"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(accountVal);
		add(cashVal);
		add(balanceVal);
		add(varMargin);
	}	
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#start()
	 */
	@Override
	public void start() throws StarterException {
		currPortfolio.OnCurrentPortfolioChanged().addListener(this);
		currPortfolio.start();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#stop()
	 */
	@Override
	public void stop() throws StarterException {
		currPortfolio.stop();
		currPortfolio.OnCurrentPortfolioChanged().removeListener(this);
	}
	
	private void updateDisplayData(Portfolio portfolio) {
		accountVal.setValue(portfolio.getAccount().getCode());
		cashVal.setValue(
				String.format("%.2f", portfolio.getCash()));
		balanceVal.setValue(
				String.format("%.2f", portfolio.getBalance()));
		varMargin.setValue(
				String.format("%.2f",portfolio.getVariationMargin()));
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if(event.isType(currPortfolio.OnCurrentPortfolioChanged())) {
			PortfolioEvent e = (PortfolioEvent) event;
			Portfolio portfolio = e.getPortfolio();
			updateDisplayData(portfolio);
			portfolio.OnChanged().addListener(this);
		}else if (event.isType(currPortfolio.getCurrentPortfolio().OnChanged())) {
			updateDisplayData(currPortfolio.getCurrentPortfolio());
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
	
	public CurrentPortfolio getCurrPortfolio() {
		return currPortfolio;
	}
}

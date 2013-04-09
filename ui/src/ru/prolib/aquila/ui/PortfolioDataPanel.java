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
	private LabeledTextValue cashVal;
	private LabeledTextValue balanceVal;
	private LabeledTextValue varMargin;
	
	private final CurrentPortfolio currPortfolio;
	
	
	public PortfolioDataPanel(CurrentPortfolio currPrt, UiTexts uiTexts) {
		super();
		currPortfolio = currPrt;
		cashVal = new LabeledTextValue(uiTexts.get("PortfolioDataPanel").get("LB_CASH"));
		balanceVal = new LabeledTextValue(uiTexts.get("PortfolioDataPanel").get("LB_BALANCE"));
		varMargin = new LabeledTextValue(uiTexts.get("PortfolioDataPanel").get("LB_VAR_MARGIN"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
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
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#stop()
	 */
	@Override
	public void stop() throws StarterException {
		currPortfolio.OnCurrentPortfolioChanged().removeListener(this);
	}
	
	private void updateDisplayData(Portfolio portfolio) {
		cashVal.setValue(
				String.format("%.2f", portfolio.getCash()));
		balanceVal.setValue(
				String.format("%.2f", portfolio.getBalance()));
		varMargin.setValue(
				String.format("%s",portfolio.getVariationMargin()));
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if(event.isType(currPortfolio.OnCurrentPortfolioChanged())) {
			PortfolioEvent e = (PortfolioEvent) event;
			updateDisplayData(e.getPortfolio());
		}
		
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

package ru.prolib.aquila.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioEvent;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;
import ru.prolib.aquila.core.BusinessEntities.Portfolios;

public class PortfolioDataPanel extends JPanel implements EventListener, Starter {

	/**
	 * $Id: PortfolioDataPanel.java 544 2013-02-25 14:31:32Z huan.kaktus $
	 */
	private static final long serialVersionUID = -5773959478808068827L;
	private static Logger logger = LoggerFactory.getLogger(PortfolioDataPanel.class);
	private final Portfolios portfolios;
	private LabeledTextValue cashVal;// = new LabeledTextValue("Cash: ");
	private LabeledTextValue balanceVal;// = new LabeledTextValue("Balance: ");
	
	private final JComboBox prtSelect = new JComboBox();
	private final CurrentPortfolio currPortfolio;
	
	
	public PortfolioDataPanel(Portfolios prts, CurrentPortfolio currPrt, UiTexts uiTexts) {
		super();
		portfolios = prts;
		currPortfolio = currPrt;
		cashVal = new LabeledTextValue(uiTexts.get("PortfolioDataPanel").get("LB_CASH"));
		balanceVal = new LabeledTextValue(uiTexts.get("PortfolioDataPanel").get("LB_BALANCE"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel select = new JPanel(new GridLayout(0, 2));
		select.add(new JLabel(uiTexts.get("PortfolioDataPanel").get("LB_ACCOUNT")));
		select.add(prtSelect);
		add(select);
		add(cashVal);
		add(balanceVal);
		
		prtSelect.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
			    	 Account item = (Account) event.getItem();
			    	 removeCurrentListener();
			    	 try {
						currPortfolio.setCurrentPortfolio(portfolios.getPortfolio(item));
						addCurrentListener();
						updateDisplayData();
					} catch (PortfolioException e) {
						logger.error("Portfolio exception: ", e);
					}
			    }
			}			
		});
	}	

	public void start() {		
		portfolios.OnPortfolioAvailable().addListener(this);
	}
	
	public void stop() {
		portfolios.OnPortfolioAvailable().removeListener(this);
		removeCurrentListener();
	}
	
	public void onEvent(Event event) {
		if ( event.isType(portfolios.OnPortfolioAvailable()) ) {			
			try {
				if(currPortfolio.getCurrentPortfolio() == null) {
					currPortfolio.setCurrentPortfolio(portfolios.getDefaultPortfolio());
					addCurrentListener();
					updateDisplayData();
				}				
				fillPrtSelect();
			} catch(PortfolioException e) {
				logger.error("PortfolioException: ", e);
			}
			
		} else if ( event instanceof PortfolioEvent ) {
			if ( event.isType(currPortfolio.getCurrentPortfolio().OnChanged()) ) {
				updateDisplayData();
			}
		}
	}
	
	private void removeCurrentListener() {
		if(currPortfolio.getCurrentPortfolio() != null) {
			currPortfolio.getCurrentPortfolio().OnChanged().removeListener(this);
		}
	}
	
	private void addCurrentListener() {
		if(currPortfolio.getCurrentPortfolio() != null) {
			currPortfolio.getCurrentPortfolio().OnChanged().addListener(this);
		}
	}
	
	private void fillPrtSelect() throws PortfolioException {
		List<Portfolio> prtfs = portfolios.getPortfolios();	
		prtSelect.removeAllItems();
		for(int i = 0; i < prtfs.size(); i++) {
			Portfolio prt = prtfs.get(i);
			prtSelect.addItem(prt.getAccount());
		}
		prtSelect.setSelectedItem(currPortfolio.getCurrentPortfolio().getAccount());
	}
	
	private void updateDisplayData() {
		cashVal.setValue(
				String.format("%.2f", currPortfolio.getCurrentPortfolio().getCash()));
		balanceVal.setValue(
				String.format("%.2f", currPortfolio.getCurrentPortfolio().getBalance()));
	}
}

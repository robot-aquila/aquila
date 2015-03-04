package ru.prolib.aquila.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ui.wrapper.*;

public class CurrentPortfolioImpl implements CurrentPortfolio {
	private static Logger logger = LoggerFactory.getLogger(CurrentPortfolioImpl.class);
	private final EventTypeSI portfolioChanged;
	private final EventDispatcher dispatcher;
	private Terminal portfolios;
	private Portfolio portfolio;
	private ButtonGroup buttons = new ButtonGroup();
	private Menu menu;
	
	private Map<EventType, Portfolio> prtList = new HashMap<EventType, Portfolio>();
	
	public CurrentPortfolioImpl(
			Terminal portfolios, EventTypeSI portfolioChanged, 
			EventDispatcher dispatcher, Menu menu) 
	{
		this.portfolioChanged = portfolioChanged;
		this.dispatcher = dispatcher;
		this.portfolios = portfolios;
		this.menu = menu;
	}
	
	@Override
	public Portfolio getCurrentPortfolio() {
		return portfolio;
	}

	@Override
	public void setCurrentPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
		try {
			menu.getItem(portfolio.getAccount().toString())
				.getUnderlyingObject().setSelected(true);
		} catch (MenuException e) {
			logger.error("MenuItem ERROR: {}", e.getMessage());
		}
		fireCurrentPortfolioChangedEvent();
	}

	@Override
	public EventType OnCurrentPortfolioChanged() {		
		return portfolioChanged;
	}
	
	private void fireCurrentPortfolioChangedEvent() {
		dispatcher.dispatch(new PortfolioEvent(portfolioChanged, portfolio));
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#start()
	 */
	@Override
	public void start() throws StarterException {
		portfolios.OnPortfolioAvailable().addListener(this);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.Starter#stop()
	 */
	@Override
	public void stop() throws StarterException {
		portfolios.OnPortfolioAvailable().removeListener(this);
		
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if(event.isType(portfolios.OnPortfolioAvailable())) {
			PortfolioEvent e = (PortfolioEvent) event;
			try {
				addMenuItem(e.getPortfolio());
			} catch (MenuException ex) {
				logger.error(ex.getMessage());
			}
			if(portfolio == null) {				
				setCurrentPortfolio(e.getPortfolio());
			}
		}else {
			EventType type = event.getType();
			if(prtList.containsKey(type)) {	
				setCurrentPortfolio(prtList.get(type));
			}
		}
	}
	
	private void addMenuItem(Portfolio prt) throws MenuException {
		String id = prt.getAccount().toString();
		MenuItem m = menu.addItem(id, id, new JRadioButtonMenuItem());
		buttons.add( m.getUnderlyingObject());
		EventType evt = m.OnCommand();
		evt.addListener(this);
		prtList.put(evt, prt);
	}
	
	public Terminal getPortfolios() {
		return portfolios;
	}
	
	public EventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public void setPortfolio(Portfolio prt) {
		portfolio = prt;
	}
	
	public Menu getMenu() {
		return menu;
	}
	
	public Map<EventType, Portfolio> getPrtList() {
		return prtList;
	}
	
	public void setButtons(ButtonGroup buttons) {
		this.buttons = buttons;
	}
	
	public ButtonGroup getButtons() {
		return buttons;
	}

}

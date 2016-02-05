package ru.prolib.aquila.ui.FastOrder;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Селектор торгового счета.
 */
public class AccountCombo extends JComboBox<Account> implements Starter, EventListener {
	private static final long serialVersionUID = -7501535410571675108L;
	private final Terminal terminal;
	private final Vector<Portfolio> list = new Vector<Portfolio>();

	public AccountCombo(Terminal portfolios) {
		super();
		this.terminal = portfolios;
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(terminal.onPortfolioAvailable()) ) {
			addPortfolio(((PortfolioEvent) event).getPortfolio());
		}
	}

	@Override
	public void start() {
		terminal.onPortfolioAvailable().addListener(this);
		list.clear();
		removeAllItems();
		for ( Portfolio portfolio : terminal.getPortfolios() ) {
			addPortfolio(portfolio);
		}
	}

	@Override
	public void stop() {
		terminal.onPortfolioAvailable().removeListener(this);
	}
	
	/**
	 * Добавить портфель для выбора.
	 * <p>
	 * @param portfolio портфель
	 */
	private void addPortfolio(final Portfolio portfolio) {
		if ( ! list.contains(portfolio) ) {
			if ( SwingUtilities.isEventDispatchThread() ) {
				list.add(portfolio);
				addItem(portfolio.getAccount());
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override public void run() {
						addPortfolio(portfolio);
					}
				});				
			}
		}
	}
	
	/**
	 * Получить торговый счет выбранного портфеля.
	 * <p>
	 * @return торговый счет или null, если портфель не выбран
	 */
	public Account getSelectedAccount() {
		Portfolio portfolio = getSelectedPortfolio();
		return portfolio != null ? portfolio.getAccount() : null;
	}
	
	/**
	 * Получить экземпляр выбранного портфеля.
	 * <p>
	 * @return выбранный портфель или null, если портфель не выбран
	 */
	public Portfolio getSelectedPortfolio() {
		int index = getSelectedIndex();
		return index >= 0 ? list.get(index) : null;
	}
	
	/**
	 * Проверить факт выбора портфеля.
	 * <p>
	 * @return true - портфель выбран, false - портфель не выборан
	 */
	public boolean isSelected() {
		return getSelectedPortfolio() != null;
	}

}

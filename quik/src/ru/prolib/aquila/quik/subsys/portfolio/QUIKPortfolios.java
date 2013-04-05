package ru.prolib.aquila.quik.subsys.portfolio;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolios;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;

/**
 * Набор портфелей.
 * <p>
 * Методы, использующие экземпляры счета в качестве аргумента, фиксят счет
 * с целью игнорирования суб-кода счета. Суб-код счета не используется для
 * идентификации портфеля, хотя в позициях и в заявках счета содержат суб-коды. 
 * <p>
 * 2013-01-24<br>
 * $Id: QUIKPortfolios.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public class QUIKPortfolios implements EditablePortfolios {
	private final EditablePortfolios portfolios;
	
	public QUIKPortfolios(EditablePortfolios portfolios) {
		super();
		this.portfolios = portfolios;
	}
	
	public EditablePortfolios getPortfoliosInstance() {
		return portfolios;
	}

	@Override
	public EventType OnPortfolioAvailable() {
		return portfolios.OnPortfolioAvailable();
	}

	@Override
	public EventType OnPortfolioChanged() {
		return portfolios.OnPortfolioChanged();
	}

	@Override
	public EventType OnPositionAvailable() {
		return portfolios.OnPositionAvailable();
	}

	@Override
	public EventType OnPositionChanged() {
		return portfolios.OnPositionChanged();
	}

	@Override
	public Portfolio getDefaultPortfolio() throws PortfolioException {
		return portfolios.getDefaultPortfolio();
	}

	@Override
	public Portfolio getPortfolio(Account account) throws PortfolioException {
		return portfolios.getPortfolio(fixAccount(account));
	}

	@Override
	public List<Portfolio> getPortfolios() {
		return portfolios.getPortfolios();
	}

	@Override
	public int getPortfoliosCount() {
		return portfolios.getPortfoliosCount();
	}

	@Override
	public boolean isPortfolioAvailable(Account account) {
		return portfolios.isPortfolioAvailable(fixAccount(account));
	}

	@Override
	public void firePortfolioAvailableEvent(Portfolio portfolio) {
		portfolios.firePortfolioAvailableEvent(portfolio);
	}

	@Override
	public EditablePortfolio getEditablePortfolio(Account account)
			throws PortfolioException
	{
		return portfolios.getEditablePortfolio(fixAccount(account));
	}

	@Override
	public void registerPortfolio(EditablePortfolio portfolio)
			throws PortfolioException
	{
		portfolios.registerPortfolio(portfolio);
	}

	@Override
	public void setDefaultPortfolio(EditablePortfolio portfolio) {
		portfolios.setDefaultPortfolio(portfolio);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == QUIKPortfolios.class ) {
			QUIKPortfolios o = (QUIKPortfolios) other;
			return new EqualsBuilder()
				.append(portfolios, o.portfolios)
				.isEquals();
		} else {
			return false;
		}
	}
	
	private Account fixAccount(Account account) {
		return new Account(account.getCode(), account.getSubCode());
	}

}

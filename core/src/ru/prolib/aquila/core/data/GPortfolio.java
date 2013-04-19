package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Геттер экземпляра портфеля.
 * <p>
 * Использует геттер кода портфеля и набор портфелей для получения экземпляра.
 * Возвращает null, если код портфеля или экземпляр портфеля не определен.
 * <p>
 * 2012-09-27<br>
 * $Id: GPortfolio.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class GPortfolio implements G<Portfolio> {
	private final G<Account> gAcc;
	private final Portfolios portfolios;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param gAcc геттер счета портфеля
	 * @param portfolios набор портфелей
	 */
	public GPortfolio(G<Account> gAcc, Portfolios portfolios) {
		super();
		this.gAcc = gAcc;
		this.portfolios = portfolios;
	}
	
	/**
	 * Получить геттер счета портфеля.
	 * <p>
	 * @return геттер
	 */
	public G<Account> getAccountGetter() {
		return gAcc;
	}
	
	/**
	 * Получить набор портфелей.
	 * <p>
	 * @return набор портфелей
	 */
	public Portfolios getPortfolios() {
		return portfolios;
	}

	@Override
	public Portfolio get(Object object) throws ValueException {
		Account account = gAcc.get(object);
		if ( account != null && portfolios.isPortfolioAvailable(account) ) {
			try {
				return portfolios.getPortfolio(account);
			} catch ( PortfolioException e ) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == GPortfolio.class ) {
			return new EqualsBuilder()
				.append(gAcc, ((GPortfolio) other).gAcc)
				.append(portfolios, ((GPortfolio) other).portfolios)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, /*0*/60015)
			.append(gAcc)
			.append(portfolios)
			.toHashCode();
	}

}

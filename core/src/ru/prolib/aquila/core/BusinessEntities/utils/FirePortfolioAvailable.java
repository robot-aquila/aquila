package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Editable;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolios;
import ru.prolib.aquila.core.BusinessEntities.FireEditableEvent;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;

/**
 * Генератор события: доступен новый портфель.
 * <p>
 * 2012-11-29<br>
 * $Id: FireEventPortfolioAvailable.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public class FirePortfolioAvailable implements FireEditableEvent {
	private final EditablePortfolios portfolios;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param portfolios набор портфелей
	 */
	public FirePortfolioAvailable(EditablePortfolios portfolios) {
		super();
		this.portfolios = portfolios;
	}
	
	/**
	 * Получить хранилище портфелей.
	 * <p>
	 * @return хранилище портфелей
	 */
	public EditablePortfolios getPortfolios() {
		return portfolios;
	}

	/**
	 * Генерировать событие о доступности нового портфеля.
	 * <p>
	 * @param object экземпляр портфеля (ожидается {@link
	 * ru.prolib.aquila.core.BusinessEntities.EditablePortfolio
	 * EditablePortfolio}).
	 */
	@Override
	public void fireEvent(Editable object) {
		portfolios.firePortfolioAvailableEvent((Portfolio) object);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == getClass() ) {
			FirePortfolioAvailable o = (FirePortfolioAvailable) other;
			return new EqualsBuilder()
				.append(portfolios, o.portfolios)
				.isEquals();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121129, 164831)
			.append(portfolios)
			.toHashCode();
	}
	

}

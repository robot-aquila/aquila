package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Editable;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolios;
import ru.prolib.aquila.core.BusinessEntities.FireEditableEvent;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;
import ru.prolib.aquila.core.BusinessEntities.Position;

/**
 * Генератор события: доступна новая позиция.
 * <p>
 * Данная реализация использует набор портфелей для получения портфеля,
 * соответствующего позиции. В связи с этим, алгоритм требует наличия
 * установленного счета позиции. В противном случае запрос портфеля завершится
 * возбуждением соответствующего исключения.
 * <p>
 * 2013-01-06<br>
 * $Id: FireEventPositionAvailableAuto.java 497 2013-02-06 18:56:51Z whirlwind $
 */
public class FirePositionAvailableAuto implements FireEditableEvent {
	private static final Logger logger;
	private final EditablePortfolios portfolios;
	
	static {
		logger = LoggerFactory.getLogger(FirePositionAvailableAuto.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param portfolios набор портфелей
	 */
	public FirePositionAvailableAuto(EditablePortfolios portfolios) {
		super();
		this.portfolios = portfolios;
	}
	
	/**
	 * Получить набор портфелей.
	 * <p>
	 * @return набор портфелей
	 */
	public EditablePortfolios getPortfolios() {
		return portfolios;
	}

	@Override
	public void fireEvent(Editable object) {
		Position position = (Position) object;
		try {
			portfolios.getEditablePortfolio(position.getAccount())
				.firePositionAvailableEvent(position);
		} catch ( PortfolioException e ) {
			logger.error("Couldn't fire position event: ", e);
			// TODO: This is PANIC state
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == FirePositionAvailableAuto.class ?
					fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		FirePositionAvailableAuto o =
				(FirePositionAvailableAuto) other;
		return new EqualsBuilder()
			.append(portfolios, o.portfolios)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 192739)
			.append(portfolios)
			.toHashCode();
	}

}

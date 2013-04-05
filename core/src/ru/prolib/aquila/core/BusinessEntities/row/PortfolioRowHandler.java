package ru.prolib.aquila.core.BusinessEntities.row;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfolioFactory;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.row.*;

/**
 * Обработчик стандартного ряда с данными портфеля.
 * <p>
 * Обработчик запрашивает у ряда объект счета, обращаясь к соответствующему
 * {@link ru.prolib.aquila.core.BusinessEntities.row.Spec#PORT_ACCOUNT
 * Spec#PORT_ACCOUNT} элементу ряда. Отсутствие счета приводит к генерации
 * события о паническом состоянии. Если портфель, соответствующий указанному
 * счету еще не создан, для инстанцирования экземпляра используется фабрика. 
 * <p>
 * 2012-09-07<br>
 * $Id$
 */
public class PortfolioRowHandler implements RowHandler {
	private final EditableTerminal terminal;
	private final PortfolioFactory factory;
	private final S<EditablePortfolio> modifier;
	
	/**
	 * Создать обработчик.
	 * <p>
	 * @param terminal терминал
	 * @param factory фабрика экземпляров портфелей
	 * @param modifier мутатор портфеля
	 */
	public PortfolioRowHandler(EditableTerminal terminal,
			PortfolioFactory factory, S<EditablePortfolio> modifier)
	{
		super();
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		if ( factory == null ) {
			throw new NullPointerException("Factory cannot be null");
		}
		if ( modifier == null ) {
			throw new NullPointerException("Mutator cannot be null");
		}
		this.terminal = terminal;
		this.factory = factory;
		this.modifier = modifier;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить фабрику экземпляров портфелей.
	 * <p>
	 * @return фабрика портфелей
	 */
	public PortfolioFactory getPortfolioFactory() {
		return factory;
	}
	
	/**
	 * Получить модификатор портфеля.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePortfolio> getPortfolioModifier() {
		return modifier;
	}

	@Override
	public void handle(Row row) {
		EditablePortfolio portfolio = null; 
		Account account = (Account) row.get(Spec.PORT_ACCOUNT);
		if ( account == null ) {
			terminal.firePanicEvent(1, "Cannot handle portfolio: NULL account");
			return;
		}
		try {
			if ( terminal.isPortfolioAvailable(account) ) {
				portfolio = terminal.getEditablePortfolio(account);
			} else {
				portfolio = factory.createPortfolio(account);
				terminal.registerPortfolio(portfolio);
			}
		} catch ( PortfolioException e ) {
			throw new RuntimeException(e);
		}
		synchronized ( portfolio ) {
			modifier.set(portfolio, row);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof PortfolioRowHandler ) {
			PortfolioRowHandler o = (PortfolioRowHandler) other;
			return new EqualsBuilder()
				.append(terminal, o.terminal)
				.append(factory, o.factory)
				.append(modifier, o.modifier)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121109, 150349)
			.append(terminal)
			.append(factory)
			.append(modifier)
			.toHashCode();
	}

}

package ru.prolib.aquila.core.BusinessEntities.row;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
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
 * TODO: удалить после того, как в связанных проектах использование будет
 * заменено на самостоятельную реализацию. 
 * <p>
 * 2012-09-07<br>
 * $Id$
 */
@Deprecated
public class PortfolioRowHandler implements RowHandler {
	private final EditableTerminal terminal;
	private final S<EditablePortfolio> modifier;
	
	/**
	 * Создать обработчик.
	 * <p>
	 * @param terminal терминал
	 * @param modifier мутатор портфеля
	 */
	public PortfolioRowHandler(EditableTerminal terminal,
			S<EditablePortfolio> modifier)
	{
		super();
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		if ( modifier == null ) {
			throw new NullPointerException("Mutator cannot be null");
		}
		this.terminal = terminal;
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
	 * Получить модификатор портфеля.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePortfolio> getPortfolioModifier() {
		return modifier;
	}

	@Override
	public void handle(Row row) throws RowException {
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
				portfolio = terminal.createPortfolio(account);
			}
		} catch ( PortfolioException e ) {
			throw new RuntimeException(e);
		}
		synchronized ( portfolio ) {
			try {
				modifier.set(portfolio, row);
			} catch ( ValueException e ) {
				throw new RowException(e);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == PortfolioRowHandler.class ) {
			PortfolioRowHandler o = (PortfolioRowHandler) other;
			return new EqualsBuilder()
				.append(terminal, o.terminal)
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
			.append(modifier)
			.toHashCode();
	}

}

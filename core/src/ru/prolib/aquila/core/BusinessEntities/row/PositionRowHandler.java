package ru.prolib.aquila.core.BusinessEntities.row;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.*;

/**
 * Обработчик ряда с данными позиции.
 * <p>
 * Обработчик запрашивает у ряда объекты счета и дескриптора инструмента,
 * обращаясь соответственно к элементам
 * {@link ru.prolib.aquila.core.BusinessEntities.row.Spec#POS_ACCOUNT
 * Spec#POS_ACCOUNT} и
 * {@link ru.prolib.aquila.core.BusinessEntities.row.Spec#POS_SECDESCR
 * Spec#POS_SECDESCR}.
 * Использует терминал для определения экземпляра портфеля и доступа к
 * набору позиции соответствующего портфеля.
 * <p>
 * TODO: удалить после того, как в связанных проектах использование будет
 * заменено на самостоятельную реализацию.
 * <p>
 * 2012-09-17<br>
 * $Id: PositionRowHandler.java 527 2013-02-14 15:14:09Z whirlwind $
 */
@Deprecated
public class PositionRowHandler implements RowHandler {
	private final EditableTerminal terminal;
	private final S<EditablePosition> modifier;
	
	/**
	 * Создать обработчик ряда.
	 * <p>
	 * @param terminal терминал
	 * @param modifier мутатор позиции
	 */
	public PositionRowHandler(EditableTerminal terminal,
			S<EditablePosition> modifier)
	{
		super();
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		this.terminal = terminal;
		if ( modifier == null ) {
			throw new NullPointerException("Mutator cannot be null");
		}
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
	 * Получить модификатор позиции.
	 * <p>
	 * @return модификатор позиции
	 */
	public S<EditablePosition> getPositionModifier() {
		return modifier;
	}

	@Override
	public void handle(Row row) throws RowException {
		EditablePosition position = null;
		Account account = (Account) row.get(Spec.POS_ACCOUNT);
		SecurityDescriptor descr =
			(SecurityDescriptor) row.get(Spec.POS_SECDESCR);
		String msg = "Cannot handle position: ";
		if ( account == null ) {
			terminal.firePanicEvent(1, msg + "account is NULL");
			return;
		}
		if ( descr == null ) {
			terminal.firePanicEvent(1, msg + "security descriptor is NULL");
			return;
		}
		try {
			position = terminal.getEditablePortfolio(account)
				.getEditablePosition(descr);
			position.setAccount(account);
		} catch ( PortfolioException e ) {
			throw new RuntimeException(e);
		}
		synchronized ( position ) {
			try {
				modifier.set(position, row);
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
		if ( other != null && other.getClass() == PositionRowHandler.class ) {
			PositionRowHandler o = (PositionRowHandler) other;
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
		return new HashCodeBuilder(20121109, 153109)
			.append(terminal)
			.append(modifier)
			.toHashCode();
	}

}

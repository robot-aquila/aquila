package ru.prolib.aquila.core.BusinessEntities.row;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.*;

/**
 * Типовой обработчик ряда с данными инструмента.
 * <p>
 * Обработчик запрашивает у ряда дескриптор инструмента под идентификатором
 * {@link ru.prolib.aquila.core.BusinessEntities.row.Spec#SEC_DESCR
 * Spec#SEC_DESCR}. Использует терминал для определения экземпляра инструмента.   
 * <p>
 * 2012-08-13<br>
 * $Id: SecurityRowHandler.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class SecurityRowHandler implements RowHandler {
	private final EditableTerminal terminal;
	private final S<EditableSecurity> modifier;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal набор инструментов
	 * @param modifier модификатор атрибутов
	 */
	public SecurityRowHandler(EditableTerminal terminal,
							  S<EditableSecurity> modifier)
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
	 * Получить мутатор инструмента.
	 * <p>
	 * @return мутатор
	 */
	public S<EditableSecurity> getModifier() {
		return modifier;
	}

	@Override
	public void handle(Row row) {
		String msg = "Cannot handle security: ";
		SecurityDescriptor descr = (SecurityDescriptor) row.get(Spec.SEC_DESCR);
		if ( descr == null ) {
			terminal.firePanicEvent(1, msg + "descriptor is NULL");
			return;
		}
		EditableSecurity security = terminal.getEditableSecurity(descr);
		synchronized ( security ) {
			modifier.set(security, row);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof SecurityRowHandler ) {
			SecurityRowHandler o = (SecurityRowHandler) other;
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
		return new HashCodeBuilder(20121109, 155847)
			.append(terminal)
			.append(modifier)
			.toHashCode();
	}

}

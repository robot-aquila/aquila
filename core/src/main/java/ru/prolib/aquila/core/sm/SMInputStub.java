package ru.prolib.aquila.core.sm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Вход-заглушка.
 * <p>
 * Используется для жесткого связывания входа с дескриптором выхода.
 */
public class SMInputStub implements SMInputAction {
	private final SMExit exit;

	/**
	 * Конструктор.
	 * <p>
	 * @param exit дескриптор выхода
	 */
	public SMInputStub(SMExit exit) {
		super();
		this.exit = exit;
	}

	@Override
	public SMExit input(Object data) {
		return exit;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(64876529, 905)
				.append(exit)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SMInputStub.class ) {
			return false;
		}
		SMInputStub o = (SMInputStub) other;
		return new EqualsBuilder()
				.append(o.exit, exit)
				.build();
	}

}

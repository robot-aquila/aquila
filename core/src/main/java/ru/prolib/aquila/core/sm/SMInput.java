package ru.prolib.aquila.core.sm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Вход (приемник данных).
 * <p>
 * Дескриптор приема данных.
 */
public class SMInput {
	private final SMStateHandler owner;
	private final SMInputAction action;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param owner состояние-владелец
	 * @param action функция
	 */
	public SMInput(SMStateHandler owner, SMInputAction action) {
		super();
		this.owner = owner;
		this.action = action;
	}

	/**
	 * Обработчик ввода данных.
	 * <p>
	 * @param data данные
	 * @return дескриптор выхода или null, если следует оставаться в текущем
	 * состоянии
	 */
	public SMExit input(Object data) {
		return action.input(data);
	}
	
	/**
	 * Получить состояние.
	 * <p>
	 * @return состояние, которому принадлежит данный вход
	 */
	public SMStateHandler getState() {
		return owner;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(55689123, 65)
				.append(owner)
				.append(action)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SMInput.class ) {
			return false;
		}
		SMInput o = (SMInput) other;
		return new EqualsBuilder()
				.append(o.owner, owner)
				.append(o.action, action)
				.build();
	}

}

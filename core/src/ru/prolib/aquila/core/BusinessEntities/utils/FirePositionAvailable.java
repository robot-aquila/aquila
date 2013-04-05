package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Editable;
import ru.prolib.aquila.core.BusinessEntities.EditablePositions;
import ru.prolib.aquila.core.BusinessEntities.FireEditableEvent;
import ru.prolib.aquila.core.BusinessEntities.Position;

/**
 * Генератор события: доступна новая позиция.
 * <p>
 * Для генерации события данный класс использует предустановленный экземпляр
 * набора позиций. Это позволяет гарантированно генерировать событие независимо
 * от состояния объекта-позиции. 
 * <p>
 * 2012-12-03<br>
 * $Id: FireEventPositionAvailable.java 397 2013-01-06 15:29:12Z whirlwind $
 */
public class FirePositionAvailable implements FireEditableEvent {
	private final EditablePositions positions;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param positions набор позиций
	 */
	public FirePositionAvailable(EditablePositions positions) {
		super();
		this.positions = positions;
	}
	
	/**
	 * Получить набор позиций.
	 * <p>
	 * @return набор позиций
	 */
	public EditablePositions getPositions() {
		return positions;
	}

	/**
	 * Генерировать событие о доступности новой позиции.
	 * <p>
	 * @param object экземпляр позиции (ожидается {@link
	 * ru.prolib.aquila.core.BusinessEntities.EditablePosition
	 * EditablePosition}).
	 */
	@Override
	public void fireEvent(Editable object) {
		positions.firePositionAvailableEvent((Position) object);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == getClass()
			&& fieldsEquals(other);
	}
	
	protected boolean fieldsEquals(Object other) {
		FirePositionAvailable o = (FirePositionAvailable) other;
		return new EqualsBuilder()
			.append(positions, o.positions)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121203, 35623)
			.append(positions)
			.toHashCode();
	}

}

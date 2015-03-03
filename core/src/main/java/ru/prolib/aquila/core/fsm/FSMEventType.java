package ru.prolib.aquila.core.fsm;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Тип выходного события состояния КА.
 * <p>
 * Выходное событие позволяет унифицировать реакцию конечного автомата на
 * изменение состояния. От стандартного типа события данный тип отличается
 * только ссылкой на актора-состояния владельца. Это позволяет более кратко
 * аргументировать в процессе составления графа переходов.
 */
public class FSMEventType extends EventTypeImpl {
	private final FSMStateActor owner;

	/**
	 * Конструктор (полный).
	 * <p>
	 * @param owner актор-владелец
	 * @param id идентификатор типа событий
	 */
	public FSMEventType(FSMStateActor owner, String id) {
		super(id, true);
		this.owner = owner;
	}
	
	/**
	 * Конструктор (краткий).
	 * <p>
	 * Использует генерацию идентификатора из реализации {@link EventTypeImpl}.
	 * <p>
	 * @param owner актор-владелец
	 */
	public FSMEventType(FSMStateActor owner) {
		this(owner, EventTypeImpl.nextId());
	}
	
	/**
	 * Получить экземпляр актора-состояния.
	 * <p>
	 * @return владелец типа события
	 */
	public FSMStateActor getOwner() {
		return owner;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FSMEventType.class ) {
			return false;
		}
		FSMEventType o = (FSMEventType) other;
		return new EqualsBuilder()
			.appendSuper(o.owner == owner)
			.append(o.getId(), getId())
			.isEquals();
	}

}

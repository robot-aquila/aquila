package ru.prolib.aquila.core.fsm;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Диспетчер выходных событий состояния КА.
 * <p>
 * В дополнение к базовым функциям диспетчера, данный диспетчер хранит список
 * созданных типов событий, которые рассматриваются как часть актора конкретного
 * состояния. Методы данного класса позволяют получать список всех созданных
 * типов событий для последующей валидации графа переходов КА (позволяет
 * выполнять проверку наличия целевого состояния для каждого выходного события).
 * <p>
 * Так как переход из одного состояния в другое должен выполняться атомарно,
 * диспетчер данного типа всегда использует реализацию очереди немедленной
 * передачи события наблюдателям для обработки {@link SimpleEventQueue}.
 */
public class FSMEventDispatcher extends EventDispatcherImpl {
	private final FSMStateActor owner;
	private final List<FSMEventType> types;

	/**
	 * Конструктор.
	 * <p>
	 * @param owner актор-владелец
	 * @param id идентификатор
	 */
	public FSMEventDispatcher(FSMStateActor owner, String id) {
		super(new SimpleEventQueue(), id);
		this.owner = owner;
		types = new Vector<FSMEventType>();
	}
	
	/**
	 * Получить экземпляр актора-состояния.
	 * <p>
	 * @return владелец диспетчера событий
	 */
	public FSMStateActor getOwner() {
		return owner;
	}
	
	@Override
	public FSMEventType createType(String typeId) {
		FSMEventType type = new FSMEventType(owner, getId() + "." + typeId);
		types.add(type);
		return type;
	}
	
	@Override
	public FSMEventType createType() {
		return createType(EventTypeImpl.nextId());
	}
	
	/**
	 * Получить список созданных типов событий.
	 * <p>
	 * @return список типов событий
	 */
	public List<FSMEventType> getCreatedTypes() {
		return types;
	}
	
	/**
	 * Получить созданный тип события по индексу.
	 * <p>
	 * @param index очередной индекс
	 * @return тип события
	 */
	public FSMEventType getCreatedType(int index) {
		return types.get(index);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != FSMEventDispatcher.class ) {
			return false;
		}
		FSMEventDispatcher o = (FSMEventDispatcher) other;
		return new EqualsBuilder()
			.append(o.owner, owner)
			.append(o.getId(), getId())
			.append(o.types, types)
			.isEquals();
	}
	
	/**
	 * Генерировать событие указанного типа.
	 * <p>
	 * @param type тип события
	 */
	public void fireEvent(FSMEventType type) {
		dispatch(new EventImpl(type));
	}

}

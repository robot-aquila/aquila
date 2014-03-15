package ru.prolib.aquila.core.fsm;

import java.util.List;
import ru.prolib.aquila.core.*;

/**
 * База актора состояния.
 */
public abstract class FSMStateActor {
	protected final FSMEventDispatcher dispatcher;
	
	public FSMStateActor(FSMEventDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
	}
	
	public FSMStateActor() {
		super();
		dispatcher = new FSMEventDispatcher(this, getClass().getSimpleName());
	}
	
	/**
	 * Создать тип выходного события.
	 * <p>
	 * @param typeId идентификатор
	 * @return тип события
	 */
	protected FSMEventType createType(String typeId) {
		return dispatcher.createType(typeId);
	}
	
	/**
	 * Процедура инициализации обработчика.
	 * <p>
	 * В момент вызова автомат уже наблюдает выходные события. Наследники,
	 * реализующие процедуру инициализации, имеют возможность генерировать
	 * выходные события уже на этапе инициализации обработчика (например, в
	 * случае ошибки).
	 * <p>
	 * Вызывается при входе в состояние.
	 */
	public abstract void enter();
	
	/**
	 * Процедура завершения работы обработчика.
	 * <p>
	 * Вызывается при выходе из состояния.
	 */
	public abstract void exit();
	
	/**
	 * Получить список выходных событий.
	 * <p>
	 * @return список событий
	 */
	public List<FSMEventType> getExitEvents() {
		return dispatcher.getCreatedTypes();
	}
	
	/**
	 * Начать отслеживание всех выходов.
	 * <p>
	 * @param listener наблюдатель
	 */
	public void startListenExitEvents(EventListener listener) {
		for ( FSMEventType type : getExitEvents() ) {
			type.addListener(listener);
		}
	}
	
	/**
	 * Прекратить отслеживание всех выходов.
	 * <p>
	 * @param listener наблюдатель
	 */
	public void stopListenExitEvents(EventListener listener) {
		for ( FSMEventType type: getExitEvents() ) {
			type.removeListener(listener);
		}
	}
	
	@Override
	public String toString() {
		return dispatcher.getId();
	}

}

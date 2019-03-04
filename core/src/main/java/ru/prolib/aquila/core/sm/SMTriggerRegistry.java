package ru.prolib.aquila.core.sm;

import java.util.LinkedHashSet;
import java.util.Set;
import ru.prolib.aquila.core.utils.KW;

/**
 * Реестр триггеров.
 */
public class SMTriggerRegistry {
	private final SMStateMachine machine;
	private final SMStateHandler state;
	private final Set<KW<SMTrigger>> triggers;
	private boolean closed = false;
	
	SMTriggerRegistry(SMStateMachine machine, SMStateHandler handler, Set<KW<SMTrigger>> triggers) {
		this.triggers = triggers;
		this.machine = machine;
		this.state = handler;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param machine конечный автомат
	 * @param state состояние владелец-триггеров
	 */
	public SMTriggerRegistry(SMStateMachine machine, SMStateHandler state) {
		this(machine, state, new LinkedHashSet<KW<SMTrigger>>());
	}
	
	public synchronized boolean isClosed() {
		return closed;
	}
	
	/**
	 * Зарегистрировать триггер.
	 * <p>
	 * Регистрирует и активирует указанный триггер. Если указанный триггер уже
	 * добавлен, то повторная активация выполнена не будет.
	 * <p>
	 * @param trigger триггер
	 */
	public void add(SMTrigger trigger) {
		boolean added = false;
		KW<SMTrigger> k = new KW<SMTrigger>(trigger);
		synchronized ( this ) {
			if ( closed ) {
				return;
			}
			if ( ! triggers.contains(k) ) {
				triggers.add(k);
				added = true;
			}
		}
		if  ( added ) {
			trigger.activate(this);
		}
	}
	
	/**
	 * Удалить триггер.
	 * <p>
	 * Деактивирует и удаляет триггер из реестра. Незарегистрированные триггеры
	 * просто игнорируются. Деактивация для них не выполняется.
	 * <p>
	 * @param trigger триггер
	 */
	public void remove(SMTrigger trigger) {
		boolean removed = false;
		KW<SMTrigger> k = new KW<SMTrigger>(trigger);
		synchronized ( this ) {
			removed = triggers.remove(k);
		}
		if ( removed ) {
			trigger.deactivate();
		}
	}
	
	/**
	 * Удалить все триггеры.
	 * <p>
	 * Деактивирует и удаляет все триггеры реестра.
	 */
	public void removeAll() {
		Set<KW<SMTrigger>> triggers_copy;
		synchronized ( this ) {
			triggers_copy = new LinkedHashSet<>(triggers);
			triggers.clear();
		}
		for ( KW<SMTrigger> k : triggers_copy ) {
			k.instance().deactivate();
		}
	}
	
	/**
	 * Подать данные на вход по умолчанию автомата.
	 * <p>
	 * @param data данные
	 * @throws SMRuntimeException исключение автомата или несоответствующее
	 * состояние автомата
	 */
	public void input(Object data) {
		dispatch(null, data);
	}
	
	/**
	 * Подать данные на вход автомата.
	 * <p>
	 * @param input идентификатор входа
	 * @param data данные
	 * @throws SMRuntimeException исключение автомата или несоответствующее
	 * состояние автомата
	 */
	public void input(SMInput input, Object data) {
		dispatch(input, data);
	}
	
	protected void dispatch(SMInput input, Object data) {
		synchronized ( this ) {
			if ( closed ) {
				return;
			}
		}
		synchronized ( machine ) {
			if ( machine.getCurrentState() != state ) {
				// This is possible to be happen because someone else
				// can switch automat to another state. Just skip this case
				return;
			}
			try {
				if ( input == null ) {
					machine.input(data);
				} else {
					machine.input(input, data);
				}
			} catch ( SMException e ) {
				throw new SMRuntimeException(e);
			}
		}
	}
	
	/**
	 * Deactivate all triggers and make object unavailable for further usage.
	 */
	public void close() {
		synchronized ( this ) {
			if ( closed ) {
				return;
			} else {
				closed = true;
			}
		}
		removeAll();
	}

}

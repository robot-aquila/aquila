package ru.prolib.aquila.core.sm;

import java.util.LinkedHashSet;
import java.util.Set;
import ru.prolib.aquila.core.utils.KW;

/**
 * Реестр триггеров.
 * <p>
 */
public class SMTriggerRegistry {
	private final SMStateMachine machine;
	private final SMState state;
	private final Set<KW<SMTrigger>> triggers;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param machine конечный автомат
	 * @param state состояние владелец-триггеров
	 */
	public SMTriggerRegistry(SMStateMachine machine, SMState state) {
		super();
		triggers = new LinkedHashSet<KW<SMTrigger>>();
		this.machine = machine;
		this.state = state;
	}
	
	/**
	 * Зарегистрировать и активировать триггер.
	 * <p>
	 * Если указанный триггер уже добавлен, то повторная активация выполнена
	 * не будет.
	 * <p>
	 * @param trigger триггер
	 */
	public synchronized void registerAndActivate(SMTrigger trigger) {
		KW<SMTrigger> k = new KW<SMTrigger>(trigger);
		if ( ! triggers.contains(k) ) {
			triggers.add(k);
			trigger.activate(this);
		}
	}
	
	/**
	 * Деактивировать и удалить триггер.
	 * <p>
	 * Незарегистрированные триггеры просто игнорируются. Деактивация для них
	 * не выполняется.
	 * <p>
	 * @param trigger триггер
	 */
	public synchronized void deactivateAndRremove(SMTrigger trigger) {
		KW<SMTrigger> k = new KW<SMTrigger>(trigger);
		if ( triggers.contains(k) ) {
			trigger.deactivate();
			triggers.remove(k);
		}
	}
	
	/**
	 * Деактивировать и удалить все триггеры.
	 */
	public synchronized void deactivateAndRemoveAll() {
		for ( KW<SMTrigger> k : triggers ) {
			k.instance().deactivate();
		}
		triggers.clear();
	}
	
	/**
	 * Подать данные на вход по умолчанию автомата.
	 * <p>
	 * @param data данные
	 * @throws SMRuntimeException исключение автомата или несоответствующее
	 * состояние автомата
	 */
	public synchronized void input(Object data) {
		synchronized ( machine ) {
			if ( machine.getCurrentState() == state ) {
				try {
					machine.input(data);
				} catch ( SMException e ) {
					throw new SMRuntimeException(e);
				}
			} else {
				throw new SMRuntimeException("Input for different state");
			}
		}
	}
	
	/**
	 * Подать данные на вход автомата.
	 * <p>
	 * @param input идентификатор входа
	 * @param data данные
	 * @throws SMRuntimeException исключение автомата или несоответствующее
	 * состояние автомата
	 */
	public synchronized void input(SMInput input, Object data) {
		synchronized ( machine ) {
			if ( machine.getCurrentState() == state ) {
				try {
					machine.input(input, data);
				} catch ( SMException e ) {
					throw new SMRuntimeException(e);
				}
			} else {
				throw new SMRuntimeException("Input for different state");
			}
		}
	}

}

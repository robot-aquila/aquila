package ru.prolib.aquila.probe.timeline;

import ru.prolib.aquila.core.sm.*;

/**
 * Автомат хронологии: состояние выполнения симуляции.
 * <p>
 * При входе в состояние подразумевается, что на вершине очереди команда
 * исполнения симуляции до отсечки или до конца рабочего периода (первое
 * обращение к очереди выполняется в блокирующем режиме).
 * <p> 
 * В этом состоянии процедура симуляции шага вызывается циклически, до тех пор,
 * пока не будет выполнено одни из условий выхода из состояния:
 * <ul>
 * <li>Поступила команда {@link TLCommand#FINISH} или
 * {@link TLCommand#PAUSE};</li>
 * <li>Достигнуто время отсечки команды исполнения;</li>
 * <li>ТА вышло за границу РП.</li>
 * </ul>
 */
public class TLStateRunning extends TLState {
	public final SMExit onFinish, onPause;
	private TLCommand current;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param facade фасад подсистемы эмуляции
	 */
	public TLStateRunning(TLSimulationFacade facade) {
		super(facade);
		onFinish = registerExit();
		onPause = registerExit();
	}

	@Override
	public void prepare() {
		facade.fireRunning();
		current = null;
	}

	@Override
	public SMExit pass() {
		TLCommand next;
		try {
			next = (current == null ? facade.pullb() : facade.pull());
		} catch ( InterruptedException e ) {
			//Thread.currentThread().interrupt();
			return onFinish;
		}
		if ( next == null && current != null ) {
			// Здесь мы можем быть только в случае повторных обращений
			// к очереди. Это нормальная ситуация, при которой мы должны
			// использовать команду, полученную на предыдущем шаге.
		} else if ( next.isRun() ) {
			// next здесь не может быть null.  
			// Если next здесь null, то это ошибка, связанная с не
			// правильной работой очереди, которая на блокирующий запрос
			// не вернула команду. Если это так, то в момент проверки
			// условия будет возбуждено исключение. Так как мы пишем
			// рабочую программу, а не ошибки, то обработка этой ситуации не
			// выполняется.
			current = next;
		} else if ( next == TLCommand.PAUSE ) {
			return onPause;
		} else /* if ( next == TLCDCommand.FINISH ) */ {
			// Здесь может быть либо команда завершения, либо неизвестная
			// команда. Независимо от команды, завершаем симуляцию.
			return onFinish;
		}
		// Здесь уже не может быть работы с паузой или завершением напрямую.
		// Только как результат обработки команды на исполнение шага симуляции.
		if ( current.isApplicableTo(facade.getPOA()) ) {
			if ( facade.executeSimulation() ) {
				facade.fireStepping();
			}
			return facade.simulationFinished() ? onFinish : null;
		} else {
			// Здесь мы будем только в одном случае: если команда
			// не подходит по времени (то есть, достигнуто время останова).
			// Нужно выбрать один из двух выходов:
			// 1) на финиш, если достигнут конец РП (симуляция завершена);
			// 2) иначе идем на паузу до следующей команды
			return facade.simulationFinished() ? onFinish : onPause;
		}
	}

	@Override
	public void cleanup() {
		
	}
	
	/**
	 * Установить значение текущей (последней) команды.
	 * <p>
	 * Служебный метод. Только для тестов!
	 * <p>
	 * @param command текущая команда
	 */
	void setCurrentCommand(TLCommand command) {
		current = command;
	}
	
	/**
	 * Получить значение текущей (последней) команды.
	 * <p>
	 * Служебный метод. Только для тестов!
	 * <p>
	 * @return текущая команда
	 */
	TLCommand getCurrentCommand() {
		return current;
	}

}

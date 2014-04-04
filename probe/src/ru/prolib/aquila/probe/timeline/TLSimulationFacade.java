package ru.prolib.aquila.probe.timeline;

import org.joda.time.DateTime;


/**
 * Фасад симулятора.
 * <p>
 * Обеспечивает доступ к компонентам симулятора подсредством единого интерфейса. 
 */
public class TLSimulationFacade {
	private final TLCommandQueue commandQueue;
	private final TLEventQueue eventQueue;
	private final TLSimulationStrategy simulation;
	private final TLSimulationEventDispatcher dispatcher;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param commandQueue очередь команд управления симулятором
	 * @param eventQueue очередь событий хронологии
	 * @param simulation симулятор
	 * @param dispatcher диспетчер событий смены состояния симулятора
	 */
	public TLSimulationFacade(TLCommandQueue commandQueue,
			TLEventQueue eventQueue, TLSimulationStrategy simulation,
			TLSimulationEventDispatcher dispatcher)
	{
		super();
		this.commandQueue = commandQueue;
		this.eventQueue = eventQueue;
		this.simulation = simulation;
		this.dispatcher = dispatcher;
	}
	
	public TLCommandQueue getCommandQueue() {
		return commandQueue;
	}
	
	public TLEventQueue getEventQueue() {
		return eventQueue;
	}
	
	public TLSimulationStrategy getSimulator() {
		return simulation;
	}
	
	public TLSimulationEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Выполнить шаг эмуляции.
	 * <p>
	 * см. {@link TLSimulationStrategy#execute()}.
	 * <p>
	 * @return true - продолжать симуляцию, false - симуляция завершена.
	 * Фактически, false означает что в конце шага был получен пустой стек,
	 * что является признаком конца данных.
	 */
	public boolean executeSimulation() {
		return simulation.execute();
	}
	
	/**
	 * Получить значение ТА.
	 * <p>
	 * @return точка актуальности
	 */
	public DateTime getPOA() {
		return eventQueue.getPOA();
	}
	
	/**
	 * Блокирующее изъятие команды.
	 * <p>
	 * см. {@link TLCommandQueue#pullb()}.
	 * <p>
	 * @return команда
	 * @throws InterruptedException
	 */
	public TLCommand pullb() throws InterruptedException {
		return commandQueue.pullb();
	}
	
	/**
	 * Неблокирующее изъятие команды.
	 * <p>
	 * см. {@link TLCommandQueue#pull()}. 
	 * <p>
	 * @return команда или null, если очередь не содержит команд
	 */
	public TLCommand pull() {
		return commandQueue.pull();
	}
	
	/**
	 * Неблокирующий запрос команды.
	 * <p>
	 * см. {@link TLCommandQueue#tell()}.
	 * <p>
	 * @return очередная команда или null, если очередь пуста
	 */
	public TLCommand tell() {
		return commandQueue.tell();
	}
	
	/**
	 * Блокирующий запрос команды.
	 * <p>
	 * см. {@link TLCommandQueue#tellb()}. 
	 * <p>
	 * @return очередная команда
	 * @throws InterruptedException
	 */
	public TLCommand tellb() throws InterruptedException {
		return commandQueue.tellb();
	}

	/**
	 * Генерировать событие: симуляция запущена.
	 * <p>
	 * см. {@link TLSimulationEventDispatcher#fireRunning()}.
	 */
	public void fireRunning() {
		dispatcher.fireRunning();
	}
	
	/**
	 * Генерировать событие: симуляция приостановлена.
	 * <p>
	 * см. {@link TLSimulationEventDispatcher#firePaused()}.
	 */
	public void firePaused() {
		dispatcher.firePaused();
	}
	
	/**
	 * Генерировать событие: симуляция завершена.
	 * <p>
	 * см. {@link TLSimulationEventDispatcher#fireFinished()}.
	 */
	public void fireFinished() {
		dispatcher.fireFinished();
	}
	
	/**
	 * Генерировать событие: шаг симуляции выполнен.
	 * <p>
	 * см. {@link TLSimulationEventDispatcher#fireStepping()}.
	 */
	public void fireStepping() {
		dispatcher.fireStepping();
	}
	
	/**
	 * Симуляция завершена?
	 * <p>
	 * см. {@link TLSimulationStrategy#finished()}.
	 * <p>
	 * @return true - симуляция завершена, false - симуляция продолжается
	 */
	public boolean simulationFinished() {
		return eventQueue.finished();
	}
	
	/**
	 * Очистить очередь команд.
	 */
	public void clearCommands() {
		commandQueue.clear();
	}

}

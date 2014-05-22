package ru.prolib.aquila.probe.timeline;

import org.joda.time.DateTime;
import org.joda.time.Interval;


/**
 * Хронология.
 * <p>
 * Касательно синхронизации для методов получения/установки атрибутов состояния
 * автомата (таких как {@link #blocking}, {@link #cutoff} ): эти атрибуты
 * используются только в работе автомата. Все фазы работы автомата выполняются
 * последовательно в отдельном потоке. Следовательно, синхронизация на
 * геттеры/сеттеры подобных атрибутов не нужна.
 */
public class TLSTimeline {
	private final TLCmdQueue cmdQueue;
	private final TLEventQueue evtQueue;
	private final TLSStrategy simulation;
	private final TLSEventDispatcher dispatcher;
	private final TLEventSources sources;
	private boolean blocking = false;
	private DateTime cutoff = null;
	private TLCmdType state = null;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param cmdQueue очередь команд управления симулятором
	 * @param evtQueue очередь событий хронологии
	 * @param simulation эмулятор
	 * @param dispatcher диспетчер событий смены состояния симулятора
	 * @param sources источники событий 
	 */
	TLSTimeline(TLCmdQueue cmdQueue, TLEventQueue evtQueue,
			TLSStrategy simulation, TLSEventDispatcher dispatcher,
			TLEventSources sources)
	{
		super();
		this.cmdQueue = cmdQueue;
		this.evtQueue = evtQueue;
		this.simulation = simulation;
		this.dispatcher = dispatcher;
		this.sources = sources;
	}
	
	/**
	 * Выполнить шаг эмуляции.
	 * <p>
	 * см. {@link TLSStrategy#execute()}.
	 * <p>
	 * @return true - продолжать симуляцию, false - симуляция завершена.
	 * Фактически, false означает что в конце шага был получен пустой стек,
	 * что является признаком конца данных.
	 */
	boolean execute() {
		return simulation.execute();
	}
	
	/**
	 * Получить значение ТА.
	 * <p>
	 * @return ТА
	 */
	public DateTime getPOA() {
		return evtQueue.getPOA();
	}
	
	/**
	 * Получить рабочий период.
	 * <p>
	 * @return РП
	 */
	public Interval getInterval() {
		return evtQueue.getInterval();
	}
	
	/**
	 * Добавить событие в последовательность.
	 * <p>
	 * @param time позиция события на временной шкале
	 * @param procedure процедура события
	 */
	public void schedule(DateTime time, Runnable procedure) {
		schedule(new TLEvent(time, procedure));
	}
	
	/**
	 * Добавить событие в последовательность.
	 * <p>
	 * @param event событие хронологии
	 */
	public void schedule(TLEvent event) {
		evtQueue.pushEvent(event);
	}
	
	/**
	 * Генерировать событие: симуляция запущена.
	 * <p>
	 * см. {@link TLSEventDispatcher#fireRun()}.
	 */
	void fireRun() {
		dispatcher.fireRun();
	}
	
	/**
	 * Генерировать событие: симуляция приостановлена.
	 * <p>
	 * см. {@link TLSEventDispatcher#firePause()}.
	 */
	void firePause() {
		dispatcher.firePause();
	}
	
	/**
	 * Генерировать событие: симуляция завершена.
	 * <p>
	 * см. {@link TLSEventDispatcher#fireFinish()}.
	 */
	void fireFinish() {
		dispatcher.fireFinish();
	}
	
	/**
	 * Генерировать событие: шаг симуляции выполнен.
	 * <p>
	 * см. {@link TLSEventDispatcher#fireStep()}.
	 */
	void fireStep() {
		dispatcher.fireStep();
	}
	
	/**
	 * Извлечь очередную команду.
	 * <p>
	 * @return команда или null в случае неблокирующего режима и отсутствия
	 * команд в очереди
	 */
	TLCmd pullCommand() {
		return blocking ? cmdQueue.pullb() : cmdQueue.pull();
	}
	
	/**
	 * Установить режим извлечения команд.
	 * <p>
	 * @param blocking true - извлечение команды будет выполняться с
	 * блокировкой до поступления команды, false - если в очереди нет команд,
	 * то извлечение команды будет завершено возвратом значения null.
	 */
	void setBlockingMode(boolean blocking) {
		this.blocking = blocking;
	}
	
	/**
	 * Получить режим извлечения команд.
	 * <p>
	 * @return true - блокирующий запрос команд, false - неблокирующий запрос.
	 */
	boolean isBlockingMode() {
		return blocking;
	}
	
	/**
	 * Установить время отсечки.
	 * <p>
	 * @param time время
	 */
	void setCutoff(DateTime time) {
		cutoff = time;
	}
	
	/**
	 * Получить время отсечки.
	 * <p>
	 * @return время 
	 */
	DateTime getCutoff() {
		return cutoff;
	}

	/**
	 * Идентифицировать текущее состояние.
	 * <p>
	 * @param state идентификатор состояния
	 */
	synchronized void setState(TLCmdType state) {
		this.state = state;
	}
	
	/**
	 * Получить идентификатор текущего состояния.
	 * <p>
	 * @return идентификатор состояния
	 */
	public synchronized TLCmdType getState() {
		return state;
	}
	
	/**
	 * Завершить работу.
	 * <p>
	 * Очищает очереди команд и событий и завершает работу с источниками
	 * событий.
	 */
	void close() {
		cmdQueue.clear();
		evtQueue.clear();
		sources.close();
	}
	
	/**
	 * Зарегистрировать источник событий.
	 * <p>
	 * @param source источник событий
	 */
	public void registerSource(TLEventSource source) {
		sources.registerSource(source);
	}
	
	/**
	 * Прекратить работу с источником событий.
	 * <p>
	 * @param source источник событий
	 */
	public void removeSource(TLEventSource source) {
		sources.removeSource(source);
	}
	
	/**
	 * В процессе выполнения?
	 * <p>
	 * @return true - в процессе, false - обработка приостановлена/завершена
	 */
	public synchronized boolean running() {
		return state == TLCmdType.RUN;
	}
	
	/**
	 * Эмуляция приостановлена?
	 * <p>
	 * @return true - приостановлена, false - выполняется или завершена
	 */
	public synchronized boolean paused() {
		return state == TLCmdType.PAUSE;
	}
	
	/**
	 * Эмуляция завершена?
	 * <p>
	 * @return true - завершена, false - симуляция продолжается или не начата
	 */
	public synchronized boolean finished() {
		return state == TLCmdType.FINISH;
	}

	/**
	 * Завершить работу.
	 */
	public synchronized void finish() {
		if ( ! finished() ) {
			cmdQueue.put(TLCmd.FINISH);
		}
	}
	
	/**
	 * Приостановить работу.
	 */
	public synchronized void pause() {
		if ( running() ) {
			cmdQueue.put(TLCmd.PAUSE);
		}
	}
	
	/**
	 * Выполнять эмуляцию до отсечки.
	 * <p>
	 * @param cutoff время отсечки
	 */
	public synchronized void runTo(DateTime cutoff) {
		if ( ! finished() ) {
			cmdQueue.put(new TLCmd(cutoff));
		}
	}

	/**
	 * Выполнять эмуляцию до конца РП.
	 * <p>
	 * Запускает процесс эмуляции до достижения конца рабочего периода.
	 */
	public synchronized void run() {
		runTo(getInterval().getEnd());
	}

}

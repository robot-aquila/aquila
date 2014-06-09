package ru.prolib.aquila.probe.timeline;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import ru.prolib.aquila.core.EventType;


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
	private volatile TLSThreadStarter starter;
	private volatile boolean blocking = false;
	private volatile DateTime cutoff = null;
	private volatile TLCmdType state = null;
	
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
	 * Установить пускач потока.
	 * <p>
	 * Функция потока эмуляции содержит ссылку на объект хронологии и по этому
	 * не может быть создана заранее и передана непосредственно в конструктор
	 * объекта данного класса. Вместо этого, функция потока создается после
	 * инстанцирования экземпляра данного класса, после чего создается пускач
	 * который и определяется для созданной хронологии на последнем этапе
	 * сборки.
	 * <p>
	 * @param starter пускач потока
	 */
	void setStarter(TLSThreadStarter starter) {
		this.starter = starter;
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
	void setState(TLCmdType state) {
		this.state = state;
	}
	
	/**
	 * Получить идентификатор текущего состояния.
	 * <p>
	 * @return идентификатор состояния
	 */
	public TLCmdType getState() {
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
	public boolean running() {
		return state == TLCmdType.RUN;
	}
	
	/**
	 * Эмуляция приостановлена?
	 * <p>
	 * @return true - приостановлена, false - выполняется или завершена
	 */
	public boolean paused() {
		return state == TLCmdType.PAUSE;
	}
	
	/**
	 * Эмуляция завершена?
	 * <p>
	 * @return true - завершена, false - симуляция продолжается или не начата
	 */
	public boolean finished() {
		return state == TLCmdType.FINISH;
	}

	/**
	 * Завершить работу.
	 */
	public void finish() {
		if ( ! finished() ) {
			cmdQueue.put(TLCmd.FINISH);
		}
	}
	
	/**
	 * Приостановить работу.
	 */
	public void pause() {
		if ( running() ) {
			cmdQueue.put(TLCmd.PAUSE);
		}
	}
	
	/**
	 * Выполнять эмуляцию до отсечки.
	 * <p>
	 * @param cutoff время отсечки
	 */
	public void runTo(DateTime cutoff) {
		synchronized ( starter ) {
			if ( ! finished() ) {
				starter.start();
				cmdQueue.put(new TLCmd(cutoff));
			}
		}
	}

	/**
	 * Выполнять эмуляцию до конца РП.
	 * <p>
	 * Запускает процесс эмуляции до достижения конца рабочего периода.
	 */
	public void run() {
		runTo(getInterval().getEnd());
	}
	
	/**
	 * Выполнить проверку ТА на достижение времени отсечки.
	 * <p>
	 * @return true - текущая ТА больше или равна времени отсечки, false -
	 * ТА меньше времени отсечки или время отсечки не определено
	 */
	boolean isCutoff() {
		return cutoff != null && getPOA().compareTo(cutoff) >= 0;
	}
	
	/**
	 * Выполнить проверку ТА на выход за пределы РП.
	 * <p>
	 * @return true - текущая ТА находится за границами рабочего периода,
	 * false - ТА внутри РП.
	 */
	boolean isOutOfInterval() {
		return ! getInterval().contains(getPOA());
	}
	
	/**
	 * Получить тип события: эмуляция завершена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnFinish() {
		return dispatcher.OnFinish();
	}
	
	/**
	 * Получить тип события: эмуляция приостановлена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPause() {
		return dispatcher.OnPause();
	}
	
	/**
	 * Получить тип события: эмуляция продолжена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRun() {
		return dispatcher.OnRun();
	}
	
	/**
	 * Получить тип события: выполнен шаг эмуляции.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStep() {
		return dispatcher.OnStep();
	}
	
	/**
	 * Установить режим вывода отладочных сообщений.
	 * <p>
	 * @param enabled true - включить, false - выключить
	 */
	public void setDebug(boolean enabled) {
		starter.setDebug(enabled);
	}

}
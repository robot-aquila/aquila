package ru.prolib.aquila.probe.timeline;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.Interval;

import ru.prolib.aquila.core.EventType;


/**
 * Хронология.
 * <p>
 * PS. Касательно синхронизации для методов получения/установки атрибутов
 * состояния автомата (таких как {@link #blocking}, {@link #cutoff} ): эти
 * атрибуты используются только в работе автомата. Все фазы работы автомата
 * выполняются последовательно в отдельном потоке. Следовательно, синхронизация
 * на геттеры/сеттеры подобных атрибутов не нужна.
 * <p>
 * <i>2014-10-01</i> Событие OnStep удалено, так как потенциально даст серьезный
 * оверхед в случае большого количества событий. В моменте трудно предположить
 * варианты использования данного события, кроме индикации процесса в UI. Но
 * для этих целей достаточно воспользоваться штатным способом и реализовать
 * индикацию путем регистрации событий в границах хронологии.
 * <p>
 * <i>2014-12-26</i> Удалены все ссылки на пускач потока.
 * <p> 
 */
@Deprecated
public class TLSTimeline {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TLSTimeline.class);
	}
	
	private final TLCmdQueue cmdQueue;
	private final TLEventQueue evtQueue;
	private final TLSStrategy simulation;
	private final TLSEventDispatcher dispatcher;
	private final EventSourceRepository sources;
	private volatile boolean blocking = false;
	private volatile LocalDateTime cutoff = null;
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
			EventSourceRepository sources)
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
	
	public LocalDateTime getPOA() {
		return evtQueue.getPOA();
	}
	
	public Interval getRunInterval() {
		return evtQueue.getInterval();
	}
	
	public void schedule(LocalDateTime time, Runnable procedure)
			throws TLOutOfIntervalException
	{
		schedule(new TLEvent(time, procedure));
	}
	
	public void schedule(TLEvent event) throws TLOutOfIntervalException {
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
	void setCutoff(LocalDateTime time) {
		if ( time == null ) {
			cutoff = null;
			return;
		}
		try {
			evtQueue.pushEvent(new TLEvent(time, new Runnable() {
				@Override public void run() { }
			}));
			cutoff = time;
		} catch ( TLOutOfIntervalException e ) {
			logger.error("Couldn't set cutoff time: ", e);
		}
	}
	
	/**
	 * Получить время отсечки.
	 * <p>
	 * @return время 
	 */
	LocalDateTime getCutoff() {
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
	
	public void close() {
		cmdQueue.clear();
		evtQueue.clear();
		sources.close();
	}
	
	public void registerSource(TLEventSource source) {
		sources.registerSource(source);
	}
	
	public void removeSource(TLEventSource source) {
		sources.removeSource(source);
	}
	
	public boolean running() {
		return state == TLCmdType.RUN;
	}
	
	public boolean paused() {
		return state == TLCmdType.PAUSE;
	}
	
	public boolean finished() {
		return state == TLCmdType.FINISH;
	}

	public void finish() {
		if ( ! finished() ) {
			cmdQueue.put(TLCmd.FINISH);
		}
	}
	
	public void pause() {
		if ( running() ) {
			cmdQueue.put(TLCmd.PAUSE);
		}
	}
	
	public void runTo(LocalDateTime cutoff) {
		if ( ! finished() ) {
			cmdQueue.put(new TLCmd(cutoff));
		}
	}

	public void run() {
		runTo(null);
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
		return ! getRunInterval().contains(getPOA().toInstant(ZoneOffset.UTC));
	}
	
	public EventType OnFinish() {
		return dispatcher.OnFinish();
	}
	
	public EventType OnPause() {
		return dispatcher.OnPause();
	}
	
	public EventType OnRun() {
		return dispatcher.OnRun();
	}

	public List<TLEventSource> getSources(LocalDateTime time) {
		return sources.getSources(time);
	}

	public List<TLEventSource> getSources() {
		return sources.getSources();
	}

	public void disableUntil(TLEventSource source, LocalDateTime time) {
		sources.disableUntil(source, time);
	}

	public boolean isRegistered(TLEventSource source) {
		return sources.isRegistered(source);
	}

	public LocalDateTime getDisabledUntil(TLEventSource source) {
		return sources.getDisabledUntil(source);
	}

}

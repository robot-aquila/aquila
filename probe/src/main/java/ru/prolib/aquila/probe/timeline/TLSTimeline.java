package ru.prolib.aquila.probe.timeline;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TLSTimeline implements Timeline {
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
	
	@Override
	public DateTime getPOA() {
		return evtQueue.getPOA();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#getInterval()
	 */
	@Override
	public Interval getRunInterval() {
		return evtQueue.getInterval();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.Timeline#schedule(org.joda.time.DateTime, java.lang.Runnable)
	 */
	@Override
	public void schedule(DateTime time, Runnable procedure)
			throws TLOutOfIntervalException
	{
		schedule(new TLEvent(time, procedure));
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.Timeline#schedule(ru.prolib.aquila.probe.timeline.TLEvent)
	 */
	@Override
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
	void setCutoff(DateTime time) {
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
	@Override
	public void close() {
		cmdQueue.clear();
		evtQueue.clear();
		sources.close();
	}
	
	/**
	 * Зарегистрировать источник событий.
	 * <p>
	 * @param source источник событий
	 */
	@Override
	public void registerSource(TLEventSource source) {
		sources.registerSource(source);
	}
	
	/**
	 * Прекратить работу с источником событий.
	 * <p>
	 * @param source источник событий
	 */
	@Override
	public void removeSource(TLEventSource source) {
		sources.removeSource(source);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#running()
	 */
	@Override
	public boolean running() {
		return state == TLCmdType.RUN;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#paused()
	 */
	@Override
	public boolean paused() {
		return state == TLCmdType.PAUSE;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#finished()
	 */
	@Override
	public boolean finished() {
		return state == TLCmdType.FINISH;
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#finish()
	 */
	@Override
	public void finish() {
		if ( ! finished() ) {
			cmdQueue.put(TLCmd.FINISH);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#pause()
	 */
	@Override
	public void pause() {
		if ( running() ) {
			cmdQueue.put(TLCmd.PAUSE);
		}
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#runTo(org.joda.time.DateTime)
	 */
	@Override
	public void runTo(DateTime cutoff) {
		if ( ! finished() ) {
			cmdQueue.put(new TLCmd(cutoff));
		}
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#run()
	 */
	@Override
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
		return ! getRunInterval().contains(getPOA());
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#OnFinish()
	 */
	@Override
	public EventType OnFinish() {
		return dispatcher.OnFinish();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#OnPause()
	 */
	@Override
	public EventType OnPause() {
		return dispatcher.OnPause();
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.probe.timeline.TimelineController#OnRun()
	 */
	@Override
	public EventType OnRun() {
		return dispatcher.OnRun();
	}

	@Override
	public List<TLEventSource> getSources(DateTime time) {
		return sources.getSources(time);
	}

	@Override
	public List<TLEventSource> getSources() {
		return sources.getSources();
	}

	@Override
	public void disableUntil(TLEventSource source, DateTime time) {
		sources.disableUntil(source, time);
	}

	@Override
	public boolean isRegistered(TLEventSource source) {
		return sources.isRegistered(source);
	}

	@Override
	public DateTime getDisabledUntil(TLEventSource source) {
		return sources.getDisabledUntil(source);
	}

}

package ru.prolib.aquila.probe;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.internal.*;

/**
 * Эмулятор торгового терминала.
 */
public class PROBETerminal extends TerminalImpl<PROBEServiceLocator>
	implements SimulationController
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PROBETerminal.class);
	}
	
	private final Set<SecurityDescriptor> registered;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад событийной системы
	 * @param locator сервис-локатор
	 */
	public PROBETerminal(EventSystem es, PROBEServiceLocator locator) {
		super(es);
		setServiceLocator(locator);
		this.registered = new HashSet<SecurityDescriptor>();
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад событийной системы
	 */
	public PROBETerminal(EventSystem es) {
		this(es, new PROBEServiceLocator());
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param queueId идентификатор очереди событий
	 */
	public PROBETerminal(String queueId) {
		this(new EventSystemImpl(new EventQueueImpl(queueId)));
	}
	
	@Override
	public void requestSecurity(SecurityDescriptor descr) {
		PROBEServiceLocator locator = getServiceLocator();
		if ( ! registered.contains(descr) ) {
			try {
				locator.registerTimelineEvents(new TickDataDispatcher(
						locator.getDataIterator(descr, getCurrentTime()),
						new CommonTickHandler(getEditableSecurity(descr))));
				registered.add(descr);
				
			} catch (DataException e) {
				logger.error("Failed connect to security data: ", e);
			}
		}
	}

	@Override
	public Interval getRunInterval() {
		return getTimeline().getRunInterval();
	}

	@Override
	public boolean running() {
		return getTimeline().running();
	}

	@Override
	public boolean paused() {
		return getTimeline().paused();
	}

	@Override
	public boolean finished() {
		return getTimeline().finished();
	}

	@Override
	public void finish() {
		getTimeline().finish();
	}

	@Override
	public void pause() {
		getTimeline().pause();
	}

	@Override
	public void runTo(DateTime cutoff) {
		getTimeline().runTo(cutoff);
	}

	@Override
	public void run() {
		getTimeline().run();
	}

	@Override
	public EventType OnFinish() {
		return getTimeline().OnFinish();
	}

	@Override
	public EventType OnPause() {
		return getTimeline().OnPause();
	}

	@Override
	public EventType OnRun() {
		return getTimeline().OnRun();
	}
	
	private SimulationController getTimeline() {
		return getServiceLocator().getTimeline();
	}

}

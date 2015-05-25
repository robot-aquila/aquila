package ru.prolib.aquila.probe;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.BasicTerminalParams;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.internal.PROBEServiceLocator;
import ru.prolib.aquila.probe.internal.SimulationController;

/**
 * Terminal simulator.
 */
public class PROBETerminal extends BasicTerminal
	implements SimulationController
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PROBETerminal.class);
	}
	
	private final Set<SecurityDescriptor> registered;
	private final PROBEServiceLocator locator;
	
	/**
	 * Constructor.
	 * <p>
	 * @param params - the basic terminal parameters
	 * @param locator - the service locator
	 */
	public PROBETerminal(BasicTerminalParams params, PROBEServiceLocator locator) {
		super(params);
		this.locator = locator;
		this.registered = new HashSet<SecurityDescriptor>();
	}
	
	public PROBEServiceLocator getServiceLocator() {
		return locator;
	}
	
	@Override
	public void requestSecurity(SecurityDescriptor descr) {
		if ( ! registered.contains(descr) ) {
			try {
				locator.getDataProvider()
					.startSupply(this, descr, getCurrentTime());
				registered.add(descr);
			} catch (DataException e) {
				logger.error("Failed to start simulation " + descr + ": ", e);
				logger.error("", e);
				fireSecurityRequestError(descr, -1, e.getMessage());
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
		if ( ! connected() ) {
			throw new IllegalStateException("Terminal not connected");
		}
		getTimeline().finish();
	}

	@Override
	public void pause() {
		if ( ! connected() ) {
			throw new IllegalStateException("Terminal not connected");
		}
		getTimeline().pause();
	}

	@Override
	public void runTo(DateTime cutoff) {
		if ( ! connected() ) {
			throw new IllegalStateException("Terminal not connected");
		}
		getTimeline().runTo(cutoff);
	}

	@Override
	public void run() {
		if ( ! connected() ) {
			throw new IllegalStateException("Terminal not connected");
		}
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
		return locator.getTimeline();
	}
	
	@Override
	public synchronized void markTerminalConnected() {
		if ( ! getTimeline().finished() ) {
			super.markTerminalConnected();
			fireTerminalReady();
		} else {
			logger.info("Mark connected request skipped because end of timeline reached.");
		}
	}
	
	@Override
	public synchronized void markTerminalDisconnected() {
		fireTerminalUnready();
		super.markTerminalDisconnected();
	}

}

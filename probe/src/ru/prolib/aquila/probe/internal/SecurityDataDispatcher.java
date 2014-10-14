package ru.prolib.aquila.probe.internal;

import java.io.IOException;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.timeline.*;

/**
 * Диспетчер данных инструмента.
 * <p>
 * 
 */
public class SecurityDataDispatcher implements TLEventSource {
	private final TickReader reader;
	private final SecurityTasks tasks;
	private Tick prevTick;
	private boolean inWork = true; 
	
	/**
	 * Конструктор.
	 * <p>
	 * @param reader поток данных
	 * @param tasks
	 */
	public SecurityDataDispatcher(TickReader reader, SecurityTasks tasks) {
		super();
		this.reader = reader;
		this.tasks = tasks;
	}

	@Override
	public TLEvent pullEvent() throws TLException {
		if ( ! inWork ) {
			return null;
		}
		Tick currTick;
		try {
			currTick = reader.read();
		} catch ( IOException e ) {
			throw new TLException(e);
		}
		if ( currTick == null ) {
			if ( prevTick != null ) {
				tasks.doFinalTask(prevTick);
			}
		} else {
			if ( prevTick == null ) {
				tasks.doInitialTask(currTick);
				tasks.doDailyTask(null, currTick);
			} else if ( currTick.getTime().toLocalDate()
					.isAfter(prevTick.getTime().toLocalDate()) )
			{
				tasks.doDailyTask(prevTick, currTick);
			}
			TLEvent event = new TLEvent(currTick.getTime(),
					tasks.createTask(currTick));
			prevTick = currTick;
			return event;
		}
		close();
		return null;
	}

	@Override
	public void close() {
		if ( inWork ) {
			reader.close();
			inWork = false;
		}
	}

	@Override
	public boolean closed() {
		return ! inWork;
	}

}

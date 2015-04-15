package ru.prolib.aquila.probe.internal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.timeline.*;

/**
 * Диспетчер тиковых данных.
 * <p>
 * Диспетчер тиковых данных объединяет две роли: источник событий для
 * хронологии и диспетчер специфических событий посредством интерфейса
 * {@link TickHandler}. 
 */
public class TickDataDispatcher implements TLEventSource {
	private final Aqiterator<Tick> reader;
	private final TickHandler handler;
	private Tick prevTick;
	private boolean inWork = true; 
	
	/**
	 * Конструктор.
	 * <p>
	 * @param reader поток данных
	 * @param handler обработчик
	 */
	public TickDataDispatcher(Aqiterator<Tick> reader, TickHandler handler) {
		super();
		this.reader = reader;
		this.handler = handler;
	}
	
	public TickHandler getTickHandler() {
		return handler;
	}

	@Override
	public TLEvent pullEvent() throws TLException {
		if ( ! inWork ) {
			return null;
		}
		try {
			if ( ! reader.next() ) {
				if ( prevTick != null ) {
					handler.doFinalTask(prevTick);
				}
			} else {
				Tick currTick = reader.item();
				if ( prevTick == null ) {
					handler.doInitialTask(currTick);
					handler.doDailyTask(null, currTick);
				} else if ( currTick.getTime().toLocalDate()
						.isAfter(prevTick.getTime().toLocalDate()) )
				{
					handler.doDailyTask(prevTick, currTick);
				}
				TLEvent event = new TLEvent(currTick.getTime(),
						handler.createTask(currTick));
				prevTick = currTick;
				return event;
			}
			close();
			return null;
		} catch ( DataException e ) {
			throw new TLException(e);
		}
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
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TickDataDispatcher.class ) {
			return false;
		}
		TickDataDispatcher o = (TickDataDispatcher) other;
		return new EqualsBuilder()
			.append(o.reader, reader)
			.append(o.handler, handler)
			.isEquals();
	}

}

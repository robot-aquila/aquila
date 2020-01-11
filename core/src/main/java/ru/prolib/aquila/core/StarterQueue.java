package ru.prolib.aquila.core;

import java.util.LinkedList;
import java.util.Vector;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Очередь стартеров.
 * <p>
 * 2012-12-03<br>
 * $Id: StarterQueue.java 513 2013-02-11 01:17:18Z whirlwind $
 */
@Deprecated
public class StarterQueue implements Starter {
	private static final Logger logger;
	private final Vector<Starter> queue;
	
	static {
		logger = LoggerFactory.getLogger(StarterQueue.class);
	}
	
	public StarterQueue() {
		super();
		queue = new Vector<Starter>();
	}
	
	/**
	 * Добавить стартер в очередь.
	 * <p>
	 * @param starter экземпляр стартера
	 * @return очередь стартеров (self-instance)
	 */
	public synchronized StarterQueue add(Starter starter) {
		queue.add(starter);
		return this;
	}
	
	/**
	 * Get subsequent starter at index.
	 * <p>
	 * @param index - index of srarter
	 * @return the starter
	 * @throws IndexOutOfBoundsException - The starter is not exists  
	 */
	public synchronized Starter get(int index)
			throws IndexOutOfBoundsException
	{
		try {
			return queue.get(index);
		} catch ( IndexOutOfBoundsException e ) {
			throw new IndexOutOfBoundsException(e.getMessage());
		}
	}
	
	public synchronized int count() {
		return queue.size();
	}

	@Override
	public synchronized void start() throws StarterException {
		LinkedList<Starter> started = new LinkedList<Starter>();
		int qs = queue.size();
		for ( int i = 0; i < qs; i ++ ) {
			try {
				//logger.debug("queue.get(i).start() for {}/" + qs + " ....",i+1);
				queue.get(i).start();
				//logger.debug("queue.get(i).start() for {}/" + qs + " done",i+1);
				started.addFirst(queue.get(i));
			} catch ( StarterException eStart ) {
				//logger.debug("rollback");
				for ( int k = 0; k < started.size(); k ++ ) {
					try {
						started.get(k).stop();
					} catch ( StarterException es ) {
						logger.error("Exception during rollback (ignored)", es);
					}
				}
				throw eStart;
			}
		}
	}

	@Override
	public synchronized void stop() throws StarterException {
		StarterException last = null;
		int qs = queue.size();
		for ( int i = qs - 1; i >= 0; i -- ) {
			try {
				//logger.debug("queue.get(i).stop() for {}/" + qs + " ....", i+1);
				queue.get(i).stop();
				//logger.debug("queue.get(i).stop() for {}/" + qs + " done", i+1);
			} catch ( StarterException e ) {
				logger.error("Exception during stop", e);
				last = e;
			}
		}
		if ( last != null ) {
			throw last;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == getClass()
			&& fieldsEquals(other); 
	}
	
	protected boolean fieldsEquals(Object other) {
		StarterQueue o = (StarterQueue) other;
		return new EqualsBuilder()
			.append(queue, o.queue)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121203, 93133)
			.append(queue)
			.toHashCode();
	}

}

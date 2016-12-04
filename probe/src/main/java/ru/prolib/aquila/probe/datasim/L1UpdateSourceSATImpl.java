package ru.prolib.aquila.probe.datasim;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.data.L1UpdateSource;

/**
 * Decorator of L1 update source with security availability test.
 * <p>
 * This class is used to prevent subscriptions on L1 updates when security still
 * not available. Such subscriptions goes to pending status and awaits when the
 * security will be available for operations. This prevents appearing of bunch
 * of events in case when no operations on security can be made due to absence
 * of its primary attributes at the moment.
 * <p>
 * Note: this implementation will work only with consumers which implement
 * {@link ru.prolib.aquila.core.BusinessEntities.EditableSecurity EditableSecurity}
 * interface. In other words, this class is targeted to supply data to the
 * securities. If the passed consumer does not implement such interface then
 * exception will be thrown.
 */
public class L1UpdateSourceSATImpl implements L1UpdateSource, EventListener, Lockable {
	private final Lock lock;
	private final LID lid;
	private final L1UpdateSource basicSource;
	private final Set<EditableSecurity> pending;
	
	/**
	 * Constructor for testing purposes.
	 * <p>
	 * @param basicSource - the basic data source
	 * @param pending - storage of pending subscriptions
	 */
	L1UpdateSourceSATImpl(L1UpdateSource basicSource, Set<EditableSecurity> pending) {
		this.lock = new ReentrantLock();
		this.lid = LID.createInstance();
		this.basicSource = basicSource;
		this.pending = pending;
	}
	
	public L1UpdateSourceSATImpl(L1UpdateSource basicSource) {
		this(basicSource, new HashSet<>());
	}

	@Override
	public void close() {
		lock.lock();
		try {
			pending.clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void subscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		if ( !(consumer instanceof EditableSecurity) ) {
			throw new IllegalArgumentException();
		}
		EditableSecurity security = (EditableSecurity) consumer;
		Multilock x = new Multilock(security, this);
		x.lock();
		try {
			if ( ! security.isAvailable() ) {
				security.onAvailable().listenOnce(this);
				pending.add(security);
				return;
			}
		} finally {
			x.unlock();
		}
		basicSource.subscribeL1(symbol, consumer);
	}

	@Override
	public void unsubscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		if ( !(consumer instanceof EditableSecurity) ) {
			throw new IllegalArgumentException();
		}
		EditableSecurity security = (EditableSecurity) consumer;
		Multilock x = new Multilock(security, this);
		x.lock();
		try {
			if ( pending.remove(security) ) {
				// It was a pending subscription. Nothing to do.
				return;
			}
		} finally {
			x.unlock();
		}
		basicSource.unsubscribeL1(symbol, consumer);
	}

	@Override
	public void onEvent(Event event) {
		if ( (event instanceof SecurityEvent) ) {
			Security security = ((SecurityEvent) event).getSecurity();
			if ( event.isType(security.onAvailable()) ) {
				Multilock x = new Multilock(security, this);
				x.lock();
				try {
					if ( ! pending.remove(security) ) {
						// It isn't a pending subscription. Nothing to do.
						return;
					}
				} finally {
					x.unlock();
				}
				basicSource.subscribeL1(security.getSymbol(), (EditableSecurity) security);
			}
		}
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

}

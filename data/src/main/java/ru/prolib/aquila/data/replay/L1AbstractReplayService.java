package ru.prolib.aquila.data.replay;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.data.DifferenceTimeConverter;
import ru.prolib.aquila.data.L1UpdateSource;
import ru.prolib.aquila.data.TimeConverter;

abstract public class L1AbstractReplayService implements TStampedReplayService, L1UpdateSource {
	private final Map<Symbol, Set<L1UpdateConsumer>> consumers;
	protected final Lock lock;
	protected final TimeConverter timeConverter;
	
	public L1AbstractReplayService(TimeConverter timeConverter) {
		consumers = new HashMap<>();
		lock = new ReentrantLock();
		this.timeConverter = timeConverter;
	}
	
	public L1AbstractReplayService() {
		this(new DifferenceTimeConverter());
	}

	@Override
	public Instant consumptionTime(Instant currentTime, TStamped object) {
		return timeConverter.convert(currentTime, object.getTime());
	}

	@Override
	public TStamped mutate(TStamped object, Instant consumptionTime) {
		return ((L1Update) object).withTime(consumptionTime);
	}

	@Override
	public void consume(TStamped object) throws IOException {
		final L1Update update = (L1Update) object;
		Set<L1UpdateConsumer> list = null;
		lock.lock();
		try {
			list = new HashSet<>(getConsumersOf(update.getSymbol()));
		} finally {
			lock.unlock();
		}
		for ( L1UpdateConsumer consumer : list ) {
			consumer.consume(update);
		}
	}
	
	@Override
	public void subscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		lock.lock();
		try {
			getConsumersOf(symbol).add(consumer);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void unsubscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		lock.lock();
		try {
			getConsumersOf(symbol).remove(consumer);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void close() {
		lock.lock();
		try {
			consumers.clear();
		} finally {
			lock.unlock();
		}
	}
	
	protected Set<L1UpdateConsumer> getConsumersOf(Symbol symbol) {
		Set<L1UpdateConsumer> list = consumers.get(symbol);
		if ( list == null ) {
			list = new HashSet<>();
			consumers.put(symbol, list);
		}
		return list;
	}

}

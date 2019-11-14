package ru.prolib.aquila.ui.subman;

import java.util.concurrent.atomic.AtomicInteger;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.TerminalRegistry;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepositoryImpl;

/**
 * Repository of Symbol Subscription Descriptors.
 * <p>
 * This class like a manager for symbol subscription.
 * The general use is to manage subscriptions created manually.
 * This class provides functions to make new subscriptions and
 * keeping subscription handlers inside. Use special function
 * to unsubscribe by subscription ID.
 */
public class SSDescRepo extends OSCRepositoryImpl<Integer, SSDesc> {
	private final TerminalRegistry registry;
	private final AtomicInteger idSeq;

	public SSDescRepo(TerminalRegistry registry,
			AtomicInteger id_seq,
			OSCFactory<Integer, SSDesc> factory,
			String repoID)
	{
		super(factory, repoID);
		this.registry = registry;
		this.idSeq = id_seq;
	}
	
	public SSDescRepo(TerminalRegistry registry, EventQueue queue) {
		this(registry, new AtomicInteger(), new SSDescFactory(queue), "SSDES-REPO");
	}
	
	/**
	 * Create new subscription for symbol data.
	 * <p>
	 * This method must be used to create entries instead of {@link #getOrCreate(Integer)}.
	 * <p>
	 * @param term_id - terminal identifier
	 * @param symbol - symbol to subscribe
	 * @param level - level of market data
	 * @return subscription descriptor ID
	 */
	public int subscribe(String term_id, Symbol symbol, MDLevel level) {
		Terminal term = registry.get(term_id);
		SubscrHandler handler = term.subscribe(symbol, level);
		int desc_id = idSeq.incrementAndGet();
		SSDesc desc = super.getOrCreate(desc_id);
		desc.consume(new DeltaUpdateBuilder()
				.withToken(SSDesc.ID, desc_id)
				.withToken(SSDesc.TERM_ID, term_id)
				.withToken(SSDesc.SYMBOL, symbol)
				.withToken(SSDesc.MD_LEVEL, level)
				.withToken(SSDesc.HANDLER, handler)
				.buildUpdate()
			);
		return desc_id;
	}
	
	public void unsibscribe(int desc_id) {
		SubscrHandler handler = null;
		lock();
		try {
			if ( contains(desc_id) ) {
				handler = getOrThrow(desc_id).getHandler();
				super.remove(desc_id);
			}
		} finally {
			unlock();
		}
		if ( handler != null ) {
			handler.close();
		}
	}
	
	@Override
	public SSDesc getOrCreate(Integer key) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Integer key) {
		throw new UnsupportedOperationException();
	}

}

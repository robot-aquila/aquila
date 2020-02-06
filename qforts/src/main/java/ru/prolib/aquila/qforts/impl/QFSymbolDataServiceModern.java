package ru.prolib.aquila.qforts.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.data.SymbolDataServiceDFB;

public class QFSymbolDataServiceModern extends SymbolDataServiceDFB implements QFSymbolDataService {
	protected static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFSymbolDataServiceModern.class);
	}
	
	private final boolean dumpSubscrChange;

	public QFSymbolDataServiceModern(EventQueue queue, boolean dump_subscr_change) {
		super(queue);
		this.dumpSubscrChange = dump_subscr_change;
	}
	
	public QFSymbolDataServiceModern(EventQueue queue) {
		this(queue, false);
	}

	@Override
	public SymbolSubscrRepository getSymbolSubscrRepository() {
		return consumerSubscriptionCounters;
	}

	@Override
	public synchronized SubscrHandler onSubscribe(Symbol symbol, MDLevel level) {
		SubscrHandler x = super.onSubscribe(symbol, level);
		debugSubscrChange(symbol, "Subscribed for " + symbol + "@" + level);
		return x;
	}
	
	@Override
	public synchronized void onUnsubscribe(Symbol symbol, MDLevel level) {
		super.onUnsubscribe(symbol, level);
		debugSubscrChange(symbol, "Unsubscribed from " + symbol + "@" + level);
	}
	
	protected void debugSubscrChange(Symbol symbol, String msg_prefix) {
		if ( dumpSubscrChange ) {
			String ls = System.lineSeparator();
			logger.debug(new StringBuilder()
					.append(msg_prefix).append(ls)
					.append(getCurrentCounters(symbol)).append(ls)
					.append(getStackTrace())
					.toString());
		}
	}
	
	protected String getCurrentCounters(Symbol symbol) {
		SymbolSubscrCounter x = consumerSubscriptionCounters.getOrThrow(symbol);
		return new StringBuilder()
				.append("Subscription counter state:")
				.append(" symbol=").append(x.getSymbol())
				.append(" L0=").append(x.getNumL0())
				.append(" L1_BBO=").append(x.getNumL1_BBO())
				.append(" L1=").append(x.getNumL1())
				.append(" L2=").append(x.getNumL2())
				.toString();
	}
	
	protected String getStackTrace() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		List<String> lines = new ArrayList<>();
		for ( StackTraceElement e : elements ) {
			lines.add(new StringBuilder()
				.append("\tat ").append(e.getClassName()).append(".").append(e.getMethodName())
				.append("(").append(e.getFileName()).append(":").append(e.getLineNumber()).append(")")
				.toString());
		}
		return new StringBuilder()
				.append("Stack trace: ").append(System.lineSeparator())
				.append(StringUtils.join(lines, System.lineSeparator()))
				.toString();
	}
	
}

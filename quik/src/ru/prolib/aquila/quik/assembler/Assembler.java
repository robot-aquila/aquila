package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Фасад подсистемы сборки и согласования объектов бизнес-модели.
 */
public class Assembler implements Starter, EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Assembler.class);
	}
	
	private final EditableTerminal terminal;
	private final Cache cache;
	private final AssemblerHighLvl high;
	
	public Assembler(EditableTerminal terminal, Cache cache,
			AssemblerHighLvl high)
	{
		super();
		this.terminal = terminal;
		this.cache = cache;
		this.high = high;
	}
	
	
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	public Cache getCache() {
		return cache;
	}

	public AssemblerHighLvl getAssemblerHighLevel() {
		return high;
	}

	@Override
	public void start() throws StarterException {
		cache.OnPortfoliosFCacheUpdate().addListener(this);
		cache.OnPositionsFCacheUpdate().addListener(this);
		cache.OnSecuritiesCacheUpdate().addListener(this);
		cache.OnOrdersCacheUpdate().addListener(this);
		cache.OnTradesCacheUpdate().addListener(this);
		cache.OnStopOrdersCacheUpdate().addListener(this);
		logger.debug("Assembler started");
	}

	@Override
	public void stop() throws StarterException {
		cache.OnStopOrdersCacheUpdate().removeListener(this);
		cache.OnTradesCacheUpdate().removeListener(this);
		cache.OnOrdersCacheUpdate().removeListener(this);
		cache.OnSecuritiesCacheUpdate().removeListener(this);
		cache.OnPositionsFCacheUpdate().removeListener(this);
		cache.OnPortfoliosFCacheUpdate().removeListener(this);
		logger.debug("Assembler stopped");
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Assembler.class ) {
			return false;
		}
		Assembler o = (Assembler) other;
		return new EqualsBuilder()
			.append(o.cache, cache)
			.append(o.high, high)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

	@Override
	public void onEvent(Event event) {
		//logger.debug("Assembling initiated by: {}", event);
		// TODO: Насчет синхронизации тут надо думать.
		synchronized ( terminal ) {
			synchronized ( cache ) {
				processEvent(event);
			}
		}
		//logger.debug("Assembling finished");
	}
	
	private void processEvent(Event event) {
		// Если будет тормозить, то можно разбить этапы по типам событий.
		high.adjustSecurities();
		high.adjustPortfolios();
		high.adjustOrders();
		high.adjustStopOrders();
		high.adjustPositions();
	}

}

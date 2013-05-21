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
		terminal.OnPortfolioAvailable().addListener(this);
		terminal.OnSecurityAvailable().addListener(this);
		terminal.OnOrderAvailable().addListener(this);
		terminal.OnStopOrderAvailable().addListener(this);
		cache.OnPortfoliosFCacheUpdate().addListener(this);
		cache.OnPositionsFCacheUpdate().addListener(this);
		cache.OnSecuritiesCacheUpdate().addListener(this);
		cache.OnOrdersCacheUpdate().addListener(this);
		cache.OnTradesCacheUpdate().addListener(this);
		cache.OnStopOrdersCacheUpdate().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		cache.OnStopOrdersCacheUpdate().removeListener(this);
		cache.OnTradesCacheUpdate().removeListener(this);
		cache.OnOrdersCacheUpdate().removeListener(this);
		cache.OnSecuritiesCacheUpdate().removeListener(this);
		cache.OnPositionsFCacheUpdate().removeListener(this);
		cache.OnPortfoliosFCacheUpdate().removeListener(this);
		terminal.OnStopOrderAvailable().removeListener(this);
		terminal.OnOrderAvailable().removeListener(this);
		terminal.OnSecurityAvailable().removeListener(this);
		terminal.OnPortfolioAvailable().removeListener(this);
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
		logger.debug("Initiated by: {}", event);
		// TODO: Насчет синхронизации тут надо думать.
		synchronized ( terminal ) {
			synchronized ( cache ) {
				processEvent(event);
			}
		}
	}
	
	private void processEvent(Event event) {
		if ( event.isType(cache.OnTradesCacheUpdate()) ) {
			high.adjustOrders();
			high.adjustPositions();
		} else if ( event.isType(terminal.OnSecurityAvailable())
		  || event.isType(terminal.OnPortfolioAvailable()) )
		{
			high.adjustOrders();
			high.adjustStopOrders();
			high.adjustPositions();
		} else if ( event.isType(cache.OnOrdersCacheUpdate()) ) {
			high.adjustOrders();
		} else if ( event.isType(cache.OnPositionsFCacheUpdate()) ) {
			high.adjustPositions();
		} else if ( event.isType(cache.OnSecuritiesCacheUpdate()) ) {
			high.adjustSecurities();
		} else if ( event.isType(cache.OnPortfoliosFCacheUpdate()) ) {
			high.adjustPortfolios();
		} else if ( event.isType(cache.OnStopOrdersCacheUpdate()) ) {
			high.adjustStopOrders();
		} else if ( event.isType(terminal.OnOrderAvailable()) ) {
			high.adjustOrders();
			high.adjustStopOrders();
		} else if ( event.isType(terminal.OnStopOrderAvailable()) ) {
			high.adjustStopOrders();
		}
	}

}

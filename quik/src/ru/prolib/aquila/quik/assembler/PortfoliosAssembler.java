package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Сборщик портфелей.
 */
public class PortfoliosAssembler implements EventListener, Starter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PortfoliosAssembler.class);
	}
	
	private final Cache cache;
	private final PortfolioAssembler assembler;
	
	public PortfoliosAssembler(Cache cache, PortfolioAssembler assembler) {
		super();
		this.cache = cache;
		this.assembler = assembler;
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public PortfolioAssembler getPortfolioAssembler() {
		return assembler;
	}

	@Override
	public void start() throws StarterException {
		cache.OnPortfoliosFCacheUpdate().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		cache.OnPortfoliosFCacheUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		for ( PortfolioFCache entry : cache.getAllPortfoliosF() ) {
			try {
				assembler.adjustByCache(entry);
			} catch ( EditableObjectException e ) {
				logger.error("Unexpected exception: ", e);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfoliosAssembler.class ) {
			return false;
		}
		PortfoliosAssembler o = (PortfoliosAssembler) other;
		return new EqualsBuilder()
			.append(cache, o.cache)
			.append(assembler, o.assembler)
			.isEquals();
	}

}

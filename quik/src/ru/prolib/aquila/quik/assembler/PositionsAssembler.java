package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableObjectException;
import ru.prolib.aquila.quik.dde.*;

/**
 * Сборщик позиций.
 */
public class PositionsAssembler implements EventListener, Starter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PortfoliosAssembler.class);
	}
	
	private final Cache cache;
	private final PositionAssembler assembler;
	
	public PositionsAssembler(Cache cache, PositionAssembler assembler) {
		super();
		this.cache = cache;
		this.assembler = assembler;
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public PositionAssembler getPositionAssembler() {
		return assembler;
	}

	@Override
	public void start() throws StarterException {
		cache.OnPositionsFCacheUpdate().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		cache.OnPositionsFCacheUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		for ( PositionFCache entry : cache.getAllPositionsF() ) {
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
		if ( other == null || other.getClass() != PositionsAssembler.class ) {
			return false;
		}
		PositionsAssembler o = (PositionsAssembler) other;
		return new EqualsBuilder()
			.append(cache, o.cache)
			.append(assembler, o.assembler)
			.isEquals();
	}

}

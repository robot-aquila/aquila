package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Сборщик инструментов.
 */
public class SecuritiesAssembler implements EventListener, Starter {
	private final Cache cache;
	private final SecurityAssembler assembler;
	
	public SecuritiesAssembler(Cache cache, SecurityAssembler assembler) {
		super();
		this.cache = cache;
		this.assembler = assembler;
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public SecurityAssembler getSecurityAssembler() {
		return assembler;
	}

	@Override
	public void onEvent(Event event) {
		for ( SecurityCache entry : cache.getAllSecurities() ) {
			assembler.adjustByCache(entry);
		}
	}

	@Override
	public void start() throws StarterException {
		cache.OnSecuritiesCacheUpdate().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		cache.OnSecuritiesCacheUpdate().removeListener(this);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecuritiesAssembler.class ) {
			return false;
		}
		SecuritiesAssembler o = (SecuritiesAssembler) other;
		return new EqualsBuilder()
			.append(cache, o.cache)
			.append(assembler, o.assembler)
			.isEquals();
	}
	
}

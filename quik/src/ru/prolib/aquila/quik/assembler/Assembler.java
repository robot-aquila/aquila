package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Фасад подсистемы сборки и согласования объектов бизнес-модели.
 */
public class Assembler implements Starter {
	private final SecuritiesAssembler securitiesAssembler;
	
	public Assembler(SecuritiesAssembler securitiesAssembler) {
		super();
		this.securitiesAssembler = securitiesAssembler;
	}
	
	public Assembler(EditableTerminal terminal, Cache cache) {
		this(new SecuritiesAssembler(cache,
				new SecurityAssembler(terminal, cache)));
	}
	
	public SecuritiesAssembler getSecuritiesAssembler() {
		return securitiesAssembler;
	}

	@Override
	public void start() throws StarterException {
		securitiesAssembler.start();
	}

	@Override
	public void stop() throws StarterException {
		securitiesAssembler.stop();
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
			.append(securitiesAssembler, o.securitiesAssembler)
			.isEquals();
	}

}

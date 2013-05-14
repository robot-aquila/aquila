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
	private final PortfoliosAssembler portfoliosAssembler;
	private final PositionsAssembler positionsAssembler;
	
	public Assembler(SecuritiesAssembler securitiesAssembler,
			PortfoliosAssembler portfoliosAssembler,
			PositionsAssembler positionsAssembler)
	{
		super();
		this.securitiesAssembler = securitiesAssembler;
		this.portfoliosAssembler = portfoliosAssembler;
		this.positionsAssembler = positionsAssembler;
	}
	
	public Assembler(EditableTerminal terminal, Cache cache) {
		this(new SecuritiesAssembler(cache,
				new SecurityAssembler(terminal, cache)),
			new PortfoliosAssembler(cache,
				new PortfolioAssembler(terminal, cache)),
			new PositionsAssembler(cache,
				new PositionAssembler(terminal, cache))
		);
	}
	
	public SecuritiesAssembler getSecuritiesAssembler() {
		return securitiesAssembler;
	}
	
	public PortfoliosAssembler getPortfoliosAssembler() {
		return portfoliosAssembler;
	}
	
	public PositionsAssembler getPositionsAssembler() {
		return positionsAssembler;
	}

	@Override
	public void start() throws StarterException {
		securitiesAssembler.start();
		portfoliosAssembler.start();
		positionsAssembler.start();
	}

	@Override
	public void stop() throws StarterException {
		positionsAssembler.stop();
		portfoliosAssembler.stop();
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
			.append(portfoliosAssembler, o.portfoliosAssembler)
			.append(positionsAssembler, o.positionsAssembler)
			.isEquals();
	}

}

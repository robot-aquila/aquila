package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * Фабрика экземпляров сделкок.
 * <p>
 * 2012-11-07<br>
 * $Id: TradeFactoryImpl.java 442 2013-01-24 03:22:10Z whirlwind $
 */
public class TradeFactoryImpl implements TradeFactory {
	private final Terminal terminal;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 */
	public TradeFactoryImpl(Terminal terminal) {
		super();
		this.terminal = terminal;
	}

	@Override
	public Trade createTrade() {
		return new Trade(terminal);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121109, 1125541)
			.append(terminal)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == TradeFactoryImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		TradeFactoryImpl o = (TradeFactoryImpl) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.isEquals();
	}

}

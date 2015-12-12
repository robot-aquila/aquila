package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Securities;
import ru.prolib.aquila.core.data.getter.GSymbol;

/**
 * Геттер инструмента.
 * <p>
 * Геттер использует экземпляр {@link GSymbol} для получения
 * дескриптора инструмента и набор инструментов для получения экземпляра
 * инструмента. Если дескриптор инструмента не определен или инструмент
 * в наборе не найден, возвращается null.
 * <p>
 * 2012-09-27<br>
 * $Id: GSecurity.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class GSecurity implements G<Security> {
	private final G<Symbol> gSymbol;
	private final Securities securities;
	
	public GSecurity(G<Symbol> gSymbol, Securities securities) {
		super();
		this.gSymbol = gSymbol;
		this.securities = securities;
	}
	
	/**
	 * Получить геттер дескриптора инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<Symbol> getSymbolGetter() {
		return gSymbol;
	}
	
	/**
	 * Получить набор инструментов.
	 * <p>
	 * @return набор инструментов
	 */
	public Securities getSecurities() {
		return securities;
	}

	@Override
	public Security get(Object object) throws ValueException {
		Symbol symbol = (Symbol) gSymbol.get(object);
		if ( symbol != null && securities.isSecurityExists(symbol) ) {
			try {
				return securities.getSecurity(symbol);
			} catch ( SecurityException e ) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == GSecurity.class ) {
			GSecurity o = (GSecurity) other;
			return new EqualsBuilder()
				.append(gSymbol, o.gSymbol)
				.append(securities, o.securities)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, /*0*/51621)
			.append(gSymbol)
			.append(securities)
			.toHashCode();
	}

}

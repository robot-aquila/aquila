package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Геттер типа заявки.
 * <p>
 * Используется для определения типа заявки (рыночная или лимитная) на основе
 * инструмента и цены. Если цена менее чем минимальный шаг цены, установленный
 * для инструмента, то заявка считается рыночной. Иначе - лимитной. Если в
 * процессе работы не удалось получить инструмент или цену заявки, возвращает
 * null.
 * <p>
 * 2012-09-27<br>
 * $Id: GOrderType.java 301 2012-11-04 01:37:17Z whirlwind $ 
 */
public class GOrderType implements G<OrderType> {
	private final G<Security> gSecurity;
	private final G<Double> gPrice;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param gSecurity геттер инструмента
	 * @param gPrice геттер цены
	 */
	public GOrderType(G<Security> gSecurity, G<Double> gPrice) {
		super();
		this.gSecurity = gSecurity;
		this.gPrice = gPrice;
	}
	
	/**
	 * Получить геттер инструмента.
	 * <p>
	 * @return геттер инструмента
	 */
	public G<Security> getSecurityGetter() {
		return gSecurity;
	}
	
	/**
	 * Получить геттер цены.
	 * <p>
	 * @return геттер цены
	 */
	public G<Double> getPriceGetter() {
		return gPrice;
	}

	@Override
	public OrderType get(Object object) {
		Security security = gSecurity.get(object);
		Double price = gPrice.get(object);
		if ( security != null && price != null ) {
			return price.compareTo(security.getMinStepSize()) < 0 ?
					OrderType.MARKET : OrderType.LIMIT;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GOrderType ) {
			return new EqualsBuilder()
				.append(gPrice, ((GOrderType) other).gPrice)
				.append(gSecurity, ((GOrderType)other).gSecurity)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/73211)
			.append(gSecurity)
			.append(gPrice)
			.toHashCode();
	}

}

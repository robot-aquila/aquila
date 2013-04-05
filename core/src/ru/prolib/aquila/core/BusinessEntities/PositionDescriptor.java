package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Дескриптор позиции.
 * <p>
 * Дескриптор позиции используется для идентификации инструмента, по которому
 * открыта позиция. Дополнительный класс введен по причине неопределенности
 * универсального способа идентификации инструмента позиции на момент разработки
 * интерфейсов.
 * <p>
 * Класс {@link SecurityDescriptor} не используется намеренно, что бы не было
 * соблазна использовать его для запроса инструментов из набора. Идеологически
 * отдельный класс видится более правильным решением, ведь торгуемый инструмент
 * и инструмент в портфеле это различные по смыслу объекты. Одна и та же ценная
 * бумага может торговаться на нескольких площадках (то есть с разными кодами
 * класса), но при этом в рамках портфеля привязка бумаги к площадки смысла не
 * имеет.
 * <p>
 * 2012-09-04<br>
 * $Id: PositionDescriptor.java 365 2012-12-24 06:58:03Z whirlwind $
 * @deprecated
 */
public class PositionDescriptor {
	private final String secCode;
	
	/**
	 * Создать дескриптор.
	 * <p>
	 * @param securityCode код инструмента
	 */
	public PositionDescriptor(String securityCode) {
		super();
		this.secCode = securityCode;
	}
	
	/**
	 * Получить код инструмента.
	 * <p>
	 * @return код инструмента
	 */
	public String getSecurityCode() {
		return secCode;
	}
	
	@Override
	public String toString() {
		return secCode;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != getClass() ) {
			return false;
		}
		PositionDescriptor o = (PositionDescriptor)other;
		return new EqualsBuilder()
			.append(getSecurityCode(), o.getSecurityCode())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getSecurityCode())
			.hashCode();
	}

}

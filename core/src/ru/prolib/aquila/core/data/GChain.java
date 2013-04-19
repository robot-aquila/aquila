package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Цепочка геттеров.
 * <p>
 * Данный геттер позволяет организовывать цепочку преобразования значения
 * источника.
 * <p>
 * @param <R> тип возвращаемого значения
 * <p>
 * 2012-12-15<br>
 * $Id: GChain.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class GChain<R> implements G<R> {
	private final G<?> first;
	private final G<R> second;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param first геттер-трансформатор входящего значения
	 * @param second геттер-обработчик трансформированного значения
	 */
	public GChain(G<?> first, G<R> second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Получить первый геттер (трансформатор значения).
	 * <p>
	 * @return геттер
	 */
	public G<?> getFirstGetter() {
		return first;
	}
	
	/**
	 * Получить второй геттер (обработчик трансформированного значения).
	 * <p>
	 * @return геттер
	 */
	public G<R> getSecondGetter() {
		return second;
	}

	@Override
	public R get(Object source) throws ValueException {
		return second.get(first.get(source));
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 141721)
			.append(first)
			.append(second)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == GChain.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		GChain<?> o = (GChain<?>) other;
		return new EqualsBuilder()
			.append(first, o.first)
			.append(second, o.second)
			.isEquals();
	}

}

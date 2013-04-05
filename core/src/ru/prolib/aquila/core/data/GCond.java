package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.utils.Validator;

/**
 * Условный геттер по значению элемента ряда.
 * <p>
 * Геттер использует валидатор для выбора одного из двух заданных геттеров.
 * Валидатору передается источник данных и если валидатор возвращает истину, то
 * выполняется делегирование первому геттеру. Иначе - второму. Данный геттер
 * позволяет организовывать ветвление на два геттера в зависимости от условия.
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2012-09-03<br>
 * $Id: GCond.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class GCond<R> implements G<R> {
	private final Validator validator;
	private final G<R> first;
	private final G<R> second;

	/**
	 * Создать геттер.
	 * <p>
	 * @param validator валидатор условия
	 * @param first геттер, в случае выполнения условия
	 * @param second геттер, в случае невыполнения условия
	 */
	public GCond(Validator validator, G<R> first, G<R> second) {
		super();
		this.validator = validator;
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Получить валидатор условия.
	 * <p>
	 * @return валидатор
	 */
	public Validator getValidator() {
		return validator;
	}
	
	/**
	 * Получить геттер соответствия условию.
	 * <p>
	 * @return геттер
	 */
	public G<R> getFirstGetter() {
		return first;
	}
	
	/**
	 * Получить геттер несоответствия условию.
	 * <p>
	 * @return геттер
	 */
	public G<R> getSecondGetter() {
		return second;
	}

	@Override
	public R get(Object object) {
		if ( validator.validate(object) ) {
			return first.get(object);
		} else {
			return second.get(object);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == GCond.class ) {
			GCond<?> o = (GCond<?>) other;
			return new EqualsBuilder()
				.append(validator, o.validator)
				.append(first, o.first)
				.append(second, o.second)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/052755)
			.append(validator)
			.append(first)
			.append(second)
			.toHashCode();
	}

}

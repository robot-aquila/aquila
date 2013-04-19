package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Стандартный модификатор основаный на паре геттер-сеттер.
 * <p>
 * @param <T> - тип изменяемого субъекта
 * <p>
 * 2012-08-12<br>
 * $Id: MStd.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class MStd<T> implements S<T> {
	private final G<?> getter;
	private final S<T> setter;

	/**
	 * Создать мутатор.
	 * <p>
	 * @param getter геттер
	 * @param setter сеттер
	 */
	public MStd(G<?> getter, S<T> setter) {
		super();
		this.getter = getter;
		this.setter = setter;
	}
	
	/**
	 * Получить геттер.
	 * <p>
	 * @return геттер
	 */
	public G<?> getGetter() {
		return getter;
	}
	
	/**
	 * Получить сеттер.
	 * <p>
	 * @return сеттер
	 */
	public S<T> getSetter() {
		return setter;
	}

	@Override
	public void set(T subject, Object source) throws ValueException {
		setter.set(subject, getter.get(source));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof  MStd ) {
			MStd<?> o = (MStd<?>) other;
			return new EqualsBuilder()
				.append(o.getter, getter)
				.append(o.setter, setter)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, /*0*/90537)
			.append(getter)
			.append(setter)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
			+ "[" + getter + " => " + setter + "]";
	}

}

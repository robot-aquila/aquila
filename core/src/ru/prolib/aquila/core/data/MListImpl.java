package ru.prolib.aquila.core.data;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Композитный модификатор.
 * <p>
 * @param <T> - тип изменяемого субъекта (target)
 * <p>
 * 2012-08-12<br>
 * $Id: MListImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class MListImpl<T> implements MList<T> {
	private final List<S<T>> modifiers;
	
	/**
	 * Создать модификатор.
	 * <p>
	 * @param modifiers список однотипных мутаторов
	 */
	public MListImpl(List<S<T>> modifiers) {
		super();
		this.modifiers = modifiers;
	}
	
	/**
	 * Создать пустой модификатор.
	 */
	public MListImpl() {
		this(new LinkedList<S<T>>());
	}
	
	/**
	 * Получить список модификаторов.
	 * <p>
	 * @return список модификаторов
	 */
	public synchronized List<S<T>> getModifiers() {
		return modifiers;
	}

	@Override
	public synchronized void set(T subject, Object source) {
		for ( S<T> mutator : modifiers ) {
			mutator.set(subject, source);
		}
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other != null && other.getClass() == MListImpl.class ) {
			return new EqualsBuilder()
				.append(modifiers, ((MListImpl<?>) other).modifiers)
				.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public synchronized MList<T> add(S<T> modifier) {
		modifiers.add(modifier);
		return this;
	}

	@Override
	public synchronized MList<T> add(G<?> getter, S<T> setter) {
		return add(new MStd<T>(getter, setter));
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(20121103, 110623)
			.append(modifiers)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
			+ StringUtils.join(modifiers.toArray(), ",\n") + "]";
	}

}

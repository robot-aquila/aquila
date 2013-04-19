package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Геттер константного значения.
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2012-09-07<br>
 * $Id: GConst.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class GConst<R> implements G<R> {
	private final R retval;
	
	/**
	 * Создать константный геттер.
	 * <p>
	 * @param retval возвращаемое значение 
	 */
	public GConst(R retval) {
		super();
		this.retval = retval;
	}

	/**
	 * Всегда возвращает константное значение.
	 * <p>
	 * @param object значение параметра игнорируется
	 * @return значение, заданное при создании объекта 
	 */
	@Override
	public R get(Object object) throws ValueException {
		return retval;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == GConst.class ) {
			return new EqualsBuilder()
				.append(retval, ((GConst<?>) other).retval)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/51607)
			.append(retval)
			.toHashCode();
	}

}

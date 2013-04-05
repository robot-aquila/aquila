package ru.prolib.aquila.core.data.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.utils.Validator;

/**
 * Фильтр набора записей.
 * <p>
 * Декорирует исходный набор. Использует валидатор для проверки доступности
 * очередного ряда при перемещении на следующий ряд. Валидатору передается
 * исходный набор. Если валидатор возвращает false, то пытается перейти к
 * следующему ряду и так до тех пор, пока не встретит подходящий ряд или не
 * достигнет конца набора. 
 * <p>
 * 2013-02-18<br>
 * $Id$
 */
public class RowSetFilter implements RowSet {
	private final RowSet rs;
	private final Validator validator;
	
	public RowSetFilter(RowSet rs, Validator validator) {
		super();
		this.rs = rs;
		this.validator = validator;
	}
	
	public RowSet getSourceRowSet() {
		return rs;
	}
	
	public Validator getValidator() {
		return validator;
	}

	@Override
	public Object get(String name) {
		return rs.get(name);
	}

	@Override
	public boolean next() {
		while ( rs.next() ) {
			if ( validator.validate(rs) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void reset() {
		rs.reset();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowSetFilter.class ) {
			RowSetFilter o = (RowSetFilter) other;
			return new EqualsBuilder()
				.append(rs, o.rs)
				.append(validator, o.validator)
				.isEquals();
		} else {
			return false;
		}
	}

	@Override
	public void close() {
		rs.close();
	}

}

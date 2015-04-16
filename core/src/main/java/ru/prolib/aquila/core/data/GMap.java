package ru.prolib.aquila.core.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Геттер на основе карты сопоставлений.
 * <p>
 * Использует набор валидаторов для определения соответствующего геттера.
 * Если все валидаторы на запрос ответили отрицательно, то возвращается null.
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2012-10-30<br>
 * $Id: GMap.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMap<R> implements G<R> {
	private final Map<Validator, G<R>> map;
	
	/**
	 * Создать геттер.
	 * <p>
	 * @param map - the map of getters.
	 */
	public GMap(Map<Validator, G<R>> map) {
		super();
		this.map = map;
	}
	
	@Override
	public R get(Object source) throws ValueException {
		Iterator<Entry<Validator, G<R>>> it = map.entrySet().iterator();
		try {
			while ( it.hasNext() ) {
				Entry<Validator, G<R>> e = it.next();
				if ( e.getKey().validate(source) ) {
					return e.getValue().get(source);
				}
			}
		} catch ( ValidatorException e ) {
			throw new ValueException(e);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GMap ) {
			GMap<?> o = (GMap<?>) other;
			return new EqualsBuilder().append(map, o.map).isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/54447)
			.append(map)
			.toHashCode();
	}

}

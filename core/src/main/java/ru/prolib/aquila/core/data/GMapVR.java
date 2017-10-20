package ru.prolib.aquila.core.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.utils.Validator;

/**
 * Геттер на основе карты сопоставлений (Validator -gt; Result).
 * <p>
 * Использует набор валидаторов для определения соответствующего значения.
 * Если все валидаторы на запрос ответили отрицательно, то возвращается null.
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2012-10-30<br>
 * $Id: GMapVR.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMapVR<R> implements G<R> {
	private final GMap<R> getter;
	private final Map<Validator, R> map;

	/**
	 * Создать геттер.
	 * <p>
	 * @param map - map of validators to values.
	 */
	public GMapVR(Map<Validator, R> map) {
		super();
		Map<Validator, G<R>> subMap = new HashMap<Validator, G<R>>();
		Iterator<Entry<Validator, R>> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			Entry<Validator, R> e = it.next();
			subMap.put(e.getKey(), new GConst<R>(e.getValue()));
		}
		getter = new GMap<R>(subMap);
		this.map = map;
	}

	@Override
	public R get(Object source) throws ValueException {
		return getter.get(source);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GMapVR ) {
			GMapVR<?> o = (GMapVR<?>) other;
			return new EqualsBuilder()
				.append(getter, o.getter)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/24319)
			.append(map)
			.toHashCode();
	}

}

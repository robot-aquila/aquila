package ru.prolib.aquila.core.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorEq;

/**
 * Геттер на основе карты сопоставлений (Source Type -gt; Result).
 * <p>
 * Использует геттер ключа для определения соответствующего возвращаемого
 * значения. Если набор не содержит соответствующего ключа, то возвращается
 * null.
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2012-11-02<br>
 * $Id: GMapTR.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMapTR<R> implements G<R> {
	private final GMap<R> getter;
	private final G<?> key;
	private final Map<?, R> map;
	
	public GMapTR(G<?> key, Map<?, R> map) {
		super();
		Map<Validator, G<R>> subMap = new HashMap<Validator, G<R>>();
		Iterator<?> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			@SuppressWarnings("unchecked")
			Entry<?, R> e = (Entry<?, R>) it.next();
			subMap.put(new GValidator(key, new ValidatorEq(e.getKey())),
					new GConst<R>(e.getValue()));
		}
		getter = new GMap<R>(subMap);
		this.key = key;
		this.map = map;
	}

	@Override
	public R get(Object source) throws ValueException {
		return getter.get(source);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GMapTR ) {
			GMapTR<?> o = (GMapTR<?>) other;
			return new EqualsBuilder()
				.append(key, o.key)
				.append(map, o.map)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, /*0*/22213)
			.append(key)
			.append(map)
			.toHashCode();
	}

}

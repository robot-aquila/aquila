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
 * Геттер на основе карты сопоставлений (Source Type -> Getter Result).
 * <p>
 * Использует геттер ключа для определения соответствующего геттера.
 * Если набор не содержит соответствующего ключа, то возвращается null.
 * <p>
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2012-10-30<br>
 * $Id: GMapTG.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GMapTG<R> implements G<R> {
	private final GMap<R> getter;
	private final G<?> key;
	private final Map<?, G<R>> map;
	
	public GMapTG(G<?> key, Map<?, G<R>> map) {
		super();
		Map<Validator, G<R>> subMap = new HashMap<Validator, G<R>>();
		Iterator<?> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			@SuppressWarnings("unchecked")
			Entry<?, G<R>> e = (Entry<?, G<R>>) it.next();
			subMap.put(new GValidator(key, new ValidatorEq(e.getKey())),
					e.getValue());
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
		if ( other != null && other instanceof GMapTG ) {
			GMapTG<?> o = (GMapTG<?>) other;
			return new EqualsBuilder()
				.append(getter, o.getter)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/54747)
			.append(key)
			.append(map)
			.toHashCode();
	}

}

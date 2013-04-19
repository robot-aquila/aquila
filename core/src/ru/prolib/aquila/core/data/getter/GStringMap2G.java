package ru.prolib.aquila.core.data.getter;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер на основе карты сопоставления строк геттерам произвольного типа.
 * <p>
 * Конвертирует строковое значение, полученное с помощью подчиненного строкового
 * гетера, в геттер произвольного типа, с помощью которого извлекается конечное
 * значение.
 * <p>
 * Строгий характер поведения в рамках данного геттера не предусмотрен, так как
 * соответствующие ограничения могут быть наложены на геттеры карты или
 * декоратором данного геттера.
 * <p>
 * 2013-02-23<br>
 * $Id: GStringMap2G.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class GStringMap2G<R> implements G<R> {
	private final G<String> gKey;
	private final Map<String, G<R>> map;
	private final R defaultValue;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param gKey геттер значения
	 * @param map карта соответствий значений геттерам
	 * @param defaultValue значение по-умолчанию возвращается, если нет
	 * соответствующего геттера в карте
	 */
	public GStringMap2G(G<String> gKey, Map<String, G<R>> map, R defaultValue) {
		super();
		this.gKey = gKey;
		this.map = map;
		this.defaultValue = defaultValue;
	}

	/**
	 * Получить геттер ключа.
	 * <p>
	 * @return геттер
	 */
	public G<String> getKeyGetter() {
		return gKey;
	}
	
	/**
	 * Получить значение по-умолчанию.
	 * <p>
	 * @return значение по-умолчанию
	 */
	public R getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Получить карту соответствия.
	 * <p>
	 * @return карта
	 */
	public Map<String, G<R>> getMap() {
		return map;
	}

	@Override
	public R get(Object source) throws ValueException {
		G<R> getter = map.get(gKey.get(source));
		if ( getter != null ) {
			return getter.get(source);
		} else {
			return defaultValue;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GStringMap2G.class ) {
			GStringMap2G<?> o = (GStringMap2G<?>) other;
			return new EqualsBuilder()
				.append(gKey, o.gKey)
				.append(map, o.map)
				.append(defaultValue, o.defaultValue)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[value=" + gKey
			+ ", map=" + map + ", def=" + defaultValue + "]";
	}

}

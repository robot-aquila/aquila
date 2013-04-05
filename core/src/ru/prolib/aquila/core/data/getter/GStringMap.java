package ru.prolib.aquila.core.data.getter;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.data.G;

/**
 * Геттер на основе карты сопоставления строк значениям произвольного типа.
 * <p>
 * Конвертирует строковое значение, полученное с помощью подчиненного строкового
 * гетера, в соответствующее значение карты сопоставлений. Позволяет определять
 * характер поведения: строгое (strict) и нестрогое (nice).
 * <p>
 * Значение может быть получено в процессе сопоставления или, в случае если
 * сопоставлением определить значение не удается, то в результате использования
 * значения по-умолчанию. Строгое поведение подразумевает, что в результате
 * работы должно быть получено определенное ненулевое значение. В случае, если
 * в результате работы определить значение не удалось (нет соответствия в карте
 * и значение по-умолчанию не определено), генерируется событие о паническом
 * состоянии. Нестрогое поведение не предусматривает реакции на неопределенное
 * результирующее значение, что позволяет использовать геттер для опциональных
 * данных. 
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2013-02-22<br>
 * $Id: GStringMap.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class GStringMap<R> implements G<R> {
	private final FirePanicEvent firePanic;
	private final G<String> gKey;
	private final Map<String, R> map;
	private final R defaultValue;
	private final boolean strict;
	private final String msgPrefix;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 * @param gKey геттер значения
	 * @param map карта соответствия
	 * @param defaultValue значение по-умолчанию
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @param msgPrefix  префикс сообщения о паническом состоянии
	 */
	public GStringMap(FirePanicEvent firePanic, G<String> gKey,
			Map<String, R> map, R defaultValue, boolean strict,
			String msgPrefix)
	{
		super();
		this.firePanic = firePanic;
		this.gKey = gKey;
		this.map = map;
		this.defaultValue = defaultValue;
		this.strict = strict;
		this.msgPrefix = msgPrefix;
	}
	
	/**
	 * Получить генератор событий.
	 * <p>
	 * @return генератор событий
	 */
	public FirePanicEvent getFirePanicEvent() {
		return firePanic;
	}
	
	/**
	 * Проверить строгий характер поведения.
	 * <p>
	 * @return true - строгий характер, false - нестрогий
	 */
	public boolean isStrict() {
		return strict;
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
	 * Получить префикс сообщения об ошибке.
	 * <p>
	 * @return префикс сообщения
	 */
	public String getMessagePrefix() {
		return msgPrefix;
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
	public Map<String, R> getMap() {
		return map;
	}

	@Override
	public R get(Object source) {
		String key = gKey.get(source);
		R value = map.get(key);
		if ( value == null ) {
			value = defaultValue;
		}
		if ( value == null && strict ) {
			String msg = msgPrefix + "No matches found: {}";
			firePanic.firePanicEvent(1, msg, new Object[] { key });
		}
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GStringMap.class ) {
			GStringMap<?> o = (GStringMap<?>) other;
			return new EqualsBuilder()
				.append(firePanic, o.firePanic)
				.append(gKey, o.gKey)
				.append(map, o.map)
				.append(defaultValue, o.defaultValue)
				.append(strict, o.strict)
				.append(msgPrefix, o.msgPrefix)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[value=" + gKey
			+ ", map=" + map + ", strict=" + strict + ", def=" + defaultValue
			+ ", msgPfx='" + msgPrefix + "']";
	}

}

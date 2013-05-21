package ru.prolib.aquila.quik.subsys.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер кода валюты из таблицы инструментов.
 * <p>
 * TODO: выпилить, после полного перехода на DDE-кэш
 * <p>
 * 2013-02-25<br>
 * $Id: QUIKGetCurrency.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class QUIKGetCurrency implements G<String> {
	private final G<String> gCode;
	private final String defaultCurrencyCode;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param gCode геттер кода валюты
	 * @param defaultCurrencyCode код валюты по-умолчанию
	 */
	public QUIKGetCurrency(G<String> gCode, String defaultCurrencyCode) {
		super();
		this.gCode = gCode;
		this.defaultCurrencyCode = defaultCurrencyCode;
	}
	
	/**
	 * Получить геттер кода валюты.
	 * <p>
	 * @return геттер
	 */
	public G<String> getCodeGetter() {
		return gCode;
	}

	/**
	 * Получить код валюты по умолчанию.
	 * <p>
	 * @return код валюты
	 */
	public String getDefaultCurrencyCode() {
		return defaultCurrencyCode;
	}

	@Override
	public String get(Object source) throws ValueException {
		String code = gCode.get(source);
		if ( code == null || code.length() == 0 ) {
			return defaultCurrencyCode;
		} else {
			return code;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == QUIKGetCurrency.class ) {
			QUIKGetCurrency o = (QUIKGetCurrency) other;
			return new EqualsBuilder()
				.append(gCode, o.gCode)
				.append(defaultCurrencyCode, o.defaultCurrencyCode)
				.isEquals();
		} else {
			return false;
		}
	}

}

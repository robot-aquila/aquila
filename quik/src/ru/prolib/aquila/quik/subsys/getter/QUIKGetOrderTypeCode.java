package ru.prolib.aquila.quik.subsys.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.G;

/**
 * Геттер кода типа заявки.
 * <p>
 * Тип заявки кодируется трехсимвольным кодов. Первый символ информирует
 * непосредственно о типе заявки. Данный геттер извлекает первый символ
 * строки, полученной посредством подчиненного геттера.
 * <p>
 * 2013-02-22<br>
 * $Id: QUIKGetOrderTypeCode.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class QUIKGetOrderTypeCode implements G<String> {
	private final G<String> gMode;
	
	public QUIKGetOrderTypeCode(G<String> gMode) {
		super();
		this.gMode = gMode;
	}
	
	public G<String> getModeGetter() {
		return gMode;
	}

	@Override
	public String get(Object source) {
		String mode = gMode.get(source);
		if ( mode != null && mode.length() >= 1 ) {
			return mode.substring(0, 1);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other != null && other.getClass() == QUIKGetOrderTypeCode.class ) {
			QUIKGetOrderTypeCode o = (QUIKGetOrderTypeCode) other;
			return new EqualsBuilder()
				.append(gMode, o.gMode)
				.isEquals();
		} else {
			return false;
		}
	}

}
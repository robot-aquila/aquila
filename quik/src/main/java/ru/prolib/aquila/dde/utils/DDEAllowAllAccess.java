package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Контроллер прав доступа по-умолчанию.
 * <p>
 * Разрешает доступ на любые запросы.
 * <p>
 * 2012-07-29<br>
 * $Id: DDEAllowAllAccess.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDEAllowAllAccess implements DDEAccessControl {
	
	/**
	 * Создать объект.
	 */
	public DDEAllowAllAccess() {
		super();
	}

	@Override
	public boolean isAllowed(DDEAccessSubject subject) {
		return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, /*0*/71705).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof DDEAllowAllAccess;
	}

}

package ru.prolib.aquila.core.utils;

import java.util.Date;

/**
 * Выравнивание (группировка) временной метки.
 */
public interface AlignTime extends Align<Date> {
	
	public int getPeriod();

}

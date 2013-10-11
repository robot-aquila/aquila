package ru.prolib.aquila.core.data;

import org.joda.time.Interval;

/**
 * Интерфейс редактируемого ряда временных меток.
 * <p>
 * 2013-03-11<br>
 * $Id: EditableTimeSeries.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public interface EditableIntervalSeries
	extends IntervalSeries, EditableSeries<Interval>
{

}

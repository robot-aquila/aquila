package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.FixedList;

/**
 * Расчет минимального значения за период. 
 * <p>
 * 2012-05-14<br>
 * $Id: Lowest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class Lowest extends CommonPeriod implements EventListener {
	private final FixedList<Double> list;
	
	/**
	 * Создать объект
	 * <p>
	 * @param target целевое значение
	 * @param period период
	 */
	public Lowest(EditableSeries<Double> target, int period) {
		super(target, period);
		list = new FixedList<Double>(period);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(Event event) {
		ValueEvent<Double> e = (ValueEvent<Double>)event;
		list.addLast(e.getNewValue());
		Double lo = null;
		for ( int i = 0; i < list.size(); i ++ ) {
			Double cv = list.get(i);
			if ( cv != null && (lo == null || cv < lo) ) {
				lo = cv;
			}
		}
		try {
			target.add(lo);
		} catch ( ValueException ex ) {
			throw new RuntimeException("Unexpected exception: ", ex);
		}
	}

}

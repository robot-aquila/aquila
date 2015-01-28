package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.FixedList;

/**
 * Расчет максимального значения за период. 
 * <p>
 * 2012-05-14<br>
 * $Id: Highest.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class Highest extends CommonPeriod implements EventListener {
	private final FixedList<Double> list;
	
	/**
	 * Создать объект
	 * <p>
	 * @param target целевое значение
	 * @param period период
	 */
	public Highest(EditableSeries<Double> target, int period) {
		super(target, period);
		list = new FixedList<Double>(period);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(Event event) {
		ValueEvent<Double> e = (ValueEvent<Double>)event;
		list.addLast(e.getNewValue());
		Double hi = null;
		for ( int i = 0; i < list.size(); i ++ ) {
			Double cv = list.get(i);
			if ( cv != null && (hi == null || cv > hi) ) {
				hi = cv;
			}
		}
		try {
			target.add(hi);
		} catch ( ValueException ex ) {
			throw new RuntimeException("Unexpected exception: ", ex);
		}
	}

}

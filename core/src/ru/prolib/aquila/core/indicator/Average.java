package ru.prolib.aquila.core.indicator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import ru.prolib.aquila.core.CompositeEvent;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.data.EditableSeries;
import ru.prolib.aquila.core.data.ValueEvent;

/**
 * Расчет среднего арифметического по нескольким значениям.
 * <p>
 * Реализован как наблюдатель композитного события, который использует
 * значения составных событий типа {@link ru.prolib.aquila.core.data.ValueEvent}
 * для расчета среднего арифметического.
 * <p>
 * 2012-05-03<br>
 * $Id: Average.java 565 2013-03-10 19:32:12Z whirlwind $
 */
public class Average extends Common implements EventListener {
	
	/**
	 * Создать объект
	 * <p>
	 * @param target целевое значение
	 */
	public Average(EditableSeries<Double> target) {
		super(target);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(Event event) {
		CompositeEvent e = (CompositeEvent) event;
		LinkedHashMap<EventType, Event> state = e.getState();
		if ( state.size() == 0 ) {
			target.add(null);
			return;
		}
		Iterator<Entry<EventType, Event>> i = state.entrySet().iterator();
		Double next, result = 0d;
		while ( i.hasNext() ) {
			next = ((ValueEvent<Double>) i.next().getValue()).getNewValue();
			if ( next == null ) {
				target.add(null);
				return;
			} else {
				result += next;
			}
		}
		target.add(result / state.size());
	}

}

package ru.prolib.aquila.probe.timeline;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.joda.time.DateTime;

/**
 * Кэш событий.
 * <p>
 * Данный класс группирует поступающие события в стеки событий.  
 * <p>
 * Время POA (ТА, точка актуальности) указывает на текущее местоположение
 * системы на временной шкале. Попытка добавить устаревшее событие (датированное
 * временем меньше ТА) приведет к возбуждению исключения.
 * <p>
 * Момент достаточности данных определяется пользовательским кодом. Сдвиг точки
 * актуальности выполняется при извлечении очередного стека событий: ТА
 * сдвигается в будущее на миллисекунду
 * (см. {@link #pullStack()}).
 */
public class TLEventCache {
	private DateTime poa;
	private final Map<DateTime, TLEventStack> stacks;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param initialTime стартовая ТА
	 */
	public TLEventCache(DateTime initialTime) {
		super();
		this.poa = initialTime;
		stacks = new Hashtable<DateTime, TLEventStack>();
	}
	
	/**
	 * Получить значение точки актуальности.
	 * <p>
	 * @return точка актуальности
	 */
	public synchronized DateTime getPOA() {
		return poa;
	}
	
	/**
	 * Добавить событие.
	 * <p>
	 * Размещает событие в общей последовательности.
	 * <p>
	 * @param event экземпляр события
	 * @throws TLOutOfDateException попытка разместить устаревшее событие 
	 */
	public synchronized void pushEvent(TLEvent event)
		throws TLOutOfDateException
	{
		DateTime time = event.getTime();
		if ( time.compareTo(poa) < 0 ) {
			throw new
				TLOutOfDateException("Event of " + time + " but POA " + poa);
		}
		TLEventStack stack = stacks.get(time);
		if ( stack == null ) {
			stack = new TLEventStack(event);
			stacks.put(time, stack);
		} else {
			stack.pushEvent(event);
		}
	}
	
	/**
	 * Получить стек событий.
	 * <p>
	 * Данный метод извлекает очередной (самый ранний) стек событий и сдвигает
	 * ТА на следующую миллисекунду после времени извлеченного стека. Если
	 * последовательность не содержит событий, то сдвиг ТА не выполняется,
	 * а результатом вызова будет null.
	 * <p>
	 * @return стек событий или null, если нет событий
	 */
	public synchronized TLEventStack pullStack() {
		if ( stacks.size() == 0 ) {
			return null;
		}
		List<DateTime> dummy = new Vector<DateTime>(stacks.keySet());
		Collections.sort(dummy);
		DateTime time = dummy.get(0);
		poa = time.plus(1);
		return stacks.remove(time);
	}

}

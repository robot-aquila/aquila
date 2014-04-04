package ru.prolib.aquila.probe.timeline;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.joda.time.*;

/**
 * Последовательность событий.
 * <p>
 * Данный класс сортирует и группирует поступающие события в виде стеков.
 * <p>
 * Время POA (ТА, точка актуальности) указывает на текущее местоположение
 * симуляции на временной шкале. За начальное значение ТА берется время начала
 * рабочего периода. Попытка добавить устаревшее событие (датированное
 * временем меньшим чем текущая ТА) приведет к возбуждению исключения.
 * <p>
 * Момент достаточности данных определяется пользовательским кодом. Сдвиг точки
 * актуальности выполняется при извлечении очередного стека событий: ТА
 * сдвигается в будущее на миллисекунду (см. {@link #pullStack()}).
 */
public class TLEventQueue {
	private final Map<DateTime, TLEventStack> stacks;
	private final Interval interval;
	private DateTime poa;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param interval рабочий период
	 */
	public TLEventQueue(Interval interval) {
		super();
		this.interval = interval;
		this.poa = interval.getStart();
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
	 * Получить рабочий период.
	 * <p>
	 * @return период
	 */
	public Interval getInterval() {
		return interval;
	}
	
	/**
	 * Добавить событие.
	 * <p>
	 * Размещает событие в общей последовательности.
	 * <p>
	 * @param event экземпляр события
	 */
	public synchronized void pushEvent(TLEvent event) {
		DateTime time = event.getTime();
		Interval currentInterval = new Interval(poa, interval.getEnd());
		if ( ! currentInterval.contains(time) ) {
			return;
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
	 * Данный метод извлекает очередной стек событий и сдвигает ТА на следующую
	 * миллисекунду после времени извлеченного стека. Если последовательность не
	 * содержит событий, то сдвиг ТА не выполняется, а результатом вызова будет
	 * null. Первый вызов, завершившийся возвратом null-результата, сдвигает ТА
	 * на конец РП, то есть  
	 * <p>
	 * @return стек событий или null, если нет событий
	 */
	public synchronized TLEventStack pullStack() {
		if ( stacks.size() == 0 ) {
			poa = interval.getEnd();
			return null;
		}
		List<DateTime> dummy = new Vector<DateTime>(stacks.keySet());
		Collections.sort(dummy);
		DateTime time = dummy.get(0);
		poa = time.plus(1);
		return stacks.remove(time);
	}
	
	/**
	 * Получить размер очереди.
	 * <p>
	 * @return количество стеков
	 */
	public synchronized int size() {
		return stacks.size();
	}
	
	/**
	 * Последовательность завершена?
	 * <p>
	 * Последовательность считается завершенной, когда ТА >= времени конца РП.
	 * При этом, не имеет значения есть ли в очереди события датированные
	 * за пределами РП.
	 * <p>
	 * @return true - последовательность завершена, false - последовательность
	 * еще не завершена
	 */
	public synchronized boolean finished() {
		return ! interval.contains(poa);
	}

}

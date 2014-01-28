package ru.prolib.aquila.probe.timeline;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.joda.time.DateTime;

/**
 * Хронология.
 * <p>
 * Объект хронологии используется для формирования единой последовательности
 * событий. Данная хронология подразумевает точность работы до миллисекунд.
 * <p> 
 * Работа с объектом состоит из двух чередующихся фаз: фаза накопления событий и
 * фаза извлечения стека. 
 * <p>
 * Время POA (ТА, точка актуальности) указывает на текущее время начала
 * последовательности. Попытка отнести к хронологии событие, датированное
 * временем меньше POA приведет к возбуждению исключения. Таким образом,
 * события из различных источников могут быть добавлены в последовательность
 * в произвольном порядке. Главное условие, что бы они были позже или на точке
 * актуальности.
 * <p>
 * Поскольку для определения момента готовности очередного стека событий
 * требуется дополнительные данные, эта функция вынесена за пределы зоны
 * ответственности класса хронологии. Момент достаточности данных определяется
 * пользовательским кодом и фиксируется в хронологии вместе с извлечением
 * очередного стека событий. При извлечении событий POA сдвигается в будущее
 * (см. {@link #pullStack()}).
 * 
 */
public class TLTimeline {
	private DateTime poa;
	private final Map<DateTime, TLEventStack> stacks;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param pointOfActuality точка актуальности
	 */
	public TLTimeline(DateTime pointOfActuality) {
		super();
		poa = pointOfActuality;
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
	 * Разместить событие в последовательности.
	 * <p>
	 * @param event экземпляр события
	 * @throws TLOutOfDateException попытка разместить событие раньше чем POA 
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
	 * Получить очередной стек событий.
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

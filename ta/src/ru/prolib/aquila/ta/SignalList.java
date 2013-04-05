package ru.prolib.aquila.ta;

import java.util.*;

/**
 * Обеспечивает накопление последовательности типовых сигналов с последующей
 * выдачей в виде списка.
 */
public class SignalList implements ISignalTranslator {
	private final LinkedList<Signal> list;
	private int sourceId = -1;
	
	public SignalList() {
		list = new LinkedList<Signal>();
	}
	
	/**
	 * Установить новый идентиикатор источника.
	 * @param id
	 */
	public synchronized void setSourceId(int id) {
		sourceId = id;
	}
	
	/**
	 * Получить список зарегистрированных сигналов.
	 * @return
	 */
	public synchronized List<Signal> getSignals() {
		return new LinkedList<Signal>(list);
	}
	
	/**
	 * Очищает список зарегистрированных сигналов.
	 * Идентификатор источника сбрасывается в -1.
	 */
	public synchronized void clearSignals() {
		list.clear();
		sourceId = -1;
	}
	
	/**
	 * Найти сигналы в соответствии с указанными критериями.
	 * При передаче в качестве критерия значения null, соответствующий фильтр
	 * не применяется. 
	 * @param sourceId - фильтр по указанному идентификатору источника
	 * @param type - фильтр по типу сигнала {@link Signal#BUY} или
	 * {@link Signal#SELL}
	 * @return список сигналов. Если не найдено ни одного подходящего критерия,
	 * возвращается пустой список.
	 */
	public synchronized List<Signal> find(Integer sourceId, Integer type) {
		LinkedList<Signal> found = new LinkedList<Signal>();
		for ( int i = 0; i < list.size(); i ++ ) {
			Signal s = list.get(i);
			if ( (sourceId != null && s.getSourceId() != sourceId)
				|| (type != null && s.getType() != type) )
			{
				continue;
			}
			found.add(s);
		}
		return found;
	}
	
	/**
	 * Найти один сигнал, в соответствии с указанными критериями. Выполняет
	 * отбор по аналогии с методом {@ #find(Integer, Integer)} но возвращает
	 * только первый сигнал, если есть.
	 * @param sourceId - фильтр по указанному идентификатору источника
	 * @param type - фильтр по типу сигнала {@link Signal#BUY} или
	 * {@link Signal#SELL}
	 * @return возвращает подходящий сигнал или null, если не найдено ни одного
	 * подходящего сигнала.
	 */
	public synchronized Signal findOne(Integer sourceId, Integer type) {
		List<Signal> found = find(sourceId, type);
		return found.size() > 0 ? found.get(0) : null;
	}

	@Override
	public synchronized void signalToBuy(double price, String comment) {
		list.add(new Signal(sourceId, Signal.BUY, price, comment));
	}

	@Override
	public synchronized void signalToSell(double price, String comment) {
		list.add(new Signal(sourceId, Signal.SELL, price, comment));
	}

	@Override
	public void signalToBuy(double price) {
		signalToBuy(price, "Default");
	}

	@Override
	public void signalToSell(double price) {
		signalToSell(price, "Default");
	}


}

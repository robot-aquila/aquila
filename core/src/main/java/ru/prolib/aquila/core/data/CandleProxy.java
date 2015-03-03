package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;

/**
 * Конвертер свечи в составной элемент свечи.
 * <p>
 * Обеспечивает доступ к элементу свечи через интерфейс {@link Series}.
 * Транслирует запросы значений в запрос соответствующей свечи, после чего 
 * передает полученную свечу на обработку указанному геттеру и возвращает
 * полученный результат. Так же ретранслирует события модификации
 * последовательности свечей.
 * <p>
 * 2012-04-25<br>
 * $Id: CandleProxy.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleProxy<T> implements Series<T>, EventListener {
	private final GCandlePart<T> getter;
	private final Series<Candle> candles;
	private final String id;
	private final EventDispatcher dispatcher;
	private final EventTypeSI onAdd, onUpd;
	
	/**
	 * Создать объект.
	 * <p>
	 * @param es фасад системы событий
	 * @param valueId идентификатор значения
	 * @param candles источник свечей
	 * @param getter геттер атрибута свечи
	 */
	public CandleProxy(EventSystem es,  String valueId,
			Series<Candle> candles, GCandlePart<T> getter)
	{
		super();
		this.id = valueId;
		this.candles = candles;
		this.getter = getter;
		dispatcher = es.createEventDispatcher(id);
		onAdd = dispatcher.createType("Add");
		onUpd = dispatcher.createType("Upd");
		candles.OnAdded().addSyncListener(this);
		candles.OnUpdated().addSyncListener(this);
	}
	
	/**
	 * Получить источник свечей.
	 * <p>
	 * @return значение
	 */
	public Series<Candle> getCandles() {
		return candles;
	}
	
	/**
	 * Получить геттер атрибута свечи.
	 * <p>
	 * @return геттер
	 */
	public GCandlePart<T> getGetter() {
		return getter;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public T get() throws ValueException {
		return getter.get(candles.get());
	}

	@Override
	public T get(int index) throws ValueException {
		return getter.get(candles.get(index));
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == CandleProxy.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		CandleProxy<?> o = (CandleProxy<?>) other;
		return new EqualsBuilder()
			.append(id, o.id)
			.append(candles, o.candles)
			.append(getter, o.getter)
			.isEquals();
	}

	@Override
	public EventType OnAdded() {
		return onAdd;
	}

	@Override
	public EventType OnUpdated() {
		return onUpd;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(Event event) {
		if ( event.isType(candles.OnAdded()) ) {
			ValueEvent<Candle> candleEvent = (ValueEvent<Candle>) event;
			dispatcher.dispatch(new ValueEvent<T>(onAdd,
					getter.get(candleEvent.getNewValue()),
					candleEvent.getValueIndex()));
		} else if ( event.isType(candles.OnUpdated()) ) {
			ValueEvent<Candle> candleEvent = (ValueEvent<Candle>) event;
			dispatcher.dispatch(new ValueEvent<T>(onUpd,
					getter.get(candleEvent.getOldValue()),
					getter.get(candleEvent.getNewValue()),
					candleEvent.getValueIndex()));
		}
	}

}

package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.SimpleEventQueue;

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
	private final G<T> getter;
	private final Series<Candle> candles;
	private final String id;
	private final EventDispatcher dispatcher;
	private final EventType onAdd, onUpd;
	
	/**
	 * Создать объект.
	 * <p>
	 * @param valueId идентификатор значения
	 * @param candles источник свечей
	 * @param getter геттер атрибута свечи
	 */
	public CandleProxy(String valueId,  Series<Candle> candles, G<T> getter) {
		super();
		this.id = valueId;
		this.candles = candles;
		this.getter = getter;
		dispatcher = new EventDispatcherImpl(new SimpleEventQueue(), id);
		onAdd = new EventTypeImpl(dispatcher, "Add");
		onUpd = new EventTypeImpl(dispatcher, "Upd");
		candles.OnAdd().addListener(this);
		candles.OnUpd().addListener(this);
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
	public G<T> getGetter() {
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
	public EventType OnAdd() {
		return onAdd;
	}

	@Override
	public EventType OnUpd() {
		return onUpd;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(Event event) {
		if ( event.isType(candles.OnAdd()) ) {
			ValueEvent<Candle> candleEvent = (ValueEvent<Candle>) event;
			dispatcher.dispatch(new ValueEvent<T>(onAdd,
					getter.get(candleEvent.getNewValue()),
					candleEvent.getValueIndex()));
		} else if ( event.isType(candles.OnUpd()) ) {
			ValueEvent<Candle> candleEvent = (ValueEvent<Candle>) event;
			dispatcher.dispatch(new ValueEvent<T>(onUpd,
					getter.get(candleEvent.getOldValue()),
					getter.get(candleEvent.getNewValue()),
					candleEvent.getValueIndex()));
		}
	}

}

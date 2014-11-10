package ru.prolib.aquila.probe;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.timeline.*;

/**
 * Сервис-локатор.
 */
public class PROBEServiceLocator {
	private DataStorage dataStorage;
	private TLSTimeline timeline;
	
	/**
	 * Конструктор.
	 */
	public PROBEServiceLocator() {
		super();
	}
	
	/**
	 * Получить хранилище данных.
	 * <p>
	 * @return хранилище данных
	 * @throws NullPointerException экземпляр хранилища не определен
	 */
	public DataStorage getDataStorage() {
		if ( dataStorage == null ) {
			throw new NullPointerException("Storage was not defined");
		}
		return dataStorage;
	}
	
	/**
	 * Назначить хранилище данных.
	 * <p>
	 * @param storage хранилище данных
	 */
	public void setDataStorage(DataStorage storage) {
		this.dataStorage = storage;
	}
	
	/**
	 * Получить хронологию событий.
	 * <p>
	 * @return хронология событий
	 * @throws NullPointerException экземпляр не определен
	 */
	public TLSTimeline getTimeline() {
		if ( timeline == null ) {
			throw new NullPointerException("Timeline instance was not defined");
		}
		return timeline;
	}
	
	/**
	 * Назначить хронологию событий.
	 * <p>
	 * @param timeline хронология событий
	 */
	public void setTimeline(TLSTimeline timeline) {
		this.timeline = timeline;
	}

	/**
	 * Получить итератор тиковых данных.
	 * <p>
	 * Ярлык. Делегирует хранилищу данных.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param start время начала данных
	 * @return итератор данных соответствующего инструмента
	 * @throws DataException ошибка доступа к данным
	 */
	public Aqiterator<Tick>
		getDataIterator(SecurityDescriptor descr, DateTime start)
			throws DataException
	{
		return getDataStorage().getIterator(descr, start);
	}
	
	/**
	 * Зарегистрировать источник временных событий.
	 * <p>
	 * Ярлык. Делегирует объекту хронологии.
	 * <p>
	 * @param source источник событий
	 */
	public void registerTimelineEvents(TLEventSource source) {
		getTimeline().registerSource(source);
	}
	
}

package ru.prolib.aquila.probe.internal;

import org.joda.time.DateTime;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.probe.timeline.*;

/**
 * Сервис-локатор.
 */
public class PROBEServiceLocator {
	private PROBEDataStorage dataStorage;
	private DataProvider dataProvider;
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
	public PROBEDataStorage getDataStorage() {
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
	public void setDataStorage(PROBEDataStorage storage) {
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
	 * Получить провайдер данных.
	 * <p>
	 * @return провайдер данных
	 * @throws NullPointerException экземпляр не определен
	 */
	public DataProvider getDataProvider() {
		if ( dataProvider == null ) {
			throw new NullPointerException("Data provider was not defined");
		}
		return dataProvider;
	}
	
	/**
	 * Назначить провайдер данных.
	 * <p>
	 * @param dataProvider провайдер данных
	 */
	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}
	
	/**
	 * Запустить симуляцию инструмента.
	 * <p> 
	 * @param descr дескриптор инструмента
	 * @param start начальная датировка данных 
	 * @throws DataException ошибка инициализации
	 */
	public void startSimulation(SecurityDescriptor descr, DateTime start)
			throws DataException
	{
		getDataProvider().startSupply(descr, start);
	}
	
}

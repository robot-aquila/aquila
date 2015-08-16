package ru.prolib.aquila.probe.internal;

import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;
import ru.prolib.aquila.probe.timeline.*;

/**
 * Сервис-локатор.
 */
public class PROBEServiceLocator {
	private TickDatabase tickDatabase;
	private DataProvider dataProvider;
	private Timeline timeline;
	
	/**
	 * Конструктор.
	 */
	public PROBEServiceLocator() {
		super();
	}
	
	/**
	 * Получить хранилище тиковых данных.
	 * <p>
	 * @return хранилище тиковых данных
	 * @throws NullPointerException экземпляр хранилища не определен
	 */
	public TickDatabase getTickDatabase() {
		if ( tickDatabase == null ) {
			throw new NullPointerException("Tick database was not defined");
		}
		return tickDatabase;
	}
	
	/**
	 * Назначить хранилище тиковых данных.
	 * <p>
	 * @param storage хранилище тиковых данных
	 */
	public void setTickDatabase(TickDatabase storage) {
		this.tickDatabase = storage;
	}
	
	/**
	 * Получить хронологию событий.
	 * <p>
	 * @return хронология событий
	 * @throws NullPointerException экземпляр не определен
	 */
	public Timeline getTimeline() {
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
	public void setTimeline(Timeline timeline) {
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
	
}

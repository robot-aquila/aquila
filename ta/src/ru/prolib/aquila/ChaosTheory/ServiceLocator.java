package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.ipc.ISession;
import ru.prolib.aquila.rxltdde.Receiver.ReceiverService;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.jdbc.DbAccessor;
import ru.prolib.aquila.ta.ds.quik.RXltDdeDispatcher;

public interface ServiceLocator {
	
	/**
	 * Получить акцессор подключения к базе данных.
	 * @return
	 * @throws ServiceLocatorException не удалось инициализировать объект 
	 */
	public DbAccessor getDatabase() throws ServiceLocatorException;
	
	/**
	 * Установить акцессор подключения к БД.
	 * @param db
	 * @throws ServiceLocatorException
	 */
	public void setDatabase(DbAccessor db) throws ServiceLocatorException;
	
	/**
	 * Получить источник данных о торгах.
	 * @return
	 * @throws ServiceLocatorException не удалось инициализировать объект 
	 */
	public MarketData getMarketData() throws ServiceLocatorException;
	
	/**
	 * Установить источник данных о торгах.
	 * @param ds 
	 * @throws ServiceLocatorException
	 */
	public void setMarketData(MarketData ds) throws ServiceLocatorException;
	
	/**
	 * Получить объект сессии IPC
	 * @return
	 * @throws ServiceLocatorException не удалось инициализировать объект
	 */
	public ISession getIpcSession() throws ServiceLocatorException;
	
	/**
	 * Установить объект сессии IPC.
	 * @param sess
	 * @throws ServiceLocatorException
	 */
	public void setIpcSession(ISession sess) throws ServiceLocatorException;
	
	/**
	 * Получить диспетчер данных DDE.
	 * @return
	 * @throws ServiceLocatorException не удалось инициализировать объект
	 */
	public RXltDdeDispatcher getRXltDdeDispatcher()
		throws ServiceLocatorException;
	
	/**
	 * Установить диспетчер данных DDE.
	 * @param dispatche
	 * @throws ServiceLocatorException
	 */
	public void setRXltDdeDispatcher(RXltDdeDispatcher dispatcher)
		throws ServiceLocatorException;
	
	/**
	 * Получить приемник данных DDE.
	 * @return
	 * @throws ServiceLocatorException не удалось инициализировать объект
	 */
	public ReceiverService getRXltDdeReceiver() throws ServiceLocatorException;
	
	/**
	 * Установить приемник данных DDE.
	 * @param recv
	 * @throws ServiceLocatorException
	 */
	public void setRXltDdeReceiver(ReceiverService recv)
		throws ServiceLocatorException;
	
	/**
	 * Получить сервис экспорта данных.
	 * @return
	 * @throws ServiceLocatorException
	 */
	public Export getExportService() throws ServiceLocatorException;
	
	/**
	 * Установить сервис экспорта данных.
	 * @param service
	 * @throws ServiceLocatorException
	 */
	public void setExportService(Export service)
		throws ServiceLocatorException;
	
	/**
	 * Получить набор параметров.
	 * @return
	 * @throws ServiceLocatorException
	 */
	public Props getProperties() throws ServiceLocatorException;
	
	/**
	 * Установить набор параметров.
	 * @param props
	 * @throws ServiceLocatorException
	 */
	public void setProperties(Props props)
		throws ServiceLocatorException;
	
	/**
	 * Получить портфель.
	 * @return
	 * @throws ServiceLocatorException
	 */
	public Portfolio getPortfolio() throws ServiceLocatorException;
	
	/**
	 * Установить портфель.
	 * @param port
	 * @throws ServiceLocatorException
	 */
	public void setPortfolio(Portfolio port) throws ServiceLocatorException;
	
	/**
	 * Получить сервис активов.
	 * @return
	 * @throws ServiceLocatorException
	 */
	public Assets getAssets() throws ServiceLocatorException;
	
	/**
	 * Установить сервис активов.
	 * @param service
	 * @throws ServiceLocatorException
	 */
	public void setAssets(Assets service) throws ServiceLocatorException;
	
	/**
	 * Получить объект состояния портфеля.
	 * @return
	 * @throws ServiceLocatorException
	 */
	public PortfolioState getPortfolioState() throws ServiceLocatorException;
	
	/**
	 * Установить объект состояния портфеля.
	 * @param state
	 * @throws ServiceLocatorException
	 */
	public void setPortfolioState(PortfolioState state)
		throws ServiceLocatorException;
	
	/**
	 * Получить объект доступа к заявкам
	 * @return
	 * @throws ServiceLocatorException
	 */
	public PortfolioOrders getPortfolioOrders() throws ServiceLocatorException;
	
	/**
	 * Установить объект доступа к заявкам
	 * @param orders
	 * @throws ServiceLocatorException
	 */
	public void setPortfolioOrders(PortfolioOrders orders)
		throws ServiceLocatorException;
	
	/**
	 * Получить сервис отслеживания трейдов.
	 * @return
	 * @throws ServiceLocatorException
	 */
	public TrackingTrades getTrackingTrades() throws ServiceLocatorException;
	
	/**
	 * Установить сервис отслеживания трейдов.
	 * @param service
	 * @throws ServiceLocatorException
	 */
	public void setTrackingTrades(TrackingTrades service)
		throws ServiceLocatorException;

}

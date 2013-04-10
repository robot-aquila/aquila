package ru.prolib.aquila.quik;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.*;
import ru.prolib.aquila.quik.api.*;
import ru.prolib.aquila.quik.subsys.*;
import ru.prolib.aquila.quik.subsys.row.RowAdapters;
import ru.prolib.aquila.t2q.*;

/**
 * Фабрика QUIK-терминала.
 * <p>
 * 2012-09-21<br>
 * $Id: QUIKFactory.java 548 2013-02-27 03:01:50Z whirlwind $
 */
public class QUIKFactory implements TerminalFactory {
	public static final String DEFAULT_DDE_SERVER;
	public static final String DEFAULT_T2Q_FACTORY;
	
	private static final Logger logger;
	
	static {
		DEFAULT_DDE_SERVER = "ru.prolib.aquila.dde.jddesvr.JddesvrServer";
		DEFAULT_T2Q_FACTORY = "ru.prolib.aquila.t2q.jqt.JQTServiceFactory";
		logger = LoggerFactory.getLogger(QUIKFactory.class);
	}
	
	public QUIKFactory() {
		super();
	}
	
	/**
	 * Создать экземпляр DDE-сервера по-умолчанию.
	 * <p>
	 * @return DDE-сервер
	 * @throws Exception ошибка инстанцирования DDE-сервера
	 */
	public DDEServer createDefaultDDEServer() throws Exception {
		return (DDEServer) Class.forName(DEFAULT_DDE_SERVER)
			.getMethod("getInstance", new Class[] { })
			.invoke(null, new Object[] { });
	}
	
	/**
	 * Создать фабрику сервиса транзакций по-умолчанию.
	 * <p>
	 * @return фабрика сервиса транзакций
	 * @throws Exception ошибка инстанцирования
	 */
	public T2QServiceFactory createDefaultT2QServiceFactory() throws Exception {
		return (T2QServiceFactory)Class.forName(DEFAULT_T2Q_FACTORY)
			.getConstructor(new Class[] { })
			.newInstance(new Object[] { });
	}
	
	/**
	 * Создать терминал.
	 * <p>
	 * Доступные параметры конфигурации:<br>
	 * dde-name - имя DDE-сервиса для экспорта таблиц (обязательный);<br>
	 * quik-path - полный путь к каталогу программы QUIK, с которой будет
	 * устанавливаться соединение (обязательный);<br>
	 * deals - имя таблицы всех сделок. Значение по-умолчанию deals;<br>
	 * orders - имя таблицы заявок. Значение по-умолчанию orders;<br>
	 * stop-orders - имя таблицы стоп-заявок. Значение по-умолчанию
	 * stop-orders;<br>
	 * port-fut - имя таблицы портфелей по деривативам. Значение по-умолчанию
	 * portfolio.fut;<br>
	 * port-stk - имя таблицы портфелей по бумагам. Значение по-умолчанию
	 * portfolio;<br>
	 * pos-fut - имя таблицы позиций по деривативам. Значение по-умолчанию
	 * position.fut;<br>
	 * pos-stk - имя таблицы позиций по бумагам. Значение по-умолчанию
	 * position;<br>
	 * securities - имя таблицы текущих параметров. Значение по-умолчанию
	 * securities;<br>
	 * date-format - формат даты для импортируемых по DDE данных;<br>
	 * time-format - формат времени для импортируемых по DDE данных;<br>
	 * skip-trans2quik - укажи 1, если не нужно подключаться к терминалу через
	 * TRANS2QUIK API. Данная настройка используется только для тех случаев,
	 * когда выполняется работа на основе данных, полученных через DDE.
	 * При включении этой настройки отправка транзакций в терминал становится
	 * недоступной. По-умолчанию значение не определено;<br> 
	 * <p>
	 * @param props параметры конфигурации
	 * @return терминал
	 * @throws Exception ошибка инстанцирования
	 */
	public Terminal createTerminal(Properties props) throws Exception {
		QUIKConfigImpl config = new QUIKConfigImpl();
		config.allDeals = props.getProperty("deals", "deals");
		config.orders = props.getProperty("orders", "orders");
		config.portfoliosFUT = props.getProperty("port-fut", "portfolio.fut");
		config.portfoliosSTK = props.getProperty("port-stk", "portfolio");
		config.positionsFUT = props.getProperty("pos-fut", "position.fut");
		config.positionsSTK = props.getProperty("pos-stk", "position");
		config.securities = props.getProperty("securities", "securities");
		config.stopOrders = props.getProperty("stop-orders", "stop-orders");
		config.serviceName = props.getProperty("dde-name");
		config.quikPath = props.getProperty("quik-path");
		config.dateFormat = props.getProperty("date-format");
		config.timeFormat = props.getProperty("time-format");
		config.skipTRANS2QUIK = props.getProperty("skip-trans2quik") != null;
		return createTerminal(config);
	}
	
	/**
	 * Создать терминал.
	 * <p>
	 * Создает терминал с DDE-сервером и фабрикой сервиса транзакций
	 * по-умолчанию.
	 * <p>
	 * @param config конфигурация терминала
	 * @return терминал
	 * @throws Exception ошибка инстанцирования
	 */
	public Terminal createTerminal(QUIKConfig config) throws Exception {
		return createTerminal(config, createDefaultDDEServer(), true,
				createDefaultT2QServiceFactory());
	}
	
	/**
	 * Создать терминал.
	 * <p>
	 * Данный метод подразумевает, что DDE-сервер управляется извне терминала
	 * (внешним кодом). Указанный сервер не добавляется в цепочку запуска
	 * терминала. 
	 * <p>
	 * @param config конфигурация терминала
	 * @param server DDE-сервер
	 * @param tsf фабрика сервиса транзакций
	 * @return терминал
	 */
	public Terminal createTerminal(QUIKConfig config, DDEServer server,
			T2QServiceFactory tsf)
	{
		return createTerminal(config, server, false, tsf);
	}
	
	/**
	 * Создать терминал.
	 * <p>
	 * Данный метод позволяет явно указывать, следует ли выполнять запуск
	 * указанного сервера при запуске терминала.
	 * <p>
	 * @param config конфигурация терминала
	 * @param server DDE-сервер
	 * @param startServer true - запускать, false - не запускать сервер
	 * @param tsf фабрика сервиса транзакций
	 * @return терминал
	 */
	public Terminal createTerminal(QUIKConfig config,
			final DDEServer server, boolean startServer,
			T2QServiceFactory tsf)
	{
		TerminalDecorator decorator = new TerminalDecorator();
		QUIKServiceLocator locator = new QUIKServiceLocator(decorator);
		locator.setConfig(config);
		
		EventSystem es = locator.getEventSystem();
		DDEObservableServiceImpl service = DDEObservableServiceImpl
			.createService(config.getServiceName(), es);
		
		QUIKCompFactory fc = locator.getCompFactory();
		
		
		StarterQueue starter = new StarterQueue();
		starter.add(new EventQueueStarter(es.getEventQueue(), 10000));
		
		// DDE
		starter.add(new QUIKTableListenerStarter(locator, service));
		if ( startServer ) {
			logger.debug("Add DDE server to start queue");
			starter.add(new DDEServerStarter(server));
		} else {
			logger.debug("DDE server not added to start queue");
		}
		starter.add(new DDEServiceStarter(server, service));
		
		// Make terminal instance
		EventDispatcher dispatcher = es.createEventDispatcher("QUIKTerminal");
		final EditableTerminal terminal = new TerminalImpl(starter,
				fc.createSecurities(RowAdapters.SEC_DEFAULT_CURRENCY,
						SecurityType.STK),
				fc.createPortfolios(), fc.createOrders(), fc.createOrders(),
				fc.createOrderBuilder(),
				locator.getOrderProcessor(),
				dispatcher,
				es.createGenericType(dispatcher, "OnConnected"),
				es.createGenericType(dispatcher, "OnDisconnected"),
				es.createGenericType(dispatcher, "OnStarted"),
				es.createGenericType(dispatcher, "OnStopped"),
				es.createGenericType(dispatcher, "OnPanic"));
		decorator.setTerminal(terminal);
		
		// QUIK API
		if ( ! config.skipTRANS2QUIK() ) {
			ApiServiceHandler apiHandler = createApiHandler(es);
			T2QService quikApiService = tsf.createService(apiHandler);
			ApiService api = new ApiService(quikApiService, apiHandler);
			api.OnConnStatus()
				.addListener(new ConnectionStatusHandler(terminal));
			locator.setApi(api);
			starter.add(new QUIKConnectionKeeper(locator,
				new T2QServiceStarter(quikApiService, config.getQUIKPath())));
		}

		if ( config.skipTRANS2QUIK() ) {
			terminal.OnStarted().addListener(new EventListener() {
				@Override
				public void onEvent(Event event) {
					terminal.fireTerminalConnectedEvent();
				}
			});
		}
		
		return decorator;
	}
	
	private ApiServiceHandler createApiHandler(EventSystem es) {
		EventDispatcher dispatcher = es.createEventDispatcher("Api");
		return new ApiServiceHandler(dispatcher,
				new EventTypeMap<Long>(es, dispatcher),
				es.createGenericType(dispatcher, "connect"),
				es.createGenericType(dispatcher, "order"),
				es.createGenericType(dispatcher, "trade"));
	}


}

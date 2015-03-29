package ru.prolib.aquila.quik;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalReadyIfConnected;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.quik.assembler.*;
import ru.prolib.aquila.quik.assembler.cache.dde.*;

/**
 * Фабрика QUIK-терминала.
 * <p>
 * 2012-09-21<br>
 * $Id: QUIKFactory.java 548 2013-02-27 03:01:50Z whirlwind $
 */
public class QUIKFactory implements TerminalFactory {
	private static final String DEFAULT_DDE_SERVER;
	/**
	 * Нумератор экземпляров.
	 * <p>
	 * Номер экземпляра добавляется в конец идентификатора очереди.
	 */
	private static final Counter id = new SimpleCounter();
	private static DDEServer server;
	
	static {
		DEFAULT_DDE_SERVER = "ru.prolib.aquila.dde.jddesvr.JddesvrServer";
	}
	
	public QUIKFactory() {
		super();
	}
	
	/**
	 * Получить экземпляр DDE-сервера.
	 * <p>
	 * Создает, если еще не создан, и возвращает дефолтную реализацию DDE
	 * сервера. В случае инстанцирования, выполняется попытка запуска сервера.
	 * После запуска сервер будет выполнять работу до завершения работы
	 * программы. Это так же означает, что из-за особенностей WinAPI DDE вы не
	 * сможете запустить никакую иную реализацию DDE сервера. 
	 * <p>
	 * @return DDE-сервер
	 * @throws Exception ошибка инстанцирования DDE-сервера
	 */
	public static synchronized DDEServer getDDEServer() throws Exception {
		if ( server == null ) {
			server = (DDEServer) Class.forName(DEFAULT_DDE_SERVER)
				.getMethod("getInstance", new Class[] { })
				.invoke(null, new Object[] { });
			server.start();
		}
		return server; 
	}
	
	/**
	 * Получить идентификатор для следующего экземпляра терминала.
	 * <p>
	 * @return идентификатор терминала
	 */
	private static final String getNextId() {
		return "QUIK" + id.incrementAndGet();
	}
	
	/**
	 * Создать терминал.
	 * <p>
	 * Доступные параметры конфигурации:<br>
	 * dde-name - имя DDE-сервиса для экспорта таблиц (обязательный);<br>
	 * quik-path - полный путь к каталогу программы QUIK, с которой будет
	 * устанавливаться соединение (обязательный);<br>
	 * deals - имя таблицы всех сделок. Значение по-умолчанию deals;<br>
	 * trades - имя таблицы собственных сделок. По-умолчанию trades;<br>
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
	@Override
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
		config.dateFormat = props.getProperty("date-format",
				((SimpleDateFormat) DateFormat.getDateInstance()).toPattern());
		config.timeFormat = props.getProperty("time-format",
				((SimpleDateFormat) DateFormat.getTimeInstance()).toPattern());
		config.skipTRANS2QUIK = props.getProperty("skip-trans2quik") != null;
		config.trades = props.getProperty("trades", "trades");
		return createTerminal(config);
	}
	
	/**
	 * Создать терминал.
	 * <p>
	 * Создает терминал с DDE-сервером по-умолчанию.
	 * <p>
	 * @param config конфигурация терминала
	 * @return терминал
	 * @throws Exception ошибка инстанцирования
	 */
	public Terminal createTerminal(QUIKConfig config) throws Exception {
		return createTerminal(config, getDDEServer());
	}
	
	/**
	 * Создать терминал.
	 * <p>
	 * Данный метод подразумевает, что DDE-сервер управляется
	 * извне терминала (внешним кодом).
	 * <p>
	 * @param config конфигурация терминала
	 * @param server DDE-сервер
	 * @return терминал
	 */
	public Terminal createTerminal(QUIKConfig config, final DDEServer server) {
		QUIKTerminal terminal = new QUIKTerminal(getNextId());
		StarterQueue starter = (StarterQueue) terminal.getStarter();
		Assembler asm = new Assembler(terminal);
		terminal.getClient().setMainHandler(new MainHandler(terminal, asm));
		terminal.setOrderProcessor(new QUIKOrderProcessor(terminal));
		starter.add(new EventQueueStarter(terminal.getEventSystem()
				.getEventQueue(), 3000));
		starter.add(asm);
		starter.add(new QUIKDDEStarter(config, asm, server));
		starter.add(new ConnectionHandler(terminal, config));
		starter.add(new TerminalReadyIfConnected(terminal));
		return terminal;
	}

}

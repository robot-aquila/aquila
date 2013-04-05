package ru.prolib.aquila.quik.subsys;

import java.util.Timer;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.BMFactoryImpl;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;
import ru.prolib.aquila.quik.QUIKConfig;
import ru.prolib.aquila.quik.subsys.order.QUIKOrderProcessor;
import ru.prolib.aquila.quik.subsys.portfolio.QUIKAccounts;
import ru.prolib.aquila.quik.subsys.security.QUIKSecurityDescriptors;
import ru.prolib.aquila.quik.subsys.security.QUIKSecurityDescriptorsImpl;
import ru.prolib.aquila.t2q.T2QService;

/**
 * Сервис-локатор терминала QUIK.
 * <p>
 * 2013-01-19<br>
 * $Id: QUIKServiceLocator.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class QUIKServiceLocator {
	private final EditableTerminal terminal;
	private final EventSystem es;
	private final QUIKSecurityDescriptors descrs;
	private final QUIKAccounts accounts;
	private final Counter failedOrderId = new SimpleCounter();
	private final Counter transId = new SimpleCounter();
	private QUIKCompFactory fcomp;
	private T2QService transactionService;
	private QUIKOrderProcessor orderProcessor;
	private Timer timer;
	private QUIKConfig config;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 */
	public QUIKServiceLocator(EditableTerminal terminal) {
		super();
		this.terminal = terminal;
		descrs = new QUIKSecurityDescriptorsImpl(this);
		accounts = new QUIKAccounts(terminal);
		es = new EventSystemImpl(new EventQueueImpl("QUIK"));
	}
	
	/**
	 * Получить экземпляр терминала.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить фасад системы событий.
	 * <p>
	 * @return система событий
	 */
	public EventSystem getEventSystem() {
		return es;
	}
	
	/**
	 * Получить хранилище дескрипторов инструментов.
	 * <p>
	 * @return хранилище дескрипторов
	 */
	public QUIKSecurityDescriptors getDescriptors() {
		return descrs;
	}
	
	/**
	 * Получить фабрику компонентов.
	 * <p>
	 * @return фабрика компонентов
	 */
	public synchronized QUIKCompFactory getCompFactory() {
		if ( fcomp == null ) {
			fcomp = new QUIKCompFactory(this, new BMFactoryImpl(es, terminal));
		}
		return fcomp;
	}
	
	/**
	 * Получить нумератор отклоненных заявок.
	 * <p>
	 * @return нумератор
	 */
	public synchronized Counter getFailedOrderNumerator() {
		return failedOrderId;
	}
	
	/**
	 * Получить сервис транзакций QUIK.
	 * <p>
	 * @return сервис транзакций QUIK
	 * @throws QUIKServiceNotAvailableException
	 */
	public synchronized T2QService getTransactionService() {
		if ( transactionService == null ) {
			throw new QUIKServiceNotAvailableException("TRANS2QUIK");
		}
		return transactionService;
	}
	
	/**
	 * Установить сервис транзакций QUIK.
	 * <p>
	 * @param service сервис транзакций QUIK
	 */
	public synchronized void setTransactionService(T2QService service) {
		transactionService = service;
	}
	
	/**
	 * Получить нумератор транзакций.
	 * <p>
	 * @return нумератор транзакций
	 */
	public Counter getTransactionNumerator() {
		return transId;
	}
	
	/**
	 * Получить обработчик заявок.
	 * <p>
	 * @return обработчик заявок
	 */
	public synchronized QUIKOrderProcessor getOrderProcessor() {
		if ( orderProcessor == null ) {
			orderProcessor = new QUIKOrderProcessor(this);
		}
		return orderProcessor;
	}
	
	/**
	 * Получить таймер.
	 * <p>
	 * @return таймер
	 */
	public synchronized Timer getTimer() {
		if ( timer == null ) {
			timer = new Timer(true);
		}
		return timer;
	}
	
	/**
	 * Установить таймер.
	 * <p>
	 * @param timer таймер
	 */
	public synchronized void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	/**
	 * Установить параметры конфигурации терминала.
	 * <p>
	 * @param config конфигурация
	 */
	public synchronized void setConfig(QUIKConfig config) {
		this.config = config;
	}
	
	/**
	 * Получить параметры конфигурации.
	 * <p>
	 * @return конфигурация
	 */
	public synchronized QUIKConfig getConfig() {
		return config;
	}
	
	/**
	 * Получить хранилище счетов.
	 * <p>
	 * @return хранилище счетов
	 */
	public QUIKAccounts getAccounts() {
		return accounts;
	}

}

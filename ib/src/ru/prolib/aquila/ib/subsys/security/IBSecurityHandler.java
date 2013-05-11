package ru.prolib.aquila.ib.subsys.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.ib.event.IBEventContract;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.IBRequestContract;
import ru.prolib.aquila.ib.subsys.api.IBRequestMarketData;

/**
 * Поставщик инструмента.
 * <p>
 * Предоставляя доступ к статусу инструмента, соответствующего указанному
 * при создании объекта дескриптору, прозрачно выполняет создание экземпляра
 * инструмента и установку основных его атрибутов. Таким образом, получение
 * статуса {@link IBSecurityStatus#DONE} означает, что потребитель инструмента
 * может получить экземпляр соответствующего инструмента через набор.
 * <p>
 * 2012-11-18<br>
 * $Id: IBSecurityHandler.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBSecurityHandler implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBSecurityHandler.class);
	}
	
	private final IBServiceLocator locator;
	private final SecurityDescriptor descr;
	private final IBRequestContract reqContract;
	private final IBRequestMarketData reqMktData;
	private final long reqTimeout;
	private final S<EditableSecurity> modifier;
	private final Object monitor = new Object();
	private IBSecurityStatus status = IBSecurityStatus.NONE;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator сервис-локатор
	 * @param descr дескриптор инструмента
	 * @param reqContract запрос на получение деталей контракта
	 * @param reqMktData запрос на получение котировок по инструменту
	 * @param modifier модификатор инструмента
	 * @param timeout таймаут ожидания ответа
	 */
	public IBSecurityHandler(IBServiceLocator locator,
			SecurityDescriptor descr,
			IBRequestContract reqContract,
			IBRequestMarketData reqMktData,
			S<EditableSecurity> modifier, long timeout)
	{
		super();
		this.locator = locator;
		this.descr = descr;
		this.reqContract = reqContract;
		this.reqMktData = reqMktData;
		this.modifier = modifier;
		this.reqTimeout = timeout;
	}
	
	/**
	 * Установить начальный статус инструмента.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @param status статус
	 */
	public synchronized void setInitialStatus(IBSecurityStatus status) {
		this.status = status;
	}
	
	/**
	 * Проверить текущий статус инструмента.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @return соответствие указанному статусу
	 */
	public synchronized boolean isCurrentStatus(IBSecurityStatus expected) {
		return status == expected;
	}
	
	/**
	 * Получить монитор.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @return монитор
	 */
	public Object getMonitor() {
		return monitor;
	}
	
	/**
	 * Получить запрос деталей контракта.
	 * <p>
	 * @return запрос
	 */
	public IBRequestContract getRequestContract() {
		return reqContract;
	}
	
	/**
	 * Получить запрос тиковых данных по контракту.
	 * <p>
	 * @return запрос
	 */
	public IBRequestMarketData getRequestMarketData() {
		return reqMktData;
	}
	
	/**
	 * Получить модификатор инструмента.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableSecurity> getSecurityModifier() {
		return modifier;
	}
	
	/**
	 * Получить таймаут ожидания ответа.
	 * <p>
	 * @return таймаут в мс.
	 */
	public long getRequestTimeout() {
		return reqTimeout;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public IBServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	/**
	 * Получить статус инструмента.
	 * <p>
	 * @return статус инструмента
	 * @throws IBSecurityTimeoutException
	 * @throws IBSecurityInterruptedException
	 */
	public IBSecurityStatus getSecurityStatus() throws SecurityException {
		synchronized ( monitor ) {
			// Статус NONE, указывает на то, что запрос не выполнялся
			// или завершился неудачей. По этому при возникновения исключения
			// в данном методе, статус возвращается к NONE, что бы обеспечить
			// возможность повторного запроса при следующем обращении к методу.
			if ( status == IBSecurityStatus.NONE ) {
				reqContract.start();
				status = IBSecurityStatus.SENT;
			}
			if ( status == IBSecurityStatus.SENT ) {
				try {
					monitor.wait(reqTimeout);
					if ( status == IBSecurityStatus.SENT ) {
						status = IBSecurityStatus.NONE;
						throw new IBSecurityTimeoutException(reqTimeout);
					}
				} catch ( InterruptedException e ) {
					status = IBSecurityStatus.NONE;
					Thread.currentThread().interrupt();
					throw new IBSecurityInterruptedException(e);
				}
			}
			return status;
		}
	}

	@Override
	public void onEvent(Event event) {
		try {
			if ( event.isType(reqContract.OnError()) ) {
				onRequestContractError((IBEventError) event);
			} else if ( event.isType(reqContract.OnResponse()) ) {
				onContractResponse((IBEventContract) event);
			} else if ( event.isType(reqMktData.OnTick()) ) {
				modifier.set(getSecurity(), event);
			} else if ( event.isType(locator.getApiClient()
					.OnConnectionOpened()))
			{
				onConnectionOpened();
			}
		} catch ( ValueException e ) {
			panic(e);
		} catch ( SecurityException e ) {
			panic(e);
		}
	}
	
	/**
	 * Обработать исключение.
	 * <p>
	 * @param e исключение
	 */
	private void panic(Exception e) {
		Object args[] = { e };
		locator.getTerminal()
			.firePanicEvent(1, "Cannot handle security: {}", args);		
	}
	
	/**
	 * Обработать повторное подключение.
	 */
	private void onConnectionOpened() {
		synchronized ( monitor ) {
			// Если хендлер создан и информация по контракту уже была
			// получена, значит нужно рестартнуть запрос котировок после
			// восстановления соединения. 
			if ( status == IBSecurityStatus.DONE ) {
				reqMktData.start();
			}
		}
	}

	/**
	 * Обработать детали контракта.
	 * <p>
	 * @param event событие
	 */
	private void onContractResponse(IBEventContract event)
			throws ValueException, SecurityException
	{
		if ( event.getSubType() == IBEventContract.SUBTYPE_END ) {
			// Данный маркер используется для опционов. В настоящее время
			// работа с опционами через IB не реализована.
			return;
		}
		modifier.set(getSecurity(), event.getContractDetails());
		reqMktData.start();
		synchronized ( monitor ) {
			status = IBSecurityStatus.DONE;
			monitor.notifyAll();
		}
	}
	
	/**
	 * Обработать ответ с ошибкой на запрос контракта.
	 * <p>
	 * @param e событие
	 */
	private void onRequestContractError(IBEventError e) {
		if ( e.getCode() == 200 ) {
			synchronized ( monitor ) {
				status = IBSecurityStatus.NFND;
				monitor.notifyAll();
			}
		}
		// Остальные коды ошибок в данном случае нас не интересуют.
		// По крайней мере до тех пор, пока не станет известна ситуация,
		// при которой понадобится обработка других кодов ошибок. Вывод ошибок
		// IB в информационных целях должен выполняться на более низком уровне.
	}

	public void start() {
		// Сигнал о том, что данный инструмент не существует
		// приходит в виде ошибки с известным кодом.
		reqContract.OnError().addListener(this);
		reqContract.OnResponse().addListener(this);
		reqMktData.OnTick().addListener(this);
		// Событие открытия подключения используется для повторных
		// запросов котировок после восстановления подключения.
		locator.getApiClient().OnConnectionOpened().addListener(this);
	}
	
	/**
	 * Получить связанный с обработчиком инструмент.
	 * <p>
	 * @return инструмент
	 * @throws SecurityException
	 */
	private EditableSecurity getSecurity() throws SecurityException {
		EditableTerminal terminal = locator.getTerminal();
		if ( terminal.isSecurityExists(descr) ) {
			return terminal.getEditableSecurity(descr);
		} else {
			return terminal.createSecurity(descr);
		}
	}

}

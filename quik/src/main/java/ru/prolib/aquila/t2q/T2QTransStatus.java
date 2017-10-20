package ru.prolib.aquila.t2q;

/**
 * Статус транзакции.
 * <p>
 * 2013-01-29<br>
 * $Id: T2QTransStatus.java 457 2013-01-29 11:32:17Z whirlwind $
 */
public class T2QTransStatus {
	
	/**
	 * Транзакция отправлена серверу.
	 */
	public static final T2QTransStatus SENT;
	
	/**
	 * Транзакция получена на сервер QUIK от клиента.
	 */
	public static final T2QTransStatus RECV;
	
	/**
	 * Транзакция выполнена. 
	 */
	public static final T2QTransStatus DONE;
	
	/**
	 * Подразумевает локальную ошибку обработки транзакции. Фактически
	 * свидетельствует о том, что результат транзакции, полученный в обработчик,
	 * отличается от TRANS2QUIK_SUCCESS.
	 */
	public static final T2QTransStatus ERR_NOK;
	
	/**
	 * Ошибка при передаче транзакции в торговую систему, поскольку отсутствует
	 * подключение шлюза ММВБ, повторно транзакция не отправляется.
	 */
	public static final T2QTransStatus ERR_CON;
	
	/**
	 * Транзакция не выполнена торговой системой, код ошибки торговой системы
	 * будет расшифрован в тексте статусного сообщения.
	 */
	public static final T2QTransStatus ERR_TSYS;
	
	/**
	 * Транзакция не прошла проверку сервера QUIK по каким-либо критериям.
	 * Например, проверку на наличие прав у пользователя на отправку транзакции
	 * данного типа.
	 */
	public static final T2QTransStatus ERR_REJ;
	
	/**
	 * Транзакция не прошла проверку лимитов сервера QUIK.
	 */
	public static final T2QTransStatus ERR_LIMIT;

	/**
	 * Транзакция не поддерживается торговой системой. К примеру, попытка
	 * отправить «ACTION = MOVE_ORDERS» на ММВБ.
	 */
	public static final T2QTransStatus ERR_UNSUPPORTED;
	
	/**
	 * Транзакция не прошла проверку правильности электронной подписи. К
	 * примеру, если ключи, зарегистрированные на сервере, не соответствуют
	 * подписи отправленной транзакции.
	 */
	public static final T2QTransStatus ERR_AUTH;

	/**
	 * Не удалось дождаться ответа на транзакцию, т.к. истек таймаут ожидания.
	 * Может возникнуть при подаче транзакций из QPILE.
	 */
	public static final T2QTransStatus ERR_TIMEOUT;
	
	/**
	 * Транзакция отвергнута, т.к. ее выполнение могло привести к кросс-сделке
	 * (т.е. сделке с тем же самым клиентским счетом).
	 */
	public static final T2QTransStatus ERR_CROSS;
	
	/**
	 * Другая ошибка.
	 */
	public static final T2QTransStatus ERR_UNK;
	
	static {
		SENT = new T2QTransStatus("SENT");
		RECV = new T2QTransStatus("RECV");
		DONE = new T2QTransStatus("DONE");
		
		ERR_REJ			= new T2QTransStatus("ERR_REJ");
		ERR_NOK			= new T2QTransStatus("ERR_NOK");
		ERR_CON			= new T2QTransStatus("ERR_CON");
		ERR_TSYS		= new T2QTransStatus("ERR_TSYS");
		ERR_LIMIT		= new T2QTransStatus("ERR_LIMIT");
		ERR_UNSUPPORTED	= new T2QTransStatus("ERR_UNSUPPORTED");
		ERR_AUTH		= new T2QTransStatus("ERR_AUTH");
		ERR_TIMEOUT		= new T2QTransStatus("ERR_TIMEOUT");
		ERR_CROSS		= new T2QTransStatus("ERR_CROSS");
		ERR_UNK			= new T2QTransStatus("ERR_UNK");
	}
	
	private final String code;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param code код статуса
	 */
	private T2QTransStatus(String code) {
		super();
		this.code = code;
	}
	
	/**
	 * Получить код статуса.
	 * <p>
	 * @return код статуса
	 */
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return getCode();
	}

}

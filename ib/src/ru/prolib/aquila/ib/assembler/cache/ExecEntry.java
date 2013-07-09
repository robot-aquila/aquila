package ru.prolib.aquila.ib.assembler.cache;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;

import com.ib.client.*;

/**
 * Кэш-запись сделки.
 * <p>
 * Инкапсулирует данные, полученные через метод execDetails. 
 */
public class ExecEntry extends CacheEntry {
	private static final Logger logger;
	private static final SimpleDateFormat timeFormat;
	private static ExecIdCache idCache = new ExecIdCache();
	
	static {
		logger = LoggerFactory.getLogger(ExecEntry.class);
		timeFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	}
	
	private final Contract contract;
	private final Execution execution;
	
	public ExecEntry(Contract contract, Execution execution) {
		super();
		this.contract = contract;
		this.execution = execution;
	}
	
	/**
	 * Установить экземпляр кэша идентификаторов.
	 * <p>
	 * @param cache экземпляр кэша
	 */
	static synchronized void setIdCache(ExecIdCache cache) {
		idCache = cache;
	}
	
	/**
	 * Получить текущий кэш идентификаторов.
	 * <p>
	 * @return экземпляр кэша идентификаторов
	 */
	static synchronized ExecIdCache getIdCache() {
		return idCache;
	}
	
	/**
	 * Получить объект торгового счета.
	 * <p>
	 * @return торговый счет
	 */
	public Account getAccount() {
		return new Account(execution.m_acctNumber);
	}
	
	/**
	 * Получить дескриптор контракта.
	 * <p>
	 * @return дескриптор контракта
	 */
	public Contract getContract() {
		return contract;
	}
	
	/**
	 * Получить направление сделки.
	 * <p>
	 * @return направление
	 */
	public Direction getDirection() {
		return execution.m_side.equals("BOT") ? Direction.BUY : Direction.SELL;
	}
	
	/**
	 * Получить код биржи.
	 * <p>
	 * @return код биржи
	 */
	public String getExchange() {
		return execution.m_exchange;
	}
	
	/**
	 * Получить номер заявки.
	 * <p>
	 * @return номер заявки
	 */
	public int getOrderId() {
		return execution.m_orderId;
	}
	
	/**
	 * Получить дескриптор сделки.
	 * <p>
	 * @return дескриптор сделки
	 */
	public Execution getExecution() {
		return execution;
	}

	/**
	 * Получить идентификатор сделки.
	 * <p>
	 * @return идентификатор
	 */
	public Long getId() {
		return idCache.getId(execution.m_execId);
	}
	
	/**
	 * Получить оригинальный идентификатор сделки.
	 * <p>
	 * @return идентификатор
	 */
	public String getNativeId() {
		return execution.m_execId;
	}

	/**
	 * Получить цену.
	 * <p>
	 * @return цена
	 */
	public Double getPrice() {
		return execution.m_price;
	}

	/**
	 * Получить количество сделки.
	 * <p>
	 * @return количество
	 */
	public Long getQty() {
		return new Long(execution.m_shares);
	}
	
	/**
	 * Получить время сделки.
	 * <p>
	 * @return время
	 */
	public Date getTime() {
		try {
			return timeFormat.parse(execution.m_time);
		} catch ( ParseException e ) {
			logger.error("Bad time format: {}", execution.m_time);
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ExecEntry.class ) {
			return false;
		}
		ExecEntry o = (ExecEntry) other;
		return new EqualsBuilder()
			.append(o.contract, contract)
			.append(o.execution, execution)
			.isEquals();
	}
	
	/**
	 * Получить идентификатор контракта.
	 * <p>
	 * @return идентификатор контракта
	 */
	public int getContractId() {
		return contract.m_conId;
	}
	
}

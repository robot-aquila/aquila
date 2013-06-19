package ru.prolib.aquila.ib.assembler.cache;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Кэш-запись с деталями контракта.
 */
public class ContractEntry extends CacheEntry {
	private static final Logger logger;
	private static final Map<String, SecurityType> types;
	private static final int DIGITS = 8;
	private static final DecimalFormat format;
	private static final char[] nzero = {'1','2','3','4','5','6','7','8','9'};
	
	static {
		logger = LoggerFactory.getLogger(ContractEntry.class);
		types = new Hashtable<String, SecurityType>();
		types.put("STK",  SecurityType.STK);
		types.put("OPT",  SecurityType.OPT);
		types.put("FUT",  SecurityType.FUT);
		types.put("CASH", SecurityType.CASH);
		format = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
		format.setMaximumFractionDigits(DIGITS);
		format.setGroupingUsed(false);
		format.applyPattern("0." + StringUtils.repeat('0', DIGITS));
	}
	
	private final ContractDetails details;
	
	public ContractEntry(ContractDetails details) {
		super();
		this.details = details;
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * Возвращает дескриптор инструмента, сформированный на основе деталей
	 * контракта. 
	 * <p>
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getSecurityDescriptor() {
		Contract contract = getContract();
		String classCode = contract.m_primaryExch;
		if ( classCode == null ) {
			classCode = contract.m_exchange;
		}
		return new SecurityDescriptor(contract.m_symbol, classCode,
				contract.m_currency, getType());
	}
	
	/**
	 * Получить детали контракта.
	 * <p>
	 * @return детали контракта
	 */
	public ContractDetails getContractDetails() {
		return details;
	}
	
	public Contract getContract() {
		return details.m_summary;
	}
	
	/**
	 * Получить список доступных бирж.
	 * <p>
	 * @return список доступных бирж
	 */
	public List<String> getValidExchanges() {
		List<String> list = new Vector<String>();
		for (String token : StringUtils.split(details.m_validExchanges, ',')) {
			list.add(token);
		}
		return list;
	}
	
	/**
	 * Получить числовой идентификатор IB-контракта.
	 * <p>
	 * @return идентификатор контракта
	 */
	public int getContractId() {
		return getContract().m_conId;
	}
	
	public boolean isSmart() {
		return getValidExchanges().contains("SMART");
	}
	
	public SecurityType getType() {
		SecurityType type = types.get(getContract().m_secType);
		return type == null ? SecurityType.UNK : type;
	}
	
	public Integer getPrecision() {
		return detectPrecision(details.m_minTick);
	}
	
	public Double getMinStepPrice() {
		return details.m_minTick;
	}
	
	public Double getMinStepSize() {
		return getMinStepPrice();
	}
	
	public String getDisplayName() {
		return getContract().m_localSymbol;
	}
	
	/**
	 * Определить точность вещественного значения в знаках после точки.
	 * <p>
	 * @param forValue исходное значение
	 * @return точность
	 */
	private Integer detectPrecision(double forValue) {
		String revValue = StringUtils.reverse(format.format(forValue));
		int index = StringUtils.indexOfAny(revValue, nzero);
		if ( index < 0 ) {
			// Если ни одной ненулевой цифры не было найдено, то возможно 
			// значение слишком мало и лежит за пределами точности 
			// {@link #DIGITS} знаков после точки. Эта ситуация маловероятна,
			// но возможна. Ситуация исключительная, но возможно точность
			// фактически не понадобится. Что бы не заморачиваться раньше
			// времени, просто вернем null и информируем в журнал.
			Object a[] = { forValue, DIGITS };
			logger.warn("Couldn't obtain precision for {}, max digits={}", a);
			return null;
		} else if ( index >= DIGITS ) {
			// Точка в развернутой строке находится на позиции с индексом DIGIT.
			// Если индекс найденной цифры больше чем позиция точки, значит
			// эта цифра относится к целой части. При этом дробная часть
			// полностью состоит из нулей. В таком случае точность значения
			// считаем равной нулю.
			return 0;
		} else {
			return DIGITS - index;			
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ContractEntry.class ) {
			return false;
		}
		ContractEntry o = (ContractEntry) other;
		return new EqualsBuilder()
			.append(details, o.details)
			.isEquals();
	}

}

package ru.prolib.aquila.ib.subsys.contract;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

/**
 * Реализация утилит контрактов.
 * <p>
 * 2013-01-05<br>
 * $Id: IBContractUtilsImpl.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBContractUtilsImpl implements IBContractUtils {
	private static final Map<String, SecurityType> mType;
	
	static {
		mType = new HashMap<String, SecurityType>();
		mType.put("STK",  SecurityType.STK );
		mType.put("OPT",  SecurityType.OPT );
		mType.put("FUT",  SecurityType.FUT );
		mType.put("CASH", SecurityType.CASH);
	}

	private final Map<Integer, SecurityDescriptor> defaultDescr; 
	
	public IBContractUtilsImpl() {
		super();
		defaultDescr = new Hashtable<Integer, SecurityDescriptor>();
	}

	@Override
	public synchronized SecurityDescriptor 
			getAppropriateSecurityDescriptor(ContractDetails details)
	{
		Integer conId = details.m_summary.m_conId;
		SecurityDescriptor descr = defaultDescr.get(conId);
		if ( descr == null ) {
			descr = getSuitableDescriptor(details);
			defaultDescr.put(conId, descr);
		}
		return descr;
	}
	
	/**
	 * Получить подходящий дескриптор.
	 * <p>
	 * @param details детали контракта
	 * @return дескриптор
	 */
	protected SecurityDescriptor getSuitableDescriptor(ContractDetails details) {
		Contract contract = details.m_summary;
		return new SecurityDescriptor(contract.m_symbol,
				getSuitableExchange(details),
				contract.m_currency,
				getSuitableType(contract));
	}
	
	/**
	 * Получить подходящую биржу.
	 * <p>
	 * Если список доступных бирж содержит SMART, то возвращает SMART. Иначе,
	 * если список валидных бирж не пуст, то возвращает первую биржу из списка.
	 * Если список бирж пуст, то возвращает первичную биржу из атрибутов
	 * контракта. Если и первичная биржа не определена, то возвращает null.
	 * <p>
	 * @param details детали контракта
	 * @return подходящая биржа
	 */
	protected String getSuitableExchange(ContractDetails details) {
		String[] valid = StringUtils.split(details.m_validExchanges, ',');
		if ( valid == null || valid.length == 0 ) {
			return details.m_summary.m_primaryExch;
		}
		int index = ArrayUtils.indexOf(valid, "SMART");
		if ( index >= 0 ) return "SMART";
		return valid[0];
	}
	
	protected SecurityType getSuitableType(Contract contract) {
		SecurityType type = mType.get(contract.m_secType);
		return type == null ? SecurityType.UNK : type;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBContractUtilsImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		if ( other == this ) {
			return true;
		}
		IBContractUtilsImpl o = (IBContractUtilsImpl) other;
		return new EqualsBuilder()
			.append(defaultDescr, o.defaultDescr)
			.isEquals();
	}

}

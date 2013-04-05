package ru.prolib.aquila.ib.getter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.G;

/**
 * Конвертер дескриптора инструмента в экземпляр IB контракта.
 * <p>
 * 2012-12-19<br>
 * $Id: IBGetSecurityDescriptorContract.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetSecurityDescriptorContract implements G<Contract> {
	private final static Map<SecurityType, String> mType;
	static {
		mType = new HashMap<SecurityType, String>();
		mType.put(SecurityType.BOND, "STK");
		mType.put(SecurityType.CASH, "CASH");
		mType.put(SecurityType.FUT, "FUT");
		mType.put(SecurityType.OPT, "OPT");
		mType.put(SecurityType.STK, "STK");
		mType.put(SecurityType.UNK, "STK");
	}
	
	public IBGetSecurityDescriptorContract() {
		super();
	}

	@Override
	public Contract get(Object source) {
		if ( source instanceof SecurityDescriptor ) {
			SecurityDescriptor descr = (SecurityDescriptor) source;
			Contract contract = new Contract();
			contract.m_symbol = descr.getCode();
			contract.m_exchange = descr.getClassCode();
			contract.m_currency = descr.getCurrency();
			contract.m_secType = mType.get(descr.getType());
			return contract;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetSecurityDescriptorContract.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121219, 5615).toHashCode();
	}

}

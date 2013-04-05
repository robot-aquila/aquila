package ru.prolib.aquila.ib.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.Contract;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;

/**
 * Геттер дескриптора инструмента на основе контракта.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetSecurityDescriptor.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class IBGetSecurityDescriptor
	extends IBGetContractAttr<SecurityDescriptor>
{
	private static Logger logger;
	private final IBContracts contracts;
	
	static {
		logger = LoggerFactory.getLogger(IBGetSecurityDescriptor.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param contracts фасад подсистемы контрактов
	 */
	public IBGetSecurityDescriptor(IBContracts contracts) {
		super();
		this.contracts = contracts;
	}
	
	/**
	 * Получить фасад подсистемы контрактов.
	 * <p>
	 * @return фасад подсистемы контрактов
	 */
	public IBContracts getContracts() {
		return contracts;
	}

	@Override
	protected SecurityDescriptor getContractAttr(Contract contract) {
		try {
			return contracts.getAppropriateSecurityDescriptor(contract.m_conId);
		} catch ( IBException e ) {
			logger.error("Unable to get security descriptor:", e);
			return null;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121215, 173649)
			.append(IBGetSecurityDescriptor.class)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBGetSecurityDescriptor.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBGetSecurityDescriptor o = (IBGetSecurityDescriptor) other;
		return new EqualsBuilder()
			.append(contracts, o.contracts)
			.isEquals();
	}

}

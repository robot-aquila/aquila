package ru.prolib.aquila.ib.subsys.contract;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.ib.IBException;

import com.ib.client.ContractDetails;

/**
 * Интерфейс утилит контракта.
 * <p>
 * 2013-01-05<br>
 * $Id: IBContractUtils.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface IBContractUtils {
	
	/**
	 * Сформировать наиболее подходящий дескриптор инструмента.
	 * <p>
	 * @param details детали контракта
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor
			getAppropriateSecurityDescriptor(ContractDetails details)
					throws IBException;

}

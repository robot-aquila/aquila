package ru.prolib.aquila.ib.subsys.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.ib.IBException;

import com.ib.client.ContractDetails;

/**
 * Реализация фасада подсистемы контрактов.
 * <p>
 * 2013-01-07<br>
 * $Id: IBContractsImpl.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBContractsImpl implements IBContracts {
	private final IBContractsStorage storage;
	private final IBContractUtils utils;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param storage хранилище контрактов
	 * @param utils утилиты
	 */
	public IBContractsImpl(IBContractsStorage storage, IBContractUtils utils) {
		super();
		this.storage = storage;
		this.utils = utils;
	}
	
	/**
	 * Получить хранилище контрактов.
	 * <p>
	 * @return хранилище контрактов
	 */
	public IBContractsStorage getContractsStorage() {
		return storage;
	}
	
	/**
	 * Получить утилиты.
	 * <p>
	 * @return утилиты
	 */
	public IBContractUtils getContractUtils() {
		return utils;
	}

	@Override
	public ContractDetails getContract(int conId) throws IBException {
		return storage.getContract(conId);
	}

	@Override
	public boolean isContractAvailable(int conId) {
		return storage.isContractAvailable(conId);
	}

	@Override
	public void loadContract(int conId) {
		storage.loadContract(conId);
	}

	@Override
	public EventType OnContractLoadedOnce(int conId) {
		return storage.OnContractLoadedOnce(conId);
	}

	@Override
	public void start() {
		storage.start();
	}

	@Override
	public SecurityDescriptor
			getAppropriateSecurityDescriptor(ContractDetails details)
					throws IBException
	{
		return utils.getAppropriateSecurityDescriptor(details);
	}

	@Override
	public SecurityDescriptor getAppropriateSecurityDescriptor(int conId)
			throws IBException
	{
		return
			utils.getAppropriateSecurityDescriptor(storage.getContract(conId));
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBContractsImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBContractsImpl o = (IBContractsImpl) other;
		return new EqualsBuilder()
			.append(storage, o.storage)
			.append(utils, o.utils)
			.isEquals();
	}

}

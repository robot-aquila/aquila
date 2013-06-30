package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.api.ContractHandler;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.api.MainHandler;

/**
 * Обработчик запроса инструмента по идентификатору контракта.
 * <p>
 * Выполняет роль диспетчера данных в основной обработчик. Независимо от
 * результата, выполняет удаление самого себя из реестра обработчиков клиента.
 * Собственную регистрацию в реестре обработчиков не выполняет.
 */
public class IBRequestContractHandler implements ContractHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBRequestContractHandler.class);
	}
	
	private final IBEditableTerminal terminal;
	private final int reqId;
	private final int contractId;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal экземпляр терминала
	 * @param reqId номер запроса
	 * @param contractId идентификатор контракта
	 */
	public IBRequestContractHandler(IBEditableTerminal terminal,
			int reqId, int contractId)
	{
		super();
		this.terminal = terminal;
		this.reqId = reqId;
		this.contractId = contractId;
	}
	
	int getRequestId() {
		return reqId;
	}
	
	IBEditableTerminal getTerminal() {
		return terminal;
	}

	int getContractId() {
		return contractId;
	}

	@Override
	public void error(int reqId, int errorCode, String errorMsg) {
		getMainHandler().error(reqId, errorCode, errorMsg);
		getClient().removeHandler(reqId);
	}

	@Override
	public void connectionOpened() {
		Contract contract = new Contract();
		contract.m_conId = contractId;
		getClient().reqContractDetails(reqId, contract);
	}

	@Override
	public void connectionClosed() {

	}

	@Override
	public void contractDetails(int reqId, ContractDetails details) {
		logger.debug("Response for contract request: {}", contractId);
		getMainHandler().contractDetails(reqId, details);
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails details) {
		logger.debug("Response for contract request: {}", contractId);
		getMainHandler().bondContractDetails(reqId, details);
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		getClient().removeHandler(reqId);
	}

	@Override
	public void tickPrice(int reqId, int tickType, double value) {

	}

	@Override
	public void tickSize(int reqId, int tickType, int size) {

	}
	
	/**
	 * Получить базовый обработчик данных.
	 * <p>
	 * @return текущий базовый обработчик
	 */
	private MainHandler getMainHandler() {
		return getClient().getMainHandler();
	}
	
	/**
	 * Получить экземпляр подключения к API.
	 * <p>
	 * @return подключение
	 */
	private IBClient getClient() {
		return terminal.getClient();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if (other==null || other.getClass() != IBRequestContractHandler.class) {
			return false;
		}
		IBRequestContractHandler o = (IBRequestContractHandler) other;
		return new EqualsBuilder()
			.append(o.contractId, contractId)
			.append(o.reqId, reqId)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}

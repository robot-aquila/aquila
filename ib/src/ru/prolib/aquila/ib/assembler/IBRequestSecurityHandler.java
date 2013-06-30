package ru.prolib.aquila.ib.assembler;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.api.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

/**
 * Обработчик запроса инструмента по дескриптору.
 * <p>
 * В случае успешного запроса, выполняет роль диспетчера данных в основной
 * обработчик. В случае ошибки, инициирует вызов соответствующего метода
 * терминала. Независимо от результата, выполняет удаление самого себя из
 * реестра обработчиков клиента. Собственную регистрацию в реестре не выполняет.
 */
public class IBRequestSecurityHandler implements ContractHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBRequestSecurityHandler.class);
	}
	
	private static final Map<SecurityType, String> types;
	
	static {
		types = new Hashtable<SecurityType, String>();
		types.put(SecurityType.BOND, "STK");
		types.put(SecurityType.CASH, "CASH");
		types.put(SecurityType.FUT, "FUT");
		types.put(SecurityType.OPT, "OPT");
		types.put(SecurityType.STK, "STK");
		types.put(SecurityType.UNK, "STK");
	}
	
	private final SecurityDescriptor descr;
	private final int reqId;
	private final IBEditableTerminal terminal;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal экземпляр терминала
	 * @param reqId номер запроса
	 * @param descr дескриптор запрашиваемого инструмента
	 */
	public IBRequestSecurityHandler(IBEditableTerminal terminal, int reqId,
			SecurityDescriptor descr)
	{
		super();
		this.descr = descr;
		this.reqId = reqId;
		this.terminal = terminal;
	}
	
	SecurityDescriptor getSecurityDescriptor() {
		return descr;
	}
	
	int getRequestId() {
		return reqId;
	}
	
	IBEditableTerminal getTerminal() {
		return terminal;
	}

	@Override
	public void error(int reqId, int errorCode, String errorMsg) {
		getMainHandler().error(reqId, errorCode, errorMsg);
		terminal.fireSecurityRequestError(descr, errorCode, errorMsg);
		getClient().removeHandler(reqId);
	}

	@Override
	public void connectionOpened() {
		Contract contract = new Contract();
		contract.m_symbol = descr.getCode();
		contract.m_exchange = descr.getClassCode();
		contract.m_currency = descr.getCurrency();
		contract.m_secType = types.get(descr.getType());
		getClient().reqContractDetails(reqId, contract);
	}

	@Override
	public void connectionClosed() {
		
	}

	@Override
	public void contractDetails(int reqId, ContractDetails details) {
		Object args[] = { descr, details.m_summary.m_conId };
		logger.debug("Response for security {} request: {}", args);
		getMainHandler().contractDetails(reqId, details);
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails details) {
		Object args[] = { descr, details.m_summary.m_conId };
		logger.debug("Response for security {} request: {}", args);
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
		if (other==null || other.getClass() != IBRequestSecurityHandler.class) {
			return false;
		}
		IBRequestSecurityHandler o = (IBRequestSecurityHandler) other;
		return new EqualsBuilder()
			.append(o.reqId, reqId)
			.append(o.descr, descr)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}

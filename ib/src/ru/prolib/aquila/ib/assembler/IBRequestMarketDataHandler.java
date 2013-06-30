package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.TickType;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.api.ContractHandler;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.api.MainHandler;
import ru.prolib.aquila.ib.assembler.cache.ContractEntry;

/**
 * Обработчик запроса котировок инструмента.
   */
public class IBRequestMarketDataHandler implements ContractHandler {
	private final IBEditableTerminal terminal;
	private final EditableSecurity security;
	private final int reqId;
	private final ContractEntry entry;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал инструмента
	 * @param security целевой инструмент котировок
	 * @param reqId номер запроса
	 * @param entry детали контракта
	 */
	public IBRequestMarketDataHandler(IBEditableTerminal terminal,
			EditableSecurity security, int reqId, ContractEntry entry)
	{
		super();
		this.terminal = terminal;
		this.security = security;
		this.reqId = reqId;
		this.entry = entry;
	}
	
	IBEditableTerminal getTerminal() {
		return terminal;
	}
	
	EditableSecurity getSecurity() {
		return security;
	}
	
	int getRequestId() {
		return reqId;
	}
	
	ContractEntry getContractEntry() {
		return entry;
	}

	@Override
	public void error(int reqId, int errorCode, String errorMsg) {
		getMainHandler().error(reqId, errorCode, errorMsg);
		getClient().removeHandler(reqId);
	}

	@Override
	public void connectionOpened() {
		Contract contract = new Contract();
		contract.m_conId = entry.getContractId();
		contract.m_exchange = entry.getDefaultExchange();
		getClient().reqMktData(reqId, contract, null, false);
	}

	@Override
	public void connectionClosed() {

	}

	@Override
	public void contractDetails(int reqId, ContractDetails details) {

	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails details) {

	}

	@Override
	public void contractDetailsEnd(int reqId) {

	}

	@Override
	public void tickPrice(int reqId, int tickType, double value) {
		switch ( tickType ) {
			case TickType.ASK:
				security.setAskPrice(value);
				break;
			case TickType.BID:
				security.setBidPrice(value);
				break;
			case TickType.LAST:
				security.setLastPrice(value);
				break;
			case TickType.OPEN:
				security.setOpenPrice(value);
				break;
			case TickType.HIGH:
				security.setHighPrice(value);
				break;
			case TickType.LOW:
				security.setLowPrice(value);
				break;
			case TickType.CLOSE:
				security.setClosePrice(value);
				break;
		}
	}

	@Override
	public void tickSize(int reqId, int tickType, int size) {
		switch ( tickType ) {
			case TickType.ASK_SIZE:
				security.setAskSize(new Long(size));
				break;
			case TickType.BID_SIZE:
				security.setBidSize(new Long(size));
				break;
		}

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
		if ( other == null ||
				other.getClass() != IBRequestMarketDataHandler.class )
		{
			return false;
		}
		IBRequestMarketDataHandler o = (IBRequestMarketDataHandler) other;
		return new EqualsBuilder()
			.appendSuper(terminal == o.terminal && security == o.security )
			.append(reqId, o.reqId)
			.append(entry, o.entry)
			.isEquals();
	}

}

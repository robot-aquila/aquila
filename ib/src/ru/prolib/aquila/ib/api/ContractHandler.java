package ru.prolib.aquila.ib.api;

import com.ib.client.*;

/**
 * Интерфейс обработчика запроса данных контракта.
 */
public interface ContractHandler extends ResponseHandler {
	
	public void contractDetails(int reqId, ContractDetails details);
	
	public void bondContractDetails(int reqId, ContractDetails details);
	
	public void contractDetailsEnd(int reqId);
	
	public void tickPrice(int reqId, int tickType, double value);
	
	public void tickSize(int reqId, int tickType, int size);

}

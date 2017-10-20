package ru.prolib.aquila.ib.api;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;

/**
 * Интерфейс базового обработчика данных.
 * <p>
 * Представляет собой композицию методов-обработчиков из набора
 * {@link com.ib.client.EWrapper}. Содержит прототипы методов обработки данных,
 * не подразумевающих разрез по номерам запросов. Интерфейсы
 * узкоспециализированных обработчиков объект наследует с целью обработки
 * запросов, для которых узкоспециализированный обработчик не определен.
 */
public interface MainHandler
	extends ResponseHandler, ContractHandler, OrderHandler
{
	
	public void accountDownloadEnd(String accountName);
	
	public void commissionReport(CommissionReport report);
	
	public void currentTime(long time);
	
	public void managedAccounts(String accounts);
	
	public void nextValidId(int orderId);
	
	public void updateAccount(String key, String value, String currency,
			String accountName);
	
	public void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName);

}

package ru.prolib.aquila.ib.utils;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.subsys.api.IBWrapper;

/**
 * $Id$
 */
public class IBUtilsWrapper extends IBWrapper {

	/**
	 * @param dispatcher
	 * @param onConnectionClosed
	 * @param onError
	 * @param onNextValidId
	 * @param onContractDetails
	 * @param onManagedAccounts
	 * @param onUpdateAccount
	 * @param onUpdatePortfolio
	 * @param onOpenOrder
	 * @param onOrderStatus
	 * @param onTick
	 */
	private EventType onHistoricalData;
	
	public IBUtilsWrapper(EventDispatcher dispatcher,
			EventType onConnectionClosed, EventType onError,
			EventType onNextValidId, EventType onContractDetails,
			EventType onManagedAccounts, EventType onUpdateAccount,
			EventType onUpdatePortfolio, EventType onOpenOrder,
			EventType onOrderStatus, EventType onTick, EventType onHistoricalData) {
		super(dispatcher, onConnectionClosed, onError, onNextValidId,
				onContractDetails, onManagedAccounts, onUpdateAccount,
				onUpdatePortfolio, onOpenOrder, onOrderStatus, onTick);
		this.onHistoricalData = onHistoricalData;
	}
	
	@Override
	public void historicalData(int reqId, String date, double open,
			double high, double low, double close, int volume, int count,
			double WAP, boolean hasGaps)
	{
		IBHistoricalRow row = new IBHistoricalRow(
				reqId, date, open, high, low, close, volume, hasGaps);
		getEventDispatcher().dispatch(new IBEventHistoricalData(onHistoricalData, row));
	}
	
	public EventType OnHistoricalData() {
		return onHistoricalData;
	}

}

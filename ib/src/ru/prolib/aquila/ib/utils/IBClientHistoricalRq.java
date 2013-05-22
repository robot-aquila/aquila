package ru.prolib.aquila.ib.utils;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.subsys.api.IBApiEventDispatcher;
import ru.prolib.aquila.ib.subsys.api.IBClientImpl;

/**
 * $Id$
 */
public class IBClientHistoricalRq extends IBClientImpl {

	/**
	 * @param socket
	 * @param wrapper
	 * @param dispatcher
	 * @param onConnectionOpened
	 * @param onConnectionClosed
	 */
	public IBClientHistoricalRq(EClientSocket socket,
			IBApiEventDispatcher wrapper, EventDispatcher dispatcher,
			EventType onConnectionOpened, EventType onConnectionClosed) {
		super(socket, wrapper, dispatcher, onConnectionOpened, onConnectionClosed);
	}
	
	public void reqHistoricalData(int id, Contract contract, String endDateTime, 
			String durationStr, String barSizeSetting, String whatToShow, 
			int useRTH, int formatDate)
	{
		getSocket().reqHistoricalData(id, contract, endDateTime, durationStr, 
				barSizeSetting, whatToShow, useRTH, formatDate);
	}

}

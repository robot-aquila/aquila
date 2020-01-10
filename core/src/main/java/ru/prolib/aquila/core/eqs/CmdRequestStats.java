package ru.prolib.aquila.core.eqs;

import ru.prolib.aquila.core.EventQueueStats;

public class CmdRequestStats extends CmdRequestObject<EventQueueStats> {

	public CmdRequestStats() {
		super(CmdType.GET_STATS);
	}

}

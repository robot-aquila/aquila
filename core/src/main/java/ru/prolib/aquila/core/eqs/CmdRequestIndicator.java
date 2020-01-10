package ru.prolib.aquila.core.eqs;

import ru.prolib.aquila.core.FlushIndicator;

public class CmdRequestIndicator extends CmdRequestObject<FlushIndicator> {

	public CmdRequestIndicator() {
		super(CmdType.CREATE_INDICATOR);
	}

}

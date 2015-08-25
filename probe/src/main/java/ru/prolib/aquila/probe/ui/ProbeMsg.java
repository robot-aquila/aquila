package ru.prolib.aquila.probe.ui;

import ru.prolib.aquila.core.text.MsgID;

public class ProbeMsg {
	private static final String SECTION_ID = "Probe";
	
	public static MsgID msgID(String messageId) {
		return new MsgID(SECTION_ID, messageId);
	}

}

package ru.prolib.aquila.qforts.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class QFortsMsg {
	public static final String SECTION_ID = "QForts";
	
	static{
		Messages.registerLoader(SECTION_ID, QFortsMsg.class.getClassLoader());
	}
	
	private static MsgID newMsgID(String messageID) {
		return new MsgID(SECTION_ID, messageID);
	}
	
	public static final MsgID VMARGIN = newMsgID("TXT_VMARGIN");
	public static final MsgID VMARGIN_INTER = newMsgID("TXT_VMARGIN_INTER");
	public static final MsgID VMARGIN_CLOSE = newMsgID("TXT_VMARGIN_CLOSE");
	
}

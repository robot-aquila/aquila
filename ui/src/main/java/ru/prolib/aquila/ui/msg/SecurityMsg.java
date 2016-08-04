package ru.prolib.aquila.ui.msg;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class SecurityMsg {
	public static final String SECTION_ID = "Security";
	
	static {
		Messages.registerLoader(SECTION_ID, SecurityMsg.class.getClassLoader());
	}
	
	public static final MsgID SYMBOL = newMsgID("COL_SYMBOL");
	public static final MsgID EXCHANGE = newMsgID("COL_EXCHANGE");
	public static final MsgID NAME = newMsgID("COL_NAME");
	public static final MsgID LOT_SIZE = newMsgID("COL_LOT");
	public static final MsgID TICK_SIZE = newMsgID("COL_TICK_SIZE");
	public static final MsgID SCALE = newMsgID("COL_SCALE");
	public static final MsgID CURRENCY = newMsgID("COL_CURRENCY");
	public static final MsgID TYPE = newMsgID("COL_TYPE");
	public static final MsgID LAST_PRICE = newMsgID("COL_LAST_PRICE");
	public static final MsgID LAST_SIZE = newMsgID("COL_LAST_SIZE");
	public static final MsgID OPEN_PRICE = newMsgID("COL_OPEN_PRICE");
	public static final MsgID HIGH_PRICE = newMsgID("COL_HIGH_PRICE");
	public static final MsgID LOW_PRICE = newMsgID("COL_LOW_PRICE");
	public static final MsgID CLOSE_PRICE = newMsgID("COL_CLOSE_PRICE");
	public static final MsgID ASK_PRICE = newMsgID("COL_ASK_PRICE");
	public static final MsgID ASK_SIZE = newMsgID("COL_ASK_SIZE");
	public static final MsgID BID_PRICE = newMsgID("COL_BID_PRICE");
	public static final MsgID BID_SIZE = newMsgID("COL_BID_SIZE");
	public static final MsgID LOWER_PRICE = newMsgID("COL_LOWER_PRICE_LIMIT");
	public static final MsgID UPPER_PRICE = newMsgID("COL_UPPER_PRICE_LIMIT");
	public static final MsgID TICK_VALUE = newMsgID("COL_TICK_VALUE");
	public static final MsgID SETTLEMENT_PRICE = newMsgID("COL_SETTLEMENT_PRICE");
	public static final MsgID INITIAL_MARGIN = newMsgID("COL_INITIAL_MARGIN");
	public static final MsgID TERMINAL_ID = newMsgID("COL_TERMINAL_ID");
	public static final MsgID SECURITIES = newMsgID("SECURITIES");
	public static final MsgID SELECT_SECURITY = newMsgID("SELECT_SECURITY");
	public static final MsgID SHOW_SECURITIES = newMsgID("SHOW_SECURITIES");
	
	public static MsgID newMsgID(String messageId) {
		return new MsgID(SECTION_ID, messageId);
	}
}

package ru.prolib.aquila.ui.msg;

import ru.prolib.aquila.core.text.MsgID;

public class SecurityMsg {
	public static final String SECTION_ID = "UISecuritiesPlugin";
	
	public static final MsgID SYMBOL = newMsgID("COL_SYMBOL");
	public static final MsgID CLASS = newMsgID("COL_CLASS");
	public static final MsgID NAME = newMsgID("COL_NAME");
	public static final MsgID LOT_SIZE = newMsgID("COL_LOT");
	public static final MsgID TICK_SIZE = newMsgID("COL_TICK");
	public static final MsgID PRICE_PREC = newMsgID("COL_PREC");
	public static final MsgID CURRENCY = newMsgID("COL_CURR");
	public static final MsgID TYPE = newMsgID("COL_TYPE");
	public static final MsgID LAST_PRICE = newMsgID("COL_LAST");
	public static final MsgID OPEN_PRICE = newMsgID("COL_OPEN");
	public static final MsgID HIGH_PRICE = newMsgID("COL_HIGH");
	public static final MsgID LOW_PRICE = newMsgID("COL_LOW");
	public static final MsgID CLOSE_PRICE = newMsgID("COL_CLOSE");
	public static final MsgID ASK_PRICE = newMsgID("COL_ASK");
	public static final MsgID ASK_SIZE = newMsgID("COL_ASK_SIZE");
	public static final MsgID BID_PRICE = newMsgID("COL_BID");
	public static final MsgID BID_SIZE = newMsgID("COL_BID_SIZE");
	public static final MsgID STATUS = newMsgID("COL_STATUS");
	public static final MsgID MIN_PRICE = newMsgID("COL_MIN");
	public static final MsgID MAX_PRICE = newMsgID("COL_MAX");
	public static final MsgID TICK_PRICE = newMsgID("COL_STEP_PRICE");
	public static final MsgID INIT_PRICE = newMsgID("COL_INIT_PRICE");
	public static final MsgID INIT_MARGIN = newMsgID("COL_INIT_MARGIN");
	
	public static final MsgID SECURITIES_TITLE = newMsgID("TAB_SECURITIES");
	public static final MsgID SECURITIES_MENU = newMsgID("MENU_SEC");
	public static final MsgID SELECT_SECURITY = newMsgID("SELECT_SECURITY");
	public static final MsgID SHOW_SECURITIES = newMsgID("SHOW_SECURITIES");
	
	public static MsgID newMsgID(String messageId) {
		return new MsgID(SECTION_ID, messageId);
	}
}

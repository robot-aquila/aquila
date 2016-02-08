package ru.prolib.aquila.ui.msg;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class CommonMsg {
	public static final String SECTION_ID = "Common";
	
	static {
		Messages.registerLoader(SECTION_ID, CommonMsg.class.getClassLoader());
	}
	
	public static final MsgID OK = new MsgID(SECTION_ID, "ID_OK");
	public static final MsgID CANCEL = new MsgID(SECTION_ID, "ID_CANCEL");
	public static final MsgID SELECT = new MsgID(SECTION_ID, "ID_SELECT");
	public static final MsgID CLOSE = new MsgID(SECTION_ID, "ID_CLOSE");
	public static final MsgID PORTFOLIO = new MsgID(SECTION_ID, "ID_PORTFOLIO");
	public static final MsgID TITLE_SELECT_PORTFOLIO = new MsgID(SECTION_ID, "ID_SELECT_PORTFOLIO");
	public static final MsgID TITLE_SHOW_PORTFOLIOS = new MsgID(SECTION_ID, "ID_SHOW_PORTFOLIOS");
	public static final MsgID CODE = new MsgID(SECTION_ID, "ID_CODE");
	public static final MsgID SUBCODE = new MsgID(SECTION_ID, "ID_SUBCODE");
	public static final MsgID SUBCODE2 = new MsgID(SECTION_ID, "ID_SUBCODE2");
	public static final MsgID CURRENCY = new MsgID(SECTION_ID, "ID_CURRENCY");
	public static final MsgID TERMINAL = new MsgID(SECTION_ID, "ID_TERMINAL");
	public static final MsgID DRIVER = new MsgID(SECTION_ID, "ID_DRIVER");
	public static final MsgID TITLE_SELECT_DRIVER = new MsgID(SECTION_ID, "ID_SELECT_DRIVER");
	public static final MsgID DISCONNECTED = new MsgID(SECTION_ID, "ID_DISCONNECTED");	
	public static final MsgID CONNECTED = new MsgID(SECTION_ID, "ID_CONNECTED");
	public static final MsgID STARTED = new MsgID(SECTION_ID, "ID_STARTED");
	public static final MsgID STOPPED = new MsgID(SECTION_ID, "ID_STOPPED");
	public static final MsgID MENU_FILE = new MsgID(SECTION_ID, "MENU_FILE");
	public static final MsgID MENU_TERM = new MsgID(SECTION_ID, "MENU_TERM");
	public static final MsgID MENU_FILE_EXIT = new MsgID(SECTION_ID, "MENU_FILE_EXIT");
	public static final MsgID MENU_TERM_START = new MsgID(SECTION_ID, "MENU_TERM_START");
	public static final MsgID MENU_TERM_STOP = new MsgID(SECTION_ID, "MENU_TERM_STOP");
	public static final MsgID MENU_VIEW = new MsgID(SECTION_ID, "MENU_VIEW");
	public static final MsgID MENU_VIEW_PORTFOLIO_STATUS = new MsgID(SECTION_ID, "MENU_VIEW_PORTFOLIO_STATUS");
	public static final MsgID ORDERS = new MsgID(SECTION_ID, "ID_ORDERS");
	public static final MsgID MENU_ORDER = new MsgID(SECTION_ID, "MENU_ORDER");
	public static final MsgID MENU_ORDER_CANCEL = new MsgID(SECTION_ID, "MENU_ORDER_CANCEL");
	public static final MsgID CASH = new MsgID(SECTION_ID, "ID_CASH");  
	public static final MsgID BALANCE = new MsgID(SECTION_ID, "ID_BALANCE");
	public static final MsgID ACCOUNT = new MsgID(SECTION_ID, "ID_ACCOUNT");
	public static final MsgID VMARGIN = new MsgID(SECTION_ID, "ID_VMARGIN");
	public static final MsgID SECURITY = new MsgID(SECTION_ID, "ID_SECURITY");
	public static final MsgID TYPE = new MsgID(SECTION_ID, "ID_TYPE");
	public static final MsgID CURR_VAL = new MsgID(SECTION_ID, "ID_CURR_VAL");
	public static final MsgID MARKET_VAL = new MsgID(SECTION_ID, "ID_MARKET_VAL");
	public static final MsgID LOCKED_VAL = new MsgID(SECTION_ID, "ID_LOCKED_VAL");
	public static final MsgID OPEN_VAL = new MsgID(SECTION_ID, "ID_OPEN_VAL");
	public static final MsgID BALANCE_VAL = new MsgID(SECTION_ID, "ID_BALANCE_VAL");
	public static final MsgID POSITIONS = new MsgID(SECTION_ID, "ID_POSITIONS");
	public static final MsgID ID = new MsgID(SECTION_ID, "ID_ID");
	public static final MsgID DIR = new MsgID(SECTION_ID, "ID_DIR");
	public static final MsgID TIME = new MsgID(SECTION_ID, "ID_TIME");
	public static final MsgID CHNG_TIME = new MsgID(SECTION_ID, "ID_CHNG_TIME");
	public static final MsgID QTY = new MsgID(SECTION_ID, "ID_QTY");
	public static final MsgID STATUS = new MsgID(SECTION_ID, "ID_STATUS");
	public static final MsgID QTY_REST = new MsgID(SECTION_ID, "ID_QTY_REST");
	public static final MsgID PRICE = new MsgID(SECTION_ID, "ID_PRICE");
	public static final MsgID EXEC_VOL = new MsgID(SECTION_ID, "ID_EXEC_VOL");
	public static final MsgID AVG_EXEC_PRICE = new MsgID(SECTION_ID, "ID_AVG_EXEC_PRICE");
	public static final MsgID ACTIVATOR = new MsgID(SECTION_ID, "ID_ACTIVATOR");
	public static final MsgID COMMENT = newMsgID("ID_COMMENT");
	public static final MsgID EQUITY = newMsgID("ID_EQUITY");
	public static final MsgID FREE_MARGIN = newMsgID("ID_FREE_MARGIN");
	public static final MsgID USED_MARGIN = newMsgID("ID_USED_MARGIN");
	public static final MsgID PROFIT_AND_LOSS = newMsgID("ID_PROFIT_AND_LOSS");
	public static final MsgID ACCOUNTS = newMsgID("ID_ACCOUNTS");
	public static final MsgID PORTFOLIOS = newMsgID("ID_PORTFOLIOS");
	public static final MsgID SECURITIES = newMsgID("ID_SECURITIES");
	
	private static MsgID newMsgID(String messageID) {
		return new MsgID(SECTION_ID, messageID);
	}

}

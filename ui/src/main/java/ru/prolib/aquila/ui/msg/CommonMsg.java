package ru.prolib.aquila.ui.msg;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class CommonMsg {
	public static final String SECTION_ID = "Common";
	
	static {
		Messages.registerLoader(SECTION_ID, CommonMsg.class.getClassLoader());
	}
	
	public static final MsgID ACCOUNT = newMsgID("ID_ACCOUNT");
	public static final MsgID ACCOUNTS = newMsgID("ID_ACCOUNTS");
	public static final MsgID ACTIVATOR = newMsgID("ID_ACTIVATOR");
	public static final MsgID AVG_EXEC_PRICE = newMsgID("ID_AVG_EXEC_PRICE");
	public static final MsgID BALANCE = newMsgID("ID_BALANCE");
	public static final MsgID BALANCE_VAL = newMsgID("ID_BALANCE_VAL");
	public static final MsgID CANCEL = newMsgID("ID_CANCEL");
	public static final MsgID CASH = newMsgID("ID_CASH");
	public static final MsgID CHNG_TIME = newMsgID("ID_CHNG_TIME");
	public static final MsgID CODE = newMsgID("ID_CODE");
	public static final MsgID CONNECTED = newMsgID("ID_CONNECTED");
	public static final MsgID COMMENT = newMsgID("ID_COMMENT");
	public static final MsgID CLOSE = newMsgID("ID_CLOSE");
	public static final MsgID CURR_VOL = newMsgID("ID_CURR_VOL");
	public static final MsgID CURR_PR = newMsgID("ID_CURR_PR");
	public static final MsgID CURRENCY = newMsgID("ID_CURRENCY");
	public static final MsgID DIR = newMsgID("ID_DIR");
	public static final MsgID DISCONNECTED = newMsgID("ID_DISCONNECTED");
	public static final MsgID DRIVER = newMsgID("ID_DRIVER");
	public static final MsgID EQUITY = newMsgID("ID_EQUITY");
	public static final MsgID EXEC_VOL = newMsgID("ID_EXEC_VOL");
	public static final MsgID FREE_MARGIN = newMsgID("ID_FREE_MARGIN");
	public static final MsgID ID = newMsgID("ID_ID");
	public static final MsgID LEVERAGE = newMsgID("ID_LEVERAGE");
	public static final MsgID LOCKED_VAL = newMsgID("ID_LOCKED_VAL");
	public static final MsgID MARKET_VAL = newMsgID("ID_MARKET_VAL");
	public static final MsgID MENU_FILE = newMsgID("MENU_FILE");
	public static final MsgID MENU_TERM = newMsgID("MENU_TERM");
	public static final MsgID MENU_FILE_EXIT = newMsgID("MENU_FILE_EXIT");
	public static final MsgID MENU_TERM_START = newMsgID("MENU_TERM_START");
	public static final MsgID MENU_TERM_STOP = newMsgID("MENU_TERM_STOP");
	public static final MsgID MENU_VIEW = newMsgID("MENU_VIEW");
	public static final MsgID MENU_VIEW_PORTFOLIO_STATUS = newMsgID("MENU_VIEW_PORTFOLIO_STATUS");
	public static final MsgID MENU_ORDER = newMsgID("MENU_ORDER");
	public static final MsgID MENU_ORDER_CANCEL = newMsgID("MENU_ORDER_CANCEL");
	public static final MsgID OK = newMsgID("ID_OK");
	public static final MsgID OPEN_PR = newMsgID("ID_OPEN_PR");
	public static final MsgID ORDERS = newMsgID("ID_ORDERS");
	public static final MsgID PORTFOLIO = newMsgID("ID_PORTFOLIO");
	public static final MsgID POSITIONS = newMsgID("ID_POSITIONS");
	public static final MsgID PROFIT_AND_LOSS = newMsgID("ID_PROFIT_AND_LOSS");
	public static final MsgID PORTFOLIOS = newMsgID("ID_PORTFOLIOS");
	public static final MsgID PRICE = newMsgID("ID_PRICE");	
	public static final MsgID QTY = newMsgID("ID_QTY");
	public static final MsgID QTY_REST = newMsgID("ID_QTY_REST");
	public static final MsgID SECURITY = newMsgID("ID_SECURITY");
	public static final MsgID SECURITIES = newMsgID("ID_SECURITIES");
	public static final MsgID SELECT = newMsgID("ID_SELECT");
	public static final MsgID STARTED = newMsgID("ID_STARTED");
	public static final MsgID STATUS = newMsgID("ID_STATUS");
	public static final MsgID STOPPED = newMsgID("ID_STOPPED");
	public static final MsgID SUBCODE = newMsgID("ID_SUBCODE");
	public static final MsgID SUBCODE2 = newMsgID("ID_SUBCODE2");
	public static final MsgID SYMBOL = newMsgID("ID_SYMBOL");
	public static final MsgID TERMINAL = newMsgID("ID_TERMINAL");
	public static final MsgID TIME = newMsgID("ID_TIME");
	public static final MsgID TITLE_SELECT_PORTFOLIO = newMsgID("ID_SELECT_PORTFOLIO");
	public static final MsgID TITLE_SHOW_PORTFOLIOS = newMsgID("ID_SHOW_PORTFOLIOS");
	public static final MsgID TITLE_SELECT_DRIVER = newMsgID("ID_SELECT_DRIVER");
	public static final MsgID TYPE = newMsgID("ID_TYPE");
	public static final MsgID USED_MARGIN = newMsgID("ID_USED_MARGIN");
	public static final MsgID VMARGIN = newMsgID("ID_VMARGIN");
	
	private static MsgID newMsgID(String messageID) {
		return new MsgID(SECTION_ID, messageID);
	}

}

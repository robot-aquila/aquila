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
	public static final MsgID TIME_DONE = newMsgID("ID_TIME_DONE");
	public static final MsgID CODE = newMsgID("ID_CODE");
	public static final MsgID CONNECTED = newMsgID("ID_CONNECTED");
	public static final MsgID COMMENT = newMsgID("ID_COMMENT");
	public static final MsgID CLOSE = newMsgID("ID_CLOSE");
	public static final MsgID CURR_VOL = newMsgID("ID_CURR_VOL");
	public static final MsgID CURR_PR = newMsgID("ID_CURR_PR");
	public static final MsgID CURRENCY = newMsgID("ID_CURRENCY");
	public static final MsgID ACTION = newMsgID("ID_ACTION");
	public static final MsgID DISCONNECTED = newMsgID("ID_DISCONNECTED");
	public static final MsgID DRIVER = newMsgID("ID_DRIVER");
	public static final MsgID EQUITY = newMsgID("ID_EQUITY");
	public static final MsgID EXECUTED_VALUE = newMsgID("ID_EXECUTED_VALUE");
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
	public static final MsgID INITIAL_VOLUME = newMsgID("ID_INITIAL_VOLUME");
	public static final MsgID CURRENT_VOLUME = newMsgID("ID_CURRENT_VOLUME");
	public static final MsgID SECURITY = newMsgID("ID_SECURITY");
	public static final MsgID SECURITIES = newMsgID("ID_SECURITIES");
	public static final MsgID SELECT = newMsgID("ID_SELECT");
	public static final MsgID STARTED = newMsgID("ID_STARTED");
	public static final MsgID STATUS = newMsgID("ID_STATUS");
	public static final MsgID STOPPED = newMsgID("ID_STOPPED");
	public static final MsgID SUBCODE = newMsgID("ID_SUBCODE");
	public static final MsgID SUBCODE2 = newMsgID("ID_SUBCODE2");
	public static final MsgID SYMBOL = newMsgID("ID_SYMBOL");
	public static final MsgID SYSTEM_MESSAGE = newMsgID("ID_SYSTEM_MESSAGE");
	public static final MsgID TERMINAL = newMsgID("ID_TERMINAL");
	public static final MsgID TIME = newMsgID("ID_TIME");
	public static final MsgID TITLE_SELECT_PORTFOLIO = newMsgID("ID_SELECT_PORTFOLIO");
	public static final MsgID TITLE_SHOW_PORTFOLIOS = newMsgID("ID_SHOW_PORTFOLIOS");
	public static final MsgID TITLE_SELECT_DRIVER = newMsgID("ID_SELECT_DRIVER");
	public static final MsgID TYPE = newMsgID("ID_TYPE");
	public static final MsgID USED_MARGIN = newMsgID("ID_USED_MARGIN");
	public static final MsgID VMARGIN = newMsgID("ID_VMARGIN");
	
	// Time selection dialog
	public static final MsgID TSD_DEFAULT_TITLE = newMsgID("TSD_DEFAULT_TITLE");
	public static final MsgID TSD_INITIAL_TIME = newMsgID("TSD_INITIAL_TIME");
	public static final MsgID TSD_SELECTED_TIME = newMsgID("TSD_SELECTED_TIME");
	
	// Symbol list selection dialog
	public static final MsgID SLD_DEFAULT_TITLE = newMsgID("SLD_DEFAULT_TITLE");
	public static final MsgID SLD_SELECTED_SYMBOLS = newMsgID("SLD_SELECTED_SYMBOLS");
	public static final MsgID SLD_ADD_SYMBOL = newMsgID("SLD_ADD_SYMBOL");
	public static final MsgID SLD_ADD_SYMBOL_TOOLTIP = newMsgID("SLD_ADD_SYMBOL_TOOLTIP");
	public static final MsgID SLD_REMOVE_SYMBOL = newMsgID("SLD_REMOVE_SYMBOL");
	public static final MsgID SLD_REMOVE_SYMBOL_TOOLTIP = newMsgID("SLD_REMOVE_SYMBOL_TOOLTIP");
	public static final MsgID SLD_INPUT_SYMBOL = newMsgID("SLD_INPUT_SYMBOL");
	public static final MsgID SLD_BAD_INPUT_SYMBOL = newMsgID("SLD_BAD_INPUT_SYMBOL");
	
	// Market depth dialog
	public static final MsgID MDD_BID_SIZE = newMsgID("MDD_BID_SIZE");
	public static final MsgID MDD_ASK_SIZE = newMsgID("MDD_ASK_SIZE");
	public static final MsgID MDD_PRICE = newMsgID("MDD_PRICE");

	
	private static MsgID newMsgID(String messageID) {
		return new MsgID(SECTION_ID, messageID);
	}

}

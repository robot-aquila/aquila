package ru.prolib.aquila.probe.scheduler.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class ProbeMsg {
	static final String SECTION_ID = "Probe";
	
	static {
		Messages.registerLoader(SECTION_ID, ProbeMsg.class.getClassLoader());
	}
	
	static MsgID newMsgID(String messageID) {
		return new MsgID(SECTION_ID, messageID);
	}
	
	public static final MsgID TOOLBAR_TITLE = newMsgID("TOOLBAR_TITLE");
	
	/**
	 * Tooltip of the "options" button.
	 */
	public static final MsgID BTN_TTIP_OPTIONS = newMsgID("BTN_TTIP_OPTIONS");
	
	/**
	 * Tooltip of the "pause" button.
	 */
	public static final MsgID BTN_TTIP_PAUSE = newMsgID("BTN_TTIP_PAUSE");
	
	/**
	 * Tooltip of the "run" button.
	 */
	public static final MsgID BTN_TTIP_RUN = newMsgID("BTN_TTIP_RUN");
	
	/**
	 * Tooltip of the "run up to time" button. 
	 */
	public static final MsgID BTN_TTIP_RUN_TIME = newMsgID("BTN_TTIP_RUN_TIME");
	
	/**
	 * Tooltip of the "run to the end of interval" button.
	 */
	public static final MsgID BTN_TTIP_RUN_INTERVAL = newMsgID("BTN_TTIP_RUN_INTERVAL");
	
	/**
	 * Tooltip of the "run step" button.
	 */
	public static final MsgID BTN_TTIP_RUN_STEP = newMsgID("BTN_TTIP_RUN_STEP");
	
	public static final MsgID SOD_DIALOG_TITLE = newMsgID("SOD_DIALOG_TITLE");
	public static final MsgID SOD_EXEC_SPEED = newMsgID("SOD_EXEC_SPEED");
	public static final MsgID SOD_EXEC_SPEED0 = newMsgID("SOD_EXEC_SPEED0");
	public static final MsgID SOD_EXEC_SPEED1 = newMsgID("SOD_EXEC_SPEED1");
	public static final MsgID SOD_EXEC_SPEED2 = newMsgID("SOD_EXEC_SPEED2");
	public static final MsgID SOD_EXEC_SPEED4 = newMsgID("SOD_EXEC_SPEED4");
	public static final MsgID SOD_EXEC_SPEED8 = newMsgID("SOD_EXEC_SPEED8");
	public static final MsgID SOD_INTERVAL = newMsgID("SOD_INTERVAL");
	public static final MsgID SOD_INTERVAL_1MIN = newMsgID("SOD_INTERVAL_1MIN");
	public static final MsgID SOD_INTERVAL_5MIN = newMsgID("SOD_INTERVAL_5MIN");
	public static final MsgID SOD_INTERVAL_10MIN = newMsgID("SOD_INTERVAL_10MIN");
	public static final MsgID SOD_INTERVAL_15MIN = newMsgID("SOD_INTERVAL_15MIN");
	public static final MsgID SOD_INTERVAL_30MIN = newMsgID("SOD_INTERVAL_30MIN");
	public static final MsgID SOD_INTERVAL_1HOUR = newMsgID("SOD_INTERVAL_1HOUR");

}

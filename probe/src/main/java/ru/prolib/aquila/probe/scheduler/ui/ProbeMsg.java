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
	 * Tooltip of the "run to the end of segment" button.
	 */
	public static final MsgID BTN_TTIP_RUN_SEGMENT = newMsgID("BTN_TTIP_RUN_SEGMENT");
	
	/**
	 * Tooltip of the "run step" button.
	 */
	public static final MsgID BTN_TTIP_RUN_STEP = newMsgID("BTN_TTIP_RUN_STEP");

}

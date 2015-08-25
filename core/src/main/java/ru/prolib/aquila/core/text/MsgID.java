package ru.prolib.aquila.core.text;

public class MsgID {
	private final String sectionId, messageId;
	
	public MsgID(String sectionId, String messageId) {
		super();
		this.sectionId = sectionId;
		this.messageId = messageId;
	}
	
	public String getSectionId() {
		return sectionId;
	}
	
	public String getMessageId() {
		return messageId;
	}
	
	@Override
	public String toString() {
		return sectionId + "." + messageId;
	}

}

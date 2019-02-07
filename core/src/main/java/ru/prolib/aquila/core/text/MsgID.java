package ru.prolib.aquila.core.text;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(722213, 4005)
				.append(sectionId)
				.append(messageId)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != MsgID.class ) {
			return false;
		}
		MsgID o = (MsgID) other;
		return new EqualsBuilder()
				.append(o.sectionId, sectionId)
				.append(o.messageId, messageId)
				.build();
	}

}

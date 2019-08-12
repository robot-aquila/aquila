package ru.prolib.aquila.exante;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.UnsupportedMessageType;
import quickfix.fix44.BusinessMessageReject;
import quickfix.fix44.Message;

public interface XResponseHandler {

	/**
	 * Process response message.
	 * <p>
	 * @param message - message instance
	 * @return true if request processing completely done, 
	 * false - keep processing multipart response. 
	 * @throws FieldNotFound - an error occurred
	 * @throws IncorrectDataFormat - an error occurred
	 * @throws IncorrectTagValue - an error occurred
	 * @throws UnsupportedMessageType - an error occurred
	 */
	boolean onMessage(Message message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType;

	/**
	 * Process business message reject error.
	 * <p>
	 * @param message - message instance
	 * @throws FieldNotFound - an error occurred
	 * @throws IncorrectDataFormat - an error occurred
	 * @throws IncorrectTagValue - an error occurred
	 * @throws UnsupportedMessageType - an error occurred
	 */
	void onReject(BusinessMessageReject message) throws
		FieldNotFound,
		IncorrectDataFormat,
		IncorrectTagValue,
		UnsupportedMessageType;
	
	/**
	 * Close handler.
	 * <p>
	 * Called once after removed from repository.
	 */
	void close();

}

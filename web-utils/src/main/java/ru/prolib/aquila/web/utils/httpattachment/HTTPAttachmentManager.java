package ru.prolib.aquila.web.utils.httpattachment;

import java.io.IOException;

public interface HTTPAttachmentManager {
	
	/**
	 * Get an attachment with most recent download.
	 * <p>
	 * @param criteria - Search criteria of an attachment. Specify the most accurate
	 * criteria to increase the probability of success. Different implementations of
	 * the attachment manager can use different criteria parameters for successful
	 * work. Some of managers may utilize URL (for example for direct downloading),
	 * some - the file name (as example Firefox browser), some - the start time (as
	 * JBrowserDriver), etc... Keep it as full as possible.
	 * @param initiator - An action to start downloading. This action may or may not
	 * called by the manager depends on its implementation. Don't rely on that it
	 * will be called.
	 * @return the last attachment descriptor that matches the specified criteria.
	 * @throws IOException - an IO-level exception occurred
	 * @throws HTTPAttachmentNotFoundException - attachment with specified parameters was not found
	 * @throws HTTPAttachmentException - an other type error linked with the attachment occurred
	 */
	HTTPAttachment getLast(HTTPAttachmentCriteria criteria, HTTPDownloadInitiator initiator)
		throws HTTPAttachmentException, IOException;

}

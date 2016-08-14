package ru.prolib.aquila.web.utils;

@Deprecated
public enum ErrorClass {
	PROTOCOL,
	IO,
	RESPONSE_VALIDATION,
	REQUEST_INITIALIZATION,
	POSSIBLE_LOGIC,
	/**
	 * Errors of this type usually points to a problem with recognizing of the
	 * form elements. Such problems can appear as a result of the change of
	 * structure of the page by site developers. That cannot be detected
	 * automatically and require investigation ASAP.
	 */
	WEB_FORM,
	WEB_DRIVER
}

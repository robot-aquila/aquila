package ru.prolib.aquila.web.utils;

/**
 * Web page exception of web-utils.
 * <p>
 * This class of exceptions points to common issues of a web-page. It may be the
 * parsing issues, searching the DOM elements, removing or adding new elements,
 * etc... In general such exceptions may be temporary. For example if a program
 * was unable to load a page, then the next part of the program will unable to
 * find appropriate DOM element. But if the page was loaded and its structure
 * was changed by developers of the site then the program also cannot find the
 * DOM element. There are two reasons and two different solutions: the first one
 * - we should wait and try again when the page will be available, the second -
 * we must check out the page changes and review the code. But the code parsing
 * the page knows nothing about those reasons. And so it will throw an exception
 * to indicate the problem. This exception.
 */
public class WUWebPageException extends WUException {
	private static final long serialVersionUID = 1L;
	
	public WUWebPageException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public WUWebPageException(String msg) {
		super(msg);
	}
	
	public WUWebPageException(Throwable t) {
		super(t);
	}
	
	public WUWebPageException() {
		super();
	}

}

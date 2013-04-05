package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.ib.IBException;

/**
 * Обертка ошибки IB.
 * <p>
 * 2012-11-26<br>
 * $Id: IBErrorException.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBErrorException extends IBException {
	private static final long serialVersionUID = -1719733654833422649L;
	
	public IBErrorException(int reqId, int code, String msg) {
		super("ReqID:" + reqId + "[" + code + "] " +  msg);
	}

}

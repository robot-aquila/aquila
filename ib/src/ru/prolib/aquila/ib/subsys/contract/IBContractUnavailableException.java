package ru.prolib.aquila.ib.subsys.contract;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.ib.IBException;

/**
 * Исключение, связанное с недоступностью контракта.
 * <p>
 * 2013-01-04<br>
 * $Id: IBContractUnavailableException.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBContractUnavailableException extends IBException {
	private static final String MSG = "Contract unavailable: ";
	private static final long serialVersionUID = 2857490508962674654L;
	
	public IBContractUnavailableException(int conId) {
		super(MSG + conId);
	}
	
	public IBContractUnavailableException(SecurityDescriptor descr) {
		super(MSG + descr);
	}

}

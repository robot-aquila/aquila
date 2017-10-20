package ru.prolib.aquila.ib;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.cache.Cache;

/**
 * Служебный интерфейс терминала IB.
 * <p>
 * Содержит дополнительные методы, необходимые для подсистем.
 */
public interface IBEditableTerminal extends EditableTerminal, IBTerminal {
	
	public IBClient getClient();

	public Cache getCache();
	
	public void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg);

}

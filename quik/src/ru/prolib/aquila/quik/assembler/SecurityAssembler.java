package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.quik.dde.*;

/**
 * Сборщик инструмента.
 */
public class SecurityAssembler {
	private final EditableTerminal terminal;
	private final Cache cache;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param cache DDE-кэш
	 */
	public SecurityAssembler(EditableTerminal terminal, Cache cache) {
		super();
		this.terminal = terminal;
		this.cache = cache;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить DDE-кэш.
	 * <p> 
	 * @return DDE-кэш
	 */
	public Cache getCache() {
		return cache;
	}
	
	/**
	 * Собрать инструмент на основе данных кэш-записи.
	 * <p>
	 * @param entry кэш запись
	 */
	public void adjustByCache(SecurityCache entry)
			throws SecurityException, EditableObjectException
	{
		SecurityDescriptor descr = entry.getDescriptor();
		EditableSecurity security = null;
		if ( terminal.isSecurityExists(descr) ) {
			security = terminal.getEditableSecurity(descr);
		} else {
			security = terminal.createSecurity(descr);
		}
		security.setAskPrice(entry.getAskPrice());
		security.setBidPrice(entry.getBidPrice());
		security.setClosePrice(entry.getClosePrice());
		security.setDisplayName(entry.getDisplayName());
		security.setHighPrice(entry.getHighPrice());
		security.setLastPrice(entry.getLastPrice());
		security.setLotSize(entry.getLotSize());
		security.setLowPrice(entry.getLowPrice());
		security.setMaxPrice(entry.getMaxPrice());
		security.setMinPrice(entry.getMinPrice());
		security.setMinStepPrice(entry.getMinStepPrice());
		security.setMinStepSize(entry.getMinStepSize());
		security.setOpenPrice(entry.getOpenPrice());
		security.setPrecision(entry.getPrecision());
		if ( security.isAvailable() ) {
			security.fireChangedEvent();
		} else {
			cache.registerSecurityDescriptor(descr, entry.getShortName());
			terminal.fireSecurityAvailableEvent(security);
			security.setAvailable(true);
		}
		security.resetChanges();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != SecurityAssembler.class ) {
			return false;
		}
		SecurityAssembler o = (SecurityAssembler) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.append(cache, o.cache)
			.isEquals();
	}
	
}

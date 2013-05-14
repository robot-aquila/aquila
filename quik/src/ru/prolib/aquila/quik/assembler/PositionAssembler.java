package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Сборщик позиции.
 */
public class PositionAssembler {
	private static final Logger logger;
	private final EditableTerminal terminal;
	private final Cache cache;
	
	static {
		logger = LoggerFactory.getLogger(PositionAssembler.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param cache кэш
	 */
	public PositionAssembler(EditableTerminal terminal, Cache cache) {
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
	
	public void adjustByCache(PositionFCache entry)
		throws EditableObjectException
	{
		Account account = new Account(entry.getFirmId(),entry.getAccountCode());
		if ( ! terminal.isPortfolioAvailable(account) ) {
			logger.debug("Still wait for portfolio: {}", account);
			return;
		}
		String secName = entry.getSecurityShortName();
		if ( ! cache.isSecurityDescriptorRegistered(secName) ) {
			logger.debug("Still wait for security by short name: {}", secName);
			return;
			
		}
		SecurityDescriptor descr = cache.getSecurityDescriptorByName(secName);
		if ( ! terminal.isSecurityExists(descr) ) {
			logger.debug("Still wait for security: {}", descr);
			return;
		}
		EditablePortfolio portfolio = terminal.getEditablePortfolio(account);
		EditablePosition position = portfolio
			.getEditablePosition(terminal.getSecurity(descr));
		position.setCurrQty(entry.getCurrentQty());
		position.setOpenQty(entry.getOpenQty());
		position.setVarMargin(entry.getVarMargin());
		if ( position.isAvailable() ) {
			position.fireChangedEvent();
		} else {
			portfolio.firePositionAvailableEvent(position);
			position.setAvailable(true);
		}
		position.resetChanges();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionAssembler.class ) {
			return false;
		}
		PositionAssembler o = (PositionAssembler) other;
		return new EqualsBuilder()
			.append(o.cache, cache)
			.append(o.terminal, terminal)
			.isEquals();
	}


}

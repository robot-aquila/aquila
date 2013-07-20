package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.*;

/**
 * Фасад подсистемы сборки и согласования объектов бизнес-модели.
 * <p>
 * Примечания по событиям связанными с заявами, стоп-заявками и сделками.
 */
public class Assembler implements Starter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Assembler.class);
	}
	
	private final QUIKEditableTerminal terminal;
	
	public Assembler(QUIKEditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}
	
	public QUIKEditableTerminal getTerminal() {
		return terminal;
	}

	@Override
	public void start() throws StarterException {
		logger.debug("started");
	}

	@Override
	public void stop() throws StarterException {
		logger.debug("stopped");
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Assembler.class ) {
			return false;
		}
		Assembler o = (Assembler) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}
	
	/**
	 * Собрать портфель.
	 * <p>
	 * @param entry кэш-запись портфеля
	 */
	public void assemble(PortfolioEntry entry) {
		Account account = entry.getAccount();
		EditablePortfolios storage = terminal.getPortfoliosInstance();
		EditablePortfolio p;
		synchronized ( storage ) {
			try {
				if ( storage.isPortfolioAvailable(account) ) {
					p = storage.getEditablePortfolio(account);
				} else {
					p = storage.createPortfolio(terminal, account);
				}
				synchronized ( p ) {
					p.setBalance(entry.getBalance());
					p.setCash(entry.getCash());
					p.setVariationMargin(entry.getVarMargin());
					if ( p.isAvailable() ) {
						p.fireChangedEvent();
					} else {
						p.setAvailable(true);
						storage.firePortfolioAvailableEvent(p);
					}
					p.resetChanges();
				}
			} catch ( PortfolioException e ) {
				Object args[] = { account, e };
				logger.error("Unable update portfolio {}: {}", args);
			}
		}
	}
	
	public void assemble(PositionEntry entry) {
		getCache().put(entry);
	}
	
	public void assemble(SecurityEntry entry) {
		getCache().put(entry);
	}
	
	public void assemble(T2QOrder entry) {
		getCache().getOrdersCache().put(entry);
	}
	
	public void assemble(T2QTrade entry) {
		getCache().put(entry);
	}
	
	public void assemble(TradesEntry entry) {
		getCache().add(entry);
	}
	
	final private Cache getCache() {
		return terminal.getDataCache();
	}

}

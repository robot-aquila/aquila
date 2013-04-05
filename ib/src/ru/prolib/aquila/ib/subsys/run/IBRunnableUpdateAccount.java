package ru.prolib.aquila.ib.subsys.run;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfolioFactory;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;

/**
 * Обновление счета.
 * <p>
 * 2013-01-09<br>
 * $Id: IBRunnableUpdateAccount.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRunnableUpdateAccount implements Runnable {
	private static final Logger logger;
	private final EditableTerminal terminal;
	private final PortfolioFactory fport;
	private final S<EditablePortfolio> modifier;
	private final IBEventUpdateAccount event;
	
	static {
		logger = LoggerFactory.getLogger(IBRunnableUpdateAccount.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param fport фабрика экземпляров портфелей
	 * @param modifier модификатор портфеля
	 * @param event событие-основание
	 */
	public IBRunnableUpdateAccount(EditableTerminal terminal,
			PortfolioFactory fport, S<EditablePortfolio> modifier,
			IBEventUpdateAccount event)
	{
		super();
		this.terminal = terminal;
		this.fport = fport;
		this.modifier = modifier;
		this.event = event;
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
	 * Фабрика портфелей.
	 * <p>
	 * @return фабрика портфелей
	 */
	public PortfolioFactory getPortfolioFactory() {
		return fport;
	}
	
	/**
	 * Модификатор портфеля.
	 * <p>
	 * @return модификатор портфеля
	 */
	public S<EditablePortfolio> getPortfolioModifier() {
		return modifier;
	}
	
	/**
	 * Получить событие-основание.
	 * <p>
	 * @return событие-основание
	 */
	public IBEventUpdateAccount getEvent() {
		return event;
	}

	@Override
	public void run() {
		Account account = new Account(event.getAccount());
		EditablePortfolio portfolio = null;
		try {
			if ( terminal.isPortfolioAvailable(account) ) {
				portfolio = terminal.getEditablePortfolio(account);
			} else {
				portfolio = fport.createPortfolio(account);
				terminal.registerPortfolio(portfolio);
			}
		} catch ( PortfolioException e ) {
			logger.error("Unexpected exception: ", e);
			terminal.firePanicEvent(1, "IBRunnableUpdateAccount#run");
			return;
		}
		modifier.set(portfolio, event);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBRunnableUpdateAccount.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRunnableUpdateAccount o = (IBRunnableUpdateAccount) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.append(fport, o.fport)
			.append(modifier, o.modifier)
			.append(event, o.event)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130109, 14255)
			.append(terminal)
			.append(fport)
			.append(modifier)
			.append(event)
			.toHashCode();
	}

}

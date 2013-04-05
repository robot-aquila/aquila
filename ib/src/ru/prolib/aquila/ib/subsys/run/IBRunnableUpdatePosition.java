package ru.prolib.aquila.ib.subsys.run;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.Contract;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;

/**
 * Обновление позиции.
 * <p>
 * 2013-01-06<br>
 * $Id: IBRunnableUpdatePosition.java 528 2013-02-14 15:27:34Z whirlwind $
 */
public class IBRunnableUpdatePosition implements Runnable {
	private static final Logger logger;
	private final EditableTerminal terminal;
	private final IBContracts contracts;
	private final S<EditablePosition> modifier;
	private final IBEventUpdatePortfolio event;
	
	static {
		logger = LoggerFactory.getLogger(IBRunnableUpdatePosition.class);
	}
	
	/**
	 * Создать задачу.
	 * <p>
	 * @param terminal терминал
	 * @param contracts фасад подсистемы контрактов
	 * @param modifier модификатор позиции
	 * @param event событие-основание для задачи
	 */
	public IBRunnableUpdatePosition(EditableTerminal terminal,
			IBContracts contracts, S<EditablePosition> modifier,
			IBEventUpdatePortfolio event)
	{
		super();
		this.terminal = terminal;
		this.contracts = contracts;
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
	 * Получить фасад подсистемы контрактов.
	 * <p>
	 * @return фасад подсистемы контрактов
	 */
	public IBContracts getContracts() {
		return contracts;
	}
	
	/**
	 * Получить модификатор позиции.
	 * <p>
	 * @return модификатор позиции
	 */
	public S<EditablePosition> getPositionModifier() {
		return modifier;
	}
	
	/**
	 * Получить событие-основание.
	 * <p>
	 * @return событие
	 */
	public IBEventUpdatePortfolio getEvent() {
		return event;
	}

	@Override
	public void run() {
		Account account = new Account(event.getAccount());
		EditablePortfolio portfolio = null;
		EditablePosition pos = null;
		try {
			portfolio = terminal.getEditablePortfolio(account); 
			pos = portfolio.getEditablePosition(contracts
					.getAppropriateSecurityDescriptor(event.getContractId()));
			modifier.set(pos, event);
		} catch ( PortfolioException e ) {
			panic(e);
		} catch ( IBException e ) {
			panic(e);
		}
	}

	/**
	 * Обработать фатальное исключение.
	 * <p>
	 * @param e исключение
	 */
	private void panic(Exception e) {
		logger.error("Unable update position: ", e);
		terminal.firePanicEvent(1, getClass().getSimpleName() + "#run");
	}
	
	@Override
	public String toString() {
		Contract contract = event.getContract();
		return "Update position for " + contract.m_secType + " "
			+ contract.m_symbol + "@" + contract.m_exchange
			+ " (primary: " + contract.m_primaryExch + ") "
			+ event.getPosition() + " pcs. x "
			+ event.getAverageCost() + " " + contract.m_currency;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBRunnableUpdatePosition.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRunnableUpdatePosition o = (IBRunnableUpdatePosition) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.append(contracts, o.contracts)
			.append(modifier, o.modifier)
			.append(event, o.event)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 15315)
			.append(terminal)
			.append(contracts)
			.append(modifier)
			.append(event)
			.toHashCode();
	}

}

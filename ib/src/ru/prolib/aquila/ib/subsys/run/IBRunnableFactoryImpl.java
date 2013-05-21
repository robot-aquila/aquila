package ru.prolib.aquila.ib.subsys.run;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.rule.RunOnceOnEvent;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrder;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;

/**
 * Фабрика исполняемых задач.
 * <p>
 * 2013-01-07<br>
 * $Id: IBRunnableFactoryImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBRunnableFactoryImpl implements IBRunnableFactory {
	private final EditableTerminal terminal;
	private final IBContracts contracts;
	private final OrderResolver resolver;
	private final S<EditableOrder> mOrder;
	private final S<EditablePosition> mPosition;
	private final S<EditablePortfolio> mPortfolio;
	
	public IBRunnableFactoryImpl(EditableTerminal terminal,
			IBContracts contracts,
			OrderResolver resolver, S<EditablePortfolio> mPortfolio,
			S<EditableOrder> mOrder, S<EditablePosition> mPosition)
	{
		super();
		this.terminal = terminal;
		this.contracts = contracts;
		this.resolver = resolver;
		this.mPortfolio = mPortfolio;
		this.mOrder = mOrder;
		this.mPosition = mPosition;
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
	 * @return подсистема контрактов
	 */
	public IBContracts getContracts() {
		return contracts;
	}
	
	/**
	 * Получить определитель заявки.
	 * <p>
	 * @return определитель заявки
	 */
	public OrderResolver getOrderResolver() {
		return resolver;
	}
	
	/**
	 * Получить модификатор заявки.
	 * <p>
	 * @return модификатор
	 */
	public S<EditableOrder> getOrderModifier() {
		return mOrder;
	}
	
	/**
	 * Получить модификатор позиции.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePosition> getPositionModifier() {
		return mPosition;
	}
	
	/**
	 * Получить модификатор портфеля.
	 * <p>
	 * @return модификатор
	 */
	public S<EditablePortfolio> getPortfolioModifier() {
		return mPortfolio;
	}

	@Override
	public Runnable createUpdateOrder(IBEventOrder event) {
		Runnable runnable = new IBRunnableUpdateOrder(resolver, mOrder, event);
		if ( event instanceof IBEventOpenOrder ) {
			return contractRoutines(((IBEventOpenOrder) event)
					.getContractId(), runnable);
		}
		return runnable;
	}

	@Override
	public Runnable createUpdatePosition(IBEventUpdatePortfolio event) {
		return contractRoutines(event.getContractId(),
			new IBRunnableUpdatePosition(terminal, contracts, mPosition,event));
	}
	
	@Override
	public Runnable createUpdateAccount(IBEventUpdateAccount event) {
		return new IBRunnableUpdateAccount(terminal, mPortfolio, event);
	}

	/**
	 * Дополнительная процедура для задач, требующих наличие контракта.
	 * <p>
	 * Проверяет наличие контракта (через фасад контрактов). Если контракта нет,
	 * то инициирует отложенную задачу по поступлению данных и запрашивает
	 * детали контракта. Если контракт уже загружен, то возвращает исходную
	 * задачу. 
	 * <p>
	 * @param conId номер контракта
	 * @param runnable задача для исполнения
	 * @return итоговая задача
	 */
	private Runnable contractRoutines(int conId, Runnable runnable) {
		synchronized ( contracts ) {
			if ( ! contracts.isContractAvailable(conId) ) {
				contracts.loadContract(conId);
				return new RunOnceOnEvent(contracts.OnContractLoadedOnce(conId),
						runnable);
			}
			return runnable;
		}		
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == IBRunnableFactoryImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBRunnableFactoryImpl o = (IBRunnableFactoryImpl) other;
		return new EqualsBuilder()
			.append(terminal, o.terminal)
			.append(contracts, o.contracts)
			.append(resolver, o.resolver)
			.append(mPortfolio, o.mPortfolio)
			.append(mOrder, o.mOrder)
			.append(mPosition, o.mPosition)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 82347)
			.append(terminal)
			.append(contracts)
			.append(resolver)
			.append(mPortfolio)
			.append(mOrder)
			.append(mPosition)
			.toHashCode();
	}

}

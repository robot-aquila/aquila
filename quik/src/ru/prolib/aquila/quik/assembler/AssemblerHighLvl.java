package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.assembler.cache.Cache;
import ru.prolib.aquila.quik.assembler.cache.PortfolioEntry;
import ru.prolib.aquila.quik.assembler.cache.PositionEntry;
import ru.prolib.aquila.quik.assembler.cache.SecurityEntry;
import ru.prolib.aquila.quik.dde.*;

/**
 * Высокоуровневый интерфейс согласования объектов.
 * <p>
 * Данный класс содержит методы, вызов которых приводит к безусловному
 * выполнению процедуры сборки и согласования соответствующих объектов.
 * Детали согласования скрыты внутри реализации класса. Пользовательский
 * код должен обеспечить своевременный вызов соответствующих процедур
 * согласования в соответствии с алгоритмом, детали которого находятся
 * вне зоны ответственности механизма согласования. 
 */
class AssemblerHighLvl implements Starter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(AssemblerHighLvl.class);
	}
	
	private final EditableTerminal terminal;
	private final Cache cache;
	private final AssemblerMidLvl middle;
	
	AssemblerHighLvl(EditableTerminal terminal, Cache cache,
			AssemblerMidLvl middle)
	{
		super();
		this.terminal = terminal;
		this.cache = cache;
		this.middle = middle;
	}
	
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	public Cache getCache() {
		return cache;
	}

	public AssemblerMidLvl getAssemblerMidLevel() {
		return middle;
	}
	
	/**
	 * Согласовать заявки.
	 * <p>
	 * @throws OrderAlreadyExistsException ошибка регистрации заявки.
	 * В данном случае это исключение свидетельствует о том, что в промежуток
	 * времени после определения необходимости создать экземпляр новой
	 * заявки, создания заявки и попыткой ее регистрации кто-то параллельно
	 * проделал аналогичную работу по заявке с аналогичным ключевым признаком.
	 */
	public void adjustOrders() throws OrderAlreadyExistsException {
		logger.debug("Adjust orders");
		for ( Order o : terminal.getOrders() ) {
			middle.checkIfOrderRemoved((EditableOrder) o);
		}
		for ( OrderCache entry : cache.getAllOrders() ) {
			if ( terminal.isOrderExists(entry.getId()) ) {
				middle.updateExistingOrder(entry);
			} else if ( terminal.hasPendingOrders() ) {
				logger.debug("Order {} wait for pending orders", entry.getId());
			} else {
				middle.createNewOrder(entry);
			}
		}
	}
	
	/**
	 * Согласовать стоп-заявки.
	 * <p>
	 * @throws OrderAlreadyExistsException ошибка регистрации стоп-заявки.
	 * В данном случае это исключение свидетельствует о том, что в промежуток
	 * времени после определения необходимости создать экземпляр новой
	 * стоп-заявки, создания стоп-заявки и попыткой ее регистрации кто-то
	 * параллельно проделал аналогичную работу по стоп-заявке с аналогичным
	 * ключевым признаком.
	 */
	public void adjustStopOrders() throws OrderAlreadyExistsException {
		logger.debug("Adjust stop-orders");
		for ( Order o : terminal.getStopOrders() ) {
			middle.checkIfStopOrderRemoved((EditableOrder) o);
		}
		for ( StopOrderCache entry : cache.getAllStopOrders() ) {
			if ( terminal.isStopOrderExists(entry.getId()) ) {
				middle.updateExistingStopOrder(entry);
			} else if ( terminal.hasPendingStopOrders() ) {
				logger.debug("Order {} wait for pending stop-orders",
						entry.getId());
			} else {
				middle.createNewStopOrder(entry);
			}
		}
	}
	
	/**
	 * Согласовать инструменты.
	 */
	public void adjustSecurities() {
		logger.debug("Adjust securities");
		for ( SecurityEntry entry : cache.getAllSecurities() ) {
			middle.updateSecurity(entry);
		}
	}
	
	/**
	 * Согласовать портфели.
	 */
	public void adjustPortfolios() {
		logger.debug("Adjust portfolios");
		for ( PortfolioEntry entry : cache.getAllPortfoliosF() ) {
			middle.updatePortfolioFORTS(entry);
		}
	}
	
	/**
	 * Согласовать позиции.
	 */
	public void adjustPositions() {
		logger.debug("Adjust positions");
		for ( PositionEntry entry : cache.getAllPositionsF() ) {
			middle.updatePositionFORTS(entry);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != AssemblerHighLvl.class ) {
			return false;
		}
		AssemblerHighLvl o = (AssemblerHighLvl) other;
		return new EqualsBuilder()
			.append(o.cache, cache)
			.append(o.middle, middle)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

	@Override
	public void start() throws StarterException {
		middle.start();
	}

	@Override
	public void stop() throws StarterException {
		middle.stop();
	}

}

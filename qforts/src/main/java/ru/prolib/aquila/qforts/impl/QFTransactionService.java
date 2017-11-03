package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.MultilockBuilderBE;

public class QFTransactionService {
	private final QFObjectRegistry registry;
	private final AtomicLong seqExecutionID;
	private final QFCalculator calculator;
	private final QFAssembler assembler;
	private final QFValidator validator;
	
	public QFTransactionService(QFObjectRegistry registry, AtomicLong seqExecutionID,
			QFCalculator calculator, QFAssembler assembler, QFValidator validator)
	{
		this.registry = registry;
		this.seqExecutionID = seqExecutionID;
		this.calculator = calculator;
		this.assembler = assembler;
		this.validator = validator;
	}
	
	public QFTransactionService(QFObjectRegistry registry, AtomicLong seqExecutionID) {
		this(registry, seqExecutionID, new QFCalculator(), new QFAssembler(), new QFValidator());
	}
	
	public void registerOrder(EditableOrder order) throws QFTransactionException {
		Lockable lock = assembler.createMultilock(new MultilockBuilderBE().add(order));
		lock.lock();
		try {
			if ( registry.isRegistered(order) ) {
				throw new QFTransactionException("Order already registered: " + order.getID());
			}
			QFOrderStatusUpdate update = calculator.updateOrderStatus(order, OrderStatus.ACTIVE, null);
			assembler.update(order, update);
			registry.register(order);
		} finally {
			lock.unlock();
		}
	}

	public void cancelOrder(EditableOrder order) throws QFTransactionException {
		Lockable lock = assembler.createMultilock(new MultilockBuilderBE().add(order));
		lock.lock();
		try {
			if ( ! registry.isRegistered(order) ) {
				throw new QFTransactionException("Order not registered: " + order.getID());
			}
			QFOrderStatusUpdate update = calculator.updateOrderStatus(order, OrderStatus.CANCELLED, null);
			assembler.update(order, update);
			registry.purgeOrder(order);
		} finally {
			lock.unlock();
		}
	}
	
	public void rejectOrder(EditableOrder order, String reason) throws QFTransactionException {
		Lockable lock = assembler.createMultilock(new MultilockBuilderBE().add(order));
		lock.lock();
		try {
			if ( ! registry.isRegistered(order) ) {
				throw new QFTransactionException("Order not registered: " + order.getID());
			}
			QFOrderStatusUpdate update = calculator.updateOrderStatus(order, OrderStatus.REJECTED, reason);
			assembler.update(order, update);
			registry.purgeOrder(order);			
		} finally {
			lock.unlock();
		}
	}
	
	public void executeOrder(EditableOrder order, CDecimal volume, CDecimal price)
			throws QFTransactionException
	{
		EditablePortfolio portfolio = (EditablePortfolio) order.getPortfolio();
		Security security = order.getSecurity();
		Lockable lock = assembler.createMultilock(new MultilockBuilderBE()
				.add(order)
				.add(security)
				.add(portfolio)
				.add(order.getPosition()));
		lock.lock();
		try {
			if ( ! registry.isRegistered(order) ) {
				throw new QFTransactionException("Order not registered: " + order.getID());
			}
			QFOrderExecutionUpdate oeu = calculator.executeOrder(order, volume, price);
			QFPortfolioChangeUpdate pcu = calculator.changePosition(portfolio, security,
					oeu.getPositionVolumeChange(), price);
			int r = validator.canChangePositon(pcu);
			if ( r != QFResult.OK ) {
				// TODO: Convert result code to message
				throw new QFValidationException("Insufficient funds", r);
			}
			assembler.update(order, oeu, seqExecutionID.incrementAndGet());
			assembler.update(portfolio, pcu);
			if ( oeu.getFinalStatus().isFinal() ) {
				registry.purgeOrder(order);
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void updateByMarket(EditablePortfolio portfolio) throws QFTransactionException {
		Lockable lock = null;
		portfolio.lockNewPositions();
		try {
			MultilockBuilderBE builder = new MultilockBuilderBE().add(portfolio);
			for ( Position x : portfolio.getPositions() ) {
				builder.add(x.getSecurity()).add(x);
			}
			lock = assembler.createMultilock(builder);
			lock.lock();
		} finally {
			portfolio.unlockNewPositions();
		}
		try {
			if ( ! registry.isRegistered(portfolio) ) {
				throw new QFTransactionException("Portfolio not registered: " + portfolio.getAccount());
			}
			QFPortfolioChangeUpdate update = calculator.updateByMarket(portfolio);
			assembler.update(portfolio, update);
		} finally {
			lock.unlock();
		}
	}
	
	public void changeBalance(EditablePortfolio portfolio, CDecimal value) throws QFTransactionException {
		Lockable lock = assembler.createMultilock(new MultilockBuilderBE().add(portfolio));
		lock.lock();
		try {
			if ( ! registry.isRegistered(portfolio) ) {
				throw new QFTransactionException("Portfolio not registered: " + portfolio.getAccount());
			}
			QFPortfolioChangeUpdate update = calculator.changeBalance(portfolio, value);
			assembler.update(portfolio, update);

		} finally {
			lock.unlock();
		}
	}
	
	public void updateMargin(EditablePosition position) throws QFTransactionException {
		Lockable lock = assembler.createMultilock(new MultilockBuilderBE()
				.add(position)
				.add(position.getPortfolio())
				.add(position.getSecurity()));
		lock.lock();
		try {
			QFPortfolioChangeUpdate update = calculator.updateMargin(position);
			assembler.update((EditablePortfolio) position.getPortfolio(), update);
		} finally {
			lock.unlock();
		}
	}
	
	public void midClearing(EditablePortfolio portfolio) throws QFTransactionException {
		Lockable lock = null;
		portfolio.lockNewPositions();
		try {
			MultilockBuilderBE builder = new MultilockBuilderBE().add(portfolio);
			for ( Position x : portfolio.getPositions() ) {
				builder.add(x.getSecurity()).add(x);
			}
			lock = assembler.createMultilock(builder);
			lock.lock();
		} finally {
			portfolio.unlockNewPositions();
		}
		try {
			if ( ! registry.isRegistered(portfolio) ) {
				throw new QFTransactionException("Portfolio not registered: " + portfolio.getAccount());
			}
			QFPortfolioChangeUpdate update = calculator.midClearing(portfolio);
			assembler.update(portfolio, update);
		} finally {
			lock.unlock();
		}
	}
	
	public void clearing(EditablePortfolio portfolio) throws QFTransactionException {
		Lockable lock = null;
		portfolio.lockNewPositions();
		try {
			MultilockBuilderBE builder = new MultilockBuilderBE().add(portfolio);
			for ( Position x : portfolio.getPositions() ) {
				builder.add(x.getSecurity()).add(x);
			}
			lock = assembler.createMultilock(builder);
			lock.lock();
		} finally {
			portfolio.unlockNewPositions();
		}
		try {
			if ( ! registry.isRegistered(portfolio) ) {
				throw new QFTransactionException("Portfolio not registered: " + portfolio.getAccount());
			}
			QFPortfolioChangeUpdate update = calculator.clearing(portfolio);
			assembler.update(portfolio, update);
		} finally {
			lock.unlock();
		}
	}

}

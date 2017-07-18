package ru.prolib.aquila.qforts.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.BusinessEntities.BusinessEntity;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.concurrency.Lockable;

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
		Set<BusinessEntity> lockable = new HashSet<>();
		lockable.add(order);
		Lockable lock = assembler.createMultilock(lockable);
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
		Set<BusinessEntity> lockable = new HashSet<>();
		lockable.add(order);
		Lockable lock = assembler.createMultilock(lockable);
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
		Set<BusinessEntity> lockable = new HashSet<>();
		lockable.add(order);
		Lockable lock = assembler.createMultilock(lockable);
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
	
	public void executeOrder(EditableOrder order, long volume, FDecimal price)
			throws QFTransactionException
	{
		EditablePortfolio portfolio = (EditablePortfolio) order.getPortfolio();
		Security security = order.getSecurity();
		Set<BusinessEntity> lockable = new HashSet<>();
		lockable.add(order);
		lockable.add(security);
		lockable.add(portfolio);
		lockable.add(order.getPosition());
		Lockable lock = assembler.createMultilock(lockable);
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
			Set<BusinessEntity> lockable = new HashSet<>();
			lockable.add(portfolio);
			for ( Position x : portfolio.getPositions() ) {
				lockable.add(x.getSecurity());
				lockable.add(x);
			}
			lock = assembler.createMultilock(lockable);
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
	
	public void changeBalance(EditablePortfolio portfolio, FMoney value) throws QFTransactionException {
		Set<BusinessEntity> lockable = new HashSet<>();
		lockable.add(portfolio);
		Lockable lock = assembler.createMultilock(lockable);
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
		Set<BusinessEntity> lockable = new HashSet<>();
		lockable.add(position);
		lockable.add(position.getPortfolio());
		lockable.add(position.getSecurity());
		Lockable lock = assembler.createMultilock(lockable);
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
			Set<BusinessEntity> lockable = new HashSet<>();
			lockable.add(portfolio);
			for ( Position x : portfolio.getPositions() ) {
				lockable.add(x.getSecurity());
				lockable.add(x);
			}
			lock = assembler.createMultilock(lockable);
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
			Set<BusinessEntity> lockable = new HashSet<>();
			lockable.add(portfolio);
			for ( Position x : portfolio.getPositions() ) {
				lockable.add(x.getSecurity());
				lockable.add(x);
			}
			lock = assembler.createMultilock(lockable);
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

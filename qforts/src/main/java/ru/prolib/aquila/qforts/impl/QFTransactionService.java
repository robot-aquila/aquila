package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.MultilockBuilderBE;

public class QFTransactionService {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFTransactionService.class);
	}
	
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
			// If order not exists it may mean that it was cancelled
			// by other thread or just executed while canceling.
			// Just check it and throw only in case if it is unfinished.
			if ( ! registry.isRegistered(order) ) {
				if ( order.getStatus().isFinal() ) {
					return;
				}
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
	
	public void executeOrder(EditableOrder order, CDecimal volume, CDecimal price, String tick_info)
			throws QFTransactionException
	{
		EditablePortfolio portfolio = (EditablePortfolio) order.getPortfolio();
		Security security = order.getSecurity();
		Position position = order.getPosition();
		Lockable lock = assembler.createMultilock(new MultilockBuilderBE()
				.add(order)
				.add(security)
				.add(portfolio)
				.add(position));
		lock.lock();
		try {
			if ( ! registry.isRegistered(order) ) {
				throw new QFTransactionException("Order not registered: " + order.getID());
			}
			QFOrderExecutionUpdate oeu = calculator.executeOrder(order, volume, price);
			oeu.setExecutionExternalID(tick_info);
			QFPortfolioChangeUpdate pcu = calculator.changePosition(portfolio, security,
					oeu.getPositionVolumeChange(), price);
			// TODO: Check that executed value from order update equal to position change value from position update 
			int r = validator.canChangePositon(pcu);
			if ( r == QFResult.OK ) {
				assembler.update(order, oeu, seqExecutionID.incrementAndGet());
				assembler.update(portfolio, pcu);
				if ( oeu.getFinalStatus().isFinal() ) {
					registry.purgeOrder(order);
				}
			} else {
				logger.debug("Order execution rejected (insufficient funds?)");
				dumpPortfolioState(portfolio);
				dumpPositionState(position);
				dumpSecurityState(security);
				dumpOrderState(order);
				logger.debug("Execution parameters ----------------------------");
				logger.debug("Exec. Price: {}", price);
				logger.debug(" Exec. Vol.: {}", volume);
				logger.debug("  Tick info: {}", tick_info);
				
				QFOrderStatusUpdate osu = calculator.updateOrderStatus(
						order,
						OrderStatus.CANCELLED,
						new StringBuilder()
							.append("Execution rejected (code ")
							.append(r)
							.append(")")
							.toString()
					);
				assembler.update(order, osu);
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

	private void dumpPortfolioState(Portfolio portfolio) {
		logger.debug("Portfolio state ---------------------------------");
		logger.debug("    Account: {}", portfolio.getAccount());
		logger.debug("     Equity: {}", portfolio.getEquity());
		logger.debug("    Balance: {}", portfolio.getBalance());
		logger.debug("Used Margin: {}", portfolio.getUsedMargin());
		logger.debug("Free Margin: {}", portfolio.getFreeMargin());
		logger.debug("        P&L: {}", portfolio.getProfitAndLoss());
		logger.debug("     Assets: {}", portfolio.getAssets());
		logger.debug("Liabilities: {}", portfolio.getLiabilities());
	}
	
	private void dumpOrderState(Order order) {
		logger.debug("Order state -------------------------------------");
		logger.debug("         ID: {}", order.getID());
		logger.debug("    Account: {}", order.getAccount());
		logger.debug("     Symbol: {}", order.getSymbol());
		logger.debug("       Type: {}", order.getType());
		logger.debug("     Action: {}", order.getAction());
		logger.debug("      Price: {}", order.getPrice());
		logger.debug("    Comment: {}", order.getComment());
		logger.debug("   Sys. Msg: {}", order.getSystemMessage());
		logger.debug("        UDL: {}", order.getUserDefinedLong());
		logger.debug("        UDS: {}", order.getUserDefinedString());
		logger.debug("       Time: {}", order.getTime());
		logger.debug("  Time Done: {}", order.getTimeDone());
		logger.debug("     Status: {}", order.getStatus());
		logger.debug("  Init. Vol: {}", order.getInitialVolume());
		logger.debug("  Curr. Vol: {}", order.getCurrentVolume());
		logger.debug("  Exec. Val: {}", order.getExecutedValue());		
	}
	
	private void dumpPositionState(Position position) {
		logger.debug("Position state ----------------------------------");
		logger.debug("    Account: {}", position.getAccount());
		logger.debug("     Symbol: {}", position.getSymbol());
		logger.debug(" Curr. Vol.: {}", position.getCurrentVolume());
		logger.debug(" Open Price: {}", position.getOpenPrice());
		logger.debug("Curr. Price: {}", position.getCurrentPrice());
		logger.debug("        P&L: {}", position.getProfitAndLoss());
		logger.debug("Used Margin: {}", position.getUsedMargin());
	}
	
	private void dumpSecurityState(Security security) {
		logger.debug("Security state ----------------------------------");
		logger.debug("       Name: {}", security.getDisplayName());
		logger.debug("     Symbol: {}", security.getSymbol());
		logger.debug(" Expiration: {}", security.getExpirationTime());
		logger.debug("   Lot Size: {}", security.getLotSize());
		logger.debug("  Tick Size: {}", security.getTickSize());
		logger.debug("  Tick Val.: {}", security.getTickValue());
		logger.debug("         IM: {}", security.getInitialMargin());
		logger.debug("Upper Price: {}", security.getUpperPriceLimit());
		logger.debug("Lower Price: {}", security.getLowerPriceLimit());
		logger.debug("Setl. Price: {}", security.getSettlementPrice());
		logger.debug("   Best Ask: {}", security.getBestAsk());
		logger.debug("   Best Bid: {}", security.getBestBid());
		logger.debug(" Last Trade: {}", security.getLastTrade());
		logger.debug("       Open: {}", security.getOpenPrice());
		logger.debug("       High: {}", security.getHighPrice());
		logger.debug("        Low: {}", security.getLowPrice());
		logger.debug("      Close: {}", security.getClosePrice());
	}

}

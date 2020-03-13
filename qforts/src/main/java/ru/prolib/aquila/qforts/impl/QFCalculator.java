package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderField;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class QFCalculator {
	private static final CDecimal ZERO = CDecimalBD.ZERO;
	private static final CDecimal ZERO_MONEY5 = CDecimalBD.ZERO_RUB5;
	private final QFCalcUtils utils;
	
	public QFCalculator(QFCalcUtils utils) {
		this.utils = utils;
	}
	
	public QFCalculator() {
		this(new QFCalcUtils());
	}
	
	private QFPortfolioChangeUpdate updateEquityAndFreeMargin(QFPortfolioChangeUpdate update) {
		return update.setFinalEquity(update.getFinalBalance().add(update.getFinalProfitAndLoss()))
			.setFinalFreeMargin(update.getFinalEquity().subtract(update.getFinalUsedMargin()));
	}
	
	private QFPortfolioChangeUpdate setInitialValues(Portfolio portfolio, QFPortfolioChangeUpdate update) {
		return update
			.setInitialBalance(portfolio.getCDecimal(PortfolioField.BALANCE, ZERO_MONEY5))
			.setInitialEquity(portfolio.getCDecimal(PortfolioField.EQUITY, ZERO_MONEY5))
			.setInitialFreeMargin(portfolio.getCDecimal(PortfolioField.FREE_MARGIN, ZERO_MONEY5))
			.setInitialProfitAndLoss(portfolio.getCDecimal(PortfolioField.PROFIT_AND_LOSS, ZERO_MONEY5))
			.setInitialUsedMargin(portfolio.getCDecimal(PortfolioField.USED_MARGIN, ZERO_MONEY5))
			.setInitialVarMargin(portfolio.getCDecimal(QFPortfolioField.QF_VAR_MARGIN, ZERO_MONEY5))
			.setInitialVarMarginClose(portfolio.getCDecimal(QFPortfolioField.QF_VAR_MARGIN_CLOSE, ZERO_MONEY5))
			.setInitialVarMarginInter(portfolio.getCDecimal(QFPortfolioField.QF_VAR_MARGIN_INTER, ZERO_MONEY5));
	}
	
	private void checkTickValue(Symbol symbol, QFPositionChangeUpdate pos_update) {
		// If both initial & final tick_val are defined that means position was increased,
		// decreased or swapped. In this case initial must equal to final tick val. Otherwise
		// it possible broken workflow case when clearing procedure was lost.
		CDecimal i_tv = pos_update.getInitialTickValue(), f_tv = pos_update.getFinalTickValue();
		if ( i_tv != null && f_tv != null && i_tv.equals(f_tv) == false ) {
			throw new IllegalStateException(new StringBuilder()
					.append("Tick value changed during position update:")
					.append(" symbol=").append(symbol)
					.append(" expected=").append(i_tv)
					.append(" actual=").append(f_tv)
					.toString());
		}
	}
	
	public QFPortfolioChangeUpdate
		changePosition(Portfolio portfolio, Security security, CDecimal volume, CDecimal price)
	{
		Symbol symbol = security.getSymbol();
		Position pos = portfolio.getPosition(symbol);
		QFPositionChangeUpdate pos_update = utils.changePosition(pos, volume, price);
		checkTickValue(symbol, pos_update);
		QFPortfolioChangeUpdate port_update = new QFPortfolioChangeUpdate(pos_update.getAccount());
		setInitialValues(portfolio, port_update)
			.setPositionUpdate(pos_update)
			.setChangeBalance(ZERO_MONEY5)
			.setChangeProfitAndLoss(pos_update.getChangeProfitAndLoss())
			.setChangeUsedMargin(pos_update.getChangeUsedMargin())
			.setChangeVarMargin(pos_update.getChangeVarMargin())
			.setChangeVarMarginClose(pos_update.getChangeVarMarginClose())
			.setChangeVarMarginInter(pos_update.getChangeVarMarginInter());
		return updateEquityAndFreeMargin(port_update);
	}
	
	public QFPortfolioChangeUpdate updateByMarket(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		CDecimal umgn, vmgn, vmgn_c, vmgn_i, pl;
		umgn = vmgn = vmgn_c = vmgn_i = pl = ZERO_MONEY5;
		for ( Position pos : portfolio.getPositions() ) {
			if ( pos.getCDecimal(PositionField.CURRENT_VOLUME, ZERO).compareTo(ZERO) != 0L ) {
				QFPositionChangeUpdate pos_update = utils.refreshByCurrentState(pos);
				update.setPositionUpdate(pos_update);
				umgn = umgn.add(pos_update.getFinalUsedMargin());
				vmgn = vmgn.add(pos_update.getFinalVarMargin());
				vmgn_c = vmgn_c.add(pos_update.getFinalVarMarginClose());
				vmgn_i = vmgn_i.add(pos_update.getFinalVarMarginInter());
				pl = pl.add(pos_update.getFinalProfitAndLoss());
			}
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(ZERO_MONEY5)
			.setFinalProfitAndLoss(pl)
			.setFinalUsedMargin(umgn)
			.setFinalVarMargin(vmgn)
			.setFinalVarMarginClose(vmgn_c)
			.setFinalVarMarginInter(vmgn_i);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate updateMargin(Position position) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(position.getAccount());
		setInitialValues(position.getPortfolio(), update)
			.setChangeBalance(ZERO_MONEY5)
			.setChangeProfitAndLoss(ZERO_MONEY5)
			.setChangeUsedMargin(ZERO_MONEY5)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5);
		if ( position.getCDecimal(PositionField.CURRENT_VOLUME, ZERO).compareTo(ZERO) != 0L ) {
			QFPositionChangeUpdate pos_update = utils.refreshByCurrentState(position);
			update.setPositionUpdate(pos_update)
				.setChangeProfitAndLoss(pos_update.getChangeProfitAndLoss())
				.setChangeUsedMargin(pos_update.getChangeUsedMargin())
				.setChangeVarMargin(pos_update.getChangeVarMargin())
				.setChangeVarMarginClose(pos_update.getChangeVarMarginClose())
				.setChangeVarMarginInter(pos_update.getChangeVarMarginInter());
		}
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate changeBalance(Portfolio portfolio, CDecimal value) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount())
			.setChangeBalance(value)
			.setChangeProfitAndLoss(ZERO_MONEY5)
			.setChangeUsedMargin(ZERO_MONEY5)
			.setChangeVarMargin(ZERO_MONEY5)
			.setChangeVarMarginClose(ZERO_MONEY5)
			.setChangeVarMarginInter(ZERO_MONEY5);
		setInitialValues(portfolio, update);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFOrderExecutionUpdate executeOrder(Order order, CDecimal volume, CDecimal price) {
		QFOrderExecutionUpdate update = new QFOrderExecutionUpdate()
			.setInitialCurrentVolume(order.getCDecimal(OrderField.CURRENT_VOLUME, ZERO))
			.setInitialExecutedValue(order.getCDecimal(OrderField.EXECUTED_VALUE, ZERO_MONEY5))
			.setInitialStatus(order.getStatus())
			.setChangeCurrentVolume(volume.negate())
			.setChangeExecutedValue(utils.priceToMoney(order, price, volume))
			.setFinalStatus(order.getStatus());
		update.setExecutionAction(order.getAction())
			.setExecutionOrderID(order.getID())
			.setExecutionPrice(price)
			.setExecutionSymbol(order.getSymbol())
			.setExecutionTime(order.getTerminal().getCurrentTime()) // No prob with concurrency
			.setExecutionValue(update.getChangeExecutedValue().abs())
			.setExecutionVolume(volume);
		if ( update.getFinalCurrentVolume().compareTo(ZERO) <= 0 ) {
			update.setFinalStatus(OrderStatus.FILLED)
				.setFinalizationTime(update.getExecutionTime());
		}
		return update;
	}
	
	public QFOrderStatusUpdate updateOrderStatus(Order order,
			OrderStatus status, String systemMessage)
	{
		QFOrderStatusUpdate update = new QFOrderStatusUpdate()
			.setInitialStatus(order.getStatus())
			.setFinalStatus(status)
			.setSystemMessage(systemMessage);
		if ( status.isFinal() ) {
			update.setFinalizationTime(order.getTerminal().getCurrentTime()); // No prob with concurrency
		}
		return update;
	}

	public QFPortfolioChangeUpdate midClearing(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		CDecimal finalUsedMargin = ZERO_MONEY5, finalVarMarginInt = ZERO_MONEY5, finalPL = ZERO_MONEY5;
		for ( Position pos : portfolio.getPositions() ) {
			QFPositionChangeUpdate pu = utils.midClearing(pos);
			update.setPositionUpdate(pu);
			finalUsedMargin = finalUsedMargin.add(pu.getFinalUsedMargin());
			finalVarMarginInt = finalVarMarginInt.add(pu.getFinalVarMarginInter());
			finalPL = finalPL.add(pu.getFinalProfitAndLoss());
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(ZERO_MONEY5)
			.setFinalProfitAndLoss(finalPL)
			.setFinalUsedMargin(finalUsedMargin)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(finalVarMarginInt);
		return updateEquityAndFreeMargin(update);
	}
	
	public QFPortfolioChangeUpdate clearing(Portfolio portfolio) {
		QFPortfolioChangeUpdate update = new QFPortfolioChangeUpdate(portfolio.getAccount());
		CDecimal finalUsedMargin = ZERO_MONEY5, changeBalance = ZERO_MONEY5;
		for ( Position pos : portfolio.getPositions() ) {
			QFPositionChangeUpdate pu = utils.clearing(pos);
			update.setPositionUpdate(pu);
			finalUsedMargin = finalUsedMargin.add(pu.getFinalUsedMargin());
			changeBalance = changeBalance.add(pu.getChangeBalance());
		}
		setInitialValues(portfolio, update)
			.setChangeBalance(changeBalance)
			.setFinalUsedMargin(finalUsedMargin)
			.setFinalProfitAndLoss(ZERO_MONEY5)
			.setFinalVarMargin(ZERO_MONEY5)
			.setFinalVarMarginClose(ZERO_MONEY5)
			.setFinalVarMarginInter(ZERO_MONEY5);
		return updateEquityAndFreeMargin(update);
	}
}

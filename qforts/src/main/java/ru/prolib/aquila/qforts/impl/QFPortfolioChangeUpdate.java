package ru.prolib.aquila.qforts.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.CDValueTriplet;

public class QFPortfolioChangeUpdate {
	private static final String RUB = "RUB";
	protected final Account account;
	protected final Map<Symbol, QFPositionChangeUpdate> positions;
	protected final CDValueTriplet balance, equity, freeMargin, profitAndLoss,
		usedMargin, varMargin, varMarginClose, varMarginInter;
	
	public QFPortfolioChangeUpdate(Account account) {
		this.account = account;
		positions = new LinkedHashMap<>();
		balance = new CDValueTriplet(5, RUB);
		equity = new CDValueTriplet(5, RUB);
		freeMargin = new CDValueTriplet(5, RUB);
		profitAndLoss = new CDValueTriplet(5, RUB);
		usedMargin = new CDValueTriplet(5, RUB);
		varMargin = new CDValueTriplet(5, RUB);
		varMarginClose = new CDValueTriplet(5, RUB);
		varMarginInter = new CDValueTriplet(5, RUB);
	}
	
	public Account getAccount() {
		return account;
	}
	
	public QFPositionChangeUpdate getOrCreatePositionUpdate(Symbol symbol) {
		QFPositionChangeUpdate x = positions.get(symbol);
		if ( x == null ) {
			x = new QFPositionChangeUpdate(account, symbol);
			positions.put(symbol, x);
		}
		return x;
	}
	
	public QFPortfolioChangeUpdate setPositionUpdate(QFPositionChangeUpdate update) {
		if ( ! account.equals(update.getAccount()) ) {
			throw new IllegalArgumentException("Unexpected account");
		}
		positions.put(update.getSymbol(), update);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeBalance(CDecimal value) {
		balance.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeEquity(CDecimal value) {
		equity.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeFreeMargin(CDecimal value) {
		freeMargin.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeProfitAndLoss(CDecimal value) {
		profitAndLoss.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeUsedMargin(CDecimal value) {
		usedMargin.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeVarMargin(CDecimal value) {
		varMargin.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeVarMarginClose(CDecimal value) {
		varMarginClose.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeVarMarginInter(CDecimal value) {
		varMarginInter.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialBalance(CDecimal value) {
		balance.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialEquity(CDecimal value) {
		equity.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialFreeMargin(CDecimal value) {
		freeMargin.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialProfitAndLoss(CDecimal value) {
		profitAndLoss.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialUsedMargin(CDecimal value) {
		usedMargin.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialVarMargin(CDecimal value) {
		varMargin.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialVarMarginClose(CDecimal value) {
		varMarginClose.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialVarMarginInter(CDecimal value) {
		varMarginInter.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalBalance(CDecimal value) {
		balance.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalEquity(CDecimal value) {
		equity.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalFreeMargin(CDecimal value) {
		freeMargin.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalProfitAndLoss(CDecimal value) {
		profitAndLoss.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalUsedMargin(CDecimal value) {
		usedMargin.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalVarMargin(CDecimal value) {
		varMargin.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalVarMarginClose(CDecimal value) {
		varMarginClose.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalVarMarginInter(CDecimal value) {
		varMarginInter.setFinalValue(value);
		return this;
	}
	
	public List<QFPositionChangeUpdate> getPositionUpdates() {
		return new ArrayList<>(positions.values());
	}
	
	public QFPositionChangeUpdate getPositionUpdate(Symbol symbol) {
		QFPositionChangeUpdate x = positions.get(symbol);
		if ( x == null ) {
			throw new IllegalArgumentException("Position not exists: " + symbol);
		}
		return x;
	}
	
	public CDecimal getChangeBalance() {
		return balance.getChangeValue();
	}
	
	public CDecimal getChangeEquity() {
		return equity.getChangeValue();
	}
	
	public CDecimal getChangeFreeMargin() {
		return freeMargin.getChangeValue();
	}
	
	public CDecimal getChangeProfitAndLoss() {
		return profitAndLoss.getChangeValue();
	}
	
	public CDecimal getChangeUsedMargin() {
		return usedMargin.getChangeValue();
	}
	
	public CDecimal getChangeVarMargin() {
		return varMargin.getChangeValue();
	}
	
	public CDecimal getChangeVarMarginClose() {
		return varMarginClose.getChangeValue();
	}
	
	public CDecimal getChangeVarMarginInter() {
		return varMarginInter.getChangeValue();
	}
	
	public CDecimal getInitialBalance() {
		return balance.getInitialValue();
	}
	
	public CDecimal getInitialEquity() {
		return equity.getInitialValue();
	}
	
	public CDecimal getInitialFreeMargin() {
		return freeMargin.getInitialValue();
	}
	
	public CDecimal getInitialProfitAndLoss() {
		return profitAndLoss.getInitialValue();
	}
	
	public CDecimal getInitialUsedMargin() {
		return usedMargin.getInitialValue();
	}
	
	public CDecimal getInitialVarMargin() {
		return varMargin.getInitialValue();
	}
	
	public CDecimal getInitialVarMarginClose() {
		return varMarginClose.getInitialValue();
	}
	
	public CDecimal getInitialVarMarginInter() {
		return varMarginInter.getInitialValue();
	}
	
	public CDecimal getFinalBalance() {
		return balance.getFinalValue();
	}
	
	public CDecimal getFinalEquity() {
		return equity.getFinalValue();
	}
	
	public CDecimal getFinalFreeMargin() {
		return freeMargin.getFinalValue();
	}
	
	public CDecimal getFinalProfitAndLoss() {
		return profitAndLoss.getFinalValue();
	}
	
	public CDecimal getFinalUsedMargin() {
		return usedMargin.getFinalValue();
	}
	
	public CDecimal getFinalVarMargin() {
		return varMargin.getFinalValue();
	}
	
	public CDecimal getFinalVarMarginClose() {
		return varMarginClose.getFinalValue();
	}
	
	public CDecimal getFinalVarMarginInter() {
		return varMarginInter.getFinalValue();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[a=" + account
			+ " bal" + balance
			+ " eq" + equity
			+ " fm" + freeMargin
			+ " pl" + profitAndLoss
			+ " um" + usedMargin
			+ " vm" + varMargin
			+ " vmc" + varMarginClose
			+ " vmi" + varMarginInter
			+ getPositionUpdates()
			+ "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QFPortfolioChangeUpdate.class ) {
			return false;
		}
		QFPortfolioChangeUpdate o = (QFPortfolioChangeUpdate) other;
		return new EqualsBuilder()
			.append(o.account, account)
			.append(o.balance, balance)
			.append(o.equity, equity)
			.append(o.freeMargin, freeMargin)
			.append(o.positions, positions)
			.append(o.profitAndLoss, profitAndLoss)
			.append(o.usedMargin, usedMargin)
			.append(o.varMargin, varMargin)
			.append(o.varMarginClose, varMarginClose)
			.append(o.varMarginInter, varMarginInter)
			.isEquals();
	}
	
}

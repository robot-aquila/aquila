package ru.prolib.aquila.qforts.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.FMValueTriplet;

public class QFPortfolioChangeUpdate {
	protected final Account account;
	protected final Map<Symbol, QFPositionChangeUpdate> positions;
	protected final FMValueTriplet balance, equity, freeMargin, profitAndLoss,
		usedMargin, varMargin, varMarginClose, varMarginInter;
	
	public QFPortfolioChangeUpdate(Account account) {
		this.account = account;
		positions = new LinkedHashMap<>();
		balance = new FMValueTriplet();
		equity = new FMValueTriplet();
		freeMargin = new FMValueTriplet();
		profitAndLoss = new FMValueTriplet();
		usedMargin = new FMValueTriplet();
		varMargin = new FMValueTriplet();
		varMarginClose = new FMValueTriplet();
		varMarginInter = new FMValueTriplet();
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
	
	public QFPortfolioChangeUpdate setChangeBalance(FMoney value) {
		balance.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeEquity(FMoney value) {
		equity.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeFreeMargin(FMoney value) {
		freeMargin.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeProfitAndLoss(FMoney value) {
		profitAndLoss.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeUsedMargin(FMoney value) {
		usedMargin.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeVarMargin(FMoney value) {
		varMargin.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeVarMarginClose(FMoney value) {
		varMarginClose.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setChangeVarMarginInter(FMoney value) {
		varMarginInter.setChangeValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialBalance(FMoney value) {
		balance.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialEquity(FMoney value) {
		equity.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialFreeMargin(FMoney value) {
		freeMargin.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialProfitAndLoss(FMoney value) {
		profitAndLoss.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialUsedMargin(FMoney value) {
		usedMargin.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialVarMargin(FMoney value) {
		varMargin.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialVarMarginClose(FMoney value) {
		varMarginClose.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setInitialVarMarginInter(FMoney value) {
		varMarginInter.setInitialValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalBalance(FMoney value) {
		balance.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalEquity(FMoney value) {
		equity.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalFreeMargin(FMoney value) {
		freeMargin.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalProfitAndLoss(FMoney value) {
		profitAndLoss.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalUsedMargin(FMoney value) {
		usedMargin.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalVarMargin(FMoney value) {
		varMargin.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalVarMarginClose(FMoney value) {
		varMarginClose.setFinalValue(value);
		return this;
	}
	
	public QFPortfolioChangeUpdate setFinalVarMarginInter(FMoney value) {
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
	
	public FMoney getChangeBalance() {
		return balance.getChangeValue();
	}
	
	public FMoney getChangeEquity() {
		return equity.getChangeValue();
	}
	
	public FMoney getChangeFreeMargin() {
		return freeMargin.getChangeValue();
	}
	
	public FMoney getChangeProfitAndLoss() {
		return profitAndLoss.getChangeValue();
	}
	
	public FMoney getChangeUsedMargin() {
		return usedMargin.getChangeValue();
	}
	
	public FMoney getChangeVarMargin() {
		return varMargin.getChangeValue();
	}
	
	public FMoney getChangeVarMarginClose() {
		return varMarginClose.getChangeValue();
	}
	
	public FMoney getChangeVarMarginInter() {
		return varMarginInter.getChangeValue();
	}
	
	public FMoney getInitialBalance() {
		return balance.getInitialValue();
	}
	
	public FMoney getInitialEquity() {
		return equity.getInitialValue();
	}
	
	public FMoney getInitialFreeMargin() {
		return freeMargin.getInitialValue();
	}
	
	public FMoney getInitialProfitAndLoss() {
		return profitAndLoss.getInitialValue();
	}
	
	public FMoney getInitialUsedMargin() {
		return usedMargin.getInitialValue();
	}
	
	public FMoney getInitialVarMargin() {
		return varMargin.getInitialValue();
	}
	
	public FMoney getInitialVarMarginClose() {
		return varMarginClose.getInitialValue();
	}
	
	public FMoney getInitialVarMarginInter() {
		return varMarginInter.getInitialValue();
	}
	
	public FMoney getFinalBalance() {
		return balance.getFinalValue();
	}
	
	public FMoney getFinalEquity() {
		return equity.getFinalValue();
	}
	
	public FMoney getFinalFreeMargin() {
		return freeMargin.getFinalValue();
	}
	
	public FMoney getFinalProfitAndLoss() {
		return profitAndLoss.getFinalValue();
	}
	
	public FMoney getFinalUsedMargin() {
		return usedMargin.getFinalValue();
	}
	
	public FMoney getFinalVarMargin() {
		return varMargin.getFinalValue();
	}
	
	public FMoney getFinalVarMarginClose() {
		return varMarginClose.getFinalValue();
	}
	
	public FMoney getFinalVarMarginInter() {
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

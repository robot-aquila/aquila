package ru.prolib.aquila.qforts.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.FDValueTriplet;
import ru.prolib.aquila.core.utils.FMValueTriplet;
import ru.prolib.aquila.core.utils.LValueTriplet;

public class QFPositionChangeUpdate {
	protected final Account account;
	protected final Symbol symbol;
	protected final FDValueTriplet currentPrice, openPrice;
	protected final FMValueTriplet profitAndLoss, usedMargin, varMargin,
		varMarginClose, varMarginInter, accountBalance;
	protected final LValueTriplet volume;
	
	public QFPositionChangeUpdate(Account account, Symbol symbol) {
		this.account = account;
		this.symbol = symbol;
		currentPrice = new FDValueTriplet();
		openPrice = new FDValueTriplet();
		profitAndLoss = new FMValueTriplet();
		usedMargin = new FMValueTriplet();
		varMargin = new FMValueTriplet();
		varMarginClose = new FMValueTriplet();
		varMarginInter = new FMValueTriplet();
		accountBalance = new FMValueTriplet();
		volume = new LValueTriplet();
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public QFPositionChangeUpdate setChangeBalance(FMoney value) {
		accountBalance.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeCurrentPrice(FDecimal value) {
		currentPrice.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeOpenPrice(FDecimal value) {
		openPrice.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeProfitAndLoss(FMoney value) {
		profitAndLoss.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeUsedMargin(FMoney value) {
		usedMargin.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVarMargin(FMoney value) {
		varMargin.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVarMarginClose(FMoney value) {
		varMarginClose.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVarMarginInter(FMoney value) {
		varMarginInter.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVolume(long value) {
		volume.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialBalance(FMoney value) {
		accountBalance.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialCurrentPrice(FDecimal value) {
		currentPrice.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialOpenPrice(FDecimal value) {
		openPrice.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialProfitAndLoss(FMoney value) {
		profitAndLoss.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialUsedMargin(FMoney value) {
		usedMargin.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVarMargin(FMoney value) {
		varMargin.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVarMarginClose(FMoney value) {
		varMarginClose.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVarMarginInter(FMoney value) {
		varMarginInter.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVolume(long value) {
		volume.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalBalance(FMoney value) {
		accountBalance.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalCurrentPrice(FDecimal value) {
		currentPrice.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalOpenPrice(FDecimal value) {
		openPrice.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalProfitAndLoss(FMoney value) {
		profitAndLoss.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalUsedMargin(FMoney value) {
		usedMargin.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVarMargin(FMoney value) {
		varMargin.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVarMarginClose(FMoney value) {
		varMarginClose.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVarMarginInter(FMoney value) {
		varMarginInter.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVolume(long value) {
		volume.setFinalValue(value);
		return this;
	}
	
	public FMoney getChangeBalance() {
		return accountBalance.getChangeValue();
	}
	
	public FDecimal getChangeCurrentPrice() {
		return currentPrice.getChangeValue();
	}
	
	public FDecimal getChangeOpenPrice() {
		return openPrice.getChangeValue();
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
	
	public long getChangeVolume() {
		return volume.getChangeValue();
	}
	
	public FDecimal getInitialCurrentPrice() {
		return currentPrice.getInitialValue();
	}
	
	public FDecimal getInitialOpenPrice() {
		return openPrice.getInitialValue();
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
	
	public long getInitialVolume() {
		return volume.getInitialValue();
	}

	public FDecimal getFinalCurrentPrice() {
		return currentPrice.getFinalValue();
	}
	
	public FDecimal getFinalOpenPrice() {
		return openPrice.getFinalValue();
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
	
	public long getFinalVolume() {
		return volume.getFinalValue();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[a=" + account
				+ " s=" + symbol
				+ " bal" + accountBalance
				+ " cp" + currentPrice
				+ " op" + openPrice
				+ " pl" + profitAndLoss
				+ " um" + usedMargin
				+ " vm" + varMargin
				+ " vmc" + varMarginClose
				+ " vmi" + varMarginInter
				+ " vol" + volume
				+ "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QFPositionChangeUpdate.class ) {
			return false;
		}
		QFPositionChangeUpdate o = (QFPositionChangeUpdate) other;
		return new EqualsBuilder()
			.append(o.account, account)
			.append(o.symbol, symbol)
			.append(o.accountBalance, accountBalance)
			.append(o.currentPrice, currentPrice)
			.append(o.openPrice, openPrice)
			.append(o.profitAndLoss, profitAndLoss)
			.append(o.usedMargin, usedMargin)
			.append(o.varMargin, varMargin)
			.append(o.varMarginClose, varMarginClose)
			.append(o.varMarginInter, varMarginInter)
			.append(o.volume, volume)
			.isEquals();
	}

}

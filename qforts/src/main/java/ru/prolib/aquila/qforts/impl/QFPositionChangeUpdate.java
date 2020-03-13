package ru.prolib.aquila.qforts.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.CDValueTriplet;

public class QFPositionChangeUpdate {
	private static final String RUB = "RUB";
	protected final Account account;
	protected final Symbol symbol;
	protected final CDValueTriplet currentPrice, openPrice, profitAndLoss,
		usedMargin, varMargin, varMarginClose, varMarginInter, accountBalance,
		volume;
	protected CDecimal initialTickValue, finalTickValue;
	
	public QFPositionChangeUpdate(Account account, Symbol symbol) {
		this.account = account;
		this.symbol = symbol;
		currentPrice = new CDValueTriplet();
		openPrice = new CDValueTriplet();
		profitAndLoss = new CDValueTriplet(5, RUB);
		usedMargin = new CDValueTriplet(5, RUB);
		varMargin = new CDValueTriplet(5, RUB);
		varMarginClose = new CDValueTriplet(5, RUB);
		varMarginInter = new CDValueTriplet(5, RUB);
		accountBalance = new CDValueTriplet(5, RUB);
		volume = new CDValueTriplet(0);
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public QFPositionChangeUpdate setChangeBalance(CDecimal value) {
		accountBalance.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeCurrentPrice(CDecimal value) {
		currentPrice.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeOpenPrice(CDecimal value) {
		openPrice.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeProfitAndLoss(CDecimal value) {
		profitAndLoss.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeUsedMargin(CDecimal value) {
		usedMargin.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVarMargin(CDecimal value) {
		varMargin.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVarMarginClose(CDecimal value) {
		varMarginClose.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVarMarginInter(CDecimal value) {
		varMarginInter.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setChangeVolume(CDecimal value) {
		volume.setChangeValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialBalance(CDecimal value) {
		accountBalance.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialCurrentPrice(CDecimal value) {
		currentPrice.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialOpenPrice(CDecimal value) {
		openPrice.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialProfitAndLoss(CDecimal value) {
		profitAndLoss.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialUsedMargin(CDecimal value) {
		usedMargin.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVarMargin(CDecimal value) {
		varMargin.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVarMarginClose(CDecimal value) {
		varMarginClose.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVarMarginInter(CDecimal value) {
		varMarginInter.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialVolume(CDecimal value) {
		volume.setInitialValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setInitialTickValue(CDecimal value) {
		this.initialTickValue = value;
		return this;
	}
	
	public QFPositionChangeUpdate setFinalBalance(CDecimal value) {
		accountBalance.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalCurrentPrice(CDecimal value) {
		currentPrice.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalOpenPrice(CDecimal value) {
		openPrice.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalProfitAndLoss(CDecimal value) {
		profitAndLoss.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalUsedMargin(CDecimal value) {
		usedMargin.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVarMargin(CDecimal value) {
		varMargin.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVarMarginClose(CDecimal value) {
		varMarginClose.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVarMarginInter(CDecimal value) {
		varMarginInter.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalVolume(CDecimal value) {
		volume.setFinalValue(value);
		return this;
	}
	
	public QFPositionChangeUpdate setFinalTickValue(CDecimal value) {
		finalTickValue = value;
		return this;
	}
	
	public CDecimal getChangeBalance() {
		return accountBalance.getChangeValue();
	}
	
	public CDecimal getChangeCurrentPrice() {
		return currentPrice.getChangeValue();
	}
	
	public CDecimal getChangeOpenPrice() {
		return openPrice.getChangeValue();
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
	
	public CDecimal getChangeVolume() {
		return volume.getChangeValue();
	}
	
	public CDecimal getInitialCurrentPrice() {
		return currentPrice.getInitialValue();
	}
	
	public CDecimal getInitialOpenPrice() {
		return openPrice.getInitialValue();
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
	
	public CDecimal getInitialVolume() {
		return volume.getInitialValue();
	}
	
	public CDecimal getInitialTickValue() {
		return initialTickValue;
	}

	public CDecimal getFinalCurrentPrice() {
		return currentPrice.getFinalValue();
	}
	
	public CDecimal getFinalOpenPrice() {
		return openPrice.getFinalValue();
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
	
	public CDecimal getFinalVolume() {
		return volume.getFinalValue();
	}
	
	public CDecimal getFinalTickValue() {
		return finalTickValue;
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
				+ new StringBuilder()
					.append(" tv[i=").append(initialTickValue).append(" f=").append(finalTickValue).append("]")
					.toString()
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
			.append(o.initialTickValue, initialTickValue)
			.append(o.finalTickValue, finalTickValue)
			.isEquals();
	}

}

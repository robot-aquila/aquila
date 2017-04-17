package ru.prolib.aquila.qforts.impl;

import java.math.RoundingMode;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class QFCalcUtils {
	
	private FDecimal getCurrentPrice(Security security) {
		Tick x = security.getLastTrade();
		if ( x == null ) {
			return security.getSettlementPrice();
		}
		return FDecimal.of(x.getPrice(), security.getScale());
	}
	
	private FDecimal getCurrentPrice(Security security, long volume) {
		return getCurrentPrice(security).multiply(volume);
	}
	
	private FMoney getUsedMargin(Security security, long volume) {
		return security.getInitialMargin().multiply(volume).abs();
	}
	
	private FMoney priceToMoney(Security security, FDecimal price) {
		return security.getTickValue().multiply(price.divide(security
				.getTickSize(), 0, RoundingMode.UNNECESSARY));
	}
	
	public FMoney priceToMoney(Security security, long volume, FDecimal price) {
		return priceToMoney(security, price.multiply(volume));
	}
	
	private FMoney getVarMargin(Security security, FDecimal fCurPr, FDecimal fOpnPr) {
		return priceToMoney(security, fCurPr.subtract(fOpnPr)).withScale(5);
	}
	
	private FMoney getVarMargin(Security security, QFPositionChangeUpdate update) {
		return getVarMargin(security, update.getFinalCurrentPrice(), update.getFinalOpenPrice());
	}
	
	private FMoney getProfitAndLoss(FMoney fVarMgn, FMoney fVarMgnC, FMoney fVarMgnI) {
		return fVarMgn.add(fVarMgnC).add(fVarMgnI).withScale(2);
	}
	
	private FMoney getProfitAndLoss(QFPositionChangeUpdate update) {
		return getProfitAndLoss(update.getFinalVarMargin(),
				update.getFinalVarMarginClose(),
				update.getFinalVarMarginInter());
	}
	
	public QFPositionChangeUpdate
		changePosition(Position position, long volume, FDecimal price)
	{
		if ( volume == 0L ) {
			throw new IllegalArgumentException("Volume cannot be zero");
		}
		Security security = position.getSecurity();
		int pvScale = security.getScale();
		long
			iCurVol = position.getLongOrZero(PositionField.CURRENT_VOLUME),
			fCurVol = 0L;
		FDecimal
			iOpnPr = position.getDecimalOrZero(PositionField.OPEN_PRICE, pvScale),
			fOpnPr = FDecimal.of(0.0, pvScale),
			iCurPr = position.getDecimalOrZero(PositionField.CURRENT_PRICE, pvScale),
			fCurPr = FDecimal.of(0.0, pvScale);
		FMoney
			iPL = position.getMoneyOrZero2(PositionField.PROFIT_AND_LOSS, FMoney.RUB),
			fPL = FMoney.ZERO_RUB2,
			iUsMgn = position.getMoneyOrZero2(PositionField.USED_MARGIN, FMoney.RUB),
			fUsMgn = FMoney.ZERO_RUB2,
			iVarMgn = position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN, 5, FMoney.RUB),
			fVarMgn = FMoney.ZERO_RUB5,
			iVarMgnC = position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_CLOSE, 5, FMoney.RUB).withScale(5),
			fVarMgnC = iVarMgnC,
			iVarMgnI = position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_INTER, 5, FMoney.RUB).withScale(5),
			fVarMgnI = iVarMgnI;
		
		if ( iCurVol == 0L ) {
			// open long/short position
			fCurVol = volume;
			fOpnPr = price.multiply(volume);
			
		} else if ( (iCurVol > 0 && volume < 0)
				 || (iCurVol < 0 && volume > 0) )
		{
			if ( iCurVol == -volume ) {
				// close long/short position
				fVarMgnC = iVarMgnC.add(priceToMoney(security, price.multiply(iCurVol)
						.subtract(iOpnPr))).withScale(5);
				fCurVol = 0L;
				fOpnPr = FDecimal.of(0.0, pvScale);
				
			} else if ( Math.abs(iCurVol) < Math.abs(volume) ) {
				// swap long/short position
				fVarMgnC = iVarMgnC.add(priceToMoney(security, price.multiply(iCurVol)
						.subtract(iOpnPr))).withScale(5);
				long V_r = iCurVol + volume;
				fCurVol = V_r;
				fOpnPr = price.multiply(V_r);
				
			} else {
				// decrease long/short position
				FDecimal OP_avg = iOpnPr.divide(iCurVol)
						.divide(security.getTickSize())
						.withScale(0)
						.multiply(security.getTickSize());
				fCurVol = iCurVol + volume;
				fOpnPr = iOpnPr.add(OP_avg.multiply(volume));
				fVarMgnC = iVarMgnC.add(priceToMoney(security, price.multiply(-volume)
						.subtract(OP_avg.multiply(-volume))));
				
			}
		} else {
			// increase long/short position
			fCurVol = iCurVol + volume;
			fOpnPr = iOpnPr.add(price.multiply(volume));
			
		}
		
		fCurPr = getCurrentPrice(security, fCurVol);
		fUsMgn = getUsedMargin(security, fCurVol);
		fVarMgn = getVarMargin(security, fCurPr, fOpnPr);
		fPL = getProfitAndLoss(fVarMgn, fVarMgnC, fVarMgnI);
		return new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(iCurPr)
			.setInitialOpenPrice(iOpnPr)
			.setInitialProfitAndLoss(iPL)
			.setInitialUsedMargin(iUsMgn)
			.setInitialVarMargin(iVarMgn)
			.setInitialVarMarginClose(iVarMgnC)
			.setInitialVarMarginInter(iVarMgnI)
			.setInitialVolume(iCurVol)
			.setFinalCurrentPrice(fCurPr)
			.setFinalOpenPrice(fOpnPr)
			.setFinalProfitAndLoss(fPL)
			.setFinalUsedMargin(fUsMgn)
			.setFinalVarMargin(fVarMgn)
			.setFinalVarMarginClose(fVarMgnC)
			.setFinalVarMarginInter(fVarMgnI)
			.setFinalVolume(fCurVol);
	}
	
	/**
	 * Calculate position update according to security current state.
	 * <p>
	 * @param position - position to refresh
	 * @param currentPrice - current price per unit. This value is used to
	 * calculate position current price.
	 * @return calculated changes
	 */
	public QFPositionChangeUpdate refreshByCurrentState(Position position, FDecimal currentPrice) {
		Security security = position.getSecurity();
		int pvScale = security.getScale();
		long volume;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setInitialCurrentPrice(position.getDecimalOrZero(PositionField.CURRENT_PRICE, pvScale))
			.setInitialOpenPrice(position.getDecimalOrZero(PositionField.OPEN_PRICE, pvScale))
			.setInitialProfitAndLoss(position.getMoneyOrZero2(PositionField.PROFIT_AND_LOSS, FMoney.RUB))
			.setInitialUsedMargin(position.getMoneyOrZero2(PositionField.USED_MARGIN, FMoney.RUB))
			.setInitialVarMargin(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN, 5, FMoney.RUB))
			.setInitialVarMarginClose(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_CLOSE, 5, FMoney.RUB))
			.setInitialVarMarginInter(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_INTER, 5, FMoney.RUB))
			.setInitialVolume(volume = position.getLongOrZero(PositionField.CURRENT_VOLUME));
		update.setFinalOpenPrice(update.getInitialOpenPrice())
			.setFinalVarMarginClose(update.getInitialVarMarginClose())
			.setFinalVarMarginInter(update.getInitialVarMarginInter())
			.setFinalVolume(volume);
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalCurrentPrice(currentPrice.multiply(volume));
		update.setFinalVarMargin(getVarMargin(security, update));
		update.setFinalProfitAndLoss(getProfitAndLoss(update));
		return update;
	}
	
	/**
	 * Calculate position update according to security current state.
	 * <p>
	 * @param position - position to refresh
	 * @return calculated changes
	 */
	public QFPositionChangeUpdate refreshByCurrentState(Position position) {
		 return refreshByCurrentState(position, getCurrentPrice(position.getSecurity()));
	}
	
	public QFPositionChangeUpdate midClearing(Position position) {
		Security security = position.getSecurity();
		int pvScale = security.getScale();
		long volume;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(FMoney.ZERO_RUB2)
			.setChangeVolume(0L)
			.setInitialCurrentPrice(position.getDecimalOrZero(PositionField.CURRENT_PRICE, pvScale))
			.setInitialOpenPrice(position.getDecimalOrZero(PositionField.OPEN_PRICE, pvScale))
			.setInitialProfitAndLoss(position.getMoneyOrZero2(PositionField.PROFIT_AND_LOSS, FMoney.RUB))
			.setInitialUsedMargin(position.getMoneyOrZero2(PositionField.USED_MARGIN, FMoney.RUB))
			.setInitialVarMargin(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN, 5, FMoney.RUB))
			.setInitialVarMarginClose(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_CLOSE, 5, FMoney.RUB))
			.setInitialVarMarginInter(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_INTER, 5, FMoney.RUB))
			.setInitialVolume(volume = position.getLongOrZero(PositionField.CURRENT_VOLUME));
		FDecimal curPr = security.getSettlementPrice().multiply(volume);
		FMoney varMgn = getVarMargin(security, curPr, update.getInitialOpenPrice());
		update.setFinalCurrentPrice(curPr)
			.setFinalOpenPrice(curPr)
			.setFinalVarMarginInter(varMgn.add(update.getInitialVarMarginClose()))
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5);
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalProfitAndLoss(getProfitAndLoss(update));
		return update;
	}
	
	public QFPositionChangeUpdate clearing(Position position) {
		Security security = position.getSecurity();
		int pvScale = security.getScale();
		long volume;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeVolume(0L)
			.setInitialCurrentPrice(position.getDecimalOrZero(PositionField.CURRENT_PRICE, pvScale))
			.setInitialOpenPrice(position.getDecimalOrZero(PositionField.OPEN_PRICE, pvScale))
			.setInitialProfitAndLoss(position.getMoneyOrZero2(PositionField.PROFIT_AND_LOSS, FMoney.RUB))
			.setInitialUsedMargin(position.getMoneyOrZero2(PositionField.USED_MARGIN, FMoney.RUB))
			.setInitialVarMargin(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN, 5, FMoney.RUB))
			.setInitialVarMarginClose(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_CLOSE, 5, FMoney.RUB))
			.setInitialVarMarginInter(position.getMoneyOrZero(QFPositionField.QF_VAR_MARGIN_INTER, 5, FMoney.RUB))
			.setInitialVolume(volume = position.getLongOrZero(PositionField.CURRENT_VOLUME));
		FDecimal curPr = security.getSettlementPrice().multiply(volume);
		FMoney cBal = getVarMargin(security, curPr, update.getInitialOpenPrice())
				.add(update.getInitialVarMarginClose())
				.add(update.getInitialVarMarginInter())
				.withScale(2);
		update.setChangeBalance(cBal);
		update.setFinalCurrentPrice(curPr)
			.setFinalOpenPrice(curPr)
			.setFinalVarMarginInter(FMoney.ZERO_RUB5)
			.setFinalVarMargin(FMoney.ZERO_RUB5)
			.setFinalVarMarginClose(FMoney.ZERO_RUB5);
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalProfitAndLoss(getProfitAndLoss(update));
		return update;
	}

}

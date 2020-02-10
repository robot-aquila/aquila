package ru.prolib.aquila.qforts.impl;

import java.math.RoundingMode;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class QFCalcUtils {
	
	private CDecimal getCurrentPrice(Security security) {
		Tick x = security.getLastTrade();
		if ( x == null ) {
			return security.getSettlementPrice(); // TODO: this does not work cuz settlement price isn't mandatory
		}
		return x.getPrice();
	}
	
	private CDecimal getCurrentPrice(Security security, CDecimal volume) {
		return getCurrentPrice(security).multiply(volume);
	}
	
	private CDecimal getUsedMargin(Security security, CDecimal volume) {
		return security.getInitialMargin().multiply(volume).abs();
	}
	
	private CDecimal priceToMoney(Security security, CDecimal price) {
		return security.getTickValue()
			.multiply(price.divide(security.getTickSize())
				.withScale(0, RoundingMode.UNNECESSARY));
	}
	
	public CDecimal priceToMoney(Security security, CDecimal volume, CDecimal price) {
		return priceToMoney(security, price.multiply(volume));
	}
	
	private CDecimal getVarMargin(Security security, CDecimal fCurPr, CDecimal fOpnPr) {
		return priceToMoney(security, fCurPr.subtract(fOpnPr)).withScale(5);
	}
	
	private CDecimal getVarMargin(Security security, QFPositionChangeUpdate update) {
		return getVarMargin(security, update.getFinalCurrentPrice(), update.getFinalOpenPrice());
	}
	
	private CDecimal getProfitAndLoss(CDecimal fVarMgn, CDecimal fVarMgnC, CDecimal fVarMgnI) {
		return fVarMgn.add(fVarMgnC).add(fVarMgnI).withScale(2);
	}
	
	private CDecimal getProfitAndLoss(QFPositionChangeUpdate update) {
		return getProfitAndLoss(update.getFinalVarMargin(),
				update.getFinalVarMarginClose(),
				update.getFinalVarMarginInter());
	}
	
	public QFPositionChangeUpdate
		changePosition(Position position, CDecimal volume, CDecimal price)
	{
		if ( volume.compareTo(CDecimalBD.ZERO) == 0 ) {
			throw new IllegalArgumentException("Volume cannot be zero");
		}
		Security security = position.getSecurity();
		CDecimal zero = CDecimalBD.ZERO,
			zeroPrice = zero.withScale(security.getScale()),
			zeroMoney2 = CDecimalBD.ZERO_RUB2,
			zeroMoney5 = CDecimalBD.ZERO_RUB5;
		CDecimal
			iCurVol = position.getCurrentVolume(),
			fCurVol = zero,
			iOpnPr = position.getOpenPrice(),
			fOpnPr = zeroPrice,
			iCurPr = position.getCurrentPrice(),
			fCurPr = zeroPrice,
			iPL = position.getProfitAndLoss(),
			fPL = zeroMoney2,
			iUsMgn = position.getUsedMargin(),
			fUsMgn = zeroMoney2,
			iVarMgn = position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5),
			fVarMgn = zeroMoney5,
			iVarMgnC = position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5),
			fVarMgnC = iVarMgnC,
			iVarMgnI = position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5),
			fVarMgnI = iVarMgnI;
		if ( iCurVol == null ) iCurVol = zero;
		if ( iOpnPr == null ) iOpnPr = zeroPrice;
		if ( iCurPr == null ) iCurPr = zeroPrice;
		if ( iPL == null ) iPL = zeroMoney2;
		if ( iUsMgn == null ) iUsMgn = zeroMoney2;		
		if ( iCurVol.compareTo(zero) == 0 ) {
			// open long/short position
			fCurVol = volume;
			fOpnPr = price.multiply(volume);
			
		} else if ( (iCurVol.compareTo(zero) > 0 && volume.compareTo(zero) < 0)
				 || (iCurVol.compareTo(zero) < 0 && volume.compareTo(zero) > 0) )
		{
			if ( iCurVol == volume.negate() ) {
				// close long/short position
				fVarMgnC = iVarMgnC.add(priceToMoney(security, price.multiply(iCurVol)
						.subtract(iOpnPr))).withScale(5);
				fCurVol = zero;
				fOpnPr = zeroPrice;
				
			} else if ( iCurVol.abs().compareTo(volume.abs()) < 0 ) {
				// swap long/short position
				fVarMgnC = iVarMgnC.add(priceToMoney(security, price.multiply(iCurVol)
						.subtract(iOpnPr))).withScale(5);
				CDecimal V_r = iCurVol.add(volume);
				fCurVol = V_r;
				fOpnPr = price.multiply(V_r);
				
			} else {
				// decrease long/short position
				CDecimal OP_avg = iOpnPr.divide(iCurVol)
						.divide(security.getTickSize())
						.withScale(0)
						.multiply(security.getTickSize());
				fCurVol = iCurVol.add(volume);
				fOpnPr = iOpnPr.add(OP_avg.multiply(volume));
				fVarMgnC = iVarMgnC.add(priceToMoney(security, price.multiply(volume.negate())
						.subtract(OP_avg.multiply(volume.negate()))));
				
			}
		} else {
			// increase long/short position
			fCurVol = iCurVol.add(volume);
			fOpnPr = iOpnPr.add(price.multiply(volume));
			
		}
		
		fCurPr = getCurrentPrice(security, fCurVol);
		fUsMgn = getUsedMargin(security, fCurVol);
		fVarMgn = getVarMargin(security, fCurPr, fOpnPr);
		fPL = getProfitAndLoss(fVarMgn, fVarMgnC, fVarMgnI);
		return new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(zeroMoney2)
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
	public QFPositionChangeUpdate refreshByCurrentState(Position position, CDecimal currentPrice) {
		Security security = position.getSecurity();
		CDecimal zero = CDecimalBD.ZERO,
				zeroPrice = zero.withScale(security.getScale()),
				zeroMoney2 = CDecimalBD.ZERO_RUB2,
				zeroMoney5 = CDecimalBD.ZERO_RUB5;
		CDecimal volume;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(zeroMoney2)
			.setInitialCurrentPrice(position.getCDecimal(PositionField.CURRENT_PRICE, zeroPrice))
			.setInitialOpenPrice(position.getCDecimal(PositionField.OPEN_PRICE, zeroPrice))
			.setInitialProfitAndLoss(position.getCDecimal(PositionField.PROFIT_AND_LOSS, zeroMoney2))
			.setInitialUsedMargin(position.getCDecimal(PositionField.USED_MARGIN, zeroMoney2))
			.setInitialVarMargin(position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5))
			.setInitialVarMarginClose(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5))
			.setInitialVarMarginInter(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5))
			.setInitialVolume(volume = position.getCDecimal(PositionField.CURRENT_VOLUME, zero));
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
		CDecimal zero = CDecimalBD.ZERO,
				zeroPrice = zero.withScale(security.getScale()),
				zeroMoney2 = CDecimalBD.ZERO_RUB2,
				zeroMoney5 = CDecimalBD.ZERO_RUB5;
		CDecimal volume;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(zeroMoney2)
			.setChangeVolume(zero)
			.setInitialCurrentPrice(position.getCDecimal(PositionField.CURRENT_PRICE, zeroPrice))
			.setInitialOpenPrice(position.getCDecimal(PositionField.OPEN_PRICE, zeroPrice))
			.setInitialProfitAndLoss(position.getCDecimal(PositionField.PROFIT_AND_LOSS, zeroMoney2))
			.setInitialUsedMargin(position.getCDecimal(PositionField.USED_MARGIN, zeroMoney2))
			.setInitialVarMargin(position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5))
			.setInitialVarMarginClose(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5))
			.setInitialVarMarginInter(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5))
			.setInitialVolume(volume = position.getCDecimal(PositionField.CURRENT_VOLUME, zero));
		CDecimal curPr = security.getSettlementPrice().multiply(volume);
		CDecimal varMgn = getVarMargin(security, curPr, update.getInitialOpenPrice());
		CDecimal varMgnInter = varMgn.add(update.getInitialVarMarginClose()).add(update.getInitialVarMarginInter());
		update.setFinalCurrentPrice(curPr)
			.setFinalOpenPrice(curPr)
			.setFinalVarMarginInter(varMgnInter)
			.setFinalVarMargin(zeroMoney5)
			.setFinalVarMarginClose(zeroMoney5);
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalProfitAndLoss(getProfitAndLoss(update));
		return update;
	}
	
	public QFPositionChangeUpdate clearing(Position position) {
		Security security = position.getSecurity();
		CDecimal zero = CDecimalBD.ZERO,
				zeroPrice = zero.withScale(security.getScale()),
				zeroMoney2 = CDecimalBD.ZERO_RUB2,
				zeroMoney5 = CDecimalBD.ZERO_RUB5;
		CDecimal volume;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeVolume(zero)
			.setInitialCurrentPrice(position.getCDecimal(PositionField.CURRENT_PRICE, zeroPrice))
			.setInitialOpenPrice(position.getCDecimal(PositionField.OPEN_PRICE, zeroPrice))
			.setInitialProfitAndLoss(position.getCDecimal(PositionField.PROFIT_AND_LOSS, zeroMoney2))
			.setInitialUsedMargin(position.getCDecimal(PositionField.USED_MARGIN, zeroMoney2))
			.setInitialVarMargin(position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5))
			.setInitialVarMarginClose(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5))
			.setInitialVarMarginInter(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5))
			.setInitialVolume(volume = position.getCDecimal(PositionField.CURRENT_VOLUME, zero));
		CDecimal curPr = security.getSettlementPrice().multiply(volume);
		CDecimal cBal = getVarMargin(security, curPr, update.getInitialOpenPrice())
				.add(update.getInitialVarMarginClose())
				.add(update.getInitialVarMarginInter())
				.withScale(2);
		update.setChangeBalance(cBal);
		update.setFinalCurrentPrice(curPr)
			.setFinalOpenPrice(curPr)
			.setFinalVarMarginInter(zeroMoney5)
			.setFinalVarMargin(zeroMoney5)
			.setFinalVarMarginClose(zeroMoney5);
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalProfitAndLoss(getProfitAndLoss(update));
		return update;
	}

}

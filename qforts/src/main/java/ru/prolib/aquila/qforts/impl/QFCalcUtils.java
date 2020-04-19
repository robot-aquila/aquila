package ru.prolib.aquila.qforts.impl;

import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import java.math.RoundingMode;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.PositionField;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class QFCalcUtils {
	private static final int MONEY_SCALE = 5;
	
	private CDecimal getCurrentPrice(Security security) {
		CDecimal x;
		if ( (x = security.getLastPrice()) != null
		  || (x = security.getSettlementPrice()) != null ) // This MAY not work cuz settlement price isn't mandatory
		{
			return x;
		}
		throw new IllegalStateException("No more candidates to determine current price");
	}
	
	private CDecimal getCurrentPrice(Security security, CDecimal volume) {
		return getCurrentPrice(security).multiply(volume);
	}
	
	private CDecimal getUsedMargin(Security security, CDecimal volume) {
		try {
			if ( volume == null || volume.compareTo(ZERO) == 0 ) {
				return ZERO_RUB5;
			}
			return security.getInitialMargin().multiply(volume).abs();
		} catch ( NullPointerException e ) {
			throw new IllegalStateException(new StringBuilder()
					.append("Used margin calculation failed. Info:")
					.append(" init.margin=").append(security.getInitialMargin())
					.append(" volume=").append(volume)
					.toString(), e);
		}
	}
	
	private CDecimal priceToMoney(CDecimal points, CDecimal tick_size, CDecimal tick_value) {
		return tick_value.multiply(points.divide(tick_size).withScale(0, RoundingMode.UNNECESSARY))
				.withScale(MONEY_SCALE);
	}
	
	private CDecimal getVarMargin(Account account, Symbol symbol, CDecimal curr_price,
				CDecimal open_price, CDecimal tick_size, CDecimal tick_value)
	{
		try {
			return priceToMoney(curr_price.subtract(open_price), tick_size, tick_value);
		} catch ( Exception e ) {
			throw new IllegalStateException(new StringBuilder()
					.append("Var.margin calculation failed. Info:")
					.append(" account=").append(account)
					.append(" symbol=").append(symbol)
					.append(" cur.pr=").append(curr_price)
					.append(" opn.pr=").append(open_price)
					.append(" tick.sz=").append(tick_size)
					.append(" tick.val=").append(tick_value)
					.toString(), e);
		}
	}
	
	private CDecimal getVarMargin(QFPositionChangeUpdate update, CDecimal tick_size, CDecimal tick_value) {
		return getVarMargin(update.getAccount(), update.getSymbol(),
				update.getFinalCurrentPrice(), update.getFinalOpenPrice(), tick_size, tick_value);
	}
	
	private CDecimal getProfitAndLoss(CDecimal fVarMgn, CDecimal fVarMgnC, CDecimal fVarMgnI) {
		return fVarMgn.add(fVarMgnC).add(fVarMgnI).withScale(MONEY_SCALE);
	}
	
	private CDecimal getProfitAndLoss(QFPositionChangeUpdate update) {
		return getProfitAndLoss(update.getFinalVarMargin(),
				update.getFinalVarMarginClose(),
				update.getFinalVarMarginInter());
	}
	
	public CDecimal priceToMoney(Order order, CDecimal exec_price, CDecimal exec_qty) {
		CDecimal tick_size = order.getSecurity().getTickSize(), tick_value = order.getSecurity().getTickValue();
		try {
			return priceToMoney(exec_price.multiply(exec_qty), tick_size, tick_value);
		} catch ( Exception e ) {
			throw new IllegalStateException(new StringBuilder()
					.append("Error converting price to money. Info:")
					.append(" orderID=").append(order.getID())
					.append(" account=").append(order.getAccount())
					.append(" symbol=").append(order.getSymbol())
					.append(" exec.pr=").append(exec_price)
					.append(" exec.qty=").append(exec_qty)
					.append(" tick.sz=").append(tick_size)
					.append(" tick.val=").append(tick_value)
					.toString(), e);
		}
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
			zeroMoney5 = CDecimalBD.ZERO_RUB5;
		CDecimal
			iCurVol = position.getCurrentVolume(),
			fCurVol = zero,
			iOpnPr = position.getOpenPrice(),
			fOpnPr = zeroPrice,
			iCurPr = position.getCurrentPrice(),
			fCurPr = zeroPrice,
			iPL = position.getProfitAndLoss(),
			fPL = zeroMoney5,
			iUsMgn = position.getUsedMargin(),
			fUsMgn = zeroMoney5,
			iVarMgn = position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5),
			fVarMgn = zeroMoney5,
			iVarMgnC = position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5),
			fVarMgnC = iVarMgnC,
			iVarMgnI = position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5),
			fVarMgnI = iVarMgnI,
			iTickVal = position.getCDecimal(QFPositionField.QF_TICK_VALUE),
			fTickVal = security.getTickValue(),
			tick_size = security.getTickSize(),
			tick_value = null; // to be determined!
		if ( iCurVol == null ) iCurVol = zero;
		if ( iOpnPr == null ) iOpnPr = zeroPrice;
		if ( iCurPr == null ) iCurPr = zeroPrice;
		if ( iPL == null ) iPL = zeroMoney5;
		if ( iUsMgn == null ) iUsMgn = zeroMoney5;		
		if ( iCurVol.compareTo(zero) == 0 ) {
			// open long/short position
			tick_value = fTickVal; // use the most recent
			fCurVol = volume;
			fOpnPr = price.multiply(volume);
			
		} else if ( (iCurVol.compareTo(zero) > 0 && volume.compareTo(zero) < 0)
				 || (iCurVol.compareTo(zero) < 0 && volume.compareTo(zero) > 0) )
		{
			tick_value = iTickVal; // use the one was determined at the time of position opening
			if ( iCurVol.compareTo(volume.negate()) == 0 ) {
				// close long/short position
				fTickVal = null;
				fVarMgnC = iVarMgnC.add(priceToMoney(price.multiply(iCurVol).subtract(iOpnPr), tick_size, tick_value));
				fCurVol = zero;
				fOpnPr = zeroPrice;
				
			} else if ( iCurVol.abs().compareTo(volume.abs()) < 0 ) {
				// swap long/short position
				fVarMgnC = iVarMgnC.add(priceToMoney(price.multiply(iCurVol).subtract(iOpnPr), tick_size, tick_value));
				CDecimal V_r = iCurVol.add(volume);
				fCurVol = V_r;
				fOpnPr = price.multiply(V_r);
				
			} else {
				// decrease long/short position
				CDecimal OP_avg = iOpnPr.divide(iCurVol).divide(tick_size).withScale(0).multiply(tick_size);
				fCurVol = iCurVol.add(volume);
				fOpnPr = iOpnPr.add(OP_avg.multiply(volume));
				fVarMgnC = iVarMgnC.add(priceToMoney(price.multiply(volume.negate())
					.subtract(OP_avg.multiply(volume.negate())), tick_size, tick_value));
				
			}
		} else {
			// increase long/short position
			tick_value = iTickVal; // use the one was determined at the time of position opening
			fCurVol = iCurVol.add(volume);
			fOpnPr = iOpnPr.add(price.multiply(volume));
			
		}
		
		fCurPr = getCurrentPrice(security, fCurVol);
		fUsMgn = getUsedMargin(security, fCurVol);
		fVarMgn = getVarMargin(position.getAccount(), position.getSymbol(), fCurPr, fOpnPr, tick_size, tick_value);
		fPL = getProfitAndLoss(fVarMgn, fVarMgnC, fVarMgnI);
		return new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(zeroMoney5)
			.setInitialCurrentPrice(iCurPr)
			.setInitialOpenPrice(iOpnPr)
			.setInitialProfitAndLoss(iPL)
			.setInitialUsedMargin(iUsMgn)
			.setInitialVarMargin(iVarMgn)
			.setInitialVarMarginClose(iVarMgnC)
			.setInitialVarMarginInter(iVarMgnI)
			.setInitialVolume(iCurVol)
			.setInitialTickValue(iTickVal)
			.setFinalCurrentPrice(fCurPr)
			.setFinalOpenPrice(fOpnPr)
			.setFinalProfitAndLoss(fPL)
			.setFinalUsedMargin(fUsMgn)
			.setFinalVarMargin(fVarMgn)
			.setFinalVarMarginClose(fVarMgnC)
			.setFinalVarMarginInter(fVarMgnI)
			.setFinalVolume(fCurVol)
			.setFinalTickValue(fTickVal);
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
				zeroMoney5 = CDecimalBD.ZERO_RUB5;
		CDecimal volume, tick_size = security.getTickSize(), tick_val;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(zeroMoney5)
			.setInitialCurrentPrice(position.getCDecimal(PositionField.CURRENT_PRICE, zeroPrice))
			.setInitialOpenPrice(position.getCDecimal(PositionField.OPEN_PRICE, zeroPrice))
			.setInitialProfitAndLoss(position.getCDecimal(PositionField.PROFIT_AND_LOSS, zeroMoney5))
			.setInitialUsedMargin(position.getCDecimal(PositionField.USED_MARGIN, zeroMoney5))
			.setInitialVarMargin(position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5))
			.setInitialVarMarginClose(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5))
			.setInitialVarMarginInter(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5))
			.setInitialVolume(volume = position.getCDecimal(PositionField.CURRENT_VOLUME, zero))
			.setInitialTickValue(tick_val = position.getCDecimal(QFPositionField.QF_TICK_VALUE));
		update.setFinalOpenPrice(update.getInitialOpenPrice())
			.setFinalVarMargin(zeroMoney5)
			.setFinalVarMarginClose(update.getInitialVarMarginClose())
			.setFinalVarMarginInter(update.getInitialVarMarginInter())
			.setFinalVolume(volume)
			.setFinalTickValue(tick_val);
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalCurrentPrice(currentPrice.multiply(volume));
		if ( volume.equals(zero) == false ) {
			update.setFinalVarMargin(getVarMargin(update, tick_size, tick_val));
		}
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
		CDecimal volume = position.getCurrentVolume(), last_price = null;
		if ( volume != null && volume.compareTo(CDecimalBD.ZERO) != 0 ) {
			last_price = getCurrentPrice(position.getSecurity());
		} else {
			last_price = position.getSecurity().getTickSize();
			if ( last_price == null ) {
				throw new NullPointerException("Tick size was not defined: " + position.getSymbol());
			}
			last_price = last_price.withZero();
		}
		return refreshByCurrentState(position, last_price);
	}
	
	/**
	 * Calculate position change at intermediate clearing.
	 * <p>
	 * Note! This works for futures & options only.
	 * <p>
	 * @param position - position instance
	 * @return update instance
	 */
	public QFPositionChangeUpdate midClearing(Position position) {
		Security security = position.getSecurity();
		CDecimal zero = CDecimalBD.ZERO,
				zeroPrice = zero.withScale(security.getScale()),
				zeroMoney5 = CDecimalBD.ZERO_RUB5,
				tick_val, tick_size = security.getTickSize();
		CDecimal volume, varMgn = zeroMoney5;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeBalance(zeroMoney5)
			.setChangeVolume(zero)
			.setInitialCurrentPrice(position.getCDecimal(PositionField.CURRENT_PRICE, zeroPrice))
			.setInitialOpenPrice(position.getCDecimal(PositionField.OPEN_PRICE, zeroPrice))
			.setInitialProfitAndLoss(position.getCDecimal(PositionField.PROFIT_AND_LOSS, zeroMoney5))
			.setInitialUsedMargin(position.getCDecimal(PositionField.USED_MARGIN, zeroMoney5))
			.setInitialVarMargin(position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5))
			.setInitialVarMarginClose(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5))
			.setInitialVarMarginInter(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5))
			.setInitialVolume(volume = position.getCDecimal(PositionField.CURRENT_VOLUME, zero))
			.setInitialTickValue(tick_val = position.getCDecimal(QFPositionField.QF_TICK_VALUE));
		boolean has_position = volume.equals(zero) == false;
		CDecimal curPr = security.getSettlementPrice().multiply(volume);
		if ( has_position ) {
			varMgn = getVarMargin(position.getAccount(), position.getSymbol(), curPr, update.getInitialOpenPrice(),
					tick_size, tick_val);
		}
		CDecimal varMgnInter = varMgn.add(update.getInitialVarMarginClose()).add(update.getInitialVarMarginInter());
		update.setFinalCurrentPrice(curPr)
			.setFinalOpenPrice(curPr)
			.setFinalVarMarginInter(varMgnInter)
			.setFinalVarMargin(zeroMoney5)
			.setFinalVarMarginClose(zeroMoney5);
		if ( has_position ) {
			update.setFinalTickValue(security.getTickValue()); // set only if non-zero position
		}
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalProfitAndLoss(getProfitAndLoss(update));
		return update;
	}
	
	public QFPositionChangeUpdate clearing(Position position) {
		Security security = position.getSecurity();
		CDecimal zero = CDecimalBD.ZERO,
				zeroPrice = zero.withScale(security.getScale()),
				zeroMoney5 = CDecimalBD.ZERO_RUB5,
				tick_val, tick_size = security.getTickSize(),
				var_mgn = zeroMoney5;
		CDecimal volume;
		QFPositionChangeUpdate update =
			new QFPositionChangeUpdate(position.getAccount(), position.getSymbol())
			.setChangeVolume(zero)
			.setInitialCurrentPrice(position.getCDecimal(PositionField.CURRENT_PRICE, zeroPrice))
			.setInitialOpenPrice(position.getCDecimal(PositionField.OPEN_PRICE, zeroPrice))
			.setInitialProfitAndLoss(position.getCDecimal(PositionField.PROFIT_AND_LOSS, zeroMoney5))
			.setInitialUsedMargin(position.getCDecimal(PositionField.USED_MARGIN, zeroMoney5))
			.setInitialVarMargin(position.getCDecimal(QFPositionField.QF_VAR_MARGIN, zeroMoney5))
			.setInitialVarMarginClose(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_CLOSE, zeroMoney5))
			.setInitialVarMarginInter(position.getCDecimal(QFPositionField.QF_VAR_MARGIN_INTER, zeroMoney5))
			.setInitialVolume(volume = position.getCDecimal(PositionField.CURRENT_VOLUME, zero))
			.setInitialTickValue(tick_val = position.getCDecimal(QFPositionField.QF_TICK_VALUE));
		boolean has_position = volume.equals(zero) == false;
		CDecimal curPr = security.getSettlementPrice().multiply(volume);
		if ( has_position ) {
			var_mgn = getVarMargin(position.getAccount(), position.getSymbol(), curPr, update.getInitialOpenPrice(),
					tick_size, tick_val);
		}
		CDecimal cBal = var_mgn.add(update.getInitialVarMarginClose())
			.add(update.getInitialVarMarginInter())
			.withScale(MONEY_SCALE);
		update.setChangeBalance(cBal);
		update.setFinalCurrentPrice(curPr)
			.setFinalOpenPrice(curPr)
			.setFinalVarMarginInter(zeroMoney5)
			.setFinalVarMargin(zeroMoney5)
			.setFinalVarMarginClose(zeroMoney5);
		if ( has_position ) {
			update.setFinalTickValue(security.getTickValue());
		}
		update.setFinalUsedMargin(getUsedMargin(security, volume));
		update.setFinalProfitAndLoss(getProfitAndLoss(update));
		return update;
	}

}

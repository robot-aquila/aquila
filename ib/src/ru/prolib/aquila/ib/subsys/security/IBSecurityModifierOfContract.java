package ru.prolib.aquila.ib.subsys.security;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.ContractDetails;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;

/**
 * Модификатор инструмента на основании деталей контракта.
 * <p>
 * 2012-11-22<br>
 * $Id: IBSecurityModifierOfContract.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBSecurityModifierOfContract implements S<EditableSecurity> {
	private static final Logger logger;
	private static final int DIGITS = 8;
	private static final DecimalFormat format;
	private static final char[] nzero = {'1','2','3','4','5','6','7','8','9'};
	
	static {
		logger = LoggerFactory.getLogger(IBSecurityModifierOfContract.class);
		format = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
		format.setMaximumFractionDigits(DIGITS);
		format.setGroupingUsed(false);
		format.applyPattern("0." + StringUtils.repeat('0', DIGITS));
	}
	
	public IBSecurityModifierOfContract() {
		super();
	}

	@Override
	public void set(EditableSecurity object, Object value) {
		if ( value instanceof ContractDetails ) {
			ContractDetails details = (ContractDetails) value;
			object.setMinStepPrice(details.m_minTick);
			object.setMinStepSize(details.m_minTick);
			object.setLotSize(1);
			object.setPrecision(detectPrecision(details.m_minTick));
			object.setDisplayName(details.m_summary.m_localSymbol);
		}
	}
	
	/**
	 * Определить точность вещественного значения в знаках после точки.
	 * <p>
	 * @param forValue исходное значение
	 * @return точность
	 */
	private Integer detectPrecision(double forValue) {
		String revValue = StringUtils.reverse(format.format(forValue));
		int index = StringUtils.indexOfAny(revValue, nzero);
		if ( index < 0 ) {
			// Если ни одной ненулевой цифры не было найдено, то возможно 
			// значение слишком мало и лежит за пределами точности 
			// {@link #DIGITS} знаков после точки. Эта ситуация маловероятна,
			// но возможна. Ситуация исключительная, но возможно точность
			// фактически не понадобится. Что бы не заморачиваться раньше
			// времени, просто вернем null и информируем в журнал.
			Object a[] = { forValue, DIGITS };
			logger.warn("Couldn't obtain precision for {}, max digits={}", a);
			return null;
		} else if ( index >= DIGITS ) {
			// Точка в развернутой строке находится на позиции с индексом DIGIT.
			// Если индекс найденной цифры больше чем позиция точки, значит
			// эта цифра относится к целой части. При этом дробная часть
			// полностью состоит из нулей. В таком случае точность значения
			// считаем равной нулю.
			return 0;
		} else {
			return DIGITS - index;			
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBSecurityModifierOfContract.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121123, 133255).toHashCode();
	}

}

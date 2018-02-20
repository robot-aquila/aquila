package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

public class ValueAxisLabelGenerator {
	private static final ValueAxisLabelGenerator instance;
	
	static {
		instance = new ValueAxisLabelGenerator();
	}
	
	public static ValueAxisLabelGenerator getInstance() {
		return instance;
	}

	public List<CDecimal> getLabelValues(ValueAxisDisplayMapper mapper, CDecimal tickSize, int labelSize) {
		CDecimal range = mapper.getMaxValue().subtract(mapper.getMinValue());
		if ( range.compareTo(tickSize) < 0 ) {
			throw new IllegalArgumentException("Value range expected to be greater or equals than tick size");
		}
		int scale = tickSize.getScale();
		int numberOfSteps = mapper.getPlotSize() / labelSize;
		CDecimal stepSize = null;
		while ( (stepSize == null || stepSize.compareTo(tickSize) < 0) && numberOfSteps > 0 ) {
			stepSize = range.divideExact(CDecimalBD.of((long)numberOfSteps), 8 + scale, RoundingMode.HALF_UP);
			CDecimal multiplier = getBestMultiplier(stepSize);
			CDecimal c = stepSize.divide(multiplier);
			 if ( c.compareTo(CDecimalBD.of(1L)) <= 0 ) {
             	c = CDecimalBD.of(1L);
             } else if ( c.compareTo(CDecimalBD.of(2L)) <= 0 ) {
             	c = CDecimalBD.of(2L);
             } else if ( c.compareTo(CDecimalBD.of(5L)) <= 0 ) {
             	c = CDecimalBD.of(5L);
             } else {
             	c = CDecimalBD.of(10L);
             }
			 stepSize = multiplier.multiply(c).withScale(scale);
			 if ( stepSize.compareTo(tickSize) < 0 ) {
				 numberOfSteps --;
			 }
		}
		List<CDecimal> result = new ArrayList<>();
		if ( stepSize != null ) {
			CDecimal currValue = mapper.getMinValue()
					.divideExact(stepSize, 0, RoundingMode.CEILING)
					.multiply(stepSize)
					.withScale(scale);
			while ( currValue.compareTo(mapper.getMaxValue()) <= 0 ) {
				result.add(currValue);
				currValue = currValue.add(stepSize);
			}
		}
		return result;
	}
	
	private CDecimal getBestMultiplier(CDecimal x) {
		// x MUST be positive
		if ( x.compareTo(CDecimalBD.ZERO) <= 0 ) {
			throw new IllegalArgumentException("Argument must be greater than zero but: " + x);
		}
		BigDecimal f = x.toBigDecimal().remainder(BigDecimal.ONE);
		BigDecimal i = x.toBigDecimal().subtract(f);
		if ( i.compareTo(BigDecimal.ZERO) > 0 ) {
			String ps = i.toPlainString();
			int pp = ps.indexOf('.');
			if ( pp < 0 ) {
				pp = ps.length();
			}
			BigDecimal r = BigDecimal.TEN.pow(pp - 1);
			return new CDecimalBD(r);
		} else {
			String ps = f.toPlainString();
			int pp = StringUtils.indexOfAny(ps, "123456789");
			if ( pp < 0 ) {
				throw new IllegalStateException("Unable to find non-zero digits in fractional part: " + f);
			}
			BigDecimal r = BigDecimal.ONE.divide(BigDecimal.TEN.pow(pp - 1));
			return new CDecimalBD(r);
		}
	}
	
}

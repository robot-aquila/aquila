package ru.prolib.aquila.stat.counter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang3.StringUtils;

public class DoubleFormat implements CounterFormat<Double> {
	private final DecimalFormat format;
	
	public DoubleFormat() {
		this(2);
	}
	
	public DoubleFormat(int scale) {
		super();
		DecimalFormatSymbols syms = new DecimalFormatSymbols();
		syms.setDecimalSeparator('.');
		if ( scale > 0 ) {
			format = new DecimalFormat("0." +
					StringUtils.repeat("0", scale), syms);
		} else {
			format = new DecimalFormat("0", syms);
		}
	}

	@Override
	public String format(Counter<Double> counter) {
		Double value = counter.getValue();
		if ( value == null ) {
			return "null";
		} else {
			return format.format(value);
		}
	}

}

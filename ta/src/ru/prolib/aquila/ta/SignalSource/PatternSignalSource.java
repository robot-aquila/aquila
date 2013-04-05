package ru.prolib.aquila.ta.SignalSource;

import ru.prolib.aquila.ta.*;

/**
 * Источник сигнала на основе шаблона.
 */
public class PatternSignalSource implements ISignalSource {
	private final int type;
	private final IPatternMatcher matcher;
	private final IPriceCalculator calculator;
	private final String comment;
	
	public PatternSignalSource(int type, IPatternMatcher matcher,
			IPriceCalculator calculator, String comment)
	{
		this.type = type;
		this.matcher = matcher;
		this.calculator = calculator;
		this.comment = comment;
	}
	
	public int getType() {
		return type;
	}
	
	public IPatternMatcher getPatternMatcher() {
		return matcher;
	}
	
	public IPriceCalculator getPriceCalculator() {
		return calculator;
	}
	
	public String getComment() {
		return comment;
	}

	@Override
	public void analyze(ISignalTranslator translator) throws ValueException {
		if ( matcher.matches() ) {
			if ( type == Signal.BUY ) {
				translator.signalToBuy(calculator.getPrice(), comment);
			} else {
				translator.signalToSell(calculator.getPrice(), comment);
			}
		}
	}
	
}
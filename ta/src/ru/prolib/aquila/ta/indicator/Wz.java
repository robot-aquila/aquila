package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Индикатор зон Била Вильямса из его Теории Хаоса.
 * Индикатор анализирует динамику изменения двух осцилляторов (подразумеваются
 * Awesome Oscillator и Acceleration Oscillator) и определяет в какой из трех
 * зон - в красной, зеленой или серой - находится рынок. Красная зона
 * соответствует динамике снижения и AO и AC. Зеленая зона соответствует
 * динамике роста и AO и AC. Серая зона соответствует периоду, когда динамика
 * AO не совпадает с динамикой AC (AO растет, а AC падает или наоборот). 
 */
public class Wz extends BaseIndicator2S<Integer, Double> {
	public static final Integer RED   = -1;
	public static final Integer GRAY  =  0;
	public static final Integer GREEN =  1;

	/**
	 * Конструктор.
	 * @param ao подразумевается AwesomeOscillator
	 * @param ac подразумевается Acceleration Oscillator
	 */
	public Wz(Value<Double> ao, Value<Double> ac) {
		super(ao, ac);
	}

	@Override
	public Integer calculate() throws ValueException {
		Integer z1 = getOscillatorZone(src1);
		Integer z2 = getOscillatorZone(src2);
		if ( z1 == null || z2 == null ) return null;
		return z1 == z2 ? z1 : GRAY;
	}
	
	final private Integer getOscillatorZone(Value<Double> o)
		throws ValueException
	{
		if ( o.getLength() >= 2 ) {
			Double v1 = o.get();
			Double v0 = o.get(-1);
			if ( v1 == null || v0 == null ) return null;
			return v1 > v0 ? GREEN : RED;  
		}
		return GRAY;
	}

}

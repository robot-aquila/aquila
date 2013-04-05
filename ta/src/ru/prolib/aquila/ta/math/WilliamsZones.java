package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.indicator.Wz;

/**
 * Индикатор зон Била Вильямса из его Теории Хаоса.
 * Индикатор анализирует динамику изменения двух осцилляторов (подразумеваются
 * Awesome Oscillator и Acceleration Oscillator) и определяет в какой из трех
 * зон - в красной, зеленой или серой - находится рынок. Красная зона
 * соответствует динамике снижения и AO и AC. Зеленая зона соответствует
 * динамике роста и AO и AC. Серая зона соответствует периоду, когда динамика
 * AO не совпадает с динамикой AC (AO растет, а AC падает или наоборот). 
 */
@Deprecated
public class WilliamsZones extends ValueImpl<Integer> {
	private final Wz wz;
	
	public WilliamsZones(Value<Double> ao, Value<Double> ac) {
		this(ao, ac, ValueImpl.DEFAULT_ID);
	}
	
	public WilliamsZones(Value<Double> ao, Value<Double> ac, String id) {
		super(id);
		wz = new Wz(ao, ac);
	}
	
	public Value<Double> getOscillator1() {
		return wz.getFirstSource();
	}
	
	public Value<Double> getOscillator2() {
		return wz.getSecondSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(wz.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}

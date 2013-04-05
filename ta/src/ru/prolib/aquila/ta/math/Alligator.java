package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;

/**
 * Дескриптор аллигатора.
 * Содержит объекты-значения, входящие в состав данного индикатора ТА.
 */
@Deprecated
public class Alligator {
	public final Value<Double> lips;
	public final Value<Double> teeth;
	public final Value<Double> jaw;
	
	public Alligator(Value<Double> lips, Value<Double> teeth,
			Value<Double> jaw)
	{
		this.lips  = lips;
		this.teeth = teeth;
		this.jaw   = jaw;
	}

}

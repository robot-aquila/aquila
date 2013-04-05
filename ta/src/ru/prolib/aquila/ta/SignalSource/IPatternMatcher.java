package ru.prolib.aquila.ta.SignalSource;

import ru.prolib.aquila.ta.ValueException;

public interface IPatternMatcher {
	
	public boolean matches() throws ValueException;

}

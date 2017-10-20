package ru.prolib.aquila.core;


/**
 * Реализация-заглушка пускового механизма.
 * <p>
 * 2012-08-18<br>
 * $Id: StarterStub.java 322 2012-11-24 12:59:46Z whirlwind $
 */
public class StarterStub implements Starter {

	/**
	 * Создать заглушку.
	 */
	public StarterStub() {
		super();
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == StarterStub.class;
	}

}

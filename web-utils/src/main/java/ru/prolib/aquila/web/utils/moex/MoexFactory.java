package ru.prolib.aquila.web.utils.moex;

public interface MoexFactory {
	
	/**
	 * Create instance of MOEX service facade.
	 * <p>
	 * @return instance of MOEX facade
	 */
	Moex createInstance();

}

package ru.prolib.aquila.core.utils;

/**
 * Правила обработки зависимости.
 * <p>
 * 2013-02-16<br>
 * $Id$
 */
public class DependencyRule {
	/**
	 * Сохранить данные и отложить выполнение обработки до тех пор, пока
	 * не будут удовлетворены все зависимости.
	 */
	public static final DependencyRule WAIT = new DependencyRule("WAIT");
	/**
	 * Отбрасывать данные и не выполнять обработку до тех пор, пока зависимости
	 * не будут удовлетворены.
	 */
	public static final DependencyRule DROP = new DependencyRule("DROP");
	
	private final String code;
	
	private DependencyRule(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}

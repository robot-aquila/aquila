package ru.prolib.aquila.quik;

/**
 * Дополнительные проверки.
 */
public class Check {
	public static final String NOTWIN = "Not a Windows OS";
	
	/**
	 * Проверить тип ОС Windows.
	 * <p>
	 * @return true - ОС Windows, false - иначе
	 */
	public static boolean isWin() {
		return System.getProperty("os.name").toLowerCase().startsWith("win");
	}

}

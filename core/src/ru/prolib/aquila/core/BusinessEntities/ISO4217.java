package ru.prolib.aquila.core.BusinessEntities;

import java.util.Currency;

/**
 * Константы-ярлыки наиболее часто-используемых валют.
 */
public class ISO4217 {
	public static final Currency
		USD = Currency.getInstance("USD"),
		EUR = Currency.getInstance("EUR"),
		RUB = Currency.getInstance("RUB"),
		RUR = Currency.getInstance("RUR"), // PS. Рубль до деноминации 1998
		GBP = Currency.getInstance("GBP");
}

package ru.prolib.aquila.stat.counter;

import java.io.PrintStream;

/**
 * Интерфейс принтера счетчиков.
 * 
 * Принтер счетчиков определяет порядок и формат вывода счетчиков.
 * 
 * 2012-02-05
 * $Id: CounterPrinter.java 197 2012-02-05 20:21:19Z whirlwind $
 */
public interface CounterPrinter {
	
	public void print(CounterSet counters, PrintStream stream);

	void printHeaders(PrintStream stream);

}

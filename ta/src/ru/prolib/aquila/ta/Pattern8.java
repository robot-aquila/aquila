package ru.prolib.aquila.ta;

import org.apache.commons.lang3.text.StrBuilder;

import ru.prolib.aquila.core.data.Candle;

/**
 * Шаблон последовательности 8 баров.
 *
 * Используется для представления последовательности баров в виде матрицы 8x8.
 * Каждый элемент матрицы представляет собой булево значение: true - цена была
 * в диапазоне, представленном данной ячейкой, false - не была. Строки матрицы
 * представляют собой ценовые уровни. Каждая строка определяет уровни в 1/8 от
 * максимального хода цены в указаном промежутке времени (совокупность всех
 * 8 баров, то есть для минутных баров - ход цены за 8 минут).
 * Колонки матрицы определяют время относительно начала последовательности.
 * 
 * Матрица расшифровывается следующим образом
 * 
 *         0 1 2 3 4 5 6 7 time
 * level 7 X X X X X X X X			max price
 *       6 X X X X X X X X
 *       5 X X X X X X X X
 *       4 X X X X X X X X
 *       3 X X X X X X X X
 *       2 X X X X X X X X
 *       1 X X X X X X X X
 *       0 X X X X X X X X			min price
 *       
 * Матрица упаковывается в значение типа long, где каждый бит соответствует
 * определенной ячейке матрицы. Координаты ячейки описываются как (level, time).
 * Значение ячейки (7,0) соответствует 63 биту, (7,7) биту номер 54, (0,0) биту
 * номер 7, (0,7) нулевому биту и т.п.
 */
public class Pattern8 {
	private long matrix;
	private double max,min,height,levelHeight;
	
	/**
	 * Конструктор.
	 * 
	 * Конструирует объект на основе массива из 8 баров. Индекс бара в массиве
	 * определяет целевую колонку матрицы.
	 * 
	 * @param bars массив из не более чем 8 баров
	 * @throws IllegalArgumentException массив баров не содержит 8 элементов
	 */
	public Pattern8(Candle[] bars) {
		super();
		if ( bars.length > 8 ) {
			throw new IllegalArgumentException("Too much bars");
		}
		if ( bars.length < 2 ) {
			throw new IllegalArgumentException("Too few bars");
		}
		max = bars[0].getHigh();
		min = bars[0].getLow();
		for ( int i = 1; i < bars.length; i ++ ) {
			if ( bars[i].getHigh() > max ) {
				max = bars[i].getHigh();
			}
			if ( bars[i].getLow() < min ) {
				min = bars[i].getLow();
			}
		}
		height = max - min;
		levelHeight = height / 8;
		matrix = 0;
		for ( int level = 7; level >= 0; level -- ) {
			for ( int time = 0; time < bars.length; time ++ ) {
				if ( checkLevel(bars[time], level) ) {
					matrix = setMatrixBitAt(matrix, level, time);
				}
			}
		}
	}
	
	public double getLevelMax(int level) {
		return min + (levelHeight * (level + 1));
	}
	
	public double getLevelMin(int level) {
		return min + (levelHeight * level);
	}
	
	/**
	 * Проверяет, входит ли цена бара в диапазон цен указанного уровня.
	 * 
	 * @param b бар
	 * @param level номер уровня 0 - нижний, 7 - верхний
	 * @return true, если цена бара в диапазоне, иначе false
	 */
	private boolean checkLevel(Candle b, int level) {
		double levelMin = getLevelMin(level);
		double levelMax = getLevelMax(level);
		if ( b.getHigh() < levelMin ) {
			return false;
		}
		if ( b.getLow() > levelMax ) {
			return false;
		}
		return true;
	}
	
	/**
	 * Получить унифицирование представление шаблона.
	 * 
	 * @return
	 */
	public long getMatrix() {
		return matrix;
	}
	
	/**
	 * Получить максимум цены в шаблоне.
	 * 
	 * @return
	 */
	public double getMax() {
		return max;
	}
	
	/**
	 * Получить минимум цены в шаблоне.
	 * 
	 * @return
	 */
	public double getMin() {
		return min;
	}
	
	/**
	 * Получить высоту шаблона в единицах цены.
	 * 
	 * Фактически Max - Min.
	 * 
	 * @return
	 */
	public double getHeight() {
		return height;
	}
	
	/**
	 * Получить значение одного уровеня цены.
	 * 
	 * Фактически Height / 8
	 * 
	 * @return
	 */
	public double getLevelHeight() {
		return levelHeight;
	}
	
	/**
	 * Сравнить матрицы шаблонов.
	 * 
	 * @return Возвращает true, если объект является шаблоном и матрица
	 * указанного шаблона идентична матрицы данного экземпляра. 
	 */
	@Override
	public boolean equals(Object o) {
		throw new RuntimeException("Not implemented");
	}
	
	/**
	 * Получить значение бита по номеру.
	 *  
	 * @param bits значение
	 * @param bitNumber номер бита 0 - младший (справа), 63 - старший (слева)
	 * @return true - включен, false - выключен
	 */
	static public boolean getBitAt(long bits, int bitNumber) {
		long mask = 1;
		for ( int i = 0; i < bitNumber; i ++ ) {
			mask <<= 1;
		}
		return (mask & bits) == mask ? true : false;
	}
	
	/**
	 * Установить значение бита в 1 по номеру.
	 *  
	 * @param bits исходное значение
	 * @param bitNumber номер бита 0 - младший (справа), 63 - старший (слева)
	 * @return результат изменения
	 */
	static public long setBitAt(long bits, int bitNumber) {
		long mask = 1;
		for ( int i = 0; i < bitNumber; i ++ ) {
			mask <<= 1;
		}
		return mask | bits;
	}
	
	/**
	 * Получить значения бита матрицы шаблона по координатам.
	 *  
	 * @param bits матрица шаблона
	 * @param level индекс ценового уровня 
	 * @param time индекс бара
	 * @return true - бит включен, false - выключен
	 */
	static public boolean getMatrixBitAt(long bits, int level, int time) {
		int number = level * 8 + (7 - time);
		return getBitAt(bits, number);
	}
	
	/**
	 * Установить значение бита матрицы шаблона по координатам.
	 * 
	 * @param bits исходная матрица шаблона
	 * @param level индекс ценового уровня
	 * @param time индекс бара
	 * @return результат изменения
	 */
	static public long setMatrixBitAt(long bits, int level, int time) {
		int number = level * 8 + (7 - time);
		return setBitAt(bits, number);
	}
	
	static public String matrixLevelToString(long bits, int level,
			String on, String of, String separator)
	{
		String chars[] = new String[8];
		for ( int time = 0; time < 8; time ++ ) {
			chars[time] = getMatrixBitAt(bits, level, time) ? on : of;
		}
		return new StrBuilder()
			.appendWithSeparators(chars, separator)
			.toString();
	}

}

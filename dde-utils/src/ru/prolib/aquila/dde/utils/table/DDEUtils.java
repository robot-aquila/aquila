package ru.prolib.aquila.dde.utils.table;

import java.text.ParseException;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.prolib.aquila.dde.DDETable;

/**
 * Функции-утилиты DDE.
 */
public class DDEUtils {
	private static final Pattern pXltRange;
	
	static {
		pXltRange = Pattern.compile("^R(\\d+)C(\\d+):R(\\d+)C(\\d+)");
	}
	
	public DDEUtils() {
		super();
	}

	/**
	 * Разобрать строку информации о регионе таблицы формата XLT.
	 * <p>
	 * @param item строка описывающая регион таблицы (например R1C1:R10C5)
	 * @return дескриптор региона таблицы
	 * @throws ParseException некорректный формат строки
	 */
	public DDETableRange parseXltRange(String item) throws ParseException {
		Matcher m = pXltRange.matcher(item);
		if ( ! m.find() || m.groupCount() != 4 ) {
			throw new ParseException("Couldn't parse string: " + item, 0);
		}
		return new DDETableRange(Integer.parseInt(m.group(1)),
				Integer.parseInt(m.group(2)),
				Integer.parseInt(m.group(3)),
				Integer.parseInt(m.group(4)));
	}
	
	/**
	 * Создать хэш-индекс заголовков на основе первого ряда таблицы.
	 * <p>
	 * Создает ассоциативный массив, построенный на основе ряда таблицы,
	 * которая рассматривается как набор заголовков таблицы. Хэш индекс
	 * позволяет организовать последующий доступ к колонкам по их заголовкам, а
	 * не индексам. Подразумевается, что таблица представлена в полном составе
	 * колонок Ключи результирующего хэша соответствуют именам колонок, а
	 * значения - индексу колонки.
	 * <p>
	 * @param table таблица
	 * @param colOffset смещение первой колонки относительно исходной таблицы.
	 * Используется, если таблица отражает область, смещенную по горизонтали (то
	 * есть, первая колонка этой таблицы не соответствует первой колонки
	 * исходной таблицы).    
	 * @return хэш-индекс
	 */
	public Map<String, Integer> makeHeadersMap(DDETable table, int colOffset) {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		int cols = table.getCols();
		for ( int i = 0; i < cols; i ++ ) {
			map.put(table.getCell(0, i).toString(), i + colOffset);
		}
		return map;
	}
	
	/**
	 * Создать хэш-индекс заголовков на основе первого ряда таблицы.
	 * <p>
	 * Короткий вызов {@link #makeHeadersMap(DDETable, int)} с нулевым смещением
	 * первой колонки.
	 * <p>
	 * @param table таблица
	 * @return хэш-индекс
	 */
	public Map<String, Integer> makeHeadersMap(DDETable table) {
		return makeHeadersMap(table, 0);
	}
	
}

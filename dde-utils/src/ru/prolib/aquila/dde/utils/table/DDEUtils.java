package ru.prolib.aquila.dde.utils.table;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
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
	 * Получить представленный регион таблицы.
	 * <p>
	 * Разбирает строку item таблицы, которая должна представлять дескриптор
	 * региона в формате XLT (например R1C1:R10C5).
	 * <p>
	 * @param table таблица
	 * @return дескриптор региона таблицы
	 * @throws XltItemFormatException некорректный формат строки item
	 */
	public DDETableRange parseXltRange(DDETable table)
			throws XltItemFormatException
	{
		Matcher m = pXltRange.matcher(table.getItem());
		if ( ! m.find() || m.groupCount() != 4 ) {
			throw new XltItemFormatException(table.getTopic(), table.getItem());
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
	@Deprecated
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
	@Deprecated
	public Map<String, Integer> makeHeadersMap(DDETable table) {
		return makeHeadersMap(table, 0);
	}
	
	/**
	 * Создать хэш-индекс заголовков на основе первого ряда таблицы.
	 * <p>
	 * Создает карту заголовков аналогично методу
	 * {@link #makeHeadersMap(DDETable, int)} с тем отличием, то в карту
	 * попадают только требуемые заголовки. Если в таблице отсутствует требуемый
	 * заголовок, то выбрасывает исключение.
	 * <p>
	 * @param table таблица
	 * @param requiredFields список требуемых заголовков
	 * @return хэш-индекс
	 * @throws NotAllRequiredFieldsException не все необходимые заголовки
	 */
	public Map<String, Integer>
			makeHeadersMap(DDETable table, String[] requiredFields)
					throws NotAllRequiredFieldsException
	{
		Map<String, Integer> map = new Hashtable<String, Integer>();
		List<String> required = Arrays.asList(requiredFields);
		int cols = table.getCols();
		for ( int i = 0; i < cols; i ++ ) {
			String hdr = table.getCell(0, i).toString();
			if ( required.contains(hdr) ) {
				map.put(hdr, i);
			}
		}
		for ( String hdr : required ) {
			if ( ! map.containsKey(hdr) ) {
				throw new NotAllRequiredFieldsException(table.getTopic(), hdr);
			}
		}
		return map;
	}
	
}

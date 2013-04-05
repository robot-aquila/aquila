package ru.prolib.aquila.dde.utils.table;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorStub;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.DDETableImpl;

/**
 * Конструктор набора рядов на базе таблицы с формальными заголовками.
 * <p>
 * Данная реализация рассматривает первую строку таблицы как имена
 * соответствующих колонок. Имена колонок проверяются и кешируются каждый раз
 * при создании набора рядов. Таким образом, пользователи создаваемых наборов
 * могут обращаться к значениям соответствующих колонок по их именам.
 * <p>
 * Фактически реализация для работы с таблицами, экспортируемыми из QUIK по DDE
 * с включенной настройкой экспорта "С заголовками столбцов". 
 * <p>
 * Номера первых строки и колонки используется для определения левой
 * верхней координаты таблицы. Если этой координате соответствует R1C1,
 * то эти значения равны 1. Так как входящие таблицы представляют собой
 * область некой таблицы, но адресуются координатами от нуля и выше,
 * для определения координаты соответствующей 0:0 входящей таблицы
 * используется разбор значения item. Номера первой строки и колонки
 * используются для корректировки смещений после разбора item.
 * <p>
 * Валидатор заголовков используется для проверки корректности набора
 * заголовков и вызывается каждый раз при обновлении заголовков (то есть,
 * при получении первой строки таблицы R1). Валидатору передается набор
 * заголовков {@link java.util.Set} с объектами типа
 * {@link java.lang.String}. Если валидатор возвращает false, то вызов
 * метода завершается возвратом пустого набора, а текущий набор заголовков
 * стирается (что бы предотвратить формирования набора рядов по невалидным
 * заголовкам при последующих вызовах метода).
 * <p>
 * 2012-08-10<br>
 * $Id: DDETableRowSetBuilderImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class DDETableRowSetBuilderImpl implements DDETableRowSetBuilder {
	private static final Logger logger;
	private static final Pattern ptrnItem = Pattern.compile("^R(\\d+)C(\\d+):");
	private final int firstCol;
	private final int firstRow;
	private final Validator validator;
	private final Map<String, Integer> header;
	
	static {
		logger = LoggerFactory.getLogger(DDETableRowSetBuilderImpl.class);
	}
	
	/**
	 * Создать обработчик.
	 * <p>
	 * Назначение аргументов см. в описании класса.
	 * <p>
	 * @param firstCol номер первой колонки
	 * @param firstRow номер первой строки
	 * @param hdrValidator валидатор набора заголовков
	 */
	public DDETableRowSetBuilderImpl(int firstCol, int firstRow,
			Validator hdrValidator)
	{
		super();
		if ( firstRow < 0 || firstCol < 0 ) {
			throw new IllegalArgumentException();
		}
		if ( hdrValidator == null ) {
			throw new NullPointerException("Validator cannot be null");
		}
		this.firstCol = firstCol;
		this.firstRow = firstRow;
		validator = hdrValidator;
		header = new HashMap<String, Integer>();
	}
	
	/**
	 * Конструктор по умолчанию.
	 * <p>
	 * В качестве номеров первой строки и колонки используется единица.
	 * В качестве валидатора используется заглушка
	 * {@link ru.prolib.aquila.core.utils.ValidatorStub ValidatorStub},
	 * на каждое обращение отвечающий true.
	 */
	public DDETableRowSetBuilderImpl() {
		this(1, 1, new ValidatorStub(true));
	}
	
	/**
	 * Получить экземпляр валидатора.
	 * <p>
	 * @return валидатор
	 */
	public Validator getValidator() {
		return validator;
	}
	
	/**
	 * Получить номер первой строки.
	 * <p>
	 * @return номер первой строки
	 */
	public int getFirstRow() {
		return firstRow;
	}
	
	/**
	 * Получить номер первой колонки.
	 * <p>
	 * @return номер первой колонки
	 */
	public int getFirstCol() {
		return firstCol;
	}

	/**
	 * Получить текущий набор заголовков.
	 * <p>
	 * @return набор заголовков
	 */
	public synchronized Set<String> getHeaders() {
		return header.keySet();
	}

	@Override
	public synchronized RowSet createRowSet(DDETable table) {
		Matcher m = ptrnItem.matcher(table.getItem());
		if ( ! m.find() || m.groupCount() != 2 ) {
			logger.warn("SKIP: Unknown item format [{}] for topic [{}]",
					new Object[] { table.getItem(), table.getTopic() });
			return new DDETableRowSet(new DDETableImpl(), header);
		}
		int row = Integer.parseInt(m.group(1)) - firstRow;
		int col = Integer.parseInt(m.group(2)) - firstCol;
		int cols = table.getCols();
		if ( row == 0 ) {
			header.clear();
			for ( int x = 0; x < cols; x ++ ) {
				header.put(table.getCell(0, x).toString(), col + x);
			}
			if ( validator.validate(header.keySet()) ) {
				table = new DDETableShift(table, 0, -1);
			} else {
				header.clear();
				return new DDETableRowSet(new DDETableImpl(), header);
			}
		}
		if ( col != 0 ) {
			table = new DDETableShift(table, col, 0);
		}
		return new DDETableRowSet(table, header);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null &&
			 other.getClass() == DDETableRowSetBuilderImpl.class )
		{
			DDETableRowSetBuilderImpl o = (DDETableRowSetBuilderImpl) other;
			return new EqualsBuilder()
				.append(firstCol, o.firstCol)
				.append(firstRow, o.firstRow)
				.append(validator, o.validator)
				.append(header, o.header)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public synchronized int hashCode() {
		return new HashCodeBuilder(20121107, /*0*/63533)
			.append(firstCol)
			.append(firstRow)
			.append(validator)
			.append(header)
			.toHashCode();
	}

}

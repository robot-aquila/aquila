package ru.prolib.aquila.probe.timeline;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

/**
 * Команда эмулятора.
 * <p>
 * Команды данного типа используются для управления процессом эмуляции.
 */
public class TLCommand {
	/**
	 * Требование немедленно завершить работу. Данная команда завершает работу
	 * эмулятора независимо от текущего положения на временной шкале.
	 */
	public static final TLCommand FINISH = new TLCommand();
	/**
	 * Приостановить работу. Если в команде указано время останова, то
	 * эмуляция выполняется до наступления указанного времени (исключая его),
	 * затем входит в режим ожидания до поступления следующей команды. Если
	 * время останова не указано, то останавливается до следующей команды. 
	 */
	public static final TLCommand PAUSE = new TLCommand();
	
	private final DateTime time;
	
	/**
	 * Конструктор.
	 */
	public TLCommand() {
		this(null);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param time аргумент-время
	 */
	public TLCommand(DateTime time) {
		super();
		this.time = time;
	}
	
	/**
	 * Получить аргумент команды.
	 * <p>
	 * @return аргумент
	 */
	public DateTime getTime() {
		return time;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TLCommand.class ) {
			return false;
		}
		TLCommand o = (TLCommand) other;
		return new EqualsBuilder()
			.append(o.time, time)
			.isEquals();
	}
	
	/**
	 * Это команда на исполнение до отсечки?
	 * <p>
	 * @return true - команда на исполнение, false - иная команда
	 */
	public boolean isRun() {
		return this != FINISH && this != PAUSE;
	}
	
	/**
	 * Это команда непрерывного последовательного исполнения?
	 * <p>
	 * @return true - исполнять без перерывов до конца последовательности 
	 */
	public boolean isRunContinuosly() {
		return this != FINISH && this != PAUSE && time == null;
	}
	
	/**
	 * Эта команда применима к указанному времени?
	 * <p>
	 * Подразумевается, что проверяется команда на исполнение, для которой
	 * значение аргумента команлы (время останова) имеет смысл. Команда
	 * считается подходящей, если время останова команды не указано (выполнять
	 * до завершения или до отмены) или меньше чем значение аргумента метода. То
	 * есть, фактически время останова указывает на миллисекунду, когда
	 * выполнение команды должно прекратиться. Если время останова команды не
	 * определено, то команда считается выполняемой последовательно, до
	 * принудительного останова командой {@link #PAUSE}, {@link #FINISH} или до
	 * конца симуляции. 
	 * <p>
	 * @param time время
	 * @return true - если команда может быть выполнена при указанном времени,
	 * false - если команда устарела и не должна выполняться
	 */
	public boolean isApplicableTo(DateTime time) {
		return this.time == null || this.time.compareTo(time) < 0;
	}

}

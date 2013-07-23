package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.row.RowSetException;
import ru.prolib.aquila.quik.*;
import ru.prolib.aquila.quik.assembler.cache.*;
import ru.prolib.aquila.t2q.*;

/**
 * Фасад подсистемы сборки и согласования объектов бизнес-модели.
 * <p>
 * Примечания по событиям связанными с заявами, стоп-заявками и сделками.
 */
public class Assembler implements Starter, EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Assembler.class);
	}
	
	private final QUIKEditableTerminal terminal;
	private final AssemblerL1 l1;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param l1 функции сборки L1
	 */
	Assembler(QUIKEditableTerminal terminal, AssemblerL1 l1) {
		super();
		this.terminal = terminal;
		this.l1 = l1;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 */
	public Assembler(QUIKEditableTerminal terminal) {
		this(terminal, new AssemblerL1(terminal));
	}
	
	/**
	 * Получить функции сборки.
	 * <p>
	 * @return функции сборки
	 */
	AssemblerL1 getAssemblerL1() {
		return l1;
	}
	
	/**
	 * Получить экземпляр терминала.
	 * <p>
	 * @return терминал
	 */
	public QUIKEditableTerminal getTerminal() {
		return terminal;
	}

	@Override
	public void start() throws StarterException {
		Cache cache = terminal.getDataCache();
		// Обработка анонимных сделок выполняется в отложеном режиме.
		// Попытка обработать анонимные сделки выполняется при появлении
		// нового дескриптора или нового блока сделок.
		cache.OnDescriptorsUpdate().addListener(this);
		cache.OnTradesUpdate().addListener(this);
		logger.debug("started");
	}

	@Override
	public void stop() throws StarterException {
		Cache cache = terminal.getDataCache();
		cache.OnTradesUpdate().removeListener(this);
		cache.OnDescriptorsUpdate().removeListener(this);
		logger.debug("stopped");
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Assembler.class ) {
			return false;
		}
		Assembler o = (Assembler) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.append(o.l1, l1)
			.isEquals();
	}
	
	/**
	 * Собрать портфель.
	 * <p>
	 * @param entry кэш-запись портфеля
	 */
	public void assemble(PortfolioEntry entry) {
		l1.tryAssemble(entry);
	}
	
	/**
	 * Собрать позицию.
	 * <p>
	 * @param entry кэш-запись позиции
	 */
	public void assemble(PositionEntry entry) {
		if ( ! l1.tryAssemble(entry) ) {
			terminal.getDataCache().put(entry);
		}
	}
	
	/**
	 * Собрать инструмент и согласовать позиции.
	 * <p>
	 * @param entry кэш-запись инструмента
	 */
	public void assemble(SecurityEntry entry) {
		l1.tryAssemble(entry);
		DescriptorsCache cache = terminal.getDataCache().getDescriptorsCache();
		// Если будет добавлен новый дескриптор, то нужно запретить аналогичную
		// параллельную реакцию на добавление дескриптора или обработку входящих
		// данных позиций до тех пор, пока кэш позиций в его текущем состоянии
		// не будет обработан.
		synchronized ( cache ) {
			if ( cache.put(entry) ) {
				l1.tryAssemblePositions(entry.getShortName());
			}
		}
	}
	
	/**
	 * Согласовать состояние заявки.
	 * <p>
	 * @param entry кэш-запись заявки
	 */
	public void assemble(T2QOrder entry) {
		l1.correctOrderNumerator(entry);
		terminal.getDataCache().put(entry);
		l1.tryAssemble(entry);
	}
	
	/**
	 * Согласовать объекты с учетом собственной сделки.
	 * <p>
	 * @param entry кэш-запись сделки
	 */
	public void assemble(T2QTrade entry) {
		Cache cache = terminal.getDataCache(); 
		cache.put(entry);
		T2QOrder order = cache.getOrder(entry.getOrderId());
		if ( order != null ) {
			l1.tryAssemble(order);			
		}
	}
	
	/**
	 * Сохранить блок сделок в очереди обработки.
	 * <p>
	 * @param entry блок сделок
	 */
	public void assemble(TradesEntry entry) {
		// Обработка очередной сделки выполняется только в том случае, если для
		// сделки есть соответствующий дескриптор инструмента. При таком подходе
		// перемещение курсора не может быть выполнено перед началом попытки
		// обработать блок, иначе на каждом проходе будет теряться одна сделка.
		// По этому, перед сохранением записи необходимо выполнить позицирование
		// блока на первой сделке. Последующие перемещения выполняются только по
		// факту обработки очередной сделки и прекращаются по достижении конца
		// блока. Если переместить курсор не удалось с самого начала, значит это
		// пустой блок и кэшировать его нет смысла.
		try {
			if ( entry.next() ) {
				terminal.getDataCache().add(entry);
			}
		} catch ( RowSetException e ) {
			Object args[] = { entry.count(), e };
			logger.error("Move cursor failed, {} trades will be lost: ", args);
		}
	}

	@Override
	public void onEvent(Event event) {
		Cache cache = terminal.getDataCache();
		if ( event.isType(cache.OnDescriptorsUpdate())
				|| (event.isType(cache.OnTradesUpdate())
						&& ((CacheEvent) event).isDataAdded()) )
		{
			l1.tryAssembleTrades();
		}
	}

}

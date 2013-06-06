package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

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
	
	private final EditableTerminal terminal;
	private final Cache cache;
	private final AssemblerHighLvl high;
	
	public Assembler(EditableTerminal terminal, Cache cache,
			AssemblerHighLvl high)
	{
		super();
		this.terminal = terminal;
		this.cache = cache;
		this.high = high;
	}
	
	
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	public Cache getCache() {
		return cache;
	}

	public AssemblerHighLvl getAssemblerHighLevel() {
		return high;
	}

	@Override
	public void start() throws StarterException {
		high.start();
		cache.OnPortfoliosFCacheUpdate().addListener(this);
		cache.OnPositionsFCacheUpdate().addListener(this);
		cache.OnSecuritiesCacheUpdate().addListener(this);
		cache.OnOrdersCacheUpdate().addListener(this);
		cache.OnTradesCacheUpdate().addListener(this);
		cache.OnStopOrdersCacheUpdate().addListener(this);
		logger.debug("Assembler started");
	}

	@Override
	public void stop() throws StarterException {
		cache.OnStopOrdersCacheUpdate().removeListener(this);
		cache.OnTradesCacheUpdate().removeListener(this);
		cache.OnOrdersCacheUpdate().removeListener(this);
		cache.OnSecuritiesCacheUpdate().removeListener(this);
		cache.OnPositionsFCacheUpdate().removeListener(this);
		cache.OnPortfoliosFCacheUpdate().removeListener(this);
		high.stop();
		logger.debug("Assembler stopped");
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
			.append(o.cache, cache)
			.append(o.high, high)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

	@Override
	public void onEvent(Event event) {
		// TODO: Насчет синхронизации тут надо думать.
		// Блокировка кэша в данной реализации особых результатов не дает,
		// так как обновление кэша выполняется с блокировкой соответствующего
		// типу объектов суб-кэша.
		synchronized ( terminal ) {
			synchronized ( cache ) {
				processEvent(event);
			}
		}
	}
	
	private void processEvent(Event event) {
		// Если будет тормозить, то можно разбить этапы по типам событий.
		try {
			high.adjustSecurities();
			high.adjustPortfolios();
			high.adjustOrders();
			high.adjustStopOrders();
			high.adjustPositions();
		} catch ( OrderAlreadyExistsException e ) {
			// Здесь это исключение свидетельствует о том, что блокировка по
			// терминалу не работает должным образом или не дает эффекта
			// блокировки хранилища заявок и стоп-заявок от внесения изменений.
			// В промежуток времени между определением необходимости создать
			// новую заявку (выполнена уровнем ниже) и непосредственно
			// попыткой регистрации заявки в соответствующем хранилище, другой
			// участок кода выполнил регистрацию заявки с точно таким же
			// ключевым идентификатором, то есть выиграл конкурентную борьбу за
			// доступ к соответствующему хранилищу заявок. Это критическая
			// ситуация, которая показывает серьезный изъян в архитектуре
			// терминала. Работать дальше не имеет смысла.
			error_RegOrderIntermedChanges(e);
			terminal.firePanicEvent(2, "Multithreading related issue.");
		}
	}
	
	private void error_RegOrderIntermedChanges(OrderAlreadyExistsException e) {
		err("Serious synchronization error. Terminal locking may be broken.");
		err("Someone made changes to orders during assembling new order.");
		logger.error("Error register order.", e);
	}
	
	private void err(String msg) {
		logger.error(msg);
	}

}

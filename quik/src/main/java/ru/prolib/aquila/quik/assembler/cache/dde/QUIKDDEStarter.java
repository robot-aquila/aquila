package ru.prolib.aquila.quik.assembler.cache.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.quik.QUIKConfig;
import ru.prolib.aquila.quik.assembler.Assembler;

/**
 * Пускач сервиса импорта данных QUIK по DDE.
 * <p>
 * Данный класс выполняет полный цикл инициализации, запуска и останова
 * сервиса обработки данных, поступающих по каналу DDE.
 */
public class QUIKDDEStarter implements Starter {
	private final QUIKConfig config;
	private final Assembler asm;
	private final DDEServer server;
	/**
	 * Имя DDE-сервиса.
	 */
	private String name;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param config конфигурация
	 * @param asm сборщик объектов
	 * @param server DDE-сервер
	 */
	public QUIKDDEStarter(QUIKConfig config, Assembler asm, DDEServer server) {
		super();
		this.config = config;
		this.asm = asm;
		this.server = server;
	}

	@Override
	public synchronized void start() throws StarterException {
		QUIKDDEService service;
		RowDataConverter conv;
		synchronized ( config ) {
			name = config.getServiceName();
			service = new QUIKDDEService(name, asm.getTerminal());
			conv = new RowDataConverter(config.getDateFormat(),
					config.getTimeFormat());
			service.setHandler(config.getSecurities(),
				new TableHandler(new SecuritiesGateway(conv, asm)));
			service.setHandler(config.getAllDeals(),
				new TableHandler(new TradesGateway(conv, asm)));
			service.setHandler(config.getPortfoliosFUT(),
				new TableHandler(new FortsPortfoliosGateway(conv, asm)));
			service.setHandler(config.getPositionsFUT(),
				new TableHandler(new FortsPositionsGateway(conv, asm)));			
		}
		try {
			server.registerService(service);
		} catch ( DDEException e ) {
			throw new StarterException(e);
		}
	}

	@Override
	public synchronized void stop() throws StarterException {
		try {
			server.unregisterService(name);
			name = null;
		} catch ( DDEException e ) {
			throw new StarterException(e);
		}
	}
	
	/**
	 * Установить имя сервиса.
	 * <p>
	 * Служебный метод. Только для тестов.
	 * <p>
	 * @param name имя сервиса
	 */
	synchronized void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Получить имя запущенного сервиса.
	 * <p>
	 * Служебный метод. Только для тестов.
	 * <p>
	 * @return имя сервиса или null, если сервис не запущен
	 */
	synchronized String getName() {
		return name;
	}
	
	QUIKConfig getConfig() {
		return config;
	}
	
	Assembler getAssembler() {
		return asm;
	}
	
	DDEServer getServer() {
		return server;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QUIKDDEStarter.class ) {
			return false;
		}
		QUIKDDEStarter o = (QUIKDDEStarter) other;
		return new EqualsBuilder()
			.appendSuper(o.server == server)
			.append(o.asm, asm)
			.append(o.config, config)
			.isEquals();
	}

}

package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounterFactory;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.qforts.impl.aspect.QFOrderTrackingAspect;

@Configuration
@EnableAspectJAutoProxy
public class QFBuildingContextConfig {
	
	@Bean
	public QFObjectRegistry objectRegistry() {
		return new QFObjectRegistryImpl();
	}
	
	@Bean
	public SymbolSubscrRepository symbolSubscriptions(EventQueue eventQueue) {
		return new SymbolSubscrRepository(new SymbolSubscrCounterFactory(eventQueue), "QFORTS-SYMBOL-SUBSCR");
	}
	
	@Bean
	public AtomicLong executionIdSequence() {
		return new AtomicLong();
	}
	
	@Bean
	public AtomicLong orderIdSequence() {
		return new AtomicLong();
	}
	
	@Bean
	public QFTransactionService transactionService() {
		return new QFTransactionService(objectRegistry(), executionIdSequence());
	}
	
	@Bean
	public QForts facade(Integer liquidityMode) {
		return new QForts(objectRegistry(), transactionService(), liquidityMode);
	}
	
	@Bean
	public QFSessionSchedule sessionSchedule() {
		return new QFSessionSchedule();
	}

	@Bean("symbolDataService")
	@Profile("modern-sds")
	public QFSymbolDataService symbolDataServiceModern(DataSource dataSource, EventQueue eventQueue) {
		QFSymbolDataService data_service = new QFSymbolDataServiceModern(eventQueue, false);
		data_service.setDataSource(dataSource);
		return data_service;
	}
	
	@Bean("symbolDataService")
	@Profile("legacy-sds")
	public QFSymbolDataService symbolDataServiceLegacy(DataSource dataSource,
			QForts facade,
			SymbolSubscrRepository subscr)
	{
		QFSymbolDataService data_service = new QFSymbolDataServiceLegacy(facade, subscr);
		data_service.setDataSource(dataSource);
		return data_service;
	}
	
	@Bean("dataProvider")
	public QFReactor dataProvider(QForts facade,
			AtomicLong orderIdSequence,
			QFSymbolDataService symbolDataService,
			QFOrderExecutionTriggerMode orderExecutionTriggerMode)
	{
		return new QFReactor(facade, objectRegistry(), sessionSchedule(), orderIdSequence,
				symbolDataService, orderExecutionTriggerMode);
	}

	@Bean("orderTracker")
	@Profile("order-exec-trigger-mode-use-trade-events")
	public QFOrderTracker orderTrackerUsingEvents(QFSymbolDataService symbolDataService) {
		return new QFSymbolAutosubscr(new QFSymbolAutosubscrActionSDS(symbolDataService));
	}

	@Bean("orderTracker")
	@Profile("order-exec-trigger-mode-l1-consumer")
	public QFOrderTracker orderTrackerAsConsumer(DataSource dataSource, QFReactor dataProvider) {
		return new QFSymbolAutosubscr(new QFSymbolAutosubscrActionDS(dataSource, dataProvider));
	}
	
	@Bean
	public QFOrderTrackingAspect orderTrackingAspect(QFOrderTracker orderTracker) {
		return new QFOrderTrackingAspect(orderTracker);
	}
	
	@Pointcut("target(ru.prolib.aquila.qforts.impl.QFObjectRegistry)")
	public void inObjectRegistry() { }
	
	@Pointcut("inObjectRegistry() && execution(public boolean purgeOrder(ru.prolib.aquila.core.BusinessEntities.Order)) && args(order)")
	public void inObjectRegistryPurgeOrder(Order order) { }
	
	@Pointcut("inObjectRegistry() && execution(public boolean register(ru.prolib.aquila.core.BusinessEntities.EditableOrder)) && args(order)")
	public void inObjectRegistryRegisterOrder(EditableOrder order) { }

}

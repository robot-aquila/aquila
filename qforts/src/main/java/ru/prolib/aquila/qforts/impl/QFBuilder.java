package ru.prolib.aquila.qforts.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.data.DataSource;

public class QFBuilder {
	private final Class<? extends QFBuildingContextConfig> contextConfig;
	private DataSource dataSource;
	private EventQueue eventQueue;
	private int liquidityMode = QForts.LIQUIDITY_LIMITED;
	private boolean legacySymbolDataService = false;
	private QFOrderExecutionTriggerMode orderExecutionTriggerMode;
	private ApplicationContext context;
	
	public QFBuilder(Class<? extends QFBuildingContextConfig> context_config) {
		this.contextConfig = context_config;
		this.orderExecutionTriggerMode = QFOrderExecutionTriggerMode.USE_LAST_TRADE_EVENT_OF_SECURITY;
	}
	
	public QFBuilder() {
		this(QFBuildingContextConfig.class);
	}
	
	private EventQueue getEventQueue() {
		if ( eventQueue == null ) {
			throw new IllegalStateException("Event queue was not specified");
		}
		return eventQueue;
	}
	
	private Integer getLiquidityMode() {
		return liquidityMode;
	}
	
	private DataSource getDataSource() {
		if ( dataSource == null ) {
			throw new IllegalStateException("Data source was not specified");
		}
		return dataSource;
	}
	
	private boolean isLegacySymbolDataService() {
		return legacySymbolDataService;
	}
	
	private QFOrderExecutionTriggerMode getOrderExecutionTriggerMode() {
		return orderExecutionTriggerMode;
	}
	
	public void setBuildingContext(ApplicationContext context) {
		this.context = context;
	}
	
	ApplicationContext getContext() {
		if ( context == null ) {
			AnnotationConfigApplicationContext c = new AnnotationConfigApplicationContext();
			Set<String> active_profiles = new HashSet<>();
			active_profiles.add(isLegacySymbolDataService() ? "legacy-sds" : "modern-sds");
			switch ( getOrderExecutionTriggerMode() ) {
				case USE_L1UPDATES_WHEN_ORDER_APPEARS:
					active_profiles.add("order-exec-trigger-mode-l1-consumer");
					break;
				case USE_LAST_TRADE_EVENT_OF_SECURITY:
					active_profiles.add("order-exec-trigger-mode-use-trade-events");
					break;
			}
			c.getEnvironment().setActiveProfiles(active_profiles.toArray(new String[0]));
			c.registerBean("eventQueue", EventQueue.class, () -> getEventQueue());
			c.registerBean("dataSource", DataSource.class, () -> getDataSource());
			c.registerBean("liquidityMode", Integer.class, () -> getLiquidityMode());
			c.registerBean("orderExecutionTriggerMode",
					QFOrderExecutionTriggerMode.class, () -> getOrderExecutionTriggerMode());
			c.register(contextConfig);
			c.refresh();
			context = c;
		}
		return context;
	}
	
	public QFBuilder withEventQueue(EventQueue queue) {
		this.eventQueue = queue;
		return this;
	}
	
	public QFBuilder withLiquidityMode(int liquidity_mode) {
		if ( liquidity_mode != QForts.LIQUIDITY_LIMITED
		  && liquidity_mode != QForts.LIQUIDITY_APPLY_TO_ORDER
		  && liquidity_mode != QForts.LIQUIDITY_UNLIMITED )
		{
			throw new IllegalArgumentException("Unknown liquidity mode: " + liquidity_mode);
		}
		this.liquidityMode = liquidity_mode;
		return this;
	}
	
	public QFBuilder withDataSource(DataSource data_source) {
		this.dataSource = data_source;
		return this;
	}
	
	public QFBuilder withLegacySymbolDataService(boolean enable_legacy_service) {
		this.legacySymbolDataService = enable_legacy_service;
		return this;
	}
	
	public QFBuilder withOrderExecutionTriggerMode(QFOrderExecutionTriggerMode mode) {
		orderExecutionTriggerMode = mode;
		return this;
	}
	
	public QFortsEnv buildEnvironment(EditableTerminal terminal) {
		return new QFortsEnv(terminal, getContext().getBean(QForts.class));
	}
	
	public DataProvider buildDataProvider() {
		return getContext().getBean(DataProvider.class);
	}

}

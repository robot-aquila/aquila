package ru.prolib.aquila.qforts.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.data.DataSource;

public class QFBuilder {
	private DataSource dataSource;
	private EventQueue eventQueue;
	private int liquidityMode = QForts.LIQUIDITY_LIMITED;
	private boolean sds_legacy = false;
	private ApplicationContext context;
	
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
	
	public void setBuildingContext(ApplicationContext context) {
		this.context = context;
	}
	
	protected ApplicationContext getContext() {
		if ( context == null ) {
			AnnotationConfigApplicationContext c = new AnnotationConfigApplicationContext();
			if ( sds_legacy ) {
				c.getEnvironment().setActiveProfiles("legacy");
			}
			c.registerBean("eventQueue", EventQueue.class, () -> getEventQueue());
			c.registerBean("liquidityMode", Integer.class, () -> getLiquidityMode());
			c.registerBean("dataSource", DataSource.class, () -> getDataSource());
			c.register(QFBuildingContextConfig.class);
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
	
	
	public QFortsEnv buildEnvironment(EditableTerminal terminal) {
		return new QFortsEnv(terminal, getContext().getBean(QForts.class));
	}
	
	public DataProvider buildDataProvider() {
		return getContext().getBean(DataProvider.class);
	}

}

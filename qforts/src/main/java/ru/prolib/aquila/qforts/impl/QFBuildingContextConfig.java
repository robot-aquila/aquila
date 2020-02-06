package ru.prolib.aquila.qforts.impl;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Order;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages="ru.prolib.aquila.qforts.impl.aspect")
public class QFBuildingContextConfig {

	@Bean
	public IQFObjectRegistry objectRegistry() {
		return new QFObjectRegistry();
	}
	
	@Pointcut("within(ru.prolib.aquila.qforts.impl.QFObjectRegistry)")
	public void inObjectRegistry() { }
	
	@Pointcut("inObjectRegistry() && execution(public void purgeOrder(ru.prolib.aquila.core.BusinessEntities.Order)) && args(order)")
	public void inObjectRegistryPurgeOrder(Order order) { }
	
	@Pointcut("inObjectRegistry() && execution(public void register(ru.prolib.aquila.core.BusinessEntities.EditableOrder)) && args(order)")
	public void inObjectRegistryRegisterOrder(EditableOrder order) { }

}

package ru.prolib.aquila.qforts.impl.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Order;

@Aspect
@Component
public class QFOrderRelatedSymbolSubscriber {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFOrderRelatedSymbolSubscriber.class);
	}
	
	@Around("ru.prolib.aquila.qforts.impl.QFBuildingContextConfig.inObjectRegistryPurgeOrder(order)")
	public void aroundPurgeOrder(ProceedingJoinPoint join_point, Order order) throws Throwable {
		//Order order = (Order)join_point.getArgs()[0];
		//logger.debug("Before purge order #{}", order.getID());
		join_point.proceed();
		//logger.debug(" After purge order #{}", order.getID());
	}
	
	@Around("ru.prolib.aquila.qforts.impl.QFBuildingContextConfig.inObjectRegistryRegisterOrder(order)")
	public void aroundRegisterOrder(ProceedingJoinPoint join_point, EditableOrder order) throws Throwable {
		//Order order = (Order)join_point.getArgs()[0];
		//logger.debug("Before register order #{}", order.getID());
		join_point.proceed();
		//logger.debug(" After register order #{}", order.getID());
	}

}

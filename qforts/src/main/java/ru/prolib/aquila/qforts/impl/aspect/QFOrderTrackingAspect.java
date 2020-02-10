package ru.prolib.aquila.qforts.impl.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.qforts.impl.QFOrderTracker;

@Aspect
public class QFOrderTrackingAspect {
	static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QFOrderTrackingAspect.class);
	}
	
	private final QFOrderTracker counter;
	
	public QFOrderTrackingAspect(QFOrderTracker counter) {
		this.counter = counter;
	}
	
	@Around("ru.prolib.aquila.qforts.impl.QFBuildingContextConfig.inObjectRegistryRegisterOrder(order)")
	public Object aroundRegisterOrder(ProceedingJoinPoint join_point, EditableOrder order) throws Throwable {
		logger.debug("Before register order #{}", order.getID());
		boolean retval = (boolean) join_point.proceed();
		if ( retval ) {
			counter.startTrackingOrder(order);
		}
		logger.debug(" After register order #{} (retval={})", order.getID(), retval);
		return retval;
	}
	
	@Around("ru.prolib.aquila.qforts.impl.QFBuildingContextConfig.inObjectRegistryPurgeOrder(order)")
	public Object aroundPurgeOrder(ProceedingJoinPoint join_point, Order order) throws Throwable {
		logger.debug("Before purge order #{}", order.getID());
		boolean retval = (boolean) join_point.proceed();
		if ( retval ) {
			counter.stopTrackingOrder(order);
		}
		logger.debug(" After purge order #{} (retval={})", order.getID(), retval);
		return retval;
	}
	
}

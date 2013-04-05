package ru.prolib.aquila.dde.jddesvr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.jddesvr.ServiceHandler;
import ru.prolib.aquila.jddesvr.Table;

class JddesvrServiceHandler extends ServiceHandler {
	@SuppressWarnings("unused")
	private static final Logger logger;
	private final DDEService service;
	
	static {
		logger = LoggerFactory.getLogger(JddesvrServiceHandler.class);
	}

	public JddesvrServiceHandler(DDEService service) {
		super(service.getName());
		this.service = service;
	}

	@Override
	public boolean onConnect(String topic) {
		return service.onConnect(topic);
	}
	
	@Override
	public void onConnectConfirm(String topic) {
		service.onConnectConfirm(topic);
	}
	
	@Override
	public void onData(Table table) {
		service.onTable(new JddesvrTable(table.makeCopy()));
	}
	
	@Override
	public void onDisconnect(String topic) {
		service.onDisconnect(topic);
	}
	
	@Override
	public boolean onRawData(String topic, String item, byte[] dataBuffer) {
		return service.onData(topic, item, dataBuffer);
	}
	
	@Override
	public void onRegister() {
		service.onRegister();
	}
	
	@Override
	public void onUnregister() {
		service.onUnregister();
	}
	
}

package ru.prolib.aquila.rxltdde;

import java.util.LinkedList;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.jddesvr.*;
import ru.prolib.aquila.rxltdde.Protocol.RawData;
import ru.prolib.aquila.rxltdde.Protocol.*;

@Deprecated
public class Transmitter {

	public interface IDataQueue {
		
		public void append(RawData raw);
		
	}
	
	public static class Monitor extends Thread {
		static final long maxPauseMin = 60;
		static final long minPauseMin = 5;
		final Logger logger = LoggerFactory.getLogger(Monitor.class);
		long requests = 0;
		
		public Monitor(String name) {
			super(name);
		}
		
		public synchronized void countRequest() {
			requests ++;			
		}
		
		@Override
		public void run() {
			long currentPauseMin = minPauseMin;
			long currentRequests = 0;
			while ( true ) {
				try {
					sleep(1000 * 60 * currentPauseMin);
				} catch ( InterruptedException e ) {
					interrupt();
					return;
				}
				synchronized ( this ) {
					currentRequests = requests;
					requests = 0;
				}
				double requestsPerMin = currentRequests / currentPauseMin;
				if ( requestsPerMin < 1 ) {
					currentPauseMin *= 2;
					if ( currentPauseMin > maxPauseMin ) {
						currentPauseMin = maxPauseMin;
					}
				} else if ( requestsPerMin > 60
						&& currentPauseMin > minPauseMin )
				{
					currentPauseMin /= 2;
					if ( currentPauseMin < minPauseMin ) {
						currentPauseMin = minPauseMin;
					}
				}
				logger.info("{} Req/Min. Next check after {} mins.",
						requestsPerMin, currentPauseMin);
			}
		}
		
	}
	
	/**
	 * Обработчик DDE-транзакций.
	 * На запросы onConnect всегда отвечает положительно. Поступающие onRawData
	 * данные отправляет на обработку в очередь.
	 */
	public static class Handler extends ServiceHandler {
		final Logger logger = LoggerFactory.getLogger(Handler.class);
		final IDataQueue queue;
		
		public Handler(String name, IDataQueue queue) {
			super(name);
			this.queue = queue;
		}
		
		@Override
		public synchronized boolean onConnect(String topic) {
			logger.debug("onConnect: {}", topic);
			return true;
		}
		
		@Override
		public synchronized boolean onRawData(String topic,
				String item, byte[] data)
		{
			queue.append(new RawData(topic, item, data));
			return true;
		}
		
		@Override
		public synchronized void onDisconnect(String topic) {
			logger.debug("onDisconnect: {}", topic);
		}
		
		@Override
		public synchronized void onRegister() {
			logger.debug("onRegister");
		}
		
		@Override
		public synchronized void onUnregister() {
			logger.debug("onUnregister");
		}
		
	}
	
	/**
	 * Обработчик очереди данных последовательно отправляет данные приемнику.
	 */
	public static class Worker implements IDataQueue,Runnable {
		final static Logger logger = LoggerFactory.getLogger(Worker.class);
		final LinkedList<RawData> queue;
		final Protocol.IWriter writer;
		final Monitor ddeRequests;
		final Monitor sentPackets;
		boolean exit;
		
		public Worker(Protocol.IWriter writer) {
			super();
			queue = new LinkedList<RawData>();
			ddeRequests = new Monitor("DDE Requests");
			sentPackets = new Monitor("Sent Packets");
			this.writer = writer;
			exit = false;
		}

		@Override
		public void append(RawData raw) {
			synchronized ( queue ) {
				queue.addLast(raw);
				queue.notifyAll();
			}
			ddeRequests.countRequest();
		}

		@Override
		public void run() {
			ddeRequests.start();
			sentPackets.start();
			logger.info("Worker thread started");
			RawData raw = null;
			while ( true ) {
				synchronized ( queue ) {
					if ( exit ) {
						break;
					}
					if ( queue.size() == 0 ) {
						try {
							queue.wait();
						} catch ( InterruptedException e ) {
							logger.error("Unexpectedly interrupted", e);
							Thread.currentThread().interrupt();
							exit = true;
						}
					}
					// Делаем так, что бы обрабатывать вне synchronized
					raw = (queue.size() != 0 ? queue.removeFirst() : null);
				} // end queue.synchronized
				if ( raw != null ) {
					try {
						writer.write(new Packet(Packet.RAWDATA, raw));
					} catch ( ProtocolException e ) {
						logger.warn("Cannot write packet", e);
					}
					sentPackets.countRequest();
				}
			}
			ddeRequests.interrupt();
			sentPackets.interrupt();
			try {
				ddeRequests.join();
				sentPackets.join();
			} catch ( InterruptedException e ) {
				Thread.currentThread().interrupt();
				logger.error("Unexpectedly interrupted [2]", e);
			}

			logger.info("Worker thread finished");
		}
		
		public void stop() {
			synchronized ( queue ) {
				exit = true;
				queue.notifyAll();
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		if ( args.length < 3 ) {
			System.err.println("Usage: <host> <port> <ddename> [log4j-config]");
			System.exit(1);
		}
		if ( args.length >= 4 ) {
			PropertyConfigurator.configure(args[3]);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String name = args[2];

		IWriter writer = new Protocol.SocketWriter(host, port);
	    Worker worker = new Worker(writer);
	    Thread workerThread = new Thread(worker);

		Handler handler = new Handler(name, worker);
		Server dde = new Server();
		dde.start();
		dde.registerService(handler);
		workerThread.start();
		workerThread.join();
		dde.unregisterService(handler);
		dde.stop();
		writer.close();
	
		System.exit(0);
	}
	
}

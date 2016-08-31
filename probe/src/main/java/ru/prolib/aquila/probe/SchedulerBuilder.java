package ru.prolib.aquila.probe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.probe.scheduler.Cmd;
import ru.prolib.aquila.probe.scheduler.SchedulerState;
import ru.prolib.aquila.probe.scheduler.SchedulerWorker;

public class SchedulerBuilder {
	private static final String DEFAULT_NAME = "PROBE-SCHEDULER-";
	private static int lastNameIndex = 0;
	
	synchronized static int getLastNameIndex() {
		return lastNameIndex;
	}
	
	synchronized static int getNextNameIndex() {
		return ++lastNameIndex;
	}
	
	private String name;
	private BlockingQueue<Cmd> queue;
	private SchedulerState state;
	private SchedulerWorker worker;
	private Thread workerThread;
	private boolean valid = true;
	
	/**
	 * Get command queue.
	 * <p>
	 * If queue instance was not previously defined then this method creates a
	 * new instance of {@link java.util.concurrent.LinkedBlockingQueue}. Newly
	 * created instance will be used by the object until another instance will
	 * be defined by calling the {@link #setCommandQueue(BlockingQueue)} method.
	 * <p>
	 * @return command queue
	 */
	public BlockingQueue<Cmd> getCommandQueue() {
		if ( queue == null ) {
			queue = new LinkedBlockingQueue<Cmd>();
		}
		return queue;
	}
	
	/**
	 * Set command queue.
	 * <p>
	 * This call overrides previously defined command queue.
	 * <p>
	 * @param queue - the command queue
	 * @return this
	 */
	public SchedulerBuilder setCommandQueue(BlockingQueue<Cmd> queue) {
		this.queue = queue;
		return this;
	}
	
	/**
	 * Get a scheduler state.
	 * <p>
	 * If state was not previously defined then this method creates a new
	 * instance of scheduler state. Newly create instance will be used by the
	 * object until another instance will be defined by calling the
	 * {@link #setState(SchedulerState)} method.
	 * <p>
	 * @return state
	 */
	public SchedulerState getState() {
		if ( state == null ) {
			state = new SchedulerState();
		}
		return state;
	}
	
	/**
	 * Set a scheduler state.
	 * <p>
	 * This call overrides previously defined scheduler state.
	 * <p>
	 * @param state - the state
	 * @return this
	 */
	public SchedulerBuilder setState(SchedulerState state) {
		this.state = state;
		return this;
	}
	
	/**
	 * Get worker.
	 * <p>
	 * If worker was not previously defined then this method creates a new
	 * instance of a worker. The results of {@link #getCommandQueue()} and
	 * {@link #getState()} will be used to produce new instance. Newly created
	 * instance will be used by the object until another instance will be
	 * defined by calling the {@link #setWorker(SchedulerWorker)} method.
	 * <p>
	 * @return worker
	 */
	public SchedulerWorker getWorker() {
		if ( worker == null ) {
			worker = new SchedulerWorker(getCommandQueue(), getState());
		}
		return worker;
	}
	
	/**
	 * Set a worker instance.
	 * <p>
	 * This call overrides previously defined worker instance. When using an
	 * explicit worker instance then command queue and state instances defined
	 * by appropriate methods have no effect regarding to a worker instance. 
	 * <p>
	 * @param worker - the worker
	 * @return this
	 */
	public SchedulerBuilder setWorker(SchedulerWorker worker) {
		this.worker = worker;
		return this;
	}
	
	/**
	 * Get name of scheduler.
	 * <p>
	 * The name is used to identify scheduler's thread. If name was not
	 * previously defined then this method builds a new name. Newly created name
	 * will be used by the object until another name will be defined by calling
	 * the {@link #setName(String)} method.
	 * <p>
	 * @return the name of scheduler
	 */
	public String getName() {
		if ( name == null ) {
			name = DEFAULT_NAME + getNextNameIndex();
		}
		return name;
	}
	
	/**
	 * Set name of scheduler.
	 * <p>
	 * This call overrides previously defined scheduler name.
	 * <p>
	 * @param name - the new name
	 * @return this
	 */
	public SchedulerBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * Get worker thread.
	 * <p>
	 * If thread was not previously defined then this method creates a new
	 * thread. The result of {@link #getWorker()} will be used to produce new
	 * instance. Newly created instance will be used by the object until another
	 * instance will be defined by calling the {@link #setWorkerThread(Thread)}
	 * method. 
	 * <p>
	 * @return worker thread
	 */
	public Thread getWorkerThread() {
		if ( workerThread == null ) {
			workerThread = new Thread(getWorker(), getName());
		}
		return workerThread;
	}
	
	/**
	 * Set worker thread.
	 * <p>
	 * This call overrides previously defined thread. When using an explicit
	 * thread instance then worker and name defined by appropriate methods have
	 * no effect regarding to a thread instance.
	 * <p>
	 * @param workerThread - the thread
	 * @return this
	 */
	public SchedulerBuilder setWorkerThread(Thread workerThread) {
		this.workerThread = workerThread;
		return this;
	}
	
	/**
	 * Build scheduler instance.
	 * <p>
	 * This method creates a new scheduler instance according to the current
	 * settings. The created instance is fully ready to work. After creating an
	 * instance the builder marked as invalid. All subsequent requests to create
	 * new scheduler will be rejected with an exception. 
	 * <p>
	 * @return new scheduler instance
	 * @throws IllegalStateException - this builder already produced a scheduler
	 */
	public Scheduler buildScheduler() {
		if ( ! valid ) {
			throw new IllegalStateException();
		}
		Scheduler scheduler = new SchedulerImpl(getCommandQueue(), getState());
		Thread workerThread = getWorkerThread();
		workerThread.setDaemon(true);
		workerThread.start();
		valid = false;
		return scheduler;
	}

}

package io.github.manhnt217.processmgr.process;

import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author manhnt
 */
public class ProcessControl {

	private final String uuid;
	private volatile boolean cancelRequest;
	private volatile double completionRate;
	private volatile Status status;
	private Object processResult;
	private Process process;
	private final ReentrantLock lock;
	private final Condition doneCondition;

	ProcessControl() {
		this.uuid = UUID.randomUUID().toString();
		this.status = Status.INITIALIZED;
		this.lock = new ReentrantLock();
		this.doneCondition = lock.newCondition();
	}

	public String getUuid() {
		return uuid;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		lock.lock();
		try {
			this.status = status;
			if (this.status.isDone()) {
				this.doneCondition.signalAll();
			}
		} finally {
			lock.unlock();
		}
	}

	public double getCompletionRate() {
		return completionRate;
	}

	public void setCompletionRate(double completionRate) {
		this.completionRate = completionRate;
	}

	public void waitForDone() throws InterruptedException {
		lock.lock();
		try {
			while (!status.isDone()) {
				this.doneCondition.await();
			}
		} finally {
			lock.unlock();
		}
	}

	public void cancel() {
		cancelRequest = true;
	}

	public Object getResult() {
		return processResult;
	}

	public void setResult(Object result) {
		this.processResult = result;
	}

	public void setProcess(Process process) {
		if (this.process != null) {
			throw new IllegalStateException("This process control is already contain another process. Each process control can be used to run only one process");
		}
		this.process = process;
	}

	void run() {
		this.process.run(this);
	}
}

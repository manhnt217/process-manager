package io.github.manhnt217.progressmgr.process;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author manhnt
 */
public class Intent {
	private final Action action;
	private final Resource resource;
	private final ReentrantLock lock;

	public Intent(Action action, Resource resource) {
		this.action = action;
		this.resource = resource;
		this.lock = new ReentrantLock(true);
	}

	public Action getAction() {
		return action;
	}

	public Resource getResource() {
		return resource;
	}

	void lock() {
		this.lock.lock();
	}

	void unlock() {
		this.lock.unlock();
	}
}

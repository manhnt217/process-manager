package io.github.manhnt217.processmgr.process;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author manhnt
 */
public class ResourceHolder {
	private final Resource resource;
	private final ReentrantLock lock;

	ResourceHolder(Resource resource) {
		this.resource = resource;
		this.lock = new ReentrantLock(true);
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

package io.github.manhnt217.processmgr.process;

/**
 * @author manhnt
 */
public enum Status {
	INITIALIZED, RUNNING, COMPLETED,
	FAILED; // cancelled or exception thrown

	public boolean isDone() {
		return this == COMPLETED || this == FAILED;
	}
}

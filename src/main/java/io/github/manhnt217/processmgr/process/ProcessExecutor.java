package io.github.manhnt217.processmgr.process;

import java.util.List;

/**
 * @author manhnt
 */
public abstract class ProcessExecutor {

	private List<Process> waitingList;
	private List<Process> runningList;

	public final synchronized ProcessControl execute(Process r) {
		final ProcessControl p = new ProcessControl();
		new Thread(() -> r.run(p)).start();
		return p;
	}
}

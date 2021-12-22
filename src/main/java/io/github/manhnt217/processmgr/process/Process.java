package io.github.manhnt217.processmgr.process;

import io.github.manhnt217.processmgr.process.dependency.ProcessDependency;

import java.util.Collection;
import java.util.List;

/**
 * @author manhnt
 */
public interface Process {
	void run(ProcessControl p);

    ProcessDependency getDependency(Collection<ProcessControl> runningProcesses);

    ProcessControl newProcessControl();
}

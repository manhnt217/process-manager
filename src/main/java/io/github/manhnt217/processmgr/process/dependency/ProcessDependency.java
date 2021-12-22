package io.github.manhnt217.processmgr.process.dependency;

/**
 * Represent the dependency on other processes
 */
public interface ProcessDependency {

    ProcessDependency EMPTY_DEPENDENCY = () -> {};

    /**
	 * Wait for the dependant processes to be completed
	 */
	void waitForDone() throws InterruptedException;

	static ProcessDependency allOf(ProcessDependency... dependencies) {
		return new AndDependency(dependencies);
	}

	static ProcessDependency someOf(int numRequired, ProcessDependency... dependencies) {
		return new SomeOfDependency(numRequired, dependencies);
	}
}

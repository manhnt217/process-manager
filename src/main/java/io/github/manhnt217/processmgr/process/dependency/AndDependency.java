package io.github.manhnt217.processmgr.process.dependency;

/**
 * @author manhnt
 */
class AndDependency implements ProcessDependency {

	private ProcessDependency[] dependencies;

	AndDependency(ProcessDependency... dependencies) {
        if (dependencies.length < 1) {
            throw new IllegalArgumentException("At least 1 dependency is needed");
        }
		this.dependencies = dependencies;
	}

	@Override
	public void waitForDone() throws InterruptedException {
		for (ProcessDependency dependency : dependencies) {
			dependency.waitForDone();
		}
	}
}

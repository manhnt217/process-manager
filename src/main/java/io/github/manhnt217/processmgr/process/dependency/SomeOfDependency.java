package io.github.manhnt217.processmgr.process.dependency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author manhnt
 */
class SomeOfDependency implements ProcessDependency {

	private final CountDownLatch depCountDown;
    private final AtomicInteger numDepRemain;
    private ProcessDependency[] dependencies;
	private final int numDep;

	SomeOfDependency(int numDepRequired, ProcessDependency... dependencies) {
        if (dependencies.length < 1) {
            throw new IllegalArgumentException("At least 1 dependency is needed");
        }
        if (numDepRequired < 1 || numDepRequired > dependencies.length) {
            throw new IllegalArgumentException("Number of required dependencies must be greater than zero and less than or equal to number of provided dependecies");
        }
		this.dependencies = dependencies;
		this.numDep = dependencies.length;
		this.depCountDown = new CountDownLatch(this.numDep);
		this.numDepRemain = new AtomicInteger(numDepRequired);
	}

	@Override
	public void waitForDone() throws InterruptedException {

		ExecutorService executorService = Executors.newFixedThreadPool(dependencies.length);

		for (ProcessDependency dependency : dependencies) {
			executorService.submit(() -> {
				try {
					dependency.waitForDone();
                    int remain = numDepRemain.decrementAndGet();
                    if (remain == 0) {
                        //set to zero because enough number of the dependencies has been satisfied
                        countDownToZero();
                    }
				} catch (InterruptedException e) {
					System.out.println("Interupted. Maybe because number of finished dependencies is enough to satisfy the condition, no need to wait for the others");
				} finally {
                    depCountDown.countDown();
                }
			});
		}
        depCountDown.await();
        executorService.shutdownNow(); // no need to wait for other threads (if any) to be completed
        if (numDepRemain.get() > 0) {
            throw new InterruptedException("Number of finished dependencies is not enough");
        }
	}

	private void countDownToZero() {
		for (int i = 0; i < numDep; i++) {
			depCountDown.countDown();
		}
	}
}

package io.github.manhnt217.progressmgr.example;

import io.github.manhnt217.progressmgr.process.Process;
import io.github.manhnt217.progressmgr.process.ProcessControl;

/**
 * @author manhnt
 */
public class FooRunner implements Process {

	private final int fooNumber;

	public FooRunner(int fooNumber) {
		this.fooNumber = fooNumber;
	}

	@Override
	public void run(ProcessControl pc) {
		for (int i = 0; i < 3; i++) {
			System.out.println("Hello foo" + fooNumber + " " + i + " time(s).");
			pc.setCompletionRate(pc.getCompletionRate() + 0.3);
		}
		System.out.println("====");
	}
}

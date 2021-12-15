package io.github.manhnt217.processmgr;

import io.github.manhnt217.processmgr.example.FooPolicy;
import io.github.manhnt217.processmgr.example.FooResource;
import io.github.manhnt217.processmgr.example.FooRunner;
import io.github.manhnt217.processmgr.process.ProcessControl;
import io.github.manhnt217.processmgr.process.ProcessManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) throws InterruptedException {
		ProcessManager processManager = ProcessManager.get();
		processManager.registerPolicy(new FooPolicy());

		List<ProcessControl> pcs = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			ProcessControl pc = processManager.allocate(new FooResource("fooRes"));
			pc.setProcess(new FooRunner(i));
			processManager.execute(pc);
			pcs.add(pc);
		}

		for (ProcessControl pc : pcs) {
			pc.waitForDone();
		}
	}
}

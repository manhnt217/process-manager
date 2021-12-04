package io.github.manhnt217.progressmgr;

import io.github.manhnt217.progressmgr.example.FooAction;
import io.github.manhnt217.progressmgr.example.FooPolicy;
import io.github.manhnt217.progressmgr.example.FooResource;
import io.github.manhnt217.progressmgr.example.FooRunner;
import io.github.manhnt217.progressmgr.process.ProcessControl;
import io.github.manhnt217.progressmgr.process.ProcessManager;

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
			ProcessControl pc = processManager.allocate(new FooResource("fooRes"), new FooAction("doFoo"));
			pc.setProcess(new FooRunner(i));
			processManager.execute(pc);
			pcs.add(pc);
		}

		for (ProcessControl pc : pcs) {
			pc.waitForDone();
		}
	}
}

package io.github.manhnt217.progressmgr.example;

import io.github.manhnt217.progressmgr.process.Action;

import java.util.Objects;

/**
 * @author manhnt
 */
public class FooAction implements Action {

	private final String name;

	public FooAction(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FooAction fooAction = (FooAction) o;
		return Objects.equals(name, fooAction.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "FooAction{" +
				"name='" + name + '\'' +
				'}';
	}
}

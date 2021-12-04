package io.github.manhnt217.progressmgr.example;

import io.github.manhnt217.progressmgr.process.Resource;

import java.util.Objects;

/**
 * @author manhnt
 */
public class FooResource implements Resource {
	private final String name;

	public FooResource(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FooResource that = (FooResource) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "FooResource{" +
				"name='" + name + '\'' +
				'}';
	}
}

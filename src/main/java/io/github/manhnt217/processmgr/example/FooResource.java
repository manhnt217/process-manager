package io.github.manhnt217.processmgr.example;

import io.github.manhnt217.processmgr.process.Resource;

import java.util.Objects;

/**
 * @author manhnt
 */
public class FooResource implements Resource {
	private String name;

    public FooResource() {
    }

    public FooResource(String name) {
		this.name = name;
	}

	@Override
	public Object getAttribute(String attrName) {
		return null;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

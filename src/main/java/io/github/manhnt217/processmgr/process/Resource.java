package io.github.manhnt217.processmgr.process;

/**
 * @author manhnt
 */
public interface Resource {
	String toString();

	/**
	 * @param attrName
	 * @return <code>null</code> if the given attribute doesn't exist
	 */
	Object getAttribute(String attrName);
}

package net.aegistudio.mpp.export;

/**
 * Throw when the corresponding naming not suitable.
 * @author aegistudio
 */

public class NamingException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final String naming;
	private final Object value;
	public NamingException(String name, Object value) {
		super(name + ": " + value);
		this.naming = name;
		this.value = value;
	}
	
	public String getNaming() {
		return this.naming;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> t) {
		return (T) this.value;
	}
}

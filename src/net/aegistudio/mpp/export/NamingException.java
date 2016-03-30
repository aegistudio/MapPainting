package net.aegistudio.mpp.export;

/**
 * Throw when the corresponding naming not suitable.
 * Like there's a conflict, or something is missing.
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
	
	/**
	 * @return at which aspect there's a naming problem.
	 */
	public String getNaming() {
		return this.naming;
	}
	
	/**
	 * @return the detailed problem of the naming.
	 */
	public Object getValue() {
		return this.value;
	}
	
	/**
	 * 
	 * @param t the type of the returned value.
	 * @return the detailed problem of the naming.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> t) {
		return (T) this.value;
	}
}

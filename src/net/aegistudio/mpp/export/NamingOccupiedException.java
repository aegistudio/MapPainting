package net.aegistudio.mpp.export;

public class NamingOccupiedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private final String naming;
	private final Object value;
	public NamingOccupiedException(String name, Object value) {
		super(name);
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

package com.atlantbh.utils.serialization;

public class SerializationException extends RuntimeException {

	private static final long serialVersionUID = 4009829994171468522L;

	public SerializationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public SerializationException(Class<?> targetClass, Throwable cause) {
		super("Error derializing " + targetClass.getName(), cause);
	}
}

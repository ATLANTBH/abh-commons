package com.atlantbh.utils.serialization;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.atlantbh.utils.serialization.DeserializationException;
import com.atlantbh.utils.serialization.SerializationException;
import com.atlantbh.utils.stream.ByteArrayOutputStream;

public class Serializer {
	public static <T> T copy(Object obj, Class<T> targetClass) {
		// Write the object out to a byte array
		ByteArrayOutputStream baos = toByteArrayOutputStream(obj);
		// Create object from bytes
		return fromByteArray(baos.getBuffer(), 0, baos.getSize(), targetClass);
	}
	
	public static Object copy(Object obj) {
		// Write the object out to a byte array
		ByteArrayOutputStream baos = toByteArrayOutputStream(obj);
		// Create object from bytes
		return fromByteArray(baos.getBuffer(), 0, baos.getSize());
	}
	
	public static ByteArrayOutputStream toByteArrayOutputStream(Object obj) {
		try {
			// Write the object out to a byte array
			ByteArrayOutputStream fbos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(fbos);
			out.writeObject(obj);
			out.flush();
			out.close();
			return fbos;
		} catch (Exception e) {
			throw new SerializationException(obj.getClass(), e);
		}
	}
	
	public static byte[] toByteArray(Object obj) {
		return toByteArrayOutputStream(obj).trimBuffer();
	}
	
	public static <T> T fromByteArray(byte[] objectBytes, Class<T> targetClass) {
		return fromByteArray(objectBytes, 0, objectBytes.length, targetClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T fromByteArray(byte[] objectBytes, int offset, int count, Class<? extends T> targetClass) {
		Object res = fromByteArray(objectBytes, offset, count);
		if (targetClass.isInstance(res)) {
			return (T) res;
		} else {
			throw new DeserializationException(String.format("Expected %s, got %", targetClass.getName(), res.getClass().getName()), null);
		}
	}
	
	public static Object fromByteArray(byte[] objectBytes, int offset, int count) {
		try {
			// Write the object out to a byte array
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(objectBytes, offset, count));
			return in.readObject();
		} catch (Exception e) {
			throw new DeserializationException("Error deserializing object", e);
		}
	}
}

package com.atlantbh.commons;

import com.atlantbh.utils.serialization.Serializer;
import com.atlantbh.utils.stream.ByteArrayOutputStream;

public class ObjectCopy {
	public static <T> T copy(Object obj, Class<T> targetClass) {
		// Write the object out to a byte array
		ByteArrayOutputStream baos = Serializer.toByteArrayOutputStream(obj);
		// Create object from bytes
		return Serializer.fromByteArray(baos.getBuffer(), 0, baos.getSize(), targetClass);
	}
	
	public static Object copy(Object obj) {
		// Write the object out to a byte array
		ByteArrayOutputStream baos = Serializer.toByteArrayOutputStream(obj);
		// Create object from bytes
		return Serializer.fromByteArray(baos.getBuffer(), 0, baos.getSize());
	}
}

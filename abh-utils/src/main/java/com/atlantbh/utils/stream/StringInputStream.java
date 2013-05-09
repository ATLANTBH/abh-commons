package com.atlantbh.utils.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class StringInputStream extends InputStream {
	
	private StringReader sr;

	public StringInputStream(String s) {
		sr = new StringReader(s);
	}

	@Override
	public int read() throws IOException {
		return sr.read();
	}

}

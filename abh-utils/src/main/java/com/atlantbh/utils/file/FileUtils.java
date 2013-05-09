package com.atlantbh.utils.file;

import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class FileUtils { 	
	/**
	 * System dependent temp folder
	 */
	public final static File SYS_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	
	/**
	 * 
	 * @param prefix
	 * @return
	 */
	public static File getTempDirectory(String prefix) {
		return getTempDirectory(prefix, true);
	}
	
	/**
	 * Creates new temp folde with provided prefix within SYS_TEMP_DIR.
	 * It can be registered for deletion.
	 * @param prefix for temp directoryx
	 * @param deleteOnExit flag tht
	 * @return
	 */
	public static File getTempDirectory(String prefix, boolean deleteOnExit) {
		if (prefix != null && !prefix.isEmpty()) {
			String tempFolder = prefix + UUID.randomUUID().toString();
			if (deleteOnExit) {
				tempFolders.add(tempFolder);
			}
			File tempDir = new File(SYS_TEMP_DIR, tempFolder);
			tempDir.mkdirs();
			// TODO check if looping required
			return tempDir;
		} else {
			throw new IllegalArgumentException("Prefix must be non empty value.");
		}
	}
		public static boolean deleteDirectory(File dir) {
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (dir.delete());
	}

	private static Set<String> tempFolders = new ConcurrentSkipListSet<String>();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (String temp : tempFolders) {
					System.out.println("Deleting " + temp);
					deleteDirectory(new File(temp));
				}
			}
		});
	}
}

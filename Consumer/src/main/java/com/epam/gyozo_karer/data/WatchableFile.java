package com.epam.gyozo_karer.data;

import java.io.File;

public class WatchableFile {
	
	private String path;
	private String fileName;
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		StringBuilder fileSeparator = new StringBuilder();
		fileSeparator.append(File.separatorChar);
		if (!path.contains(fileSeparator)) {
			if ("/".equals(fileSeparator)) {
				path = path.replace('\\', File.separatorChar );
			} else {
				path = path.replace('/', File.separatorChar );
			}
		}
		this.path = path;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}

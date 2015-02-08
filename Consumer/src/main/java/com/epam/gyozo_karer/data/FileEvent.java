package com.epam.gyozo_karer.data;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;

public class FileEvent {
	
	private String path;
	private String fileName;
	private Kind<?> fileEvent;
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Kind<?> getFileEvent() {
		return fileEvent;
	}
	
	public void setFileEvent(Kind<?> fileEvent) {
		this.fileEvent = fileEvent;
	}
}

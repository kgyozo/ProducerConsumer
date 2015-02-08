package com.epam.gyozo_karer;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;

public class FileEvent {
	
	public String path;
	public String fileName;
	
	public Kind<?> fileEvent;
	
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

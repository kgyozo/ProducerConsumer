package com.epam.gyozo_karer.file;

import java.util.List;

import com.epam.gyozo_karer.data.FileEvent;

public interface FileHandler {
	void setFilePath(String filePath);
	void setFileName(String fileName);
	List<String> action(FileEvent event);
}

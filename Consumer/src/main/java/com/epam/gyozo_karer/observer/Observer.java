package com.epam.gyozo_karer.observer;

import com.epam.gyozo_karer.data.FileEvent;
import com.epam.gyozo_karer.file.FileHandler;

public interface Observer {
	public void update(FileEvent event);
	public void setModifyFileHandler(FileHandler modifyFileHandler);
}

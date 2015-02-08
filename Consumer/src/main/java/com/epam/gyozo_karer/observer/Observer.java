package com.epam.gyozo_karer.observer;

import com.epam.gyozo_karer.data.FileEvent;

public interface Observer {
	public void update(FileEvent event);
}

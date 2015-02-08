package com.epam.gyozo_karer.observer;

import java.nio.file.WatchEvent.Kind;

import com.epam.gyozo_karer.data.FileEvent;

public interface Observable {
	public void attach(Observer fileHandler, Kind<?> kind);
	public void detach(Observer fileHandler, Kind<?> kind);
	public void notification(FileEvent event);
}

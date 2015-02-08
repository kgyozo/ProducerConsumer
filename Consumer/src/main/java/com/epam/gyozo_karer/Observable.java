package com.epam.gyozo_karer;

import java.nio.file.WatchEvent.Kind;

public interface Observable {
	public void attach(Observer fileHandler, Kind<?> kind);
	public void detach(Observer fileHandler, Kind<?> kind);
	public void notification(FileEvent event);
}

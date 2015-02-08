package com.epam.gyozo_karer;

import java.nio.file.WatchEvent.Kind;
import java.util.LinkedList;
import java.util.List;

public class FileObservable implements Observable {
	
	public List<Observer> observers = null;

	public void attach(Observer fileHandler, Kind<?> kind) {
		if (observers == null) {
			observers = new LinkedList<>();
		}
		observers.add(fileHandler);
		
	}

	public void detach(Observer fileHandler, Kind<?> kind) {
		observers.remove(fileHandler);
	}

	public void notification(FileEvent event) {
		for (Observer observer : observers) {
			observer.update(event);
		}
		
	}

}

package com.epam.gyozo_karer.observer;

import java.nio.file.WatchEvent.Kind;
import java.util.LinkedList;
import java.util.List;

import com.epam.gyozo_karer.data.FileEvent;

public class FileObservable implements Observable {
	
	private List<Observer> observers = null;

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

package com.epam.gyozo_karer.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.epam.gyozo_karer.data.FileEvent;
import com.epam.gyozo_karer.data.WatchableFile;
import com.epam.gyozo_karer.observer.FileObservable;
import com.epam.gyozo_karer.observer.Observable;

public class FileWatcher {

	final static Logger logger = Logger.getLogger(FileWatcher.class);

	private Observable observable;
	private final Map<String, List<String>> watchables;

	private WatchService watcher;
	private Map<WatchKey, Path> keys;

	public FileWatcher(String path, String fileName) {
		List<WatchableFile> files = setOneWatchable(path, fileName);

		watchables = new HashMap<>();
		addElementToWatchables(files);
		initializeWatcher();
		initializeWatchKeys();
		logger.info("FileWatcher is initialized");
	}

	private List<WatchableFile> setOneWatchable(String path, String fileName) {
		WatchableFile watchable = new WatchableFile();
		watchable.setPath(path);
		watchable.setFileName(fileName);
		List<WatchableFile> files = new LinkedList<>();
		files.add(watchable);
		return files;
	}

	public FileWatcher(List<WatchableFile> files) {
		watchables = new HashMap<>();
		addElementToWatchables(files);
		initializeWatcher();
		initializeWatchKeys();
		logger.info("FileWatcher is initialized");
	}

	private void addElementToWatchables(List<WatchableFile> files) {
		for (WatchableFile file : files) {
			List<String> fileList = watchables.get(file.getPath());
			if (fileList == null) {
				fileList = new LinkedList<>();
			}
			fileList.add(file.getFileName());
			watchables.put(file.getPath(), fileList);
		}
	}

	private void initializeWatcher() {
		try {
			this.watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void initializeWatchKeys() {
		this.keys = new HashMap<WatchKey, Path>();
		for (String path : watchables.keySet()) {
			Path dir = Paths.get(path);
			try {
				register(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_MODIFY);
		keys.put(key, dir);
	}

	public void setObservable(Observable observ) {
		this.observable = observ;
	}

	public void watch() {
		logger.info("FileWatcher is starting to watch file changes.");

		WatchKey key = null;
		while (true) {
			try {
				key = waitingForTheKey();
				Path dir = keys.get(key);
				if (dir == null) {
					logger.error("WatchKey not recognized!!");
					continue;
				}
				processKey(key, dir);
				
				if (!key.reset()) {
					break; 
				}
			} catch (InterruptedException ie) {
				logger.error(ie);
			}
		}
	}

	private WatchKey waitingForTheKey() throws InterruptedException {
			return watcher.take();
	}
	
	private void processKey(WatchKey key, Path dir) {
		Kind<?> kind = null;
		for (WatchEvent<?> watchEvent : key.pollEvents()) {
			kind = watchEvent.kind();
			if (OVERFLOW == kind) {
				continue; // loop
			} else if (ENTRY_MODIFY == kind) {
				processEntryModifiy((WatchEvent<Path>) watchEvent, dir);
			}
		}
	}
	
	private void processEntryModifiy(WatchEvent<Path> watchEvent, Path dir) {
		Path path = ((WatchEvent<Path>) watchEvent)
				.context();
		
		if (isWatchedFile(dir, path)) {
			logger.info(String.format("%s is modified in %s",
					path.toString(), dir.toString()));
			FileEvent event = new FileEvent();
			event.setFileEvent(ENTRY_MODIFY);
			event.setPath(dir.toString());
			event.setFileName(path.toString());
			this.observable.notification(event);
		}
	}
	
	private boolean isWatchedFile(Path dir, Path path) {
		List<String> lista = watchables.get(dir.toString());
		return (lista != null && lista.contains(path.toString()));
	}

}

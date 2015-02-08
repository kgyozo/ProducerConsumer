package com.epam.gyozo_karer.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.epam.gyozo_karer.data.FileEvent;
import com.epam.gyozo_karer.data.WatchableFile;
import com.epam.gyozo_karer.observer.FileObservable;
import com.epam.gyozo_karer.observer.Observable;

public class FileWatcher {

	private Observable observ;
	private final Map<String, List<String>> watchables;

	private WatchService watcher;
	private Map<WatchKey, Path> keys;

	public FileWatcher(String path, String fileName) {
		watchables = new HashMap<>();
		List<String> names = new LinkedList<>();
		names.add(fileName);
		watchables.put(path, names);

		this.observ = new FileObservable();

		try {
			this.watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.keys = new HashMap<WatchKey, Path>();

		Path dir = Paths.get(path);
		try {
			register(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("FileWatcher is initialized");
	}

	public FileWatcher(List<WatchableFile> files) {
		watchables = new HashMap<>();
		for (WatchableFile file : files) {
			List<String> fileList = watchables.get(file.getPath());
			if (fileList == null) {
				fileList = new LinkedList<>();
			}
			fileList.add(file.getFileName());
			watchables.put(file.getPath(), fileList);
		}

		this.observ = new FileObservable();

		try {
			this.watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.keys = new HashMap<WatchKey, Path>();

		for (String path : watchables.keySet()) {
			Path dir = Paths.get(path);
			try {
				register(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("FileWatcher is initialized");
	}

	public void setObserv(Observable observ) {
		this.observ = observ;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);
		keys.put(key, dir);
	}

	public void watch() {
		System.out.println("FileWatcher is starting to wit file changes.");

		// Start the infinite polling loop
		WatchKey key = null;
		while (true) {
			// 1. get a watch key
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			// Dequeueing events
			Kind<?> kind = null;
			// 2. process the pending event for the key
			for (WatchEvent<?> watchEvent : key.pollEvents()) {
				// 3. Get the type of the event
				kind = watchEvent.kind();
				if (OVERFLOW == kind) {
					continue; // loop
				} else if (ENTRY_CREATE == kind) {
					// A new Path was created
					Path newPath = ((WatchEvent<Path>) watchEvent).context();
					// Output
					System.out.println("New path created: " + newPath);
				} else if (ENTRY_MODIFY == kind) {
					// A new Path was created
					Path newPath = ((WatchEvent<Path>) watchEvent).context();
					// Output
					System.out.println("Path modified: " + newPath);
					List<String> lista = watchables.get(dir.toString());
					if (lista != null
							&& lista.contains(newPath.toString())) {
						FileEvent event = new FileEvent();
						event.setFileEvent(ENTRY_MODIFY);
						event.setPath(dir.toString());
						event.setFileName(newPath.toString());
//						event.setFileName(newPath);
						this.observ.notification(event);
					}
				} else if (ENTRY_DELETE == kind) {
					// A new Path was created
					Path newPath = ((WatchEvent<Path>) watchEvent).context();
					// Output
					System.out.println("Path deleted: " + newPath);
				}
			}

			if (!key.reset()) {
				break; // loop
			}
		}
	}

}

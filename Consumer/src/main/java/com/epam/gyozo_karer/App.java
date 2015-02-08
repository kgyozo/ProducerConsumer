package com.epam.gyozo_karer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import com.epam.gyozo_karer.data.WatchableFile;
import com.epam.gyozo_karer.observer.FileObservable;
import com.epam.gyozo_karer.observer.Observable;
import com.epam.gyozo_karer.observer.Observer;
import com.epam.gyozo_karer.observer.WriteOutObserver;
import com.epam.gyozo_karer.watcher.FileWatcher;

public class App 
{
    public static void main( String[] args )
    {
//        FileWatcher watcher = new FileWatcher("d://Trainings//InterviewPreparationWorkspace//test", "alma.txt");
    	WatchableFile file = new WatchableFile();
    	file.setPath("e:/Gyozo/sts-bundle");
    	file.setFileName("alma.txt");
    	List<WatchableFile> files = new LinkedList<>();
    	files.add(file);
        FileWatcher watcher = new FileWatcher(files);
        
        Observer observer = new WriteOutObserver();
        Observable observable = new FileObservable();
        observable.attach(observer, ENTRY_MODIFY);
        watcher.setObserv(observable);
        watcher.watch();
    }
}

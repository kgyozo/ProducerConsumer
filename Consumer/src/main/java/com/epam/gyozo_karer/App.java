package com.epam.gyozo_karer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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

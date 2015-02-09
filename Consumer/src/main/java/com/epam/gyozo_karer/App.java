package com.epam.gyozo_karer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.epam.gyozo_karer.data.WatchableFile;
import com.epam.gyozo_karer.file.FileHandler;
import com.epam.gyozo_karer.file.ModifiedFileHandler;
import com.epam.gyozo_karer.file.Writer;
import com.epam.gyozo_karer.observer.FileObservable;
import com.epam.gyozo_karer.observer.Observable;
import com.epam.gyozo_karer.observer.Observer;
import com.epam.gyozo_karer.observer.WriteOutObserver;
import com.epam.gyozo_karer.watcher.FileWatcher;

public class App 
{
    public static void main( String[] args )
    {
    	ApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeans.xml");
        FileWatcher watcher = (FileWatcher) ctx.getBean("fileWatcher");

        Observer observer = (WriteOutObserver) ctx.getBean("writeOutObserver");
        
        FileHandler modifiedFileHandler = (ModifiedFileHandler) ctx.getBean("modifiedFileHandler");

        observer.setModifyFileHandler(modifiedFileHandler);
        Observable observable = new FileObservable();
        observable.attach(observer, ENTRY_MODIFY);
        watcher.setObservable(observable);
        
        watcher.watch();
    }
}

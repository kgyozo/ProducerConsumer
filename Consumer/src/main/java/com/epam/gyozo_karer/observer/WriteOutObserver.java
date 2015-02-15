package com.epam.gyozo_karer.observer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.util.List;

import org.apache.log4j.Logger;

import com.epam.gyozo_karer.data.FileEvent;
import com.epam.gyozo_karer.file.FileHandler;
import com.epam.gyozo_karer.file.Writer;

public class WriteOutObserver implements Observer {
	
	final static Logger logger = Logger.getLogger(WriteOutObserver.class);

	private FileHandler modifyFileHandler;
	private Writer writer;

	public void update(FileEvent event) {

		if (ENTRY_MODIFY == event.getFileEvent()) {
			List<String> lines = modifyFileHandler.action(event);
			if (!lines.isEmpty()) {
				//DI
				writer = new Writer();
			    writer.setFile("d:/Trainings/InterviewPreparationWorkspace/test", "alma.out");
				
			    writer.writeOut(lines);
			}
		}
	}

	public FileHandler getModifyFileHandler() {
		return modifyFileHandler;
	}

	public void setModifyFileHandler(FileHandler modifyFileHandler) {
		this.modifyFileHandler = modifyFileHandler;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}
	
	
}

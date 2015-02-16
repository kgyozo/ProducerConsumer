package com.epam.gyozo_karer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;

public class Producer {
	
	final static Logger logger = Logger.getLogger(Producer.class);
	
	private Writer writer;
	private LineCreator lineCreator;
	private File file;
	private int maxCounter = 50;
	private RandomAccessFile raf;
	private ApplicationContext ctx;

	public void run() {
		Preconditions.checkNotNull(this.file, "file argument cannot be null");
        Preconditions.checkNotNull(this.writer, "writer argument cannot be null");
        Preconditions.checkNotNull(this.lineCreator, "lineCreator argument cannot be null");
        Preconditions.checkNotNull(this.ctx, "ApplicationContext argument cannot be null");
        
		int loopCounter = 0;
		
		while (loopCounter < maxCounter) {
			String line = lineCreator.createLineContext(file.getParent(), file.getName());
			FileChannel channel;
			writer.setFile(file);
			try {
				raf = (RandomAccessFile) ctx.getBean("raf");
				writer.setRaf(raf);
				channel = raf.getChannel();
				writer.setChannel(channel);
				writer.setLock(channel.lock());
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			writer.write(line);
			loopCounter++;
			try {
				if ((loopCounter < maxCounter)) {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}

	}

	public LineCreator getLineCreator() {
		return lineCreator;
	}

	public void setLineCreator(LineCreator lineCreator) {
		this.lineCreator = lineCreator;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public void setMaxCounter(int maxCounter) {
		this.maxCounter = maxCounter;
	}

	public void setRaf(RandomAccessFile raf) {
		this.raf = raf;
	}

	public void setCtx(ApplicationContext ctx) {
		this.ctx = ctx;
	}


	

}

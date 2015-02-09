package com.epam.gyozo_karer;

import org.apache.log4j.Logger;

public class Producer {
	
	final static Logger logger = Logger.getLogger(Producer.class);
	
	private String filePath;
	private String fileName;
	private Writer writer;
	private LineCreator lineCreator;

	public void run() {
		int loopCounter = 0;
		
		while (loopCounter < 50) {
			String line = lineCreator.createLineContext(filePath, fileName);
			writer.write(filePath, fileName, line);
			loopCounter++;
			try {
				Thread.sleep(1000);
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}
	
	
	

}

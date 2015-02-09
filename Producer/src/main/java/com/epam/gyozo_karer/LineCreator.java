package com.epam.gyozo_karer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

public class LineCreator {
	
	final static Logger logger = Logger.getLogger(LineCreator.class);

	public String createLineContext(String filePath, String fileName) {
		RandomAccessFile ram = null;
		FileChannel channel = null;
		FileLock lock = null;
		String newLine = null;
		try {

			File file = new File(filePath, fileName);

			ram = new RandomAccessFile(file, "rw");
			channel = ram.getChannel();

			lock = channel.lock();

			String line;
			int lineCounter = 1;
			while ((line = ram.readLine()) != null) {
				lineCounter++;
			}
			newLine = "Line number " + lineCounter + "\n";

		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				if (lock != null && lock.isValid())
					lock.release();

				if (channel != null)
					channel.close();

				if (ram != null) {
					ram.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e);
			} finally {
				return newLine;
			}
		}
	}
}

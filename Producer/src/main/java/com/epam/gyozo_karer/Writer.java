package com.epam.gyozo_karer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

public class Writer {

	final static Logger logger = Logger.getLogger(Writer.class);

	private File file = null;
	private RandomAccessFile ram = null;
	private FileChannel channel = null;
	private FileLock lock = null;

	public void write(String line) {
		logger.info(String.format("Write the into %s file", file.getName()));

		try {

			// file = new File(filePath, fileName);

			// ram = new RandomAccessFile(file, "rw");
//			channel = ram.getChannel();

//			lock = channel.lock();
			long fileLength = file.length();
			ram.seek(fileLength);

			ByteBuffer buf = ByteBuffer.allocate(20);
			buf.clear();
			buf.put(line.getBytes());
			buf.flip();

			channel.write(buf);

		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			if (/*lock != null && */lock.isValid())
				try {
					lock.release();
					channel.close();
					if (ram != null)
						ram.close();

				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e);
				}

		}
	}

	public void setRam(RandomAccessFile ram) {
		this.ram = ram;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public void setChannel(FileChannel channel) {
		this.channel = channel;
	}

	public void setLock(FileLock lock) {
		this.lock = lock;
	}



}

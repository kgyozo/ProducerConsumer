package com.epam.gyozo_karer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public class Writer {

	final static Logger logger = Logger.getLogger(Writer.class);

	private File file = null;
	private RandomAccessFile raf = null;
	private FileChannel channel = null;
	private FileLock lock = null;
	
	public Writer() {
    }
	
	public Writer(File file, RandomAccessFile raf, FileChannel channel, FileLock lock) {
        Preconditions.checkNotNull(file, "file argument cannot be null");
        Preconditions.checkNotNull(raf, "raf argument cannot be null");
        Preconditions.checkNotNull(channel, "channel argument cannot be null");
        Preconditions.checkNotNull(lock, "lock argument cannot be null");
       
        this.file = file;
        this.raf = raf;
        this.channel = channel;
        this.lock = lock;
    }

	public void write(String line) {
		Preconditions.checkNotNull(this.file, "file argument cannot be null");
        Preconditions.checkNotNull(this.raf, "raf argument cannot be null");
        Preconditions.checkNotNull(this.channel, "channel argument cannot be null");
        Preconditions.checkNotNull(this.lock, "lock argument cannot be null");
        
		logger.info(String.format("Write the into %s file", file.getName()));

		try {
			long fileLength = file.length();
			raf.seek(fileLength);

			ByteBuffer buf = ByteBuffer.allocate(20);

			buf.clear();
			buf.put(line.getBytes());
			buf.flip();

			channel.write(buf);
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		} finally {
			if (lock.isValid())
				try {
					lock.release();
					channel.close();
					if (raf != null)
						raf.close();

				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e);
				}

		}
	}

	public void setRaf(RandomAccessFile raf) {
		this.raf = raf;
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

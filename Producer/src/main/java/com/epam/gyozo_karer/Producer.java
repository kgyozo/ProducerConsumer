package com.epam.gyozo_karer;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class Producer {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		RandomAccessFile ram = null;
		FileChannel channel = null;
		FileLock lock = null;
		int loopCounter = 0;
		while (loopCounter < 50) {
			try {
				String path = "e:/Gyozo/sts-bundle/";
				String fileName = "alma.txt";
				File file = new File(path, fileName);

				ram = new RandomAccessFile(file, "rw");
				channel = ram.getChannel();

				lock = channel.lock();

				String line;
				int lineCounter = 1;
				while ((line = ram.readLine()) != null) {
					lineCounter++;
				}
				String newLine = "Line number " + lineCounter + "\n";
				System.out.print(newLine);
				ByteBuffer buf = ByteBuffer.allocate(20);
				buf.clear();
				buf.put(newLine.getBytes());

				buf.flip();

				channel.write(buf);


			} catch (IOException e) {
				System.out.println("I/O Error: " + e.getMessage());
			} finally {
				if (lock != null && lock.isValid())
					lock.release();

				if (channel != null)
					channel.close();

				if (ram != null)
					ram.close();
			}
			
			loopCounter++;
			Thread.sleep(1000);
		}

	}

}

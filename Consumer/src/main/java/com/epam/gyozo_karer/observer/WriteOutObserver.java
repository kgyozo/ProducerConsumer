package com.epam.gyozo_karer.observer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.epam.gyozo_karer.data.FileEvent;

public class WriteOutObserver implements Observer {

	private StringBuilder lastLine = null;

	public void update(FileEvent event) {
		String filePath = "e:/Gyozo/sts-bundle/";
		String fileName = "almas.out";
		System.out.printf("Modosult a file (%s), kiolvassuk es kiirjuk\n",
				event.getFileName());
		if (ENTRY_MODIFY == event.getFileEvent()) {
			try {

				List<String> lines = readLines(event);
				
				String linePattern = "^Line number [0-9]+$";
				boolean writeOut = false;
				File fileOut = new File("e:/Gyozo/sts-bundle", "almaCopy.txt");
				for (int i = 0; i < lines.size(); i++) {
					String line = lines.get(i);
					if (line.matches(linePattern)) {
						if (lastLine == null && !fileOut.exists()) {
							lastLine = new StringBuilder();
							writeOut = true;
						} else if (lastLine == null && !writeOut
								&& fileOut.exists()) {
							Path pathOut = Paths.get("e:/Gyozo/sts-bundle",
									"almaCopy.txt");
							List<String> linesFromOut = Files
									.readAllLines(pathOut);
							if (linesFromOut.size() == 0) {
								lastLine = new StringBuilder();
								writeOut = true;
							} else {
								lastLine = new StringBuilder(
										linesFromOut.get(linesFromOut.size() - 1));
								// writeOut = true;
							}
						}
						if (writeOut) {
							this.lastLine.setLength(0);
							this.lastLine.append(line);
							System.out.println(line);
						} else {
							lines.remove(i);
							System.out.println("torolve> " + line);
							i--;
						}

						if (lastLine != null && !writeOut
								&& lastLine.toString().equals(line)) {
							writeOut = true;
						}
					} else {
						lines.remove(i);
						System.out.println("torolve> " + line);
						i--;
					}
				}

				if (writeOut) {
					if (!fileOut.exists()) {
						fileOut.createNewFile();
					}
					BufferedWriter bw = null;
					try {
						FileWriter fw = new FileWriter(fileOut, true);
						bw = new BufferedWriter(fw);

						for (String line : lines) {
							bw.write(line.toString());
							bw.write("\n");
						}
					} finally {
						bw.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> readLines(FileEvent event) {
		Path path = Paths.get(event.getPath(), event.getFileName());

		File file = new File(event.getPath(), event.getFileName());
		FileInputStream fis = null;
		FileChannel channel = null;
		FileLock lock = null;
		List<String> lines = null;
		try {
			fis = new FileInputStream(file);
			channel = fis.getChannel();
			lock = channel.lock(0L, Long.MAX_VALUE, true);

			lines = Files.readAllLines(path);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				lock.release();
				channel.close();
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return lines;
	}
}

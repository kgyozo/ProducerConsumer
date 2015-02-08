package com.epam.gyozo_karer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

public class WriteOutObserver implements Observer {

	private StringBuilder lastLine = null;

	public void update(FileEvent event) {
		String filePath = "e:/Gyozo/sts-bundle/";
		String fileName = "almas.out";
		System.out.printf("Modosult a file (%s), kiolvassuk es kiirjuk\n",
				event.getFileName());
		if (ENTRY_MODIFY == event.getFileEvent()) {
			try {
				Path path = Paths.get(event.getPath(), event.getFileName());

				File file = new File(event.getPath(), event.getFileName());
				FileInputStream fis = new FileInputStream(file);
				FileChannel channel = fis.getChannel();
				FileLock lock = channel.lock(0L, Long.MAX_VALUE, true);

				List<String> lines = Files.readAllLines(path);

				lock.release();
				channel.close();
				fis.close();

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

				// File fileOut = new File("e:/Gyozo/sts-bundle",
				// "almaCopy.txt");
				if (writeOut) {
					if (!fileOut.exists()) {
						fileOut.createNewFile();
					}
					BufferedWriter bw = null;
					try {
						FileWriter fw = new FileWriter(fileOut, true);
						// BufferedWriter writer give better performance
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public FileOutputStream openOutputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file
						+ "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file
							+ "' could not be created");
				}
			}
		}
		return new FileOutputStream(file);
	}
}

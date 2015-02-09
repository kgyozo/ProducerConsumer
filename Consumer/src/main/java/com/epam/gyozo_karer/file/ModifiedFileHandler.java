package com.epam.gyozo_karer.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.epam.gyozo_karer.data.FileEvent;

public class ModifiedFileHandler implements FileHandler {

	private StringBuilder lastLine = null;
	private String linePattern = "^Line number [0-9]+$";
	private String filePath;
	private String fileName;

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLinePattern() {
		return linePattern;
	}

	public void setLinePattern(String linePattern) {
		this.linePattern = linePattern;
	}

	@Override
	public List<String> action(FileEvent event) {

		List<String> lines = null;
		lines = readLines(event);
		boolean writeOut = false;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.matches(this.linePattern)) {
				if (isAfterServiceStartFileDoesNOTExist()) {
					this.lastLine = new StringBuilder();
					writeOut = true;
				} else if (isAfterServiceStartFileDoesExist(writeOut)) {
					writeOut = startPreparationIfOutputFileExists(writeOut);
				}

				if (writeOut) {
					this.lastLine.setLength(0);
					this.lastLine.append(line);
				} else {
					lines.remove(i);
					i--;
				}

				if (lastLine != null && !writeOut
						&& lastLine.toString().equals(line)) {
					writeOut = true;
				}
			} else {
				lines.remove(i);
				i--;
			}
		}

		return lines;
	}

	private boolean isAfterServiceStartFileDoesNOTExist() {
		File fileOut = new File(filePath, fileName);
		return lastLine == null && !fileOut.exists();
	}

	private boolean isAfterServiceStartFileDoesExist(boolean writeOut) {
		File fileOut = new File(filePath, fileName);
		return (lastLine == null && !writeOut && fileOut.exists());
	}

	private boolean startPreparationIfOutputFileExists(boolean writeOut) {
		Path pathOut = Paths.get(filePath, fileName);
		List<String> linesFromOut = null;
		try {
			linesFromOut = Files.readAllLines(pathOut);
			if (linesFromOut.size() == 0) {
				this.lastLine = new StringBuilder();
				writeOut = true;
			} else {
				this.lastLine = new StringBuilder(linesFromOut.get(linesFromOut
						.size() - 1));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return writeOut;
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				lock.release();
				channel.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return lines;
	}
}

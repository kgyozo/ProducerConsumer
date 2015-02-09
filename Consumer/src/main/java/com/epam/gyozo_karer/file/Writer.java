package com.epam.gyozo_karer.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writer {

	private String filePath;
	private String fileName;

	public void setFile(String filePath, String fileName) {
		this.fileName = fileName;
		this.filePath = filePath;
	}

	public void writeOut(List<String> lines) {
		File fileOut = new File(filePath, fileName);
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

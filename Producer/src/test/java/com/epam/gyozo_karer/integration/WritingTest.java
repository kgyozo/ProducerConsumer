package com.epam.gyozo_karer.integration;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.epam.gyozo_karer.Producer;

public class WritingTest {

	private Producer producer;
	private ApplicationContext ctx;
	private File outputFile;

	private final static String WRONG_LINE = "Not match tha pattern";
	private final static String GOOD_LINE_PREFIX = "Line number ";

	@Before
	public void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext("SpringBeansTest.xml");

		this.outputFile = new File((String) ctx.getBean("outputFilePath"),
				(String) ctx.getBean("outputFileName"));
		writeOutput(null, false);

		this.producer = (Producer) ctx.getBean("producer");
		producer.setCtx(ctx);
	}

	@Test
	public void testWriteInANewFile() {
		this.producer.run();
		List<String> lines = readLines();
		Assert.assertEquals(1, lines.size());
		Assert.assertEquals(GOOD_LINE_PREFIX + "1", lines.get(0));
	}

	@Test
	public void testWriteInExistingFile() {
		List<String> content = new LinkedList<>();
		content.add(GOOD_LINE_PREFIX + "1");
		writeOutput(content, false);

		this.producer.run();
		List<String> lines = readLines();
		Assert.assertEquals(2, lines.size());
		Assert.assertEquals(GOOD_LINE_PREFIX + "2", lines.get(1));
	}
	
	@Test
	public void testWriteInExistingFileButNotJustCorrectLines() {
		List<String> content = new LinkedList<>();
		content.add(GOOD_LINE_PREFIX + "1");
		content.add(WRONG_LINE);
		writeOutput(content, false);

		this.producer.run();
		
		List<String> lines = readLines();
		Assert.assertEquals(3, lines.size());
		Assert.assertEquals(GOOD_LINE_PREFIX + "3", lines.get(2));
	}

	private void writeOutput(List<String> content, boolean append) {

		String path = (String) ctx.getBean("outputFilePath");
		String name = (String) ctx.getBean("outputFileName");
		File file = new File(path, name);

		try {
			FileWriter fw;

			fw = new FileWriter(outputFile.getAbsoluteFile(), append);
			BufferedWriter bw = new BufferedWriter(fw);
			if (content == null) {
				bw.write("");
			} else {
				for (String line : content) {
					bw.write(line + "\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> readLines() {
		String outputPath = (String) ctx.getBean("outputFilePath");
		String name = (String) ctx.getBean("outputFileName");
		Path path = Paths.get(outputPath, name);

		List<String> lines = null;
		try {
			lines = Files.readAllLines(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}

}

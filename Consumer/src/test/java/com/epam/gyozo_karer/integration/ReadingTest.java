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

import com.epam.gyozo_karer.file.FileHandler;
import com.epam.gyozo_karer.file.ModifiedFileHandler;
import com.epam.gyozo_karer.observer.FileObservable;
import com.epam.gyozo_karer.observer.Observable;
import com.epam.gyozo_karer.observer.Observer;
import com.epam.gyozo_karer.observer.WriteOutObserver;
import com.epam.gyozo_karer.watcher.FileWatcher;

public class ReadingTest {

	private FileWatcher watcher;
	private File inputPath;
	private ApplicationContext ctx;
	private File inputFile;
	private File outputFile;
	
	private final static String WRONG_LINE= "Not match tha pattern";
	private final static String GOOD_LINE_PREFIX= "Line number ";

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext("SpringBeansTest.xml");

		this.inputPath = new File((String) ctx.getBean("watchableFilePath"));
		this.outputFile = new File((String) ctx.getBean("outputFilePath"), (String) ctx.getBean("outputFileName"));
		this.watcher = (FileWatcher) ctx.getBean("fileWatcher");
		Observer observer = (WriteOutObserver) ctx.getBean("writeOutObserver");

		FileHandler modifiedFileHandler = (ModifiedFileHandler) ctx
				.getBean("modifiedFileHandler");

		observer.setModifyFileHandler(modifiedFileHandler);
		Observable observable = new FileObservable();
		observable.attach(observer, ENTRY_MODIFY);
		watcher.setObservable(observable);
	}

	@After
	public void tearDown() throws Exception {
		inputFile.delete();
		outputFile.delete();
	}

	@Test
	public void testWriteInANewFile() {
		List<String> content = new LinkedList<>();
		content.add(GOOD_LINE_PREFIX + "1");

		(new Thread() {
			public void run() {
				watcher.watch();
			}
		}).start();

		try {
			Thread.sleep(1000);
			newInputFile();
			writeInput(content);
			
			Thread.sleep(1000);
			
			List<String> lines = readLines();
			
			Assert.assertEquals(1, lines.size());
			Assert.assertEquals(GOOD_LINE_PREFIX + "1", lines.get(0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testMultipleWriteInANewFile() {
		List<String> content = new LinkedList<>();
		content.add(GOOD_LINE_PREFIX + "1");
		content.add(WRONG_LINE);
		content.add(GOOD_LINE_PREFIX + "3");

		(new Thread() {
			public void run() {
				watcher.watch();
			}
		}).start();

		try {
			Thread.sleep(1000);
			newInputFile();
			writeInput(content);
			
			Thread.sleep(1000);
			
			List<String> lines = readLines();
			
			Assert.assertEquals(2, lines.size());
			Assert.assertEquals(GOOD_LINE_PREFIX + "1", lines.get(0));
			Assert.assertEquals(GOOD_LINE_PREFIX + "3", lines.get(1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testWriteInANewFileWrongLine() {
		List<String> content = new LinkedList<>();
		content.add(WRONG_LINE);

		(new Thread() {
			public void run() {
				watcher.watch();
			}
		}).start();

		try {
			Thread.sleep(1000);
			newInputFile();
			writeInput(content);
			Assert.assertFalse(outputFile.exists());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testMultipleLinesWriteInAnExistingFileButOutputFileDoesNotExist() {
		List<String> content = new LinkedList<>();
		content.add(GOOD_LINE_PREFIX + "1");
		content.add(WRONG_LINE);
		content.add(GOOD_LINE_PREFIX + "3");
		newInputFile();
		writeInput(content);
		
		content.clear();
		content.add(GOOD_LINE_PREFIX + "4");
		content.add(GOOD_LINE_PREFIX + "5");

		(new Thread() {
			public void run() {
				watcher.watch();
			}
		}).start();

		try {
			Thread.sleep(1000);
			writeInput(content);
			
			Thread.sleep(1000);
			
			List<String> lines = readLines();
			
			Assert.assertEquals(4, lines.size());
			Assert.assertEquals(GOOD_LINE_PREFIX + "1", lines.get(0));
			Assert.assertEquals(GOOD_LINE_PREFIX + "3", lines.get(1));
			Assert.assertEquals(GOOD_LINE_PREFIX + "4", lines.get(2));
			Assert.assertEquals(GOOD_LINE_PREFIX + "5", lines.get(3));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testMultipleLinesWriteInAnExistingFileButOutputFileExist() {
		List<String> content = new LinkedList<>();
		content.add(GOOD_LINE_PREFIX + "1");
		content.add(WRONG_LINE);
		content.add(GOOD_LINE_PREFIX + "3");
		newInputFile();
		writeInput(content);
		
		content.remove(1);
		writeOutput(content);
		
		content.clear();
		content.add(GOOD_LINE_PREFIX + "4");
		content.add(GOOD_LINE_PREFIX + "5");

		(new Thread() {
			public void run() {
				watcher.watch();
			}
		}).start();

		try {
			Thread.sleep(1000);
			writeInput(content);
			
			Thread.sleep(1000);
			
			List<String> lines = readLines();
			
			Assert.assertEquals(4, lines.size());
			Assert.assertEquals(GOOD_LINE_PREFIX + "1", lines.get(0));
			Assert.assertEquals(GOOD_LINE_PREFIX + "3", lines.get(1));
			Assert.assertEquals(GOOD_LINE_PREFIX + "4", lines.get(2));
			Assert.assertEquals(GOOD_LINE_PREFIX + "5", lines.get(3));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testMultipleLinesWriteInANewFileManyTimes() {
		List<String> content = new LinkedList<>();
		content.add(GOOD_LINE_PREFIX + "1");
		content.add(WRONG_LINE);
		content.add(GOOD_LINE_PREFIX + "3");

		(new Thread() {
			public void run() {
				watcher.watch();
			}
		}).start();

		try {
			Thread.sleep(1000);
			newInputFile();
			writeInput(content);
			
			Thread.sleep(1000);
			
			List<String> lines = readLines();
			
			Assert.assertEquals(2, lines.size());
			Assert.assertEquals(GOOD_LINE_PREFIX + "1", lines.get(0));
			Assert.assertEquals(GOOD_LINE_PREFIX + "3", lines.get(1));
			
			content.clear();
			content.add(WRONG_LINE);
			content.add(GOOD_LINE_PREFIX + "5");
			writeInput(content);
			
			Thread.sleep(1000);
			lines = readLines();
			
			Assert.assertEquals(3, lines.size());
			Assert.assertEquals(GOOD_LINE_PREFIX + "5", lines.get(2));

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}


	private void newInputFile() {
		String path = (String) ctx.getBean("watchableFilePath");
		String name = (String) ctx.getBean("watchableFileName");
		this.inputFile = new File(path, name);

		try {
			this.inputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeInput(List<String> content) {

		String path = (String) ctx.getBean("watchableFilePath");
		String name = (String) ctx.getBean("watchableFileName");
		File file = new File(path, name);

		try {
			FileWriter fw;

			fw = new FileWriter(inputFile.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (String line : content) {
				bw.write(line + "\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void writeOutput(List<String> content) {

		String path = (String) ctx.getBean("outputFilePath");
		String name = (String) ctx.getBean("outputFileName");
		File file = new File(path, name);

		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw;

			fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (String line : content) {
				bw.write(line + "\n");
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

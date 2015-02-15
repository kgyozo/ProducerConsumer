package com.epam.gyozo_karer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({AFinalClass.class, FileChannel.class})
public class WriterTest {
	
	private static final String TEST_LINE = "blabla";
	
	File outputFile;
	RandomAccessFile raf;

	@Test
	public void successWriteOut() {
		System.out.println("successWriteOut");
		
		outputFile = Mockito.mock(File.class);
		raf = Mockito.mock(RandomAccessFile.class);
		FileChannel channel = PowerMockito.mock(FileChannel.class);
		FileLock lock = Mockito.mock(FileLock.class);
		try {
			Mockito.when(channel.write(ByteBuffer.allocate(20))).thenReturn(20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Writer writer = new Writer();
		writer.setFile(outputFile);
		writer.setRam(raf);
		writer.setChannel(channel);
		writer.setLock(lock);
		writer.write("blabla");
		
		Mockito.verify(outputFile, Mockito.times(1)).length();
	}
	
	@Test
	public void seekProblemDuringWriteOut() {
		System.out.println("seekProblemDuringWriteOut");
		
		outputFile = Mockito.mock(File.class);
		raf = Mockito.mock(RandomAccessFile.class);
		FileChannel channel = PowerMockito.mock(FileChannel.class);
		FileLock lock = Mockito.mock(FileLock.class);
		boolean exception = false;
		try {
			Mockito.doThrow(new IOException()).when(raf).seek(0);
		} catch (IOException e) {
			exception = true;
		}
		
		Writer writer = new Writer();
		
		writer.setFile(outputFile);
		writer.setRam(raf);
		writer.setChannel(channel);
		writer.setLock(lock);
		writer.write(TEST_LINE);
		assertFalse("seek does not handle exception", exception);
	}
	
	@Test
	public void lockReleaseProblemDuringWriteOut() {
		System.out.println("lockReleaseProblemDuringWriteOut");
		
		outputFile = Mockito.mock(File.class);
		raf = Mockito.mock(RandomAccessFile.class);
		FileChannel channel = PowerMockito.mock(FileChannel.class);
		FileLock lock = Mockito.mock(FileLock.class);
		boolean exception = false;
		try {
			Mockito.when(channel.write(ByteBuffer.allocate(20))).thenReturn(20);
			Mockito.when(lock.isValid()).thenReturn(true);
			Mockito.doThrow(new IOException()).when(lock).release();
		} catch (IOException e) {
			exception = true;
		}
		Writer writer = new Writer();
		writer.setFile(outputFile);
		writer.setRam(raf);
		writer.setChannel(channel);
		writer.setLock(lock);
		writer.write(TEST_LINE);
		assertFalse("lock release does not handle exception", exception);
	}
	
	@Test
	public void channelCloseProblemDuringWriteOut() {
		System.out.println("channelCloseProblemDuringWriteOut");
		
		outputFile = Mockito.mock(File.class);
		raf = Mockito.mock(RandomAccessFile.class);
		FileChannel channel = PowerMockito.mock(FileChannel.class);

		FileLock lock = Mockito.mock(FileLock.class);
		boolean exception = false;
		try {
			Mockito.when(channel.write(ByteBuffer.allocate(20))).thenReturn(20);
			Mockito.when(lock.isValid()).thenReturn(true);
			channel.close();
			PowerMockito.doThrow(new IOException()).when(channel).close();
		} catch (IOException e) {
			e.printStackTrace();
			exception = true;
		}
		Writer writer = new Writer();
		writer.setFile(outputFile);
		writer.setRam(raf);
		writer.setChannel(channel);
		writer.setLock(lock);
		writer.write(TEST_LINE);
		assertFalse("channel close does not handle exception", exception);
	}
	
	@Test
	public void rafCloseProblemDuringWriteOut() {
		System.out.println("rafCloseProblemDuringWriteOut");
		
		outputFile = Mockito.mock(File.class);
		raf = Mockito.mock(RandomAccessFile.class);
		FileChannel channel = PowerMockito.mock(FileChannel.class);
		FileLock lock = Mockito.mock(FileLock.class);
		boolean exception = false;
		try {
			Mockito.when(channel.write(ByteBuffer.allocate(20))).thenReturn(20);
			Mockito.when(lock.isValid()).thenReturn(true);
			Mockito.doThrow(new IOException()).when(raf).close();
		} catch (IOException e) {
			e.printStackTrace();
			exception = true;
		}
		Writer writer = new Writer();
		writer.setFile(outputFile);
		writer.setRam(raf);
		writer.setChannel(channel);
		writer.setLock(lock);
		writer.write(TEST_LINE);
		assertFalse("file close does not handle exception", exception);
	}

}

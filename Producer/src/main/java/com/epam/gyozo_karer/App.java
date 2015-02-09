package com.epam.gyozo_karer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"SpringBeans.xml");
		Producer producer = (Producer) ctx.getBean("producer");
		producer.run();
	}
}

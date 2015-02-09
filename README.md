Implement two applications in java. The name of the applications are Producer and Consumer.
The Producer writes into a text file a line with the content "Line number N" where N is the sequence of line that has been written.
The Consumer listens to change in the text file and writes the read lines in a new file

(Note: you can use Java 8 new capabilities for listening to file change notifications)

This needs to be properly tested. We want the tests implemented in JUnit. We are particularly interested to check the processes can communicate correctly across the file

Please, provide a Java project with Maven with the solution to the problem

UserGuide
=========

There are 2 projects (Consumer, Producer).

The Producer application generate 50 messages with 1 minute per 1 message speed. After 50 messages the application stops.
The Consumer application waits for the file changes. you need to force stop the application.

You need to compile the applications one by one.
compile : mvn clean assembly:assembly
execute : java -jar <name>-jar-with-dependencies.jar
PITest: mvn org.pitest:pitest-maven:mutationCoverage

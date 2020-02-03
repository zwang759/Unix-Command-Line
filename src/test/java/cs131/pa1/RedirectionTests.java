package cs131.pa1;


import cs131.pa1.filter.Message;
import cs131.pa1.filter.concurrent.ConcurrentREPL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class RedirectionTests {

	@Test
	public void testCatRedirected(){
		testInput("cat hello-world.txt > new-hello-world.txt\nexit");
		ConcurrentREPL.main(null);
		assertFileContentsEquals("new-hello-world.txt", "hello\nworld\n");
		assertOutput(Message.NEWCOMMAND.toString());
		AllConcurrentTests.destroyFile("new-hello-world.txt");
	}
	
	@Test
	public void testComplexRedirection(){
		testInput("cat fizz-buzz-10000.txt | grep F | wc > trial-file.txt\nexit");
		ConcurrentREPL.main(null);
		assertFileContentsEquals("trial-file.txt", "3334 3334 16004\n");
		assertOutput(Message.NEWCOMMAND.toString());
		AllConcurrentTests.destroyFile("trial-file.txt");
	}
	
	@Test
	public void testDirectoryShiftedRedirection() throws FileNotFoundException{
		testInput("cd dir1\nls > folder-contents.txt\nexit");
		ConcurrentREPL.main(null);
		Set<String> expected = new HashSet<String>();
		expected.add("dir2");
		expected.add("f1.txt");
		Set<String> output = new HashSet<String>();
		File f = new File("dir1/folder-contents.txt");
		try {
			Scanner sc = new Scanner(f);
			while (sc.hasNextLine()) {
				String line = sc.nextLine().replaceAll(Message.NEWCOMMAND.toString(), "");
				if (!line.equals(Message.WELCOME.toString()) && !line.equals(Message.GOODBYE.toString()) && !line.equals("")) 
					output.add(line);
			}
			sc.close();
		} catch (Exception e) {
			throw new FileNotFoundException("The dir1/folder-contents.txt file was not found");
		}
		
		try {
			assertEquals(expected, output);
		} catch (AssertionError e) {
			expected.add("folder-contents.txt");
			assertEquals(expected, output);
		}
		assertOutput(Message.NEWCOMMAND.toString() + Message.NEWCOMMAND.toString());
		AllConcurrentTests.destroyFile("dir1/folder-contents.txt");
	}
	
	private static void assertFileContentsEquals(String fileName, String expected){
		File f = new File(fileName);
		try {
			Scanner scan = new Scanner(f);
			String result = "";
			while (scan.hasNextLine()){
				result += scan.nextLine() + "\n";
			}
			scan.close();
			assertEquals(expected, result);
		} catch (FileNotFoundException e) {
			assertTrue(false);
		}
	}
	
	// Boilerplate, standard across test case files.
	
	private ByteArrayInputStream inContent;
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	
	public void testInput(String s){
		inContent = new ByteArrayInputStream(s.getBytes());
		System.setIn(inContent);
	}
	
	public void assertOutput(String expected){
                AllConcurrentTests.assertOutput(expected, outContent);
	}
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@After
	public void cleanUpStreams() {
		System.setIn(null);
	    System.setOut(null);
	    System.setErr(null);
	}
}

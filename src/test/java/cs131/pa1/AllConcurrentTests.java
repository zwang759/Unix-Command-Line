package cs131.pa1;

import cs131.pa1.filter.Message;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({RedirectionTests.class, REPLTests.class, TextProcessingTests.class, WorkingDirectoryTests.class})
public class AllConcurrentTests {
	
	final static boolean DEBUGGING_MODE = false;

    @BeforeClass
    public static void setUp() {
    	createFile("empty.txt", "");
    	createFile("hello.txt", "HELLO");
    	createFile("world.txt", "WORLD");
    	createFile("hello-world.txt", "hello\nworld");
    	createFile("fizz-buzz-10.txt", generateFizzBuzz(10));
    	createFile("fizz-buzz-100.txt", generateFizzBuzz(100));
    	createFile("fizz-buzz-10000.txt", generateFizzBuzz(10000));
    	createFile("fizz-buzz-1500000.txt", generateFizzBuzz(1500000));
    	createFile("ascii.txt", generateASCII());
    	createFile("pi.txt", generatePi());
    	
    	File f = new File("dir1/dir2/dir3/dir4");
    	f.mkdirs();
	createFile("dir1/f1.txt", "FILE 1\nTHIS IS THE FIRST FILE.\nI HOPE YOU LIKE IT\n\n\nYOU DO????");
	createFile("dir1/dir2/f2.txt", "FILE 2\nTHIS IS THE SECOND FILE.\nIT IS PRETTY SIMILAR\nI HOPE YOU LIKE IT\n\n\nDO YOU????");
	createFile("dir1/dir2/dir3/dir4/f4.txt", "FILE 1\nTHIS IS THE LAST FILE.\nI HOPE YOU LIKED IT\n\n\nDID YOU????");
    	
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	if (!AllConcurrentTests.DEBUGGING_MODE){
	    	String[] files = {"folder-contents.txt","hello.txt", "hello2.txt", "world.txt", "hello-world.txt", "fizz-buzz-100.txt",
					"fizz-buzz-10000.txt", "fizz-buzz-1500000.txt", "fizz-buzz-5000000.txt", 
					"replTest1.txt", "replTest2.txt", "replTest3.txt", "killTest1.txt", "killTest2.txt", "killTest3.txt", "killTest4.txt", "replTest3.txt",
					"ascii.txt", "empty.txt","pi.txt","fizz-buzz-10.txt"};
	    	for (String fileName : files){
	    		File f = new File(fileName);
	    		f.delete();
	    	}
			recursivelyDeleteFolders(new File("dir1"));
			
	    	// Test to see if all files were properly deleted
			for (String fileName : files){
	    		File f = new File(fileName);
	    		if (f.exists()) {
//	    			throw new Exception("File " + fileName + " should have been deleted");
	    		}
	    	}
		}    	
    }
    
	private static void recursivelyDeleteFolders(File f){
		for(File sub : f.listFiles()){
			if (sub.isDirectory()){
				recursivelyDeleteFolders(sub);
			} else {
				sub.delete();
			}
		}
		f.delete();
	}

    private static void createFile(String fileName, String content){
    	File f = new File(fileName);
    	PrintWriter pw;
		try {
			pw = new PrintWriter(f);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("This should not happen; we are creating a new file.");
		}
    	pw.print(content);
    	pw.close();
    }
    
    public static void assertOutput(String expected, ByteArrayOutputStream outContent){
		String result = outContent.toString().replace("\r", "");
		expected = String.format("%s%s%s%s", Message.WELCOME, expected, Message.NEWCOMMAND, Message.GOODBYE);
		assertEquals(expected, result);
	}
    
    private static String generateFizzBuzz(int max){
    	StringBuffer sb = new StringBuffer();
    	for(int i = 0; i <= max; i++){
    		if (i % 3 == 0 && i % 5 == 0){
    			sb.append("FizzBuzz\n");
    		} else if (i % 3 == 0){
    			sb.append("Fizz\n");
    		} else if (i % 5 == 0){
    			sb.append("Buzz\n");
    		} else {
    			sb.append(i + "\n");
    		}
    	}
    	return sb.toString();
    }
    
    private static String generateASCII() {
    		StringBuffer sb = new StringBuffer();
    		for(int i = 32; i <= 126; i++) {
    			if (i != 62 && i != 124) 
    				sb.append((char)i + "\n");
    		}
    		return sb.toString();
    }
    
    private static String generatePi() {
    		return "Pi\nis\n3\n.\n1\n4\n1\n5\n9\n2\n6\n5\n3\n5\n9\n.\n.\n.\n";
    }
    
    
    
	// Cleanup message
	public static void destroyFile(String fileName){
		if (!DEBUGGING_MODE) {
			File f = new File(fileName);
			if (f.exists()){
				f.delete();
			}
		}
	}
	
    
}
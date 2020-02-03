package cs131.pa1.filter.concurrent;

import java.io.File;

import cs131.pa1.filter.Filter;
import cs131.pa1.filter.Message;

/**
 * The filter for cd command
 * @author cs131a
 *
 */
public class CdFilter extends ConcurrentFilter {
	/**
	 * The directory to change to
	 */
	private String dirToSet;
	
	/**
	 * The constructor of the cd filter
	 * @param line the arguments for the cd command
	 * @throws Exception throws exception when there is an error in the parameters, or 
	 * when the specified directory is not found
	 */
	public CdFilter(String line) throws Exception {
		super();
		dirToSet = ConcurrentREPL.currentWorkingDirectory;
		String[] args = line.trim().split(" ");
		if(args.length == 1) {
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), line.trim());
			throw new Exception();
		}
		if(args[1].equals("..")) {
			String current = ConcurrentREPL.currentWorkingDirectory;
			current = current.substring(0, current.lastIndexOf(Filter.FILE_SEPARATOR));
			dirToSet = current;
		} else if (!args[1].equals(".")) {
			String current = ConcurrentREPL.currentWorkingDirectory;
			current = current + Filter.FILE_SEPARATOR + args[1];
			File test = new File(current);
			if (test.isDirectory()) {
				dirToSet = current;
			} else {
				System.out.printf(Message.DIRECTORY_NOT_FOUND.toString(), line);
				throw new IllegalArgumentException();
			}
		}
	}
	
	/**
	 * Simply calls processLine with an empty parameter
	 */
	public void process() {
		if(!exit) {
			processLine("");
		}
		IsDone = true;
	}
	
	/**
	 * sets the currentWorkingDirectory field of ConcurrentREPL to the specified directory.
	 */
	public String processLine(String line) {
		ConcurrentREPL.currentWorkingDirectory = dirToSet;
		return null;
	}
}

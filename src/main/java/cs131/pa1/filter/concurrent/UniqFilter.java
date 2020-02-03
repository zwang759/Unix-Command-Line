package cs131.pa1.filter.concurrent;

import java.util.HashSet;
/**
 * The filter for uniq command
 * @author cs131a
 *
 */
public class UniqFilter extends ConcurrentFilter{
	private HashSet<String> existingStringSet;
	//This set will record what strings are existing
	/**
	 * The constructor of the uniq filter
	 */
	public UniqFilter (){
		existingStringSet = new HashSet<String> ();
	}

	/**
	 * processes a line from the input queue and returns it if it is not found before
	 * @param line the line as got from the input queue
	 * @return the line if it was not found before, null otherwise 
	 */
	public String processLine(String line) {
		if(existingStringSet.contains(line)) {
			return null;
		}else {
			existingStringSet.add(line);
			return line;
		}
	}
}

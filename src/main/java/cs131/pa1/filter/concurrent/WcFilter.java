package cs131.pa1.filter.concurrent;
/**
 * The filter for wc command
 * @author cs131a
 *
 */
public class WcFilter extends ConcurrentFilter {
	/**
	 * The count of lines found
	 */
	private int linecount;
	/**
	 * The count of words found
	 */
	private int wordcount;
	/**
	 * The count of characters found
	 */
	private int charcount;
	
	public WcFilter() {
		super();
		linecount = 0;
		wordcount = 0;
		charcount = 0;
	}

	@Override
	/**
	 * Counts the number of lines, words and characters from the input queue
	 */
	public void process() {
		while (!exit && (!prev.isDone() || !input.isEmpty())) {
			while (!input.isEmpty() && !exit ) {
					String line;
					try {
						line = input.take();
						linecount++;
						charcount += line.length();
						if (line.length() > 0) {
							String[] words = line.trim().split("\\s+");
							for (String word : words) {
								if (!word.equals(" ") && word.length() > 0) {
									wordcount++;
								}
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}
		if (!exit) {
			try {
				output.put(linecount + " "
						+ wordcount + " "
						+ charcount);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		IsDone = true;
	}

	@Override
	protected String processLine(String line) {
		return null;
	}
}

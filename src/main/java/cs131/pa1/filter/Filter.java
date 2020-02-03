package cs131.pa1.filter;

/**
 * The abstract class that represents a filter to perform shell commands
 * @author cs131a
 *
 */
public abstract class Filter {
    
	/**
	 * The file separator used for generating paths. It is got as a system property.
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	/**
	 * The next filter
	 */
	protected Filter next;
	/**
	 * The previous filter
	 */
	protected Filter prev;
	
	/**
	 * sets the next filter as the specified filter
	 * @param next the next filter to be set
	 */
	public abstract void setNextFilter(Filter next);
	/**
	 * sets the previous filter as the specified filter
	 * @param prev the previous filter to be set
	 */
	public abstract void setPrevFilter(Filter prev);
	/**
	 * Specifies whether the filter is done processing or not
	 * @return true if the filter is done, false otherwise
	 */
	public abstract boolean isDone();
	
}

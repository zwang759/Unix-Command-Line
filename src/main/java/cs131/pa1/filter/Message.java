package cs131.pa1.filter;

/**
 * The Enum to hold all messages that the shell should display
 * @author cs131a
 *
 */
public enum Message {
    WELCOME("Welcome to the Unix-ish command line.\n"),
    NEWCOMMAND("> "),
    GOODBYE("Thank you for using the Unix-ish command line. Goodbye!\n"),
    FILE_NOT_FOUND("At least one of the files in the command [%s] was not found.\n"),
    DIRECTORY_NOT_FOUND("The directory specified by the command [%s] was not found.\n"),
    COMMAND_NOT_FOUND("The command [%s] was not recognized.\n"),
    REQUIRES_INPUT("The command [%s] requires input.\n"),
    CANNOT_HAVE_OUTPUT("The command [%s] cannot have an output.\n"),
    REQUIRES_PARAMETER("The command [%s] requires parameter(s).\n"),
    INVALID_PARAMETER("The parameter for command [%s] is invalid.\n"),
    CANNOT_HAVE_INPUT("The command [%s] cannot have an input.\n")
    ;
	/**
	 * The message to be displayed
	 */
    private final String message;
    /**
     * Constructs a Message object
     * @param message the message to be specified
     */
    private Message(String message){
        this.message=message;
    }
    /**
     * Returns the string representation of the message
     * @return the string representation of the message
     */
    public String toString(){
        return this.message;
    }
    /**
     * sets a specified parameter to the message
     * @param parameter the parameter to be set in the message
     * @return the string to be displayed that includes the specified parameter
     */
    public String with_parameter(String parameter){
        if(this==WELCOME || this==NEWCOMMAND || this == GOODBYE){
            return this.toString();
        }
        return String.format(this.message, parameter.trim());
    }
    
}

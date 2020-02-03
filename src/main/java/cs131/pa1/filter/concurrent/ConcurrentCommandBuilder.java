package cs131.pa1.filter.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cs131.pa1.filter.Message;

/**
 * This class manages the parsing and execution of a command.
 * It splits the raw input into separated subcommands, 
 * creates subcommand filters, and links them into a list. 
 * @author cs131a
 *
 */
public class ConcurrentCommandBuilder {
	
	/**
	 * Creates and returns a list of filters from the specified command
	 * @param command the command to create filters from
	 * @return the list of ConcurrentFilter that represent the specified command
	 */
	public static ConcurrentFilter createFiltersFromCommand(String command){
		//initialize the list that will hold all of the filters
				List<ConcurrentFilter> filters = new LinkedList<ConcurrentFilter>();
				//adding whitespace so that string splitting doesn't bug
				command = " " + command + " ";
				//removing the final filter here
				String truncCommand = adjustCommandToRemoveFinalFilter(command);
				if(truncCommand == null) {
					return null;
				}
				//for all the commands, split them by pipes, construct each filter, and add them to the filters list.
				String[] commands = truncCommand.split("\\|");
				for(int i = 0; i < commands.length; i++) {
					ConcurrentFilter filter = constructFilterFromSubCommand(commands[i].trim());
					if(filter != null) {
						filters.add(filter);
					} else {
						return null;
					}
				}
				
				ConcurrentFilter fin = determineFinalFilter(command);
				if(fin == null) {
					return null;
				}
				filters.add(fin);
				
				if(linkFilters(filters, command)){
					return filters.get(0);
				} else {
					return null;
				}
	}
	/**
	 * Returns the filter that appears last in the specified command
	 * @param command the command to search from
	 * @return the ConcurrentFilter that appears last in the specified command
	 */
	private static ConcurrentFilter determineFinalFilter(String command){
		String[] redir = command.split(">");
		if(redir.length == 1) {
			return new PrintFilter();
		} else {
			try{
				return new RedirectFilter("> " + redir[1]);
			} catch (Exception e) {
				return null;
			}
		}
	}
	/**
	 * Returns a string that contains the specified command without the final filter
	 * @param command the command to parse and remove the final filter from 
	 * @return the adjusted command that does not contain the final filter
	 */
	private static String adjustCommandToRemoveFinalFilter(String command){
		String[] removeRedir = command.split(">");
		//checking for error cases here. If there is a redirection...
		if(removeRedir.length > 1) {
			//if the redirection does not have an input, then output an error
			if(removeRedir[0].trim().equals("")) {
				System.out.printf(Message.REQUIRES_INPUT.toString(), (">" + removeRedir[1]).trim());
				return null;
			}
			//if redirection is attempted to be piped, output an error
			if(removeRedir[1].contains("|")) {
				System.out.printf(Message.CANNOT_HAVE_OUTPUT.toString(), ">" + removeRedir[1].substring(0, removeRedir[1].indexOf("|")));
				return null;
			}
			//if multiple redirections are in the command, output an error
			if(removeRedir.length > 2) {
				System.out.printf(Message.CANNOT_HAVE_OUTPUT.toString(), removeRedir[1].trim());
				return null;
			}
		}
		return removeRedir[0];
	}
	/**
	 * Creates a single filter from the specified subCommand
	 * @param subCommand the command to create a filter from
	 * @return the ConcurrentFilter created from the given subCommand
	 */
	private static ConcurrentFilter constructFilterFromSubCommand(String subCommand){
		String[] commandextract = subCommand.split(" ");
		ConcurrentFilter filter;
		try {
			switch (commandextract[0]) {
				case "cat":
					filter = new CatFilter(subCommand);
					break;
				case "cd":
					filter = new CdFilter(subCommand);
					break;
				case "ls":
					filter = new LsFilter();
					break;
				case "pwd":
					filter = new PwdFilter();
					break;
				case "grep":
					filter = new GrepFilter(subCommand);
					break;
				case "wc":
					filter = new WcFilter();
					break;
				case "uniq":
					filter = new UniqFilter();
					break;
				default:
					System.out.printf(Message.COMMAND_NOT_FOUND.toString(), subCommand);
					return null;
			}
		} catch (Exception e) {
			return null;
		}
		return filter;
	}
	/**
	 * links the given filters with the order they appear in the list
	 * @param filters the given filters to link
	 * @param command the initial command, so that to display proper error messages 
	 * @return true if the link was successful, false if there were errors encountered.
	 * Any error should be displayed by using the Message enum.
	 */
	private static boolean linkFilters(List<ConcurrentFilter> filters, String command){
		Iterator<ConcurrentFilter> iter = filters.iterator();
		ConcurrentFilter prev;
		ConcurrentFilter curr = iter.next();
		String[] cmdlist = command.split("\\|");	//command is brought in so we can output proper error messages
		int cmdindex = 0;
		
		//check to make sure grep and wc are not the first filters
		if(curr instanceof GrepFilter || curr instanceof WcFilter) {
			System.out.printf(Message.REQUIRES_INPUT.toString(),cmdlist[cmdindex].trim());
			return false;
		}
		
		while(iter.hasNext()) {
			prev = curr;
			curr = iter.next();
			cmdindex++;
			
			//additional checks
			if(curr instanceof CdFilter || curr instanceof CatFilter || curr instanceof LsFilter || curr instanceof PwdFilter) {
				System.out.printf(Message.CANNOT_HAVE_INPUT.toString(), cmdlist[cmdindex].trim());
				return false;
			}
			if(prev instanceof CdFilter && !(curr instanceof PrintFilter)) {
				System.out.printf(Message.CANNOT_HAVE_OUTPUT.toString(), cmdlist[cmdindex-1].trim());
				return false;
			}
			
			prev.setNextFilter(curr);
		}
		return true;
	}
}

package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.*;

/**
 * The main implementation of the REPL loop (read-eval-print loop).
 * It reads commands from the user, parses them, executes them and displays the result.
 *
 * @author cs131a
 */
public class ConcurrentREPL {
    /**
     * the path of the current working directory
     */
    static String currentWorkingDirectory;

    /**
     * the list of background thread
     */
    private static LinkedList<Thread> backgroundThread;

    /**
     * the hashtable to store all processes (a list of filters) with their number
     */
    private static Hashtable<String, LinkedList<ConcurrentFilter>> processes = new Hashtable<String, LinkedList<ConcurrentFilter>>();

    /**
     * the hashtable to store all processes commands with their number
     */
    private static Hashtable<String, String> jobs = new Hashtable<String, String>();

    /**
     * the hashtable to store all the last thread of background processes
     */
    private static Hashtable<String, Thread> LastThread = new Hashtable<String, Thread>();

    /**
     * give each job a id
     */
    private static int id = 1;

    /**
     * The method is used to check and delete inactive thread
     */
    public static void deleteInactiveProcess() {
        Enumeration<String> enumeration = jobs.keys();
        while (enumeration.hasMoreElements()) {
            String jobId = enumeration.nextElement();
            // check if the last filter is done
            if (processes.get(jobId).getLast().IsDone) {
                jobs.remove(jobId);
                processes.remove(jobId);
                LastThread.remove(jobId);
            }
        }
    }

    /**
     * The main method that will execute the REPL loop
     *
     * @param args not used
     */
    public static void main(String[] args) {
        currentWorkingDirectory = System.getProperty("user.dir");
        Scanner s = new Scanner(System.in);
        System.out.print(Message.WELCOME);
        String command;
        while (true) {
            //obtaining the command from the user
            System.out.print(Message.NEWCOMMAND);
            command = s.nextLine();
            if (command.equals("exit")) {
                break;
            }
            // print a list of the alive processes
            else if (command.equals("repl_jobs")) {
                if (!jobs.isEmpty()) {
                    deleteInactiveProcess();
                }
                List<String> v = new ArrayList<String>(jobs.keySet());
                Collections.sort(v);

                for (String str : v) {
                    System.out.println("\t" + str + ". " + jobs.get(str));
                }

//				// getting entrySet() into Set
//				Set<Map.Entry<String, String>> entrySet = jobs.entrySet();
//
//				// for-each loop
//				for(Map.Entry<String, String> entry : entrySet) {
//					System.out.println("\t" + entry.getKey() + ". " + entry.getValue());
//				}
            } else if (command.split("\\s+")[0].equals("kill")) {
                if (command.split("\\s+").length == 1) {
                    System.out.printf(Message.REQUIRES_PARAMETER.toString(), command);
                } else if (!jobs.containsKey(command.split("\\s+")[1])) {
                    System.out.printf(Message.INVALID_PARAMETER.toString(), command);
                } else {
                    String jobId = command.split("\\s+")[1];
                    // to kill a thread, just reset each filter to true then the unreached filter will not even be executed
                    for (ConcurrentFilter filter : processes.get(jobId)) {
                        // set exit to true to escape from the loop
                        filter.exit = true;
                    }
					if (!jobs.isEmpty()) {
						deleteInactiveProcess();
					}
                }
            } else if (command.endsWith("&")) {
                // build the filters list from the command after remove '&'
                ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(
                        command.substring(0, command.length() - 1).trim());

                //build linkedlist to store filter list for ez retrieval
                LinkedList<ConcurrentFilter> backgroundFilterLists = new LinkedList<ConcurrentFilter>();

                // initialize a backgroundThread to store threads
                backgroundThread = new LinkedList<Thread>();

                if (filterlist != null) {
                    while (filterlist != null) {
                        backgroundThread.add(new Thread(filterlist));

                        // add to linkedlist
                        backgroundFilterLists.add((ConcurrentFilter) filterlist);

                        filterlist = (ConcurrentFilter) filterlist.getNext();
                    }
//					 increase id
					if (!jobs.isEmpty()) {
						deleteInactiveProcess();
					}
                    int i;
                    for (i = 1; jobs.containsKey(String.valueOf(i)); i++) ;
//					id++;
                    id = i;


                    jobs.put(String.valueOf(id), command);
                    LastThread.put(String.valueOf(id), backgroundThread.getLast());
                    processes.put(String.valueOf(id), backgroundFilterLists);
                    for (Thread thread : backgroundThread) {
                        thread.start();
                    }

                }
            } else if (!command.trim().equals("")) {
                //build the filters list from the command
                ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
                if (filterlist != null) {
                    LinkedList<Thread> threads = new LinkedList<Thread>();
                    while (filterlist != null) {
                        threads.add(new Thread(filterlist));
                        filterlist = (ConcurrentFilter) filterlist.getNext();
                    }
                    for (Thread thread : threads) {
                        thread.start();
                    }
                    // wait for the last filter to finish
                    try {
                        threads.getLast().join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//					// wait for previous filter
//					for (Thread thread : threads) {
//						try {
//							thread.join();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
                }
            }
//			if (!jobs.isEmpty()) {
//				deleteInactiveProcess();
//			}
        }
        s.close();
        System.out.print(Message.GOODBYE);
    }

}

package cs131.pa1.filter.concurrent;

/**
 * The filter for printing in the console
 *
 * @author cs131a
 */
public class PrintFilter extends ConcurrentFilter {
    public PrintFilter() {
        super();
    }

    public void process() {
        while (!exit && (!prev.isDone() || !input.isEmpty())) {
            while (!exit && !input.isEmpty()) {
                processLine(input.poll());
            }
        }
        IsDone = true;
    }

    public String processLine(String line) {
        System.out.println(line);
        return null;
    }
}

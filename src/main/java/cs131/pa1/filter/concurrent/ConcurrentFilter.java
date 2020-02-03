package cs131.pa1.filter.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Filter;

/**
 * An abstract class that extends the Filter and implements the basic functionality of all filters. Each filter should
 * extend this class and implement functionality that is specific for that filter.
 *
 * @author cs131a
 */
public abstract class ConcurrentFilter extends Filter implements Runnable {
    /**
     * The input queue for this filter
     */
    protected LinkedBlockingQueue<String> input;
    /**
     * The output queue for this filter
     */
    protected LinkedBlockingQueue<String> output;
    /**
     * The flag to mark if a filter is finished processing
     */
    protected boolean IsDone = false;
    /**
     * The flag to help stopping a thread
     */
    protected boolean exit = false;

    /**
     * override run to call process
     */
    public void run() {
        process();
    }


    @Override
    public void setPrevFilter(Filter prevFilter) {
        prevFilter.setNextFilter(this);
    }

    @Override
    public void setNextFilter(Filter nextFilter) {
        if (nextFilter instanceof ConcurrentFilter) {
            ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
            this.next = sequentialNext;
            sequentialNext.prev = this;
            if (this.output == null) {
                // use LinkedBlockingQueue instead of linkedList
                this.output = new LinkedBlockingQueue<String>();
            }
            sequentialNext.input = this.output;
        } else {
            throw new RuntimeException("Should not attempt to link dissimilar filter types.");
        }
    }

    /**
     * Gets the next filter
     *
     * @return the next filter
     */
    public Filter getNext() {
        return next;
    }

    /**
     * processes the input queue and writes the result to the output queue
     */
    public void process() {

        while (!exit && (!prev.isDone() || !input.isEmpty())) {
            while (!input.isEmpty() && !exit) {
                String line;
                try {
                    line = input.take();
                    String processedLine = processLine(line);
                    if (processedLine != null) {
                        output.put(processedLine);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        //mark IsDone
        IsDone = true;
    }

    @Override
    public boolean isDone() {
        return this.IsDone;
    }

    protected abstract String processLine(String line);

}

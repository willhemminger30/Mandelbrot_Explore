import javax.swing.*;

/**
 * SyncCounter.java
 * A synchronized counter for use in multiple threads
 * @author William Hemminger
 * 8 March 2021
 */

public class SyncCounter {
    private int counter;
    private int loading;
    private final int total;
    private JProgressBar bar;

    // constructor that takes in total number of pixels and progress bar
    public SyncCounter(int total, JProgressBar bar)
    {
        this.counter = 0;
        this.loading = 0;
        this.total = total;
        this.bar = bar;
        //System.out.println(bar);
    }

    // increments counter and adjusts progress bar
    public synchronized void increment()
    {
        //System.out.println(bar);
        if(counter == 0)
        {
            System.out.print("0% Complete");
        }
        this.counter++;

        int percent = (int)(counter / (double) total * 100);


        if(percent % 10 == 0 && percent != loading)
        {
            System.out.print("\n" + percent + "% Complete");
            loading = percent;
        }

        if(percent != loading)
        {

            System.out.print(".");
            loading = percent;

        }

        //System.out.println("HERE");
        bar.setValue(loading);
    }

    public int getCounter()
    {
        return this.counter;
    }
}

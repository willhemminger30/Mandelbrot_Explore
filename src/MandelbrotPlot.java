/**
 * MandelbrotPlot.java
 * A class that manipulates the Mandelbrot plot image.
 * @author William Hemminger
 * 8 March 2021
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MandelbrotPlot {

    private BufferedImage plot; // the image of the current Mandelbrot plot
    private JProgressBar bar;

    // constructor that takes in progress bar object
    public MandelbrotPlot(JProgressBar bar)
    {
        this.plot = null;
        this.bar = bar;
    }

    // constructor for initial width & height parameters
    public MandelbrotPlot(int width, int height, JProgressBar bar)
    {
        plot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.bar = bar;
    }

    // the meat of the program, uses multithreading to plot the Mandelbrot set
    public void plotImage(int width, int height, int iterations, double zoomScale, double offsetX, double offsetY, double shadingFactor)
    {
        plot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        SyncCounter counter = new SyncCounter(plot.getWidth() * plot.getHeight(), bar);
        int numCores = Runtime.getRuntime().availableProcessors();
        Thread [] plottingThreads = new Thread[numCores];

        long startTime = System.currentTimeMillis();
        System.out.println("Plotting image...");
        System.out.println("Number of processors detected: " + numCores);
        System.out.println("Generating " + numCores + " threads...");

        // threads are initialized and responsible for different horizontal strips of the image
        for(int i = 0; i < numCores; i++)
        {
            plottingThreads[i] = new Thread(new PlotThread(iterations, (int)(((double) i / numCores) * plot.getHeight()),
                    (int)((((double)(i + 1)) / numCores) * plot.getHeight()) - 1, zoomScale, offsetX, offsetY,
                    plot.getWidth(), plot, counter, shadingFactor), "Thread " + (i + 1));
        }

        System.out.println("Starting Threads");
        for(int i = 0; i < numCores; i++)
        {
            plottingThreads[i].start();
        }

        try
        {
            for(int i = 0; i < numCores; i++)
            {
                Thread current = plottingThreads[i];
                current.join();
                //System.out.println(current.getName() + " finished.");
            }
        }
        catch(InterruptedException e)
        {
            System.err.println("Threads Interrupted.");
        }
        System.out.println("\nDone.");

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        System.out.println("Time to plot: " + (duration / 1000) + " seconds.");
    }


    public void saveImage(String name)
    {
        try
        {
            System.out.println("Saving image...");
            ImageIO.write(plot, "png", new File(name + ".png"));
            System.out.println("Success");
        } catch(Exception e)
        {
            System.err.println("There was an error:");
            e.printStackTrace();
        }
    }

    public BufferedImage getPlot()
    {
        return this.plot;
    }

    public void setPlot(BufferedImage newPlot)
    {
        this.plot = newPlot;
    }
}


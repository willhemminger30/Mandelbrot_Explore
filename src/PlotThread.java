/**
 * PlotThread.java
 * A thread class to plot a section of the Mandelbrot set
 * @author William Hemminger
 * 8 March 2021
 */

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.System.exit;

public class PlotThread implements Runnable {
    private final int startRow;
    private final int stopRow;
    private final int width;
    private final double zoomScale;
    private final double offsetX;
    private final double offsetY;
    private final BufferedImage plot;
    private final int iterations;
    private final SyncCounter counter;

    // constructor for thread that takes in calculation parameters
    public PlotThread(int iterations, int startRow, int stopRow, double zoomScale, double offsetX, double offsetY,
                      int width, BufferedImage image, SyncCounter counter)
    {
        this.iterations = iterations;
        this.startRow = startRow;
        this.stopRow = stopRow;
        this.width = width;
        this.zoomScale = zoomScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.plot = image;
        this.counter = counter;
    }

    // this function runs when the thread is started, works to assign color values to plot based on equation result
    public void run()
    {
        //System.out.println("Start: " + startRow + " Stop: " + stopRow);

        for(int i = 0; i < width; i++)
        {
            for(int j = startRow; j <= stopRow; j++)
            {
                try{
                    plot.setRGB(i, j, iterate(iterations, i, j, zoomScale, offsetX, offsetY).getRGB());
                    counter.increment();
                }catch(Exception e)
                {
                    System.out.println(e.getMessage());
                    System.out.println(plot.getHeight() + " " +  plot.getWidth());
                    System.out.println("i: " + i + " j: " + j);
                    exit(0);
                }

            }
        }
    }

    // iterate returns a color to run()
    public Color iterate(int iterations, int x, int y, double zoomScale, double offsetX, double offsetY)
    {

        double r = (x - ((int)((plot.getWidth()) / 2))) * zoomScale;
        double i = (((int)((plot.getHeight()) / 2)) - y) * zoomScale;

        //offset
        r += offsetX;
        i += offsetY;


        Complex z = new Complex(0, 0);
        Complex c = new Complex(r, i);

        //iterates the equation for the input coordinate
        for(int k = 0; k < iterations; k++)
        {
            z = (z.sqr()).add(c);

            if(z.abs() > 2)
            {
                int red;
                int green;
                int blue;

                //colorval based on value of 765
                //divide 765 by max number of iterations to get multiplier
                int colorVal = (int) ((765.0 / iterations) * k);


                if(colorVal <= 255)
                {
                    red = 255 - colorVal;
                    green = colorVal;
                    blue = 0;
                }
                else if(colorVal <= 510)
                {
                    red = 0;
                    green = 255 - (colorVal - 255);
                    blue = colorVal - 255;
                }
                else if(colorVal <= 765)
                {
                    red = 0 ;
                    green = 0;
                    blue = 255 - (colorVal - 510);
                }
                else
                {
                    red = 0;
                    green = 0;
                    blue = 0;
                }

                return new Color(red, green, blue);
            }
        }

        return Color.BLACK;
    }
}

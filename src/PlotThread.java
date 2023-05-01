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
    private MandelbrotPlot plot;
    private BufferedImage image;
    private final int iterations;
    private final SyncCounter counter;
    private final double shadingFactor;

    // constructor for thread that takes in calculation parameters
    public PlotThread(int iterations, int startRow, int stopRow, double zoomScale, double offsetX, double offsetY,
                      int width, MandelbrotPlot plot, SyncCounter counter, double shadingFactor)
    {
        this.iterations = iterations;
        this.startRow = startRow;
        this.stopRow = stopRow;
        this.width = width;
        this.zoomScale = zoomScale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.plot = plot;
        this.counter = counter;
        this.shadingFactor = shadingFactor;
        this.image = this.plot.getPlot();
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
                    plot.getPlot().setRGB(i, j, iterate(iterations, i, j, zoomScale, offsetX, offsetY).getRGB());
                    counter.increment();
                }catch(Exception e)
                {
                    System.out.println(e.getMessage());
                    System.out.println(image.getHeight() + " " +  image.getWidth());
                    System.out.println("i: " + i + " j: " + j);
                    exit(0);
                }

            }
        }
    }

    // iterate returns a color to run()
    public Color iterate(int iterations, int x, int y, double zoomScale, double offsetX, double offsetY)
    {
        double scale;
        double colorFactor;
        if(plot.getShadingChanged() && !plot.getImageChanged()) {
            colorFactor = ((768.0 / iterations) * plot.getIterationPerPixel()[x][y]); // multiplied by current iteration k gives current color value out of max
            scale = (1 - 1.0 / (Math.pow(shadingFactor * (colorFactor / 768), 10) + 1)); // shading scale
            Color pixelColor = new Color(plot.getRgbPerPixel()[x][y]);
            return new Color((int)(pixelColor.getRed() * scale), (int)(pixelColor.getGreen()
                    * scale), (int)(pixelColor.getBlue() * scale));
        }

        double r = (x - ((int)((image.getWidth()) / 2))) * zoomScale;
        double i = (((int)((image.getHeight()) / 2)) - y) * zoomScale;

        //offset
        r += offsetX;
        i += offsetY;


        Complex z = new Complex(0, 0);
        Complex c = new Complex(r, i);
        int colorVal;

        //iterates the equation for the input coordinate
        for(int k = 1; k <= iterations; k++)
        {
            z = (z.sqr()).add(c);

            if(z.abs() > 2)
            {
                int red;
                int green;
                int blue;

                //colorval based on value of 768
                //divide 768 by max number of iterations to get multiplier
                colorFactor = ((768.0 / iterations) * k); // multiplied by current iteration k gives current color value out of max
                scale = 1 - 1.0 / (Math.pow(shadingFactor * (colorFactor / 768), 10) + 1); // shading scale

                colorVal = (int) colorFactor;

                if(colorVal > 0)
                {
                    colorVal -= 1;
                }

                boolean color1, color2, color3;
                color3 = color2 = color1 = false;

                if(colorVal >= 768)
                    System.out.println("high" + colorVal);
                if(colorVal <= 255)
                {
                    color1 = true;
                    red = 255 - colorVal;
                    green = colorVal;
                    blue = 0;
                }
                else if(colorVal <= 511)
                {
                    color2 = true;
                    green = 255 - (colorVal - 256);
                    blue = colorVal - 256;
                    red = 0;
                }
                else if(colorVal <= 767)
                {
                    color3 = true;
                    red = 0 ;
                    green = 0;
                    blue = 255 - (colorVal - 512);
                }
                else
                {
                    red = 0;
                    green = 0;
                    blue = 0;
                }

                if((blue * scale) > 255 || (blue * scale) < 0)
                {
                    System.out.println();
                    System.out.println(color1);
                    System.out.println(color2);
                    System.out.println(color3);
                    System.out.println("BLUE: " + (blue * scale));
                    System.out.println("ColorVal: " + colorVal);
                }

                if((green * scale) > 255 || (green * scale) < 0)
                {
                    System.out.println();
                    System.out.println(color1);
                    System.out.println(color2);
                    System.out.println(color3);

                    System.out.println("GREEN: " + (green * scale));
                    System.out.println("ColorVal: " + colorVal);
                }

                if((red * scale) > 255 || (red * scale) < 0)
                {
                    System.out.println();
                    System.out.println(color1);
                    System.out.println(color2);
                    System.out.println(color3);

                    System.out.println("RED: " + (red * scale));
                    System.out.println("ColorVal: " + colorVal);
                }

                Color color = new Color((int) (red * scale), (int) (green * scale), (int) (blue * scale));
                plot.getIterationPerPixel()[x][y] = k;
                plot.getRgbPerPixel()[x][y] = new Color(red, green, blue).getRGB();
                return color;
            }
        }

        plot.getIterationPerPixel()[x][y] = iterations;
        plot.getRgbPerPixel()[x][y] = Color.BLACK.getRGB();
        return Color.BLACK;
    }
}

/**
 * Mandelbrot.java
 * A program to plot the Mandelbrot set using png images.
 * This class contains the frame for the GUI
 * @author William Hemminger
 * 7 April 2021
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Scanner;

public class Mandelbrot extends JFrame {
    private static JPanel viewer;
    private static int width;
    private static int height;
    private static double scale;
    private static double xOffset;
    private static double yOffset;
    private static double tempX;
    private static double tempY;
    private static int iterations;
    private static MandelbrotPlot mandelbrot;
    private static MandelbrotPlot viewerMandelbrot;
    private static JPanel rightPanel;
    private static JPanel rightTop;
    private static JPanel rightBottom;
    private static JButton plotButton;
    private static JTextField widthField;
    private static JTextField heightField;
    private static JTextField iterationField;
    private static JTextField scaleField;
    private static JTextField xOffsetField;
    private static JTextField yOffsetField;
    private static JPanel topFields;
    private static JPanel bottomFields;
    private static JPanel widthPanel;
    private static JPanel heightPanel;
    private static JPanel iterationPanel;
    private static JPanel scalePanel;
    private static JPanel xOffsetPanel;
    private static JPanel yOffsetPanel;
    private static JLabel widthLabel;
    private static JLabel heightLabel;
    private static JLabel iterationLabel;
    private static JLabel scaleLabel;
    private static JLabel xOffsetLabel;
    private static JLabel yOffsetLabel;
    private static JButton saveButton;
    private static BufferedImage backGroundImage;
    private static JProgressBar bar;
    private static JButton resetButton;
    private static KeyListener changeListener;

    // this is the default constructor for the GUI
    public Mandelbrot()
    {
        setTitle("Mandelbrot Plot");
        setMinimumSize(new Dimension(800, 450));
        setMaximumSize(new Dimension(1300,800));
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        this.setLayout(new BorderLayout());
        viewer = new JPanel();
        rightPanel = new JPanel(new BorderLayout());
        rightTop = new JPanel(new BorderLayout());
        rightBottom = new JPanel(new BorderLayout(10, 80));
        plotButton = new JButton("Plot");
        saveButton = new JButton("Save");
        resetButton = new JButton("Reset");
        //plotButton.setSize(new Dimension(40, 40));
        widthField = new JTextField("3841");
        heightField = new JTextField("2161");
        iterationField = new JTextField("1000");
        scaleField = new JTextField("0.001");
        xOffsetField = new JTextField("0");
        yOffsetField = new JTextField("0");
        topFields = new JPanel(new BorderLayout());
        bottomFields = new JPanel(new BorderLayout());

        changeListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                try
                {
                    // if GUI fields do not match actual values, disable the save button
                    if(Integer.parseInt(widthField.getText()) != width || Integer.parseInt(heightField.getText()) != height
                            || Integer.parseInt(iterationField.getText()) != iterations || Double.parseDouble(scaleField.getText()) != scale
                            || Double.parseDouble(xOffsetField.getText()) != xOffset || Double.parseDouble(yOffsetField.getText())
                            != yOffset)
                    {
                        saveButton.setEnabled(false);
                    }
                    else // otherwise make sure that save is enabled
                    {
                        saveButton.setEnabled(true);
                    }
                }
                catch(NumberFormatException ex)
                {
                    saveButton.setEnabled(false);
                }
            }
        };

        widthPanel = new JPanel(new BorderLayout());
        heightPanel = new JPanel(new BorderLayout());
        iterationPanel = new JPanel(new BorderLayout());
        scalePanel = new JPanel(new BorderLayout());
        xOffsetPanel = new JPanel(new BorderLayout());
        yOffsetPanel = new JPanel(new BorderLayout());
        bar = new JProgressBar();
        bar.setValue(0);
        bar.setStringPainted(true);

        widthLabel = new JLabel("WIDTH");
        heightLabel = new JLabel("HEIGHT");
        iterationLabel = new JLabel("ITERATIONS");
        scaleLabel = new JLabel("SCALE");
        xOffsetLabel = new JLabel("REAL-OFFSET");
        yOffsetLabel = new JLabel("IMAGINARY-OFFSET");

        widthField.setPreferredSize(new Dimension(70, 20));
        heightField.setPreferredSize(new Dimension(70, 20));
        iterationField.setPreferredSize(new Dimension(70, 20));
        scaleField.setPreferredSize(new Dimension(150, 20));
        xOffsetField.setPreferredSize(new Dimension(150, 20));
        yOffsetField.setPreferredSize(new Dimension(150, 20));


        viewer.setPreferredSize(new Dimension(1921, 1081));

        // create a new JPanel object to serve as the preview image viewer
        viewer = new JPanel() {
            @Override
            public void paintComponent(Graphics g) // paints the current Mandelbrot image
            {
                int difference = 0;
                super.paintComponent(g);

                if(viewer.getWidth() <= viewer.getHeight() * (16.0 / 9))
                {
                    difference = viewer.getHeight() - (int) (this.getWidth() * (9/16.0));
                    g.drawImage(backGroundImage, 0, difference / 2, this.getWidth(), (int) (this.getWidth() * (9/16.0)), null);
                }
                else
                {
                    difference = viewer.getWidth() - (int) (this.getHeight() * (16.0 / 9));
                    g.drawImage(backGroundImage, difference / 2, 0, (int) (this.getHeight() * (16.0 / 9)), this.getHeight(), null);
                }

            }};

        // mouse click serves to set new plot center point
        viewer.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run()
                    {
                        // when clicked, draw a circle over existing image and repaint
                        int difference = 0;

                        int xClick = e.getX();
                        int yClick = e.getY();

                        backGroundImage = new BufferedImage(1921, 1081, BufferedImage.TYPE_INT_RGB) ;
                        Graphics g = backGroundImage.getGraphics();

                        g.drawImage(viewerMandelbrot.getPlot(), 0, 0, null);
                        repaint();

                        g.setColor(new Color(Color.GRAY.getRGB()));

                        if(viewer.getWidth() <= viewer.getHeight() * (16.0 / 9))
                        {
                            difference = viewer.getHeight() - (int) (viewer.getWidth() * (9/16.0));
                            g.drawOval((int) (xClick * (1921.0 / viewer.getWidth())) - 25, (int) ((yClick - (difference / 2.0)) * (1921.0 / viewer.getWidth())) - 25, 50, 50);
                            g.drawOval((int) (xClick * (1921.0 / viewer.getWidth())) - 24, (int) ((yClick - (difference / 2.0)) * (1921.0 / viewer.getWidth())) - 24, 48, 48);
                            g.drawOval((int) (xClick * (1921.0 / viewer.getWidth())) - 26, (int) ((yClick - (difference / 2.0)) * (1921.0 / viewer.getWidth())) - 26, 52, 52);
                            tempX = xOffset + (xClick  - (viewer.getWidth() / 2.0)) * scale * (1921.0 / viewer.getWidth()) * ((double)width / 1921.0);
                            tempY = yOffset - (yClick - (viewer.getHeight() / 2.0))  * scale * (1921.0 / viewer.getWidth()) * ((double)width / 1921.0);
                        }

                        else
                        {
                            difference = viewer.getWidth() - (int) (viewer.getHeight() * (16.0 / 9));
                            g.drawOval((int) ((xClick - (difference / 2.0)) * (1081.0 / viewer.getHeight())) - 25, (int) ((yClick) * (1081.0 / viewer.getHeight())) - 25, 50, 50);
                            g.drawOval((int) ((xClick - (difference / 2.0)) * (1081.0 / viewer.getHeight())) - 24, (int) ((yClick) * (1081.0 / viewer.getHeight())) - 24, 48, 48);
                            g.drawOval((int) ((xClick - (difference / 2.0)) * (1081.0 / viewer.getHeight())) - 26, (int) ((yClick) * (1081.0 / viewer.getHeight())) - 26, 52, 52);
                            tempX = xOffset + ((xClick - (viewer.getWidth() / 2.0))) * scale * (1081.0 / viewer.getHeight()) * ((double)height / 1081.0);
                            tempY = yOffset - (yClick  - (viewer.getHeight() / 2.0)) * scale * (1081.0 / viewer.getHeight()) * ((double)height / 1081.0);
                        }

                        repaint();


                        xOffsetField.setText(Double.toString(tempX));
                        yOffsetField.setText(Double.toString(tempY));

                        saveButton.setEnabled(false);

                    }
                };

                //drawing occurs in separate thread
                Thread newThread = new Thread(runnable);
                newThread.start();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //System.out.println("MousePressed");
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //System.out.println("MouseReleased");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //System.out.println("MouseEntered");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //System.out.println("MouseExited");
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                //System.out.println("MouseDragged");
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                //System.out.println("MouseMoved");
            }
        });

        // redraw plot according to current user parameters
        plotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Plot");
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());

                if((width != mandelbrot.getPlot().getWidth()) || (width != mandelbrot.getPlot().getHeight()))
                {
                    mandelbrot.setPlot(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
                }

                plotImage();

            }
        });

        // save the image to .png at user's resolution of choice
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Save");
                saveImage();
            }
        });

        // reset the plot to the default position
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                widthField.setText("3841");
                heightField.setText("2161");
                iterationField.setText("1000");
                scaleField.setText("0.001");
                xOffsetField.setText("0");
                tempX = 0;
                yOffsetField.setText("0");
                tempY = 0;

                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());

                if((width != mandelbrot.getPlot().getWidth()) || (width != mandelbrot.getPlot().getHeight()))
                {
                    mandelbrot.setPlot(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
                }


                plotImage();
            }
        });

        widthField.addKeyListener(changeListener);
        heightField.addKeyListener(changeListener);
        iterationField.addKeyListener(changeListener);
        scaleField.addKeyListener(changeListener);
        xOffsetField.addKeyListener(changeListener);
        yOffsetField.addKeyListener(changeListener);


        viewer.setVisible(true);
        add(viewer, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        rightPanel.add(rightTop, BorderLayout.NORTH);
        rightPanel.add(rightBottom, BorderLayout.SOUTH);

        rightBottom.add(bar, BorderLayout.NORTH);
        rightBottom.add(plotButton, BorderLayout.WEST);
        rightBottom.add(saveButton, BorderLayout.EAST);
        rightBottom.add(resetButton, BorderLayout.CENTER);

        rightTop.add(topFields, BorderLayout.NORTH);
        rightTop.add(bottomFields, BorderLayout.CENTER);

        widthPanel.add(widthField, BorderLayout.CENTER);
        heightPanel.add(heightField, BorderLayout.CENTER);
        iterationPanel.add(iterationField, BorderLayout.CENTER);
        scalePanel.add(scaleField, BorderLayout.CENTER);
        xOffsetPanel.add(xOffsetField, BorderLayout.CENTER);
        yOffsetPanel.add(yOffsetField, BorderLayout.CENTER);

        widthPanel.add(widthLabel, BorderLayout.NORTH);
        heightPanel.add(heightLabel, BorderLayout.NORTH);
        iterationPanel.add(iterationLabel, BorderLayout.NORTH);
        scalePanel.add(scaleLabel, BorderLayout.NORTH);
        xOffsetPanel.add(xOffsetLabel, BorderLayout.NORTH);
        yOffsetPanel.add(yOffsetLabel, BorderLayout.NORTH);

        topFields.add(widthPanel, BorderLayout.NORTH);
        topFields.add(heightPanel, BorderLayout.CENTER);
        topFields.add(iterationPanel, BorderLayout.SOUTH);

        bottomFields.add(scalePanel, BorderLayout.NORTH);
        bottomFields.add(xOffsetPanel, BorderLayout.CENTER);
        bottomFields.add(yOffsetPanel, BorderLayout.SOUTH);

        setVisible(true);
    }


    public static void main(String[] args) {

        width = 3841;
        height = 2161;
        iterations = 1000;
        scale = 0.001;
        xOffset = 0;
        yOffset = 0;

        new Mandelbrot();

        // initialize full size plot
        mandelbrot = new MandelbrotPlot(3841, 2161, bar);

        // initialize viewer plot
        viewerMandelbrot = new MandelbrotPlot(bar);

        Mandelbrot.plotImage();

    }

    // function to save the user's current plot with a chosen filename
    public static void saveImage()
    {

        width = Integer.parseInt(widthField.getText());
        height = Integer.parseInt(heightField.getText());

        if((width != mandelbrot.getPlot().getWidth()) || (height != mandelbrot.getPlot().getHeight()))
        {
            mandelbrot.setPlot(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
        }

        scale = Double.parseDouble(scaleField.getText());
        iterations = Integer.parseInt(iterationField.getText());
        xOffset = Double.parseDouble(xOffsetField.getText());
        yOffset = Double.parseDouble(yOffsetField.getText());

        String fileName = JOptionPane.showInputDialog(null, "Name of saved image:");

        if(fileName != null)
        {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    plotButton.setEnabled(false);
                    resetButton.setEnabled(false);
                    saveButton.setEnabled(false);

                    mandelbrot.plotImage(width, height, iterations, scale, xOffset, yOffset);

                    mandelbrot.saveImage(fileName + "_" + width + "_" + height + "_" + iterations + "_" + scale
                            + "_" + xOffset + "_" + yOffset);

                    plotButton.setEnabled(true);
                    resetButton.setEnabled(true);
                    saveButton.setEnabled(true);
                }
            };
            Thread t = new Thread(r);
            t.start();

        }
    }

    // function to plot with the current user parameters
    public static void plotImage()
    {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                plotButton.setEnabled(false);
                resetButton.setEnabled(false);
                saveButton.setEnabled(false);
                xOffset = Double.parseDouble(xOffsetField.getText());
                yOffset = Double.parseDouble(yOffsetField.getText());
                scale = Double.parseDouble(scaleField.getText());
                iterations = Integer.parseInt(iterationField.getText());
//
//                System.out.println(tempX);
//                System.out.println(tempY);


                viewerMandelbrot.plotImage(1921, 1081, iterations,
                        scale * ((double)width / 1921.0), xOffset, yOffset);

                backGroundImage = new BufferedImage(1921, 1081, BufferedImage.TYPE_INT_RGB) ;
                Graphics g = backGroundImage.getGraphics();

                g.drawImage(viewerMandelbrot.getPlot(), 0, 0, null);

                viewer.repaint();

                plotButton.setEnabled(true);
                resetButton.setEnabled(true);
                saveButton.setEnabled(true);
            }
        };

        Thread t = new Thread(r);
        t.start();

    }
}

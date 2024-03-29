/**
 * Mandelbrot.java
 * A program to plot the Mandelbrot set using png images.
 * This class contains the frame for the GUI
 * @author William Hemminger
 * 7 April 2021
 */

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Mandelbrot extends JFrame {
    private static boolean mouseEnabled;
    private static JPanel viewer;
    private static int width;
    private static int height;
    private static double scale;
    private static double xOffset;
    private static double yOffset;
    private static double tempX;
    private static double tempY;
    private static int iterations;
    private static double shadingFactor;
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
    private static JPanel shadingPanel;
    private static JLabel widthLabel;
    private static JLabel heightLabel;
    private static JLabel iterationLabel;
    private static JLabel scaleLabel;
    private static JLabel xOffsetLabel;
    private static JLabel yOffsetLabel;
    private static JLabel shadingLabel;
    private static JLabel shadingField;
    private static JButton saveButton;
    private static BufferedImage backGroundImage;
    private static JProgressBar bar;
    private static JButton resetButton;
    private static KeyListener changeListener;
    private static JSlider shadingSlider;

    // this is the default constructor for the GUI
    public Mandelbrot()
    {
        setTitle("Mandelbrot Plot");
        setMinimumSize(new Dimension(1280, 720));
        setMaximumSize(new Dimension(1300,800));
        mouseEnabled = true;
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
        shadingSlider = new JSlider(1, 1000, 500);
        shadingSlider.setPreferredSize(new Dimension(300,0));
        shadingLabel = new JLabel("SHADING FACTOR", SwingConstants.CENTER);
        shadingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        shadingLabel.setFont(new Font("Tahoma", Font.BOLD, 10));
        shadingField = new JLabel("50.0", SwingConstants.CENTER);
        shadingField.setOpaque(true);
        shadingField.setBackground(Color.WHITE);
        shadingField.setPreferredSize(new Dimension(70, 30));
        shadingField.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        shadingField.setFont(new Font("Tahoma", Font.PLAIN, 15));

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
                    if(parametersChangedGUI())
                    {
                        saveButton.setEnabled(false);
                        plotButton.setEnabled(true);
                    }
                    else if(!(((JTextField) e.getSource()).getParent() instanceof JOptionPane)) // otherwise if the typing did not change the parameters make sure that save is enabled
                    {
                        saveButton.setEnabled(true);
                        plotButton.setEnabled(true);
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
        shadingPanel = new JPanel(new BorderLayout(10, 10));
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
                if(mouseEnabled) {
                    //drawing occurs in separate thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // when clicked, draw a circle over existing image and repaint
                            plotButton.setEnabled(true);

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
                    }).start();
                }
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
                int tempWidth = Integer.parseInt(widthField.getText());
                int tempHeight = Integer.parseInt(heightField.getText());

                if((tempWidth != mandelbrot.getPlot().getWidth()) || (tempHeight != mandelbrot.getPlot().getHeight()))
                {
                    mandelbrot.setPlot(new BufferedImage(tempWidth, tempHeight, BufferedImage.TYPE_INT_RGB));
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
                shadingField.setText("50.0");

//                width = Integer.parseInt(widthField.getText());
//                height = Integer.parseInt(heightField.getText());
                shadingSlider.setValue(500);
                shadingFactor = 50;

                if((width != mandelbrot.getPlot().getWidth()) || (width != mandelbrot.getPlot().getHeight()))
                {
                    mandelbrot.setPlot(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
                }

                mandelbrot.setIterations(-1);
                plotImage();
            }
        });

        widthField.addKeyListener(changeListener);
        heightField.addKeyListener(changeListener);
        iterationField.addKeyListener(changeListener);
        scaleField.addKeyListener(changeListener);
        xOffsetField.addKeyListener(changeListener);
        yOffsetField.addKeyListener(changeListener);

        shadingSlider.addChangeListener(new javax.swing.event.ChangeListener(){
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                shadingSliderStateChanged(evt);
            }
        });

        viewer.setVisible(true);
        add(viewer, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);
        add(shadingPanel, BorderLayout.SOUTH);
        shadingPanel.add(shadingSlider, BorderLayout.CENTER);
        shadingPanel.add(shadingField, BorderLayout.EAST);
        shadingPanel.add(shadingLabel, BorderLayout.WEST);

        rightPanel.add(rightTop, BorderLayout.NORTH);
        rightPanel.add(rightBottom, BorderLayout.SOUTH);

        //rightBottom.add(shadingPanel, BorderLayout.NORTH);
        rightBottom.add(bar, BorderLayout.SOUTH);
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

    private void shadingSliderStateChanged(javax.swing.event.ChangeEvent evt)
    {
        JSlider source = (JSlider) evt.getSource();
        shadingFactor = source.getValue() / 10.0;
        shadingField.setText(Double.toString(shadingFactor));
        plotButton.setEnabled(true);
        saveButton.setEnabled(false);
    }

    // function to save the user's current plot with a chosen filename
    public static void saveImage() {
        plotButton.setEnabled(false);
        resetButton.setEnabled(false);
        saveButton.setEnabled(false);
        mouseEnabled = false;
        shadingSlider.setEnabled(false);
        viewerMandelbrot.setImageChanged(parametersChangedGUI());
        mandelbrot.setImageChanged(parametersChangedPlot(mandelbrot));
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

        if(fileName != null && !fileName.equals(""))
        {
            String saveFileName = "Images/" + fileName;
            File directory = new File("Images/");

            if(!directory.exists())
            {
                directory.mkdir();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mandelbrot.plotImage(iterations, scale, xOffset, yOffset, shadingFactor);

                    mandelbrot.saveImage(saveFileName + "_" + width + "_" + height + "_" + iterations + "_" + scale
                            + "_" + xOffset + "_" + yOffset + "_" + shadingFactor);

                    resetButton.setEnabled(true);
                    saveButton.setEnabled(true);
                    mouseEnabled = true;
                    shadingSlider.setEnabled(true);
                }
            }).start();

        }
        else
        {
            plotButton.setEnabled(true);
            resetButton.setEnabled(true);
            saveButton.setEnabled(true);
            mouseEnabled = true;
            shadingSlider.setEnabled(true);
        }
    }

    // function to plot with the current user parameters
    public static void plotImage()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mouseEnabled = false;
                shadingSlider.setEnabled(false);
                plotButton.setEnabled(false);
                resetButton.setEnabled(false);
                saveButton.setEnabled(false);
                viewerMandelbrot.setImageChanged(parametersChangedGUI());
                mandelbrot.setImageChanged(parametersChangedPlot(mandelbrot));
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                xOffset = Double.parseDouble(xOffsetField.getText());
                yOffset = Double.parseDouble(yOffsetField.getText());
                scale = Double.parseDouble(scaleField.getText());
                iterations = Integer.parseInt(iterationField.getText());

//                System.out.println(tempX);
//                System.out.println(tempY);


                viewerMandelbrot.plotImage(iterations,
                        scale * ((double)width / 1921.0), xOffset, yOffset, shadingFactor);

                backGroundImage = new BufferedImage(1921, 1081, BufferedImage.TYPE_INT_RGB) ;
                Graphics g = backGroundImage.getGraphics();

                g.drawImage(viewerMandelbrot.getPlot(), 0, 0, null);

                viewer.repaint();

                resetButton.setEnabled(true);
                saveButton.setEnabled(true);
                mouseEnabled = true;
                shadingSlider.setEnabled(true);
            }
        }).start();
    }

    private static boolean parametersChangedGUI() {
        return Integer.parseInt(widthField.getText()) != width || Integer.parseInt(heightField.getText()) != height
                || Integer.parseInt(iterationField.getText()) != iterations || Double.parseDouble(scaleField.getText()) != scale
                || Double.parseDouble(xOffsetField.getText()) != xOffset || Double.parseDouble(yOffsetField.getText())
                != yOffset;
    }

    private static boolean parametersChangedPlot(MandelbrotPlot plot) {
        return Integer.parseInt(widthField.getText()) != plot.getPlot().getWidth() || Integer.parseInt(heightField.getText()) != plot.getPlot().getHeight()
                || Integer.parseInt(iterationField.getText()) != plot.getIterations() || Double.parseDouble(scaleField.getText()) != plot.getZoomScale()
                || Double.parseDouble(xOffsetField.getText()) != plot.getOffsetX() || Double.parseDouble(yOffsetField.getText())
                != plot.getOffsetY();
    }

    public static void main(String[] args) {

//        width = 3841;
//        height = 2161;
//        iterations = 1000;
//        scale = 0.001;
//        xOffset = 0;
//        yOffset = 0;
        shadingFactor = 50.0;

        new Mandelbrot();

        // initialize full size plot
        mandelbrot = new MandelbrotPlot(3841, 2161, bar);

        // initialize viewer plot
        viewerMandelbrot = new MandelbrotPlot(1921, 1081, bar);

        Mandelbrot.plotImage();
    }
}

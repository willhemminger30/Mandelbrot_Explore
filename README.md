# Mandelbrot_Explore
## What is this program?
- This program allows you to explore the different regions of the Mandelbrot set at magnifications of your choosing.
- This program also allows you to save images of the observed area of the Mandelbrot set at a resolution of your choosing.

## How do I use this?
This program includes a .jar file under out/artifacts/MandelbrotWithGuiShading_jar/.  To use this application, your system will require
Java with version 8 or newer. 

To launch the application, double click the .jar file.  Feel free to resize the application
window to your liking.

From there, you can choose to click a new center point for the image and adjust other parameters using the available input boxes.

These parameters include:
- Width (in pixels, of final image to be saved)
- Height (in pixels, of final image to be saved)
- Iterations (number of iterations for the algorithm, more iterations = higher detail)
- Scale (This scale value adjusts the zoom for the plot, smaller value = larger image)
- Real-Offset (Left / Right offset for the image, also adjusted by clicking for new center point)
- Imaginary Offset (Up / Down offset for the image, also adjusted by clicking for new center point)
- Shading factor (determines the overall contrast of the image, smaller value makes the background darker)

Alternatively, you can manually enter coordinates instead of clicking on a new center point.

With the three buttons, you can plot a new image with the updated parameters, reset to the default image, 
and save the current image.  You should not try to save the image without plotting it first after modifying
the parameters, unless you are looking at the default image or have just reset.  

**This is still an ongoing project.  There will be bugs.**  
I do not recommend setting the scale value so low that it goes beyond fifteen decimal places.  
Any lower and the image resolution will be compromised.

## Why does it take so much time to save the image after plotting?
- This is because when you plot the image, you are plotting it on a preview screen.
  By default, this image is set to 1921 by 1081 resolution.  When you select a width and height
  for your image, this value will NOT be used until you have pressed Save, at which point the 
  image is rendered at your chosen resolution.  The default settings for the save image are 3841 by 2161.
  This system allows for a high level of detail when previewing the Mandelbrot set, without wasting the 
  time it would take to render a full scale image each time you choose a new location to view.  However, the
  image can still take a long time to plot if the number of iterations is high enough.
  
## Known Bugs:
- If you click a new centerpoint while the graph is being plotted, the preview window will display the partially rendered graph.
(This does not affect the final image, nor cause any other issues, but it does look cool.  I have not decided yet if this is 
in fact a bug, or a feature).

## Issues for Future Development
- If the user zooms in far enough, eventually the image loses resolution due to the decimal precision limit of the Java double 
primitive.  Currently, the system does not prohibit the user from doing this.  

## Recent Updates:
  08/07/21:
  - Fixed bug where user could not plot image from manually-entered coordinates.  
  
  12/20/21:
  - Added shading capabilities
  
  5/01/23:
  - Optimized the way shading is performed on the image
  - Resolved UI bugs


package Game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.image.Image;

public final class Utilities {
	
	public static final int ALPHA = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;

    public static final int HUE = 0;
    public static final int SATURATION = 1;
    public static final int BRIGHTNESS = 2;

    public static final int TRANSPARENT = 0;
	
//    public static VolatileImage toVolatileImage(Image img) {
//	   
//	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//	    GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
//	    VolatileImage bimage = gc.createCompatibleVolatileImage(img.getWidth(null), img.getHeight(null), Transparency.TRANSLUCENT);
//	    
//	    
//	    // Draw the image on to the buffered image
//	    Graphics2D bGr = bimage.createGraphics();
//	    // Make volatile Image transparent
////	    bGr.setColor(new Color(0,0,0,0));
////	    bGr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
//	    bGr.setComposite(AlphaComposite.Src);
//	    bGr.clearRect(0, 0, img.getWidth(null), img.getHeight(null)); // Clears the image.
//	    bGr.drawImage(img, 0, 0, null);
//	    bGr.dispose();
//
//	    // Return the buffered image
//	    return bimage;
////	    return toCompatibleImage(bimage);
//	}
	
//	private static BufferedImage toCompatibleImage(BufferedImage image)
//	{
//		// obtain the current system graphical settings
//		GraphicsConfiguration gfx_config = GraphicsEnvironment.
//			getLocalGraphicsEnvironment().getDefaultScreenDevice().
//			getDefaultConfiguration();
//
//		/*
//		 * if image is already compatible and optimized for current system 
//		 * settings, simply return it
//		 */
//		if (image.getColorModel().equals(gfx_config.getColorModel()))
//			return image;
//
//		// image is not optimized, so create a new image that is
//		BufferedImage new_image = gfx_config.createCompatibleImage(
//				image.getWidth(), image.getHeight(), image.getTransparency());
//
//		// get the graphics context of the new image to draw the old image on
//		Graphics2D g2d = (Graphics2D) new_image.getGraphics();
//
//		// actually draw the image and dispose of context no longer needed
//		g2d.drawImage(image, 0, 0, null);
//		g2d.dispose();
//
//		// return the new optimized image
//		return new_image; 
//	}
    
    public static String getTimeStamp()
    {
    	Date now= new Date();
    	
    	String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(now);
    	
//    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(now);
    	
    	return timeStamp;
    }
	
	
	public static BufferedImage changeColor(VolatileImage image, Color mask,
			Color replacement) {


		BufferedImage destImage = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = destImage.createGraphics();
		// Commented out for VolatileImage
		g.drawImage(image.getSnapshot(), null, 0, 0);
		g.dispose();

		for (int i = 0; i < destImage.getWidth(); i++) {
			for (int j = 0; j < destImage.getHeight(); j++) {

				int destRGB = destImage.getRGB(i, j);

				if (matches(mask.getRGB(), destRGB)) {
					int rgbnew = getNewPixelRGB(replacement.getRGB(), destRGB);
					destImage.setRGB(i, j, rgbnew);
				}
			}
		}

		return destImage;
	}
	
//	public static BufferedImage convertStringToBufferedImage(String pointValue)
//	public static VolatileImage convertStringToBufferedImage(String pointValue)
//	{
//		//create the font you wish to use
//		Font font = new Font("Tahoma", Font.PLAIN, 14);  // Font.PLAIN  // 11
//		
//		//create the FontRenderContext object which helps us to measure the text
//        FontRenderContext frc = new FontRenderContext(null, true, true);
//        
//        //get the height and width of the text
//        Rectangle2D bounds = font.getStringBounds(pointValue, frc);
//        int w = (int) bounds.getWidth();
//        int h = (int) bounds.getHeight();
//        
//        //create a BufferedImage object
////        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);  // Never worked
//        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//         
//        //calling createGraphics() to get the Graphics2D
//        Graphics2D g = image.createGraphics();
//        
//        //set color and other parameters
////        g.setColor(Color.WHITE);  // WHITE
//        g.setComposite(AlphaComposite.Clear);
//        g.fillRect(0, 0, w, h);
//        g.setComposite(AlphaComposite.Src);
////        g.setColor(Color.BLACK);  // BLACK
//        g.setColor(Color.WHITE);
//        g.setFont(font);
//        
//        g.drawString(pointValue, (float) bounds.getX(), (float) -bounds.getY());
//        
//        //releasing resources
//        g.dispose();
//        
//        return image;
//        
//	}
	
	public static VolatileImage convertStringToBufferedImage(String pointValue)
	{
		//create the font you wish to use
		Font font = new Font("Tahoma", Font.BOLD, 14);  // Font.PLAIN  // 11

		//create the FontRenderContext object which helps us to measure the text
		FontRenderContext frc = new FontRenderContext(null, true, true);

		//get the height and width of the text
		Rectangle2D bounds = font.getStringBounds(pointValue, frc);
		int w = (int) bounds.getWidth();
		int h = (int) bounds.getHeight();

		//create a BufferedImage object
		//        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		VolatileImage bimage = gc.createCompatibleVolatileImage(w, h, Transparency.TRANSLUCENT);

		//calling createGraphics() to get the Graphics2D
		Graphics2D bGr = bimage.createGraphics();

		//set color and other parameters
		//    g.setColor(Color.WHITE);  // WHITE
		bGr.setComposite(AlphaComposite.Clear);
		bGr.fillRect(0, 0, w, h);
		bGr.setComposite(AlphaComposite.Src);
		//    g.setColor(Color.BLACK);  // BLACK
		bGr.setColor(Color.YELLOW);
		bGr.setFont(font);

		bGr.drawString(pointValue, (float) bounds.getX(), (float) -bounds.getY());

		//releasing resources
		bGr.dispose();

		return bimage;
        
	}
	
//	public static URL getImageURL(String imageFilename) {
//		URL aURL= null;
//		
//		ClassLoader cl = this.getClass().getClassLoader();
//		
//		aURL= Utilities.class.getResource(imageFilename);
//   
//	     return aURL;
//	}
	
	private static int getNewPixelRGB(int replacement, int destRGB) {
        float[] destHSB = getHSBArray(destRGB);
        float[] replHSB = getHSBArray(replacement);

        int rgbnew = Color.HSBtoRGB(replHSB[HUE],
                replHSB[SATURATION], destHSB[BRIGHTNESS]);
        return rgbnew;
    }
	
	private static boolean matches(int maskRGB, int destRGB) {
        float[] hsbMask = getHSBArray(maskRGB);
        float[] hsbDest = getHSBArray(destRGB);

        if (hsbMask[HUE] == hsbDest[HUE]
                && hsbMask[SATURATION] == hsbDest[SATURATION]
                && getRGBArray(destRGB)[ALPHA] != TRANSPARENT) {

            return true;
        }
        return false;
    }

    private static int[] getRGBArray(int rgb) {
        return new int[] { (rgb >> 24) & 0xff, (rgb >> 16) & 0xff,
                (rgb >> 8) & 0xff, rgb & 0xff };
    }

    
    private static float[] getHSBArray(int rgb) {
        int[] rgbArr = getRGBArray(rgb);
        return Color.RGBtoHSB(rgbArr[RED], rgbArr[GREEN], rgbArr[BLUE], null);
    }
    
    
    public static boolean isPixelCollide(double x1, double y1, Image image1,
    		double x2, double y2, Image image2) {	
    	
    	int height1= (int) image1.getHeight();
    	int width1= (int) image1.getWidth();
    	
       	int height2= (int) image2.getHeight();
    	int width2= (int) image2.getWidth();
    	
    	return false;

//    	double width1 = x1 + image1.getWidth() -1,
//    			height1 = y1 + image1.getHeight() -1,
//    			width2 = x2 + image2.getWidth() -1,
//    			height2 = y2 + image2.getHeight() -1;
//
//    	int xstart = (int) Math.max(x1, x2),
//    			ystart = (int) Math.max(y1, y2),
//    			xend   = (int) Math.min(width1, width2),
//    			yend   = (int) Math.min(height1, height2);
//
//    	// intersection rect
//    	int toty = Math.abs(yend - ystart);
//    	int totx = Math.abs(xend - xstart);
//
//        // Use PixelReader for collision detection
//    	BufferedImage image1Snapshot= image1.getSnapshot();
//    	BufferedImage image2Snapshot= image2.getSnapshot();
//    	
//    	for (int y=1;y < toty-1;y++){
//    		int ny = Math.abs(ystart - (int) y1) + y;
//    		int ny1 = Math.abs(ystart - (int) y2) + y;
//
//    		for (int x=1;x < totx-1;x++) {
//    			int nx = Math.abs(xstart - (int) x1) + x;
//    			int nx1 = Math.abs(xstart - (int) x2) + x;
//    			try {
////    				if (((image1.getSnapshot().getRGB(nx,ny) & 0xFF000000) != 0x00) &&
////    						((image2.getSnapshot().getRGB(nx1,ny1) & 0xFF000000) != 0x00)) {
//    				if (((image1Snapshot.getRGB(nx,ny) & 0xFF000000) != 0x00) &&
//						((image2Snapshot.getRGB(nx1,ny1) & 0xFF000000) != 0x00)) {
//    					// collide!!
//    					return true;
//    				}
//    			} catch (Exception e) {
//    				//	      System.out.println("s1 = "+nx+","+ny+"  -  s2 = "+nx1+","+ny1);
//    			}
//    		}
//    	}
//
//    	return false;
    }
    
    public static void modifyImage(Image origImage, int value)
    {

    	// *** Begin ***
    	// Write code to display points just above the Image
    	// *** End ***
    	
//        BufferedImage newImage = new BufferedImage(origImage.getSnapshot().getWidth(), 
//            origImage.getSnapshot().getHeight() + 20, BufferedImage.TYPE_INT_ARGB);
////        Graphics g1 = origImage.getSnapshot().getGraphics();
//        Graphics g2 = newImage.getGraphics();
////        g2.setColor(Color.WHITE);
////        g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
//        g2.drawImage(origImage.getSnapshot(), 0, 0, origImage.getSnapshot().getWidth(), origImage.getSnapshot().getHeight(), null);
//        
//        String displayText= Integer.toString(value);
//        g2.setColor(Color.YELLOW);
////      g2.setFont(new Font("Calibri", Font.BOLD, 20));
//        g2.setFont(new Font("Tahoma", Font.BOLD, 14));
//        FontMetrics fm = g2.getFontMetrics();
//        int textWidth = fm.stringWidth(displayText);
//        
//        //center text at bottom of image in the new space
//        g2.drawString(displayText, (newImage.getWidth() / 2) - textWidth / 2,newImage.getHeight());
//        
//        g2.dispose();
//        
//        return newImage;
    }


}

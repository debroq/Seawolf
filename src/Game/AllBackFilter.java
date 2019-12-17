//package Game;
//
//import java.awt.Color;
//import java.awt.image.RGBImageFilter;
//
//public class AllBackFilter extends RGBImageFilter {
//	
//    public AllBackFilter() {
//        // The filter's operation does not depend on the
//        // pixel's location, so IndexColorModels can be
//        // filtered directly.
//        canFilterIndexColorModel = true;
//    }
//
//
//    	
//	public int filterRGB(int x, int y, int rgb) {
//		int transparency = (rgb >> 24) & 0xFF;
//		if (transparency <= 1) {
//			return rgb;
//		}
//		return Color.black.getRGB();
//	}
//		
//
//}




//package Game;
//
//import java.awt.Color;
//import java.awt.image.RGBImageFilter;
//
//public class AllRedFilter extends RGBImageFilter {
//	
//    public AllRedFilter() {
//        // The filter's operation does not depend on the
//        // pixel's location, so IndexColorModels can be
//        // filtered directly.
//        canFilterIndexColorModel = true;
//    }
//
//    public int filterRGB(int x, int y, int rgb) {
////        return ((rgb & 0xff00ff00)
////                | ((rgb & 0xff0000) >> 16)
////                | ((rgb & 0xff) << 16));
//    	
//			int transparency = (rgb >> 24) & 0xFF;
//			if (transparency <= 1) {
//				return rgb;
//			}
//			return Color.red.getRGB();
//		}
//
//}
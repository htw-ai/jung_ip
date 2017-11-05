// IP Ue1 WS2017/18
//
// Date: 2017-10-21

package ip_ws1718;

public class Filter {
	
	/**
	 * Extract edges with morphologic filters.
	 * Erode image -> invert result -> intersect result with original image
	 * @param src source image
	 * @param dst destination image (will be overwritten)
	 */
	public static void outline(RasterImage src, RasterImage dst) {
		erode(src, dst);
		invert(dst);
		intersect(src, dst);
	}
	
	/**
	 * invert each pixel (works for color & b/w images)
	 * @param img source image
	 */
	public static void invert(RasterImage img) {
		int mask = 0xff;
		
		for (int a = 0; a < img.argb.length; a++) {
			// get color
			int color = img.argb[a];
			int red = (color >> 16) & mask;
			int green = (color >> 8) & mask;
			int blue = color & mask;
			// invert each channel
			red = 255 - red;
			green = 255 - green;
			blue = 255 - blue;
			//write
			img.argb[a] = (mask << 24) | (red << 16) | (green << 8) | blue;
		}
	}
	
	/**
	 * Morphologic filter: Erosion (with 4N SE).
	 * If foreground and background are switched, the operations have to be switched, too!
	 * In this case, the foreground is black (switched), so the logic is that of dilation in slides.
	 * @param src source image
	 * @param dst destination image (will be overwritten)
	 */
	public static void erode(RasterImage src, RasterImage dst) {
		// fest verdrahtetes Strukturelement
		// erode und dilate sind vertauscht, weil Vorder- und Hintergrund vertauscht sind!
		int index, ind, valr, valg, valb;
		checkDimen(dst, dst);
		
		// traverse image pixels
		for (int h = 0; h < src.height; h++) {
			for (int w = 0; w < src.width; w++) {
				
				ind = index = h * src.width + w;
				valr = valg = valb = 0;	// set to minimum value i.o. to later find maximum!

				// structured element with 4-neighbourhood: find maximum at 5 different positions
				// border handling: border pixels are ignored
				for (int i = 0; i < 5; i++) {
					switch (i){
					case 0:	// north
						index = (h > 0) ? ((h-1) * src.width + w) : (h * src.width + w);
						break;
					case 1: // south
						index = (h < src.height - 1) ? ((h+1) * src.width + w) : (h * src.width + w);
						break;
					case 2: // west
						index = (w > 0) ? (h * src.width + w - 1) : (h * src.width + w);
						break;
					case 3: // east
						index = (w < src.width - 1) ? (h * src.width + w + 1) : (h * src.width + w);
						break;
					case 4: // hot spot
					default: 
							index = h * src.width + w;
					}
					valr = max(((src.argb[index]>> 16) & 0xff) + 1, valr);
					valg = max(((src.argb[index]>> 8) & 0xff) + 1, valg);
					valb = max((src.argb[index] & 0xff) + 1, valb);
				}
				
				// clamp values and write
				dst.argb[ind] = calcPixel(valr, valg, valb);
			}
		}
	}
	
		/**
		 * Morphologic filter: Dilation (with 4N SE).
		 * Dilation = Erosion of inverted picture plus Inversion
		 * @param src source image
		 * @param dst destination image (will be overwritten)
		 */
		public static void dilate(RasterImage src, RasterImage dst) {
			// Dilatation = Inversion der Erosion des invertierten Bildes
			RasterImage inv = new RasterImage(src);
			invert(inv);
			erode(inv, dst);
			invert(dst);
		}
	
	/**
	 * calculate intersection of 2 images
	 * @param src source image (first image of intersection)
	 * @param dst 2nd image of intersection which will receive the result
	 */
	public static void intersect(RasterImage src, RasterImage dst) {
		checkDimen(src, dst);
		
		int bg = 0xffffffff;
		
		for (int a = 0; a < src.argb.length; a++) {
			if (src.argb[a] == dst.argb[a]) {
				dst.argb[a] = src.argb[a];
			} else {
				dst.argb[a] = bg;
			}
		}
	}
		
	/* HELPERS */
	
	/**
	 * check if dimensions of 2 images match 
	 * @throws ArrayIndexOutOfBoundsException if dimensions do not match
	 * @param src 1st image
	 * @param dst 2nd image
	 */
	private static void checkDimen (RasterImage src, RasterImage dst) {
		if ((src.height != dst.height) || (src.width != dst.width) ) 
			throw new ArrayIndexOutOfBoundsException("Source and destination image do not match!");

	}
	
	/**
	 * get maximum of 2 numbers
	 * @param a first number
	 * @param b second number
	 * @return maximum
	 */
	private static int max(int a, int b) {
		if (a >= b) {
			return a;
		} else {
			return b;
		}
	}
	
	/**
	 * get minimum of 2 numbers
	 * @param a first number
	 * @param b second number
	 * @return minimum
	 */
	private static int min(int a, int b) {
		if (a <= b) {
			return a;
		} else {
			return b;
		}
	}
	
	/**
	 * calculate pixel value and do all the clamping
	 * @param r RED value
	 * @param g GREEN value
	 * @param b BLUE value
	 * @return pixel value
	 */
	private static int calcPixel(int r, int g, int b) {
		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		if (r > 255) r = 255;
		if (g > 255) g = 255;
		if (b > 255) b = 255;
		
		return ((0xff << 24) | (r << 16) | (g << 8) | b);
	}
	
}

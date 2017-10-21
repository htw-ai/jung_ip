// IP Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-08-18

package ip_ws1718;

import java.util.Arrays;

public class Filter {
	
	public static void outline(RasterImage src, RasterImage dst) {
		// TODO: implement outline filter
		//invert(src, dst);
		dilate(src, dst);
		//erode(src, dst);
	}
	
	/**
	 * invert each pixel (works for color & b/w images)
	 * @param src source image
	 * @param dst destination image
	 */
	public static void invert(RasterImage src, RasterImage dst) {
		checkDimen(dst, dst);
		int mask = 0xff;
		
		for (int a = 0; a < src.argb.length; a++) {
			// get color
			int color = src.argb[a];
			int red = (color >> 16) & mask;
			int green = (color >> 8) & mask;
			int blue = color & mask;
			// invert each channel
			red = 255 - red;
			green = 255 - green;
			blue = 255 - blue;
			//write
			dst.argb[a] = (mask << 24) | (red << 16) | (green << 8) | blue;
		}
	}
	
	// fest verdrahtetes Strukturelement
	// erode und dilate sind vertauscht, weil Vorder- und Hintergrund vertauscht sind!
	public static void dilate(RasterImage src, RasterImage dst) {
		int index, ind, valr, valg, valb;
		checkDimen(dst, dst);
		
		// traverse image pixels
		for (int h = 0; h < src.height; h++) {
			for (int w = 0; w < src.width; w++) {
				
				ind = index = h * src.width + w;
				// TODO: Rand!
				valr = valg = valb = 255;	// set to maximum value i.o. to later find minimum!

				// structured element with 4-neighbourhood: find maximum at 5 different positions
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
					valr = min(((src.argb[index]>> 16) & 0xff) - 1, valr);
					valg = min(((src.argb[index]>> 8) & 0xff) - 1, valg);
					valb = min((src.argb[index] & 0xff) - 1, valb);
				}
				
				// clamp values and write
				dst.argb[ind] = calcPixel(valr, valg, valb);
			}
		}
	}
	
	// Erosion = Inversion der Dilatation des invertierten Bildes
	public static void erode(RasterImage src, RasterImage dst) {
		RasterImage inv = new RasterImage(src.width, src.height);
		invert(src, inv);
		
		RasterImage dil = new RasterImage(src.width, src.height);
		dilate(inv, dil);
		
		invert(dil, dst);
	}
	
	private static void checkDimen (RasterImage src, RasterImage dst) {
		if ((src.height != dst.height) || (src.width != dst.width) ) 
			throw new ArrayIndexOutOfBoundsException("Source and destination image do not match!");

	}
	
	/**
	 * helper: do all the shifting and masking to get red, green and blue and compute luminosity by adding these and dividing by 3
	 * @param color	pixel value as int
	 * @return gray level 0 - 255
	 */
	
	private static int getGrayValue(int color) {
		int mask = 0xff;
		// return luminosity (gray value)
		int red = (color >> 16) & mask;
		int green = (color >> 8) & mask;
		int blue = color & mask;
		int lum = (red + green + blue) / 3;
		
		return lum;
	}
	
	private static int max(int a, int b) {
		if (a >= b) {
			return a;
		} else {
			return b;
		}
	}
	
	private static int min(int a, int b) {
		if (a <= b) {
			return a;
		} else {
			return b;
		}
	}
	
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

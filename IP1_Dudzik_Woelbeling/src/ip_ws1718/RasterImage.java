// IP Ue1 WS2017/18 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-08-18

package ip_ws1718;

import java.io.File;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RasterImage {
	
	private static final int gray  = 0xffa0a0a0;
	private static final int white = 0xffffffff;
	private static final int black = 0xff000000;
	private static final int mask = 0xff;

	public int[] argb;	// pixels represented as ARGB values in scanline order
	public int width;	// image width in pixels
	public int height;	// image height in pixels
	
	public RasterImage(int width, int height) {
		// creates an empty RasterImage of given size
		this.width = width;
		this.height = height;
		argb = new int[width * height];
		Arrays.fill(argb, gray);
	}
	
	public RasterImage(RasterImage image) {
		// copy constructor
		width = image.width;
		height = image.height;
		argb = image.argb.clone();
	}
	
	public RasterImage(File file) {
		// creates an RasterImage by reading the given file
		Image image = null;
		if(file != null && file.exists()) {
			image = new Image(file.toURI().toString());
		}
		if(image != null && image.getPixelReader() != null) {
			width = (int)image.getWidth();
			height = (int)image.getHeight();
			argb = new int[width * height];
			image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
		} else {
			// file reading failed: create an empty RasterImage
			this.width = 256;
			this.height = 256;
			argb = new int[width * height];
			Arrays.fill(argb, gray);
		}
	}
	
	public RasterImage(ImageView imageView) {
		// creates a RasterImage from that what is shown in the given ImageView
		Image image = imageView.getImage();
		width = (int)image.getWidth();
		height = (int)image.getHeight();
		argb = new int[width * height];
		image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
	}
	
	public void setToView(ImageView imageView) {
		// sets the current argb pixels to be shown in the given ImageView
		if(argb != null) {
			WritableImage wr = new WritableImage(width, height);
			PixelWriter pw = wr.getPixelWriter();
			pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argb, 0, width);
			imageView.setImage(wr);
		}
	}
	
	// image point operations to be added here
	
	public void binarizeWithThreshold(int threshold) {
		// binarize the image with the given threshold
		for (int i = 0; i < argb.length; i++) {
			int gray =  getGrayValue(argb[i]);
			argb[i] = (gray <= threshold) ? black : white;	// black is foreground
		}
	}
	
	/**
	 * 
	 * @return the threshold computed by iso-data
	 */
	public int binarizeWithIsoData() {
		int threshold = findThresholdIsodata();
		binarizeWithThreshold(threshold);
		return threshold;
	}
	
	/**
	 * the ISODATA algorithm
	 * @return computed threshold
	 */
	private int findThresholdIsodata(){
		int start = 128;	// start value
		int t, tLower, tHigher;
		
		int[] hist = getHistogram(this.argb);
		
		do {
			t = start;
			tLower = tHigher = 0;
			int sumLower = 0, sumHigher = 0;
			
			if (start == 0 || start == 255)		// in case whole image is black or white -> no 2 parts
				break;
			
			// first find medium gray in lower part
			for (int a = 0; a < t; a++) {
				tLower += (hist[a]*a);
				sumLower += hist[a];
			}
			if (sumLower > 0) {
				tLower /= sumLower;
			} else {
				// lower part is empty -> try higher threshold
				start += (hist.length - t) / 2;
				continue;
			}
			
			// second, find medium gray in upper part
			for (int a = t; a < hist.length; a++) {
				tHigher += (hist[a]*a);
				sumHigher += hist[a];
			}
			if (sumHigher > 0) {
				tHigher /= sumHigher;
			} else {
				// upper part is empty -> try lower threshold
				//if (start > 0)
					start /= 2;
				continue;
			}
			
			// adjust threshold
			start = (tLower + tHigher)/2;
			
		} while (start != t);	// no new threshold: break!
		
		return t;
	}
	
	/**
	 * compute histogram
	 * @param img the image array (pixels represented as ARGB values in scanline order)
	 * @return the histogram array (slots 0 - 255)
	 */
	
	private int[] getHistogram(int[] img) {
		// return a graylevel histogram
		int[] hist = new int[256];
		
		for (int i = 0; i < img.length; i++) {
			int gray = getGrayValue(img[i]);
			hist[gray]++;
		}
		return hist;
	}
	
	/**
	 * helper: do all the shifting and masking to get red, green and blue and compute luminosity by adding these and dividing by 3
	 * @param color	pixel value as int
	 * @return gray level 0 - 255
	 */
	
	private int getGrayValue(int color) {
		// return luminosity (gray value)
		int red = (color >> 16) & mask;
		int green = (color >> 8) & mask;
		int blue = color & mask;
		int lum = (red + green + blue) / 3;
		
		return lum;
	}

}

// IP Ue2 WS2017/18
//
// Date: 2017-11-05

package ip_ws1718;

import java.io.File;
import java.util.Arrays;

import java.util.LinkedList;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RasterImage {
	
	private static final int gray  = 0xffa0a0a0;
	private static final int white = 0xffffffff;
	private static final int black = 0xff000000;

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
	
	private int getIndex(int i, int j) {
		return i*width+j;
	}
	
	class PPoint {
		public int i;	
		public int j;	
		public int color;
		
		PPoint(int i, int j, int color) {
			this.i = i;
			this.j = j;
			this.color = color;
		}
	}
	
	private static int generateColor(Random rand) {
	    int r = rand.nextInt(255);
	    int g = rand.nextInt(255);
	    int b = rand.nextInt(255);
	    
	    return calcPixel(r, g, b);
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

	public void depthFirst() {
		int length = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            		int color = argb[getIndex(i, j)];
            	    if (color == black) {
            	    		LinkedList<PPoint> queue = new LinkedList<PPoint>();
                    Random rand = new Random();
                    int ccolor = generateColor(rand);
            	    		queue.add(new PPoint(i, j, ccolor));
                    while (!queue.isEmpty()) {
                    	   length = (queue.size() > length) ? queue.size() : length;
                    	   PPoint p = queue.removeLast();
                        if (argb[getIndex(p.i, p.j)] == black) {
                        	    argb[getIndex(p.i, p.j)] = p.color;
                            for (int ii = -1; ii < 2; ii++) {
                                for (int jj = -1; jj < 2; jj++) {
                                	PPoint pp = new PPoint(p.i + ii, p.j + jj, p.color);
                                	// Uncomment for Assignment 4
                                	// if (true) {
                                	if (argb[getIndex(pp.i, pp.j)] == black) {
                                    queue.add(pp);
                                	}
                              }
                           }
                        }
                    }
                }
            }
        }
        System.out.println("depth first max size: " + length);
	}
	
	public void breadthFirst() {
		int length = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            		int color = argb[getIndex(i, j)];
            	    if (color == black) {
            	    		LinkedList<PPoint> queue = new LinkedList<PPoint>();
                    Random rand = new Random();
                    int ccolor = generateColor(rand);
            	    		queue.add(new PPoint(i, j, ccolor));
                    while (!queue.isEmpty()) {
                    	   length = (queue.size() > length) ? queue.size() : length;
                    	   PPoint p = queue.removeFirst();
                        if (argb[getIndex(p.i, p.j)] == black) {
                        	    argb[getIndex(p.i, p.j)] = p.color;
                            for (int ii = -1; ii < 2; ii++) {
                                for (int jj = -1; jj < 2; jj++) {
                                	PPoint pp = new PPoint(p.i + ii, p.j + jj, p.color);
                                	// Uncomment for Assignment 4
                                	// if (true) {
                                	if (argb[getIndex(pp.i, pp.j)] == black) {
                                    queue.add(pp);
                                	}
                              }
                           }
                        }
                    }
                }
            }
        }
        System.out.println("breadth first max size: " + length);
	}
}


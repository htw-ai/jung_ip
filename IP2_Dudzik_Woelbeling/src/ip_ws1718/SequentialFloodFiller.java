package ip_ws1718;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class SequentialFloodFiller implements FloodFilling {
	private int foregroundColor;
	private int backgroundColor;
	int length = 0;
	int regioncount = 0;

	public SequentialFloodFiller() {
		foregroundColor = 0xff000000; // black
		backgroundColor = 0xffffffff; // white
	}

	public SequentialFloodFiller(int foregroundColor, int backgroundColor) {
		super();
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
	}

	@Override
	public void fillRegions(int[] argb, int height, int width) {
		regioncount = 2;	// seed
		HashMap<Integer, Integer> collision2 = new HashMap<>();
		HashMap<Integer, HashSet<Integer>> vec = new HashMap<>();
		HashSet<Integer> tmp = new HashSet<>();

		// traverse image and look for foreground pixels
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int index = getIndex(i, j, width);
				int pixel = argb[index];
				//System.out.println(i + ", " + j +" (" + index + "): " + pixel);
				if (pixel == foregroundColor) { // Foreground ( = new region) found

					// check four preceding neighbors
					HashSet<Integer> n = new HashSet<>();
					if (j > 0) { 
						n.add(argb[getIndex(i,j-1,width)]);
						n.add(argb[getIndex(i+1,j-1,width)]);
						if (i > 0) n.add(argb[getIndex(i-1,j-1,width)]);
					}
					if (i > 0) n.add(argb[getIndex(i-1,j,width)]);

					n.remove(backgroundColor);
					if (n.size() == 0) {	// no adjacent regions (so far)
						//argb[index] = generateColor(rand);	// new color
						tmp = new HashSet<>();
						tmp.add(regioncount);
						vec.put(regioncount, tmp);
						argb[index] = regioncount;
						regioncount++;

					} else {

						if (n.size() == 1){	// one region adjacent	
							if (n.contains(foregroundColor))
								System.out.println("Halt");
							for (int c: n) {
								argb[index] = c;
								// zur Sicherheit
								tmp = new HashSet<>();
								tmp.add(regioncount);
								vec.put(regioncount, tmp);	
							}

						} else {
							int idx = 0;
							for (int c: n) {
								if (idx == 0) {
									argb[index] = c;
									idx++;
								} else {
									collision2.put(c, argb[index]);
								}
							}
						}
					}
				}
			}
		}


		//**** step 2: resolve label collisions
	
		// Kollisionen ersetzen
		for (Map.Entry<Integer, Integer> entry : collision2.entrySet()) {
			int k = entry.getKey();
			int v = entry.getValue();
			if (k != v) {
				if (collision2.containsKey(v)) {
					collision2.put(k, collision2.get(v));
				}
			}
		}
		// build vector of sets
		for (Map.Entry<Integer, Integer> entry : collision2.entrySet()) {
			int a = entry.getKey();
			int b = entry.getValue();
			if (a != b) {
				HashSet<Integer> tmp1 = vec.get(a);
				HashSet<Integer> tmp2 = vec.get(b);
				if (tmp1 != null && tmp2 != null) tmp1.addAll(tmp2);
				vec.put(b, tmp1);	
				vec.put(a, null);
			}
		}

		//**** step 3: color!
		
		// build color map
		Random rand = new Random();
		HashMap<Integer, Integer> colormap = new HashMap<>();
		for (HashSet<Integer> r: vec.values()) {
			if (r == null) continue;
			int col = generateColor(rand);	// new color
			for (Integer region: r) {
				colormap.put(region, col);
			}
		}

		// color pixels
		for (int p = 0; p < argb.length; p++) {
			if (argb[p] != backgroundColor) {

				if (colormap.containsKey(argb[p])) {

					argb[p] = colormap.get(argb[p]);
				} else {
					System.out.println(argb[p]);
				}
			}
		}

	}
	
	@Override
	public void printSize() {
		System.out.println("collision count: " + (regioncount - 2));

	}
	
	@Override
	public int getStackSize() {
		return (regioncount - 2);
	}

	/* Helpers */
	
	public int generateColor(Random rand) {
		int r = rand.nextInt(256);
		int g = rand.nextInt(256);
		int b = rand.nextInt(256);

		int col = calcPixel(r, g, b);
		if (col == getForegroundColor()) {		// must not be foreground color!!!
			col = generateColor(rand);
		}
		return col;
	}

	public static int calcPixel(int r, int g, int b) {
		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		if (r > 255) r = 255;
		if (g > 255) g = 255;
		if (b > 255) b = 255;

		return ((0xff << 24) | (r << 16) | (g << 8) | b);
	}

	public static int getIndex(int x, int y, int width) {
		return y * width + x;
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
	
	public boolean isWithinBoundaries(int x, int y, int width, int height) {
		if (x < 0 || x >= width) return false;
		if (y < 0 || y >= height) return false;
		return true;
	}


	public int getForegroundColor() {
		return this.foregroundColor;
	}


	


}

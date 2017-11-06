package ip_ws1718;

import java.util.Random;

public interface IterativeFloodFiller extends FloodFilling {


	@Override
	public default void fillRegions(int[] argb, int height, int width) {
		
		// traverse image and look for foreground pixels
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int pixel = argb[getIndex(i, j, width)];
				if (isForegroundColor(pixel)) {
					
					// Foreground ( = new region) found -> search for connected pixels
					List<PPoint> list = createList();
					Random rand = new Random();
					int regioncolor = generateColor(rand);	// define region color
					list.add(new PPoint(i, j, regioncolor));	// seed
					
					// search for all pixels of that region
					while (!list.isEmpty()) {
						saveLength(list);
						PPoint p = list.remove();		// take item
						int ind = getIndex(p.i, p.j, width);
						//if (argb[getIndex(p.i, p.j, width)] == getForeGroundColour()) {	
						if (isWithinBoundaries(p.i, p.j, width, height) && isForegroundColor(argb[ind])) {	// 1. check
							argb[ind] = p.color;	// 2. define region (change color)
							for (int jj = -1; jj < 2; jj++) {	// 3. put all neighbours on list
								for (int ii = -1; ii < 2; ii++) {
									PPoint pp = new PPoint(p.i + ii, p.j + jj, p.color);
									// TODO: only check for assignment 4
									// TODO: check boundaries
									/*if (argb[getIndex(pp.i, pp.j, width)] == getForeGroundColour()) {
										list.add(pp);
									}*/
									addList(list, pp, argb[getIndex(pp.i, pp.j, width)], width, height);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void addList(List<PPoint> list, PPoint pp, int pixelcolor, int width, int height);

	public int getForegroundColor();

	public List<PPoint> createList();

	public void saveLength(List<PPoint> l);

	/* Helpers */
	
	default int generateColor(Random rand) {
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
	
	default boolean isForegroundColor (int col) {
		if (col == getForegroundColor()) {
			return true;
		}
		return false;
	}
	
	default boolean isWithinBoundaries(int x, int y, int width, int height) {
		if (x < 0 || x >= width) return false;
		if (y < 0 || y >= height) return false;
		return true;
	}

}

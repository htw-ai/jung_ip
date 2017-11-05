package ip_ws1718;

import java.util.Random;

public interface IterativeFloodFiller extends FloodFilling {


	@Override
	public default void fillRegions(int[] argb, int height, int width) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int color = argb[getIndex(i, j, width)];
				if (color == getForeGroundColour()) {
					List<PPoint> list = createList();
					Random rand = new Random();
					int ccolor = generateColor(rand);
					list.add(new PPoint(i, j, ccolor));
					while (!list.isEmpty()) {
						saveLength(list);
						PPoint p = list.remove();
						if (argb[getIndex(p.i, p.j, width)] == getForeGroundColour()) {
							argb[getIndex(p.i, p.j, width)] = p.color;
							for (int ii = -1; ii < 2; ii++) {
								for (int jj = -1; jj < 2; jj++) {
									PPoint pp = new PPoint(p.i + ii, p.j + jj, p.color);
									// Uncomment for Assignment 4
									// if (true) {
									if (argb[getIndex(pp.i, pp.j, width)] == getForeGroundColour()) {
										list.add(pp);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public int getForeGroundColour();

	public <T> List<T> createList();

	<T> void saveLength(List<T> l);

	/* Helpers */
	
	public static int generateColor(Random rand) {
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);

		return calcPixel(r, g, b);
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

	public static int getIndex(int i, int j, int width) {
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

}

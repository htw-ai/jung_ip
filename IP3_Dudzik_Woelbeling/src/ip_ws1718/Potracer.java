package ip_ws1718;

import java.awt.Point;
import java.util.ArrayList;

public class Potracer {

	static int SOUTH = 0;
	static int WEST = 1;
	static int NORTH = 2;
	static int EAST = 3;
	static int LEFT = 3;
	static int RIGHT = 1;

	private RasterImage img;
	static int FOREGROUNDCOLOR = 0xff000000;


	public Potracer(RasterImage img) {
		this.img = img;
	}

	public ArrayList<Kontur> scan () {
		ArrayList<Kontur> paths = new ArrayList<Kontur>();
		Kontur k;

		for (int y = 0; y < img.height; y++) {
			for (int x = 0; x < img.width; x++) {
				int pixel = img.getPixel(x, y);
				if (isForegroundColor(pixel)) {
					k = findPaths(x,y);
					invertContour(k);
					paths.add(k);
				}
			}
		}

		return paths;
	}

	public Kontur findPaths(int x, int y) {
		Point start = new Point(x, y);
		int dir2 = SOUTH;
		Point current = start;
		Kontur path = new Kontur();
		path.addVertex(start);
		Pixel tps, tpd;

		do {
			current = getNextVertex(current, dir2);	// go in direction
			path.addVertex(current);
			tpd = getNextPixelDiag(current, dir2);
			tps = getNextPixelStraight(current, dir2);

			if (isForegroundColor(img.getPixel(tpd.x, tpd.y))) {
				// turn right (GO RIGHT)
				dir2 = (dir2 + RIGHT) % 4;
			} else if (isForegroundColor(img.getPixel(tps.x, tps.y))) {
				// don't change direction (GO STRAIGHT)
			} else {
				// turn left (GO LEFT)
				dir2 = (dir2 + LEFT) % 4;
			}

		} while (!current.equals(start));

		return path;
	}

	public void invertContour (Kontur k) {
		Point first = k.getVertex(0);
		for (Point next: k.getVertices()) {
			if (next.equals(first)) continue;
			if (first.y < next.y) {
				invertRow(first.y, first.x);
			} else if (first.y > next.y) {
				invertRow(next.y, next.x);
			}
			first = next;
		}	
	}

	private void invertRow(int row, int startcol) {
		for (int x = startcol; x < img.width; x++) {
			int color = img.getPixel(x, row);
			color = (color == 0xffffffff)? 0xff000000 : 0xffffffff;		// invert color
			img.setPixel(x, row, color);
		}
	}

	private static Point getNextVertex(Point p, int d) {
		if (d == SOUTH) {
			return new Point(p.x, p.y + 1);
		} else if (d == WEST) {
			return new Point(p.x - 1, p.y);
		} else if (d == EAST) {
			return new Point(p.x + 1, p.y);
		} else if (d == NORTH) {
			return new Point(p.x, p.y - 1);
		} else {
			return null;
		}
	}

	private Pixel getNextPixelDiag(Point p, int d) {
		if (d == SOUTH) {
			return new Pixel(p.x - 1, p.y);
		} else if (d == WEST) {
			return new Pixel(p.x - 1, p.y - 1);
		} else if (d == EAST) {
			return new Pixel(p.x, p.y);
		} else if (d == NORTH) {
			return new Pixel(p.x, p.y - 1);
		} else {
			return null;
		}
	}

	private Pixel getNextPixelStraight(Point p, int d) {
		if (d == SOUTH) {
			return new Pixel(p.x, p.y);
		} else if (d == WEST) {
			return new Pixel(p.x - 1, p.y);
		} else if (d == EAST) {
			return new Pixel(p.x, p.y - 1);
		} else if (d == NORTH) {
			return new Pixel(p.x - 1, p.y - 1);
		} else {
			return null;
		}
	}

	public static boolean isForegroundColor (int col) {
		return col == FOREGROUNDCOLOR;
	}

	public boolean isForegroundPixel (Point p) {
		// if out of bounds: background
		if (p.x < 0 || p.y < 0) 
			return false;
		if (p.x >= img.width || p.y >= img.height) 
			return false;

		// test pixel color
		return isForegroundColor(img.getPixel(p.x, p.y));
	}

	private class Pixel {
		public int x;
		public int y;
		public Pixel(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}

}
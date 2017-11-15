package ip_ws1718;

import java.awt.Point;
import java.util.ArrayList;

public class Potracer {

	public enum GlobalDirection {
		SOUTH(0), 
		WEST(1), 
		NORTH(2), 
		EAST(3);
		private final int value;

		private GlobalDirection(int value) {
			this.value = value;
		}
	};
	
	static int SOUTH = 0;
	static int WEST = 1;
	static int NORTH = 2;
	static int EAST = 3;
	static int LEFT = 3;
	static int RIGHT = 1;
	static int STRAIGHT = 0;

	public enum Direction {
		LEFT, STRAIGHT, RIGHT
	}
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
		//GlobalDirection dir = GlobalDirection.SOUTH;
		int dir2 = SOUTH;
		Point current = start;
		Kontur path = new Kontur();
		path.addVertex(start);
		Point tps, tpd;
				
		do {
			current = getNextVertex(current, dir2);	// go in direction
			path.addVertex(current);

			tps = getNextPixelStraight(current, dir2);
			tpd = getNextPixelDiag(current, dir2);
					//testpixel = img.getPixel(current.x - 1, current.y + 1);	// TODO. nur zu Testzwecken
					if (isForegroundColor(img.getPixel(tpd.x, tpd.y))) {
						//dir.value = (dir.value + RIGHT) % 4;
						dir2 = (dir2 + RIGHT) % 4;
					} else if (isForegroundColor(img.getPixel(tps.x, tps.y))) {
						//dir.value = (dir.value + STRAIGHT) % 4;
						dir2 = (dir2 + STRAIGHT) % 4;	// weglassen
					} else {
						//dir.value = (dir.value + LEFT) % 4;
						dir2 = (dir2 + LEFT) % 4;
					}
				
			} while (!current.equals(start));
		
		//path.addVertex(path.getVertex(0));
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
		Point np = p;
		if (d == SOUTH) {
			np = new Point(p.x, p.y + 1);
		} else if (d == WEST) {
			//if (p.x == 0) throw new ArrayIndexOutOfBoundsException();
			np = new Point(p.x - 1, p.y);
		} else if (d == EAST) {
			np = new Point(p.x + 1, p.y);
		} else if (d == NORTH) {
			np = new Point(p.x, p.y - 1);
		}
		
		return np;
	}
	
	private Point getNextPixelDiag(Point p, int d) {
		Point np = p;
		if (d == SOUTH) {
			np = new Point(p.x - 1, p.y);
		} else if (d == WEST) {
			//if (p.x == 0) throw new ArrayIndexOutOfBoundsException();
			np = new Point(p.x - 1, p.y - 1);
		} else if (d == EAST) {
			np = new Point(p.x, p.y);
		} else if (d == NORTH) {
			np = new Point(p.x, p.y - 1);
		}
		
		return np;
	}
	
	private Point getNextPixelStraight(Point p, int d) {
		Point np = p;
		if (d == SOUTH) {
			np = new Point(p.x, p.y);
		} else if (d == WEST) {
			np = new Point(p.x - 1, p.y);
		} else if (d == EAST) {
			np = new Point(p.x, p.y - 1);
		} else if (d == NORTH) {
			np = new Point(p.x - 1, p.y - 1);
		}
		
		return np;
	}
	
	public static boolean isForegroundColor (int col) {
        return col == FOREGROUNDCOLOR;
    }
	
	public boolean isForegroundPixel (Point p) {
		// if out of bounds: background
		if (p.x < 0 || p.y < 0) return false;
		if (p.x >= img.width || p.y >= img.height) return false;
		
		// test pixel color
		return isForegroundColor(img.getPixel(p.x, p.y));
	}

}

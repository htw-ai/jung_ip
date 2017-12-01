// IP Ue4 WS2017/18
//
// Date: 2017-12-01

package ip_ws1718;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Potracer {
	enum Direction {NORTH, SOUTH, WEST, EAST};

	public Potracer() {
		// TODO Auto-generated constructor stub
	}
	
	public static ArrayList<Path> getPaths(ArrayList<Kontur> regions) {
		Point c0, c1, d;
		Point[] points;
		Point vec;
		HashSet<Direction> dir;
		int[] pivot;
		
		for (Kontur region: regions) {
			// init
			c0 = new Point(0,0);
			c1 = new Point(0,0);
			points = new Point[region.getVertices().size()];
			region.getVertices().toArray(points);
			pivot = new int[region.getVertices().size() - 1];	// one less, because start point = end point of contour

			for (int start = 0; start < points.length - 1; start++) {	// caution: start point = end point in list!
				Point i = points[start];
				int indexk = (start == points.length - 2) ? 0 : start + 1;
				Point k = points[indexk];
				dir = new HashSet<Direction>();
				dir.add(getDirection(i,k));
				
				//while (dir.size() < 4) {
				//while (k != i) {
				while(dir.size() < 4 && k != i) {
					//if (dir.size() == 4) break;	// Abbruch
					
					// calculate vector from i to k
					vec = new Point(k.x - i.x, k.y - i.y);
					
					//if constraint verletzt -> Abbruch
					if (crossproduct(c0,vec) < 0 || crossproduct(c1,vec) < 0) break;	// see p. 11
					
					// update constraint
					if (Math.abs(vec.x) > 1 || Math.abs(vec.y) > 1) {	// see p.12 (vec = a)
						
						// update c0, see slide 13 (vec = a)
						d = new Point();
							d.x = (vec.y >= 0 && (vec.y > 0 || vec.x < 0)) ? vec.x + 1 : vec.x - 1;
							d.y = (vec.x <= 0 && (vec.x < 0 || vec.y < 0)) ? vec.y + 1 : vec.y - 1;
							if (crossproduct(c0,d) >= 0) c0 = d;
							
							// update c1, see slide 14 (vec = a)
							d = new Point();
							d.x = (vec.y <= 0 && (vec.y < 0 || vec.x < 0)) ? vec.x + 1 : vec.x - 1;
							d.y = (vec.x >= 0 && (vec.x > 0 || vec.y < 0)) ? vec.y + 1 : vec.y - 1;
							if (crossproduct(c1,d) <= 0) c1 = d;
					}
					
					// note
					pivot[start] = indexk;
					
					// get next target point's index
					indexk = (indexk == points.length - 2) ? 0: indexk + 1;
					k = points[indexk];
					dir.add(getDirection(i,k));
				} 
			}
		}
		
		return null;
	}
	
	private static Direction getDirection (Point a, Point b) {
		if (a.x == b.x) {
			if (a.y > b.y) {
				return Direction.NORTH;
			} else {
				return Direction.SOUTH;
			}
		} else {
			if (a.x > b.x) {
				return Direction.WEST;
			} else {
				return Direction.EAST;
			}
		}
	}

	private static int crossproduct (Point a, Point b) {
		return a.x * b.y - a.y * b.x;
	}
}

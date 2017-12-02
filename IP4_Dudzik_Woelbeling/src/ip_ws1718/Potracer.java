// IP Ue4 WS2017/18
//
// Date: 2017-12-01

package ip_ws1718;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

public class Potracer {
	enum Direction {NORTH, SOUTH, WEST, EAST};

	
	public static ArrayList<Kontur> getPolygons(ArrayList<Kontur> regions) {
		ArrayList<Kontur> polygons = new ArrayList<Kontur>();
		for (Kontur region: regions) {
			polygons.add(getPolygon(region));
		}
		return polygons;
	}
	
 	private static int[] getStraightPaths(Kontur region) {
		Point[] points;
		int[] pivot;

		// init
		points = new Point[region.getVertices().size()];
		region.getVertices().toArray(points);
		pivot = new int[region.getVertices().size() - 1];	// one less, because start point = end point of contour

		for (int indexi = 0; indexi < points.length - 1; indexi++) {	// caution: start point = end point in list!
			HashSet<Direction> dir = new HashSet<Direction>();
			int hilf;
			int indexk = indexi;
			Point i = points[indexi];
			Point c0 = new Point(0,0);
			Point c1 = new Point(0,0);

			do {
				hilf = indexk;
				indexk = (indexk == points.length - 2) ? 0 : indexk + 1;
				Point k = points[indexk];
				dir.add(getDirection(points[hilf], k));

				// falls mehr als 3 Richtungen -> Abbruch!
				if (dir.size() > 3) break;

				// calculate vector from i to k
				Point vec = new Point(k.x - i.x, k.y - i.y);

				//if constraint verletzt -> Abbruch
				if (crossproduct(c0,vec) < 0 || crossproduct(c1,vec) > 0) break;	// see p. 11

				// update constraint
				if (Math.abs(vec.x) > 1 || Math.abs(vec.y) > 1) {	// see p.12 (vec = a)

					// update c0, see slide 13 (vec = a)
					Point d = new Point();
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
				pivot[indexi] = indexk;

			} while(true);	// Endlosschleife
		}
		return pivot;
	}
	
	private static int[] getPossibleSegments(int[] straightPaths) {
		int n = straightPaths.length;
		int[] possible = new int[n];
		int j;
		int diff  = 0;
		for (int i = 1; i < n; i++) {
			j = straightPaths[i-1] -1;
			if (j < 0) j += n;
			diff = (j < i) ? j - i + n : j - i;
			//if (diff <= n - 3 && straightPaths[i] == j) {
			if (diff <= n - 3) {
				possible[i] = j;
			} else {
				// TODO
				System.out.println("ToDo!");
			}
		}
		possible[0] = straightPaths[n - 1] - 1;	// TODO: auch prüfen?
		return possible;
	}
	
	private static Kontur getPolygon(Kontur region) {
		int start = 0;
		return getPolygon(region, start);
	}
	
	private static Kontur getPolygon(Kontur region, int start) {
		Kontur polygon = new Kontur();
		ArrayList<Point> points = region.getVertices();		
		int[] possibleSegments = getPossibleSegments(getStraightPaths(region));
		int to = start;
		int from = start;
		
		//try {
			while (!(to >= start && (from > to || from < start))){	// TODO: lesbarer machen
				polygon.addVertex(points.get(to));
				from = to;
				to = possibleSegments[from];
			}
			/*
			 * Abbruch:
			 * - from > to + to > start (start 0; from 3 und to 1)
			 * - from > to + to = start (start 0; from 3 und to 0)
			 * - from < start + to > start (start 2; from 1 und to 3)
			 * - from < start + to = start (start 1; from 0 und to 1)
			 */
			polygon.addVertex(points.get(start));
		//} catch (ArrayIndexOutOfBoundsException e) {
		//	System.out.println("Fehler: ");
		//	System.out.println("Polyline: " + from + " -> " + to + " (start " + start + ")");
		//	System.out.println("Konturlänge: " + points.size());
		//}

		return polygon;
	}
	
	private static Direction getDirection(Point a, Point b) {
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

	private static int crossproduct(Point a, Point b) {
		return a.x * b.y - a.y * b.x;
	}
}

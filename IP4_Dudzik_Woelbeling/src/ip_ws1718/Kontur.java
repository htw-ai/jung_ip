// IP Ue3 WS2017/18
//
// Date: 2017-11-16

package ip_ws1718;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Kontur-Klasse
 * @author Laura Wölbeling
 *
 */
public class Kontur {

	/**
	 * Type of contour (internal or external (or not determined))
	 * @author Laura Wölbeling
	 */
	public enum Contourtype {internal, external, undetermined};
	private Contourtype type;
	private ArrayList<Point> vertices;

	/**
	 * Constructor: set contour type as undetermined and initialize list of vertices
	 */
	public Kontur() {
		vertices = new ArrayList<Point>();
		type = Contourtype.undetermined;
	}

	/**
	 * add single vertex
	 * @param p vertex
	 */
	public void addVertex(Point p) {
		vertices.add(p);
	}

	/**
	 * get type of contour
	 * @return type of contour
	 */
	public Contourtype getType() {
		return type;
	}

	/**
	 * set type of contour
	 * @param type type of contour
	 */
	public void setType(Contourtype type) {
		this.type = type;
	}

	/**
	 * get list of vertices
	 * @return list of vertices
	 */
	public ArrayList<Point> getVertices() {
		return vertices;
	}

	/**
	 * set all vertices
	 * @param vertices list
	 */
	public void setVertices(ArrayList<Point> vertices) {
		this.vertices = vertices;
	}

	/**
	 * get single contour point with index i
	 * @param index index i
	 * @return contour point
	 */
	public Point getVertex (int index) {
		return vertices.get(index);
	}

}

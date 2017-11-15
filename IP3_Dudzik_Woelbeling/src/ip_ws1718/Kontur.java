package ip_ws1718;

import java.awt.Point;
import java.util.ArrayList;

public class Kontur {

	public enum Contourtype {internal, external, undetermined};
	private Contourtype type;
	private ArrayList<Point> vertices;
	
	public Kontur() {
		vertices = new ArrayList<Point>();
		type = Contourtype.undetermined;
	}
	
	public void addVertex(Point p) {
		vertices.add(p);
	}

	public Contourtype getType() {
		return type;
	}

	public void setType(Contourtype type) {
		this.type = type;
	}

	public ArrayList<Point> getVertices() {
		return vertices;
	}

	public void setVertices(ArrayList<Point> vertices) {
		this.vertices = vertices;
	}
	
	public Point getVertex (int index) {
		return vertices.get(index);
	}

}

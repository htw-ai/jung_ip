// IP Ue4 WS2017/18
//
// Date: 2017-12-01

package ip_ws1718;

import java.awt.Point;

public class Path {
	enum PathType {straight, possible, undefined};
	
	private Point start;
	private Point end;
	private PathType type;

	public Path(Point start, Point end, PathType type) {
		super();
		this.start = start;
		this.end = end;
		this.type = type;
	}



	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	public PathType getType() {
		return type;
	}

	public void setType(PathType type) {
		this.type = type;
	}
	
	

}

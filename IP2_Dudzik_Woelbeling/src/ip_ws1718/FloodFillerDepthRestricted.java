package ip_ws1718;

public class FloodFillerDepthRestricted extends FloodFillerDepth {

	public FloodFillerDepthRestricted() {
		super();
	}

	public FloodFillerDepthRestricted(int foregroundColor) {
		super(foregroundColor);
	}

	@Override
	public void addList(List<PPoint> list, PPoint pp, int pixelcolor, int width, int height) {
		if (isWithinBoundaries(pp.i, pp.j, width, height) && isForegroundColor(pixelcolor))
			list.add(pp);
	}
	
	

}

package ip_ws1718;

public class FloodFillerDepth implements IterativeFloodFiller {

	private int foregroundColor;
	int length = 0;

	public FloodFillerDepth() {
		foregroundColor = 0xff000000; // black
	}

	public FloodFillerDepth(int foregroundColor) {
		super();
		this.foregroundColor = foregroundColor;
	}

	@Override
	public void printSize() {
		System.out.println("depth first max size: " + length);

	}

	@Override
	public int getForegroundColor() {
		return this.foregroundColor;
	}

	@Override
	public List<PPoint> createList() {
		return new Stack<PPoint>();
	}

	@Override
	public void saveLength(List<PPoint> stack) {
		length = (stack.getLength() > length) ? stack.getLength() : length;

	}

	@Override
	public int getStackSize() {
		return length;
	}

	@Override
	public void addList(List<PPoint> list, PPoint pp, int pixelcolor, int width, int height) {
		list.add(pp);
		
	}
	
	

}

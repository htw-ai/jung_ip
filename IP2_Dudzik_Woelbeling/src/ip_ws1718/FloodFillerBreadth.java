package ip_ws1718;

public class FloodFillerBreadth implements IterativeFloodFiller {

	private int foregroundColor;
	int length = 0;

	public FloodFillerBreadth() {
		foregroundColor = 0xff000000; // black
	}

	public FloodFillerBreadth(int foregroundColor) {
		super();
		this.foregroundColor = foregroundColor;
	}



	@Override
	public void printSize() {
		System.out.println("breadth first max size: " + length);

	}

	@Override
	public int getForegroundColor() {
		return this.foregroundColor;
	}

	@Override
	public List<PPoint> createList() {
		return new Queue<PPoint>();
	}

	@Override
	public void saveLength(List<PPoint> queue) {
		length = (queue.getLength() > length) ? queue.getLength() : length;

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

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
	public int getForeGroundColour() {
		return this.foregroundColor;
	}

	@Override
	public <T> List<T> createList() {
		return new Queue<T>();
	}

	@Override
	public <T> void saveLength(List<T> queue) {
		length = (queue.getLength() > length) ? queue.getLength() : length;

	}
	
	@Override
	public int getStackSize() {
		return length;
	}

}

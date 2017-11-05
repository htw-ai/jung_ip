package ip_ws1718;

public class FloodFillerDepth implements IterativeFloodFiller {

	private int foregroundColor;
	int length = 0;

	public FloodFillerDepth() {
		foregroundColor = 0xff000000; // black
	}

	public FloodFillerDepth(int foregroundColor, int neighbourType) {
		super();
		this.foregroundColor = foregroundColor;
	}

	@Override
	public void printSize() {
		System.out.println("depth first max size: " + length);

	}

	@Override
	public int getForeGroundColour() {
		return this.foregroundColor;
	}

	@Override
	public <T> List<T> createList() {
		return new Stack<T>();
	}

	@Override
	public <T> void saveLength(List<T> stack) {
		length = (stack.getLength() > length) ? stack.getLength() : length;

	}

	@Override
	public int getStackSize() {
		return length;
	}

}

package ip_ws1718;

public interface FloodFilling {
	public int neighbourType = 4;
	
	public void fillRegions(int[] argb, int height, int width);
	public void printSize();
	public int getStackSize();
}

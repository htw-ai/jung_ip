package ip_ws1718;

public interface List<T> {

	public void add(T item);
	public T remove();
	public T peek();
	public int getLength();
	public boolean isEmpty();
}

package ip_ws1718;

import java.util.LinkedList;

public class Queue<T> implements List<T> {
	

	private LinkedList<T> queue;
	
	

	public Queue() {
		super();
		queue = new LinkedList<T>();
	}

	@Override
	public void add(T item) {
		queue.addLast(item);
		
	}

	@Override
	public T remove() {
		return queue.removeFirst();
	}

	@Override
	public T peek() {
		return queue.peekFirst();
	}

	@Override
	public int getLength() {
		return queue.size();
	}
	
	
	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}
}

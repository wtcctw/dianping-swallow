package com.dianping.swallow.web.model.dashboard;


/**
 * @author mingdongli
 *
 * 2015年7月24日上午10:20:21
 */
public class MinHeap {

	private static final int CAPACITY = 2;

	private int size; // Number of elements in Heap
	
	private Entry[] heap; // The Heap array

	public MinHeap() {
		size = 0;
		heap = new Entry[CAPACITY];
	}

	/**
	 * Construct the binary Heap given an array of items.
	 */
	public MinHeap(Entry[] array) {
		size = array.length;
		heap = new Entry[array.length + 1];

		System.arraycopy(array, 0, heap, 1, array.length);// we do not use 0
															// index

		buildHeap();
	}

	/**
	 * runs at O(size)
	 */
	private void buildHeap() {
		for (int k = size / 2; k > 0; k--) {
			percolatingDown(k);
		}
	}

	private void percolatingDown(int k) {
		Entry tmp = heap[k];
		int child;

		for (; 2 * k <= size; k = child) {
			child = 2 * k;

			if (child != size && heap[child].compareTo(heap[child + 1]) > 0)
				child++;

			if (tmp.compareTo(heap[child]) > 0)
				heap[k] = heap[child];
			else
				break;
		}
		heap[k] = tmp;
	}

	/**
	 * Sorts a given array of items.
	 */
	public void HeapSort(Entry[] array) {
		size = array.length;
		heap = new Entry[size + 1];
		System.arraycopy(array, 0, heap, 1, size);
		buildHeap();

		for (int i = size; i > 0; i--) {
			Entry tmp = heap[i]; // move top item to the end of the Heap array
			heap[i] = heap[1];
			heap[1] = tmp;
			size--;
			percolatingDown(1);
		}
		for (int k = 0; k < heap.length - 1; k++)
			array[k] = heap[heap.length - 1 - k];
	}

	/**
	 * Deletes the top item
	 */
	public Entry deleteMin() throws RuntimeException {
		if (size == 0)
			throw new RuntimeException();
		Entry min = heap[1];
		heap[1] = heap[size--];
		percolatingDown(1);
		return min;
	}

	/**
	 * Inserts a new item
	 */
	public boolean insert(Entry x) {
		try {

			if (size == heap.length - 1)
				doubleSize();

			// Insert a new item to the end of the array
			int pos = ++size;

			// Percolate up
			for (; pos > 1 && x.compareTo(heap[pos / 2]) < 0; pos = pos / 2)
				heap[pos] = heap[pos / 2];

			heap[pos] = x;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void doubleSize() {
		Entry[] old = heap;
		heap = new Entry[heap.length * 2];
		System.arraycopy(old, 1, heap, 1, size);
	}

	public String toString() {
		String out = "";
		for (int k = 0; k < size; k++)
			out += heap[k] + " ";
		return out;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Entry[] getHeap() {
		return heap;
	}

	public void setHeap(Entry[] Heap) {
		
		this.heap = Heap;
	}

	public static void main(String[] args) {

		MinHeap entrys = new MinHeap();
		for (int i = 1; i < 10; ++i) {
			Entry e = new Entry();
			e.setNumAlarm(i);
			entrys.insert(e);
		}
		System.out.println("size is " + entrys.getSize());
		System.out.println(entrys);
		System.out.println("---------------------------");
		
		int size = entrys.getSize();
		System.out.println("size is " + size);
		Entry[] sorted = new Entry[size];
		for(int i = size - 1; i >= 0; i--){
			Entry eMin = entrys.deleteMin();
			System.out.println("num is " + eMin.getNumAlarm());
			sorted[i] = eMin;
		}
		for(int i = 0; i < size; ++i){
			System.out.println(sorted[i]);
		}
		entrys.setHeap(sorted);
		entrys.setSize(size);


	}
}
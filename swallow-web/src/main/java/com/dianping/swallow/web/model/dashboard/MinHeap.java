package com.dianping.swallow.web.model.dashboard;


/**
 * @author mingdongli
 *
 * 2015年7月24日上午10:20:21
 */
public class MinHeap {

	private static final int CAPACITY = 2;

	private int size; // Number of elements in Heap
	
	private Entry[] Heap; // The Heap array

	public MinHeap() {
		size = 0;
		Heap = new Entry[CAPACITY];
	}

	/**
	 * Construct the binary Heap given an array of items.
	 */
	public MinHeap(Entry[] array) {
		size = array.length;
		Heap = new Entry[array.length + 1];

		System.arraycopy(array, 0, Heap, 1, array.length);// we do not use 0
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
		Entry tmp = Heap[k];
		int child;

		for (; 2 * k <= size; k = child) {
			child = 2 * k;

			if (child != size && Heap[child].compareTo(Heap[child + 1]) > 0)
				child++;

			if (tmp.compareTo(Heap[child]) > 0)
				Heap[k] = Heap[child];
			else
				break;
		}
		Heap[k] = tmp;
	}

	/**
	 * Sorts a given array of items.
	 */
	public void HeapSort(Entry[] array) {
		size = array.length;
		Heap = new Entry[size + 1];
		System.arraycopy(array, 0, Heap, 1, size);
		buildHeap();

		for (int i = size; i > 0; i--) {
			Entry tmp = Heap[i]; // move top item to the end of the Heap array
			Heap[i] = Heap[1];
			Heap[1] = tmp;
			size--;
			percolatingDown(1);
		}
		for (int k = 0; k < Heap.length - 1; k++)
			array[k] = Heap[Heap.length - 1 - k];
	}

	/**
	 * Deletes the top item
	 */
	public Entry deleteMin() throws RuntimeException {
		if (size == 0)
			throw new RuntimeException();
		Entry min = Heap[1];
		Heap[1] = Heap[size--];
		percolatingDown(1);
		return min;
	}

	/**
	 * Inserts a new item
	 */
	public boolean insert(Entry x) {
		try {

			if (size == Heap.length - 1)
				doubleSize();

			// Insert a new item to the end of the array
			int pos = ++size;

			// Percolate up
			for (; pos > 1 && x.compareTo(Heap[pos / 2]) < 0; pos = pos / 2)
				Heap[pos] = Heap[pos / 2];

			Heap[pos] = x;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void doubleSize() {
		Entry[] old = Heap;
		Heap = new Entry[Heap.length * 2];
		System.arraycopy(old, 1, Heap, 1, size);
	}

	public String toString() {
		String out = "";
		for (int k = 0; k < size; k++)
			out += Heap[k] + " ";
		return out;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Entry[] getHeap() {
		return Heap;
	}

	public void setHeap(Entry[] Heap) {
		
		int length = Heap.length;
		for(int i = 0; i < length; ++i){
			this.Heap[i] = Heap[i];
		}
		this.Heap[length] = null;
	}

	public static void main(String[] args) {

		MinHeap entrys = new MinHeap();
		//Entry[] en = new Entry[11];
		for (int i = 1; i < 10; ++i) {
			Entry e = new Entry();
			e.setNumAlarm(i);
			entrys.insert(e);
		}
		System.out.println("size is " + entrys.getSize());
		System.out.println(entrys);
		System.out.println("---------------------------");
		
//		Entry[] old = entrys.getHeap();
//		int size = entrys.getSize();
//		Entry[] new1 = new Entry[size];
//		System.arraycopy(old, 1, new1, 0, size);
//		entrys.HeapSort(new1);
		
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
		//entrys.setHeap(new1);
//		int size = entrys.getSize();
//		Entry[] e2 = entrys.getHeap();
//		entrys.HeapSort(entrys.getHeap());
		System.out.println("-----------------------");
		System.out.println(entrys.toString());
		
//		entrys.HeapSort(en);
//		entrys.setHeap(en);
//		entrys.setSize(11);
//		for(int i = 0; i < size; ++i){
//			System.out.println(new1[i]);
//		}
		//System.out.println(Arrays.toString(new1));
//		
		int s2 = entrys.getSize();

//		System.out.println("\n");
//		for (int k = 0; k < s2 - 1; k++)
//			System.out.println(e2[s2 - 1 - k]);

	}
}
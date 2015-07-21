package com.dianping.swallow.web.common;

/**
 * @author mingdongli
 *
 *         2015年7月20日上午11:25:13
 */
public class MaxHeap<E extends Comparable<? super E>> {

	public static final int DEFAULT_MAX = 1000;

	private E[] Heap;

	private int size; // Maximum size of the heap

	private int n; // Number of records in heap

	public MaxHeap(E[] h) {

		Heap = h;
		n = 0;
		size = DEFAULT_MAX;

		buildheap();
	}

	public MaxHeap(E[] h, int max) {

		Heap = h;
		n = 0;
		size = max;

		buildheap();
	}

	public int heapsize() {

		return n;
	}

	public boolean isLeaf(int pos) {

		return (pos >= n / 2) && (pos < n);
	}

	public int leftchild(int pos) {

		if (pos >= n / 2) {
			return -1;
		}
		return 2 * pos + 1;
	}

	public int rightchild(int pos) {

		if (pos >= (n - 1) / 2) {
			return -1;
		}
		return 2 * pos + 2;
	}

	public int parent(int pos) {

		if (pos <= 0) {
			return -1;
		}
		return (pos - 1) / 2;
	}

	public boolean insert(E val) {

		if (n >= size) {
			return false;
		}
		int curr = n++;
		Heap[curr] = val;

		while ((curr != 0) && (Heap[curr].compareTo(Heap[parent(curr)]) > 0)) {
			swap(Heap, curr, parent(curr));
			curr = parent(curr);
		}
		return true;

	}

	public void buildheap() {

		for (int i = n / 2 - 1; i >= 0; i--) {
			siftdown(i);
		}

	}

	private void siftdown(int pos) {

		if (pos < 0 || pos >= n) {
			return;
		}

		while (!isLeaf(pos)) {
			int j = leftchild(pos);

			if ((j < (n - 1)) && (Heap[j].compareTo(Heap[j + 1]) < 0)) {
				j++; // j is now index of child with greater value
			}
			if (Heap[pos].compareTo(Heap[j]) >= 0) {
				return;
			}

			swap(Heap, pos, j);
			pos = j; // Move down
		}
	}

	public E removemax() throws Exception {

		if (n <= 0) {
			throw new Exception("Remove from empty heap!");
		}
		swap(Heap, 0, --n); // Swap maximum with last value
		if (n != 0) {
			siftdown(0); // Put new heap root val in correct place
		}
		return Heap[n];
	}

	public E remove(int pos) throws Exception {

		if (pos < 0 || pos >= n) {
			throw new Exception("Illegal heap position");
		}

		if (pos == (n - 1)) {
			n--;
		} else {
			swap(Heap, pos, --n); // Swap with last value
			while ((pos > 0) && (Heap[pos].compareTo(Heap[parent(pos)]) > 0)) {
				swap(Heap, pos, parent(pos));
				pos = parent(pos);
			}
			if (n != 0) {
				siftdown(pos); // If it is little, push down
			}
		}

		return Heap[n];
	}

	private void swap(E[] A, int p1, int p2) {

		E temp = A[p1];
		A[p1] = A[p2];
		A[p2] = temp;
	}
}

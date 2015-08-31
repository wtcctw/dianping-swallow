package com.dianping.swallow.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueTest {

	private static PriorityBlockingQueue<Integer> queues = new PriorityBlockingQueue<Integer>(5);

	public static void main(String[] args) {
		queues.add(1);
		queues.add(2);
		queues.add(4);
		queues.add(3);
		queues.add(5);
		queues.add(10);
		queues.add(9);
		queues.add(11);
		
		List<Integer> list =new ArrayList<Integer>(queues);
		
		System.out.println(list);
		Collections.reverse(list);
		System.out.println(list);
		System.out.println(queues.peek());
		for (; !queues.isEmpty();) {
			System.out.println(queues.poll());
		}
	}
}

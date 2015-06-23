package com.dianping.swallow.common.internal.lifecycle.impl;


import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.dianping.swallow.common.internal.lifecycle.Ordered;

/**
 * @author mengwenchao
 *
 * 2015年6月19日 下午6:57:05
 */
public class LifecycleComponentTest {
	
	
	
	@Test
	public void testOrder(){
		
		List<Component> list = new LinkedList<LifecycleComponentTest.Component>();
		
		for(int i=0;i<10;i++){
			list.add(new Component(i));
		}
		list.add(new Component(Ordered.FIRST));
		list.add(new Component(Ordered.LAST));
		
		
		Collections.sort(list, new Comparator<Ordered>() {

			@Override
			public int compare(Ordered o1, Ordered o2) {
				
				return -o1.getOrder() + o2.getOrder();
			}
		});
		
		System.out.println(list);
		
	}

	
	
	class Component extends AbstractLifecycle{
		
		private int  order;
		public Component(int order){
			this.order = order;
		}
		
		@Override
		public int getOrder() {
			
			return order;
		}
		
		@Override
		public String toString() {
			
			return String.valueOf(order);
		}
	}
}

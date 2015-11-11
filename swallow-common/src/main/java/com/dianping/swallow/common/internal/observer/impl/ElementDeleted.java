package com.dianping.swallow.common.internal.observer.impl;

/**
 * @author mengwenchao
 *
 * 2015年11月11日 下午3:49:31
 */
public class ElementDeleted<T> {
	
	private T element;
	
	public ElementDeleted(T element){
		this.element = element;
	}
	
	public T getElement() {
		return element;
	}

}

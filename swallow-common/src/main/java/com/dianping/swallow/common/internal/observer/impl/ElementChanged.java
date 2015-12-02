package com.dianping.swallow.common.internal.observer.impl;

/**
 * @author mengwenchao
 *
 * 2015年11月11日 下午3:49:31
 */
public class ElementChanged<T> {
	
	private T oldElement;
	private T newElement;
	
	public ElementChanged(T oldElement, T newElement){
		this.oldElement = oldElement;
		this.newElement = newElement;
	}
	
	public T getOldElement() {
		return oldElement;
	}

	public T getNewElement() {
		return newElement;
	}

}

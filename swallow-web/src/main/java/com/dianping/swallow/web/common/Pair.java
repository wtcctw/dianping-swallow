package com.dianping.swallow.web.common;


/**
 * @author mingdongli
 *
 * 2015年7月17日下午1:36:03
 */
public class Pair<F, S> {

	private F first; //first member of pair
    
	private S second; //second member of pair

    public Pair(F first, S second) {

    	this.first = first;
        this.second = second;
    }

    public void setFirst(F first) {
        
    	this.first = first;
    }

    public void setSecond(S second) {
        
    	this.second = second;
    }

    public F getFirst() {
        
    	return first;
    }

    public S getSecond() {
        
    	return second;
    }

}
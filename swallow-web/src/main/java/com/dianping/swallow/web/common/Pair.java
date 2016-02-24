package com.dianping.swallow.web.common;


/**
 * @author mingdongli
 *
 * 2015年7月17日下午1:36:03
 */
public class Pair<F, S> {

	private F first; //first member of pair
    
	private S second; //second member of pair
	
	public Pair(){
		
	}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        return !(second != null ? !second.equals(pair.second) : pair.second != null);

    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }
}
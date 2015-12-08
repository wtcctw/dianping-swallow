package com.dianping.swallow.test.load;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年7月20日 下午6:38:16
 */
public class BitMarker {

    protected Logger logger       = LoggerFactory.getLogger(getClass());

	private long capacity;

	private int [] marker;
	
	AtomicLong realCount = new AtomicLong();
	
	public BitMarker(){
		
		this(1000000000L);
	}
	
	public BitMarker(long capacity){
		
		
		if(capacity < 0 || getArraySize(capacity) > Integer.MAX_VALUE){
			throw new IllegalArgumentException("cacacity error:" + capacity);
		}
		
		this.capacity = capacity;
		int size = (int) getArraySize(capacity);
		
		if(logger.isInfoEnabled()){
			logger.info("[marker size]" + (size *4/(1<<20)) + " MB");
		}
				
		marker = new int[size];
	}
	
	private long getArraySize(long capacity) {
		
		long result = capacity/32;
		
		if(capacity % 32 == 0){
			return result;
		}
		return  result + 1;
	}

	public synchronized void mark(long num){
		
		realCount.incrementAndGet();
		
		if(num > capacity){
			throw new IllegalArgumentException("wrong num:" + num + "," + capacity);
		}
		
		if(exist(num)){
			if(logger.isInfoEnabled()){
				logger.info("[exist]" + num);
			}
			return;
		}
		
		marker[(int) (num/32)] |= 1 << (num%32);
	}
	
	private boolean exist(long num) {
		
		if(((marker[(int) (num/32)]) & (1 << (num%32))) !=0 ){
			return true;
		}
		return false;
	}

	public long noRepetCount(){
		
		long count = 0;
		
		for(int num : marker){
			count += onecapacity(num);
		}
		return count;
	}
	
	public long realCount(){
		
		return realCount.get();
	}
	private int onecapacity(int num) {
		
		int result = 0;
		
		while(num != 0){
			result++;
			num &= num-1;
		}
		
		return result;
	}

	public static void main(String []argc){

		BitMarker bm = new BitMarker();
		for(int i=0;i<100000;i++){
			bm.mark(i);
		}
		bm.mark(100);
		System.out.println();
		System.out.println(bm.realCount());
		System.out.println(bm.noRepetCount());
	}

}

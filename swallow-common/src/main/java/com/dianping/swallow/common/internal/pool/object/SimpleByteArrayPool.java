package com.dianping.swallow.common.internal.pool.object;

import com.dianping.swallow.common.internal.pool.ArrayPool;

/**
 * 只支持单线程操作
 * @author mengwenchao
 *
 * 2015年9月6日 下午4:14:48
 */
public class SimpleByteArrayPool implements ArrayPool<byte[]>{
	
	protected static int lengths[] = {512, 1024, 2048, 4096, 8192};
	
	private byte objects[][];
	
	public SimpleByteArrayPool(){
		
		objects = new byte[lengths.length][];
		
		for(int i = 0; i < lengths.length ;i++){
			objects[i] = new byte[lengths[i]];
		}
	}

	@Override
	public byte[] get(int size) {
		
		if(size > lengths[lengths.length -1]){
			return new byte[size];
		}
		
		int index = getIndex(size);
		
		if(objects[index] == null){
			throw new IllegalStateException("must release before get!" + size);
		}
		try{
			return objects[index];
		}finally{
			objects[index] =  null;
		}
	}

	protected int getIndex(int size) {
		
		int start = 0, end = lengths.length - 1;
		while(start <= end){
			
			int mid = start + (end -start)/2;
			
			if(lengths[mid] == size){
				return mid;
			}
			if(lengths[mid] > size){
				end = mid - 1;
			}else{
				start = mid + 1;
			}
		}
		
		return start;
	}

	@Override
	public void release(byte[] object) {
		if(object == null){
			return;
		}
		objects[getIndex(object.length)] = object;
	}

}

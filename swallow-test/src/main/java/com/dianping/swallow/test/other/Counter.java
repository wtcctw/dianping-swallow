package com.dianping.swallow.test.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class Counter {

	public static void main(String[] args) throws IOException {
		
		String file = "/data/applogs/swallow/all";
		File f = new File(file);
	
		BufferedReader br = null;
		
		
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line = null;
			int count[] = new int[10000];
			int max = 0;
			while((line = br.readLine()) != null){
				
				String []sp = line.split("\\s+");
				int data = Integer.parseInt(sp[sp.length - 1]);
				count[data]++;
				if(data > max){
					max = data;
				}
				
			}
	
			for(int i=0;i<=max;i++){
				if(count[i] != 1){
					System.out.println(i + ":" + count[i]);
				}
			}
		}finally{
			if(br != null){
				br.close();
			}
		}
	}
}

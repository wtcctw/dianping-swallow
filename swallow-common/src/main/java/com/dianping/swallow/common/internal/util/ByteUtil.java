package com.dianping.swallow.common.internal.util;

/**
 * @author mengwenchao
 *
 * 2015年12月15日 下午5:15:37
 */
public class ByteUtil {
	
	public static int DEFAULT_LINE_LENGTH = 32;

	public static String toHexString(byte []buff){
		
		return toHexString(buff, DEFAULT_LINE_LENGTH);
	}
	
	
	public static String toHexString(byte []buff, int lineLength){
		
		StringBuilder sb = new StringBuilder();
		
		int lastStart = 0;
		for(int i=0 ; i < buff.length ; i++){
			
			sb.append(String.format("%02X", buff[i]));
			
			if((i+1)%lineLength == 0){

				addSplit(sb);
				appendChar(sb, buff, lastStart, i);
				lastStart = i + 1;
				
				if( i != buff.length - 1){
					sb.append("\n");
				}
				continue;
			}
			sb.append(' ');
		}
		
		int characterLeft = buff.length % lineLength;
		if(characterLeft != 0){
			for(int i=1; i< 3 * (lineLength - characterLeft);i++){
				sb.append(" ");
			}
			addSplit(sb);
		}
		appendChar(sb, buff, lastStart, buff.length - 1);
		
		return sb.toString();
	}
	
	
	private static void addSplit(StringBuilder sb) {
		sb.append(" : ");
	}


	private static void appendChar(StringBuilder sb, byte[]buff, int start, int end) {
		
		for(int i=start; i<= end ;i++){
			sb.append((char)buff[i]);
		}
	}

}

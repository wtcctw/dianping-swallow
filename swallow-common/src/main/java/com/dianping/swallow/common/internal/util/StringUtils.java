package com.dianping.swallow.common.internal.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author mengwenchao
 *
 * 2015年4月27日 上午11:50:42
 */
public class StringUtils {

	public static Pattern commaRegex = Pattern.compile("\\s*,\\s*");
	
	public static List<String> splitByComma(String input){
		
		List<String>  result = new LinkedList<String>();
		for(String sp : commaRegex.split(input)){
			if(isEmpty(sp)){
				continue;
			}
			result.add(sp.trim());
		}
		return result;
	}

	public static List<String> splitByDelimiter(String input, Pattern delimiterRegex){

		List<String>  result = new LinkedList<String>();

		for(String sp : delimiterRegex.split(input)){
			if(isEmpty(sp)){
				continue;
			}
			result.add(sp.trim());
		}
		return result;
	}

	public static boolean isEmpty(String buff){
		
		if(buff == null || buff.trim().length() == 0){
			return true;
		}
		return false;
	}
	
	public static String trimToNull(String buff){
		
		if( buff == null){
			return null;
		}
		buff = buff.trim();
		
		if(buff.length() == 0){
			return null;
		}
		
		return buff;
		
	}
	
	
	public static String join(String split, String ...str){
		
		StringBuilder sb = new StringBuilder();
		
		int index = 0;
		
		for(int i=0;i<str.length;i++){
			
			if(isEmpty(str[i])){
				continue;
			}
			
			if(index > 0){
				sb.append(split);
			}
			sb.append(str[i]);
			index++;
		}
		
		return sb.toString(); 
	}
}

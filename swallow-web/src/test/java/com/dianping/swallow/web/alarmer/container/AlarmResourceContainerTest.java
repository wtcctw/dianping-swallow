package com.dianping.swallow.web.alarmer.container;

import java.util.ArrayList;
import java.util.List;

public class AlarmResourceContainerTest {

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("default");
		list.add("test");
		List<String> result = new ArrayList<String>(list);
		result.remove("default");
		System.out.println(list);
		System.out.println(result);
	}

}

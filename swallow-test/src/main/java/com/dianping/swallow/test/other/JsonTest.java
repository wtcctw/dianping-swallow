package com.dianping.swallow.test.other;

import org.junit.Test;

import com.dianping.swallow.common.internal.codec.JsonBinder;

/**
 * @author mengwenchao
 *
 * 2015年7月24日 上午11:49:27
 */
public class JsonTest {

	public static class Person{
		private String name;
		private Integer age;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
		
		
		
	}
	
	@Test
	public void test(){

		Person person = new Person();
		person.setAge(1);
		
		System.out.println(JsonBinder.getNonEmptyBinder().toJson(person));
		
		
	}
}

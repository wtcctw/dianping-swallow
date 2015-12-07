package com.dianping.swallow.test.other;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;

/**
 * @author mengwenchao
 *
 * 2015年7月24日 上午11:49:27
 */
public class JsonTest {

	public static class Person{
		private String name;
		private Integer age;
		
		public Person() {
		}
		
		
		public Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}
		
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
	public void testJson(){
		Map<Person, Person> maps = new HashMap<JsonTest.Person, JsonTest.Person>();
		Person person = new Person("john", 11);
		maps.put(person, person);
		
		String json = JsonBinder.getNonEmptyBinder().toJson(maps);
		System.out.println(json);
	}
	
	@Test
	public void test(){

		Person person = new Person();
		person.setAge(1);
		
		System.out.println(JsonBinder.getNonEmptyBinder().toJson(person));
	}
}

package com;

import com.beanManager.BeanManager;
import com.injection.EntityManager;
import com.injection.EntityManagerBase;
import com.model.Person;
// import com.orm.EntityManager;

public class ReadingObjects {
	public static void main(String[] args) throws Exception {
		BeanManager beanManager = BeanManager.getInstance();
		EntityManagerBase<Person> entityManager = beanManager.getInstance(EntityManager.class);

		// EntityManager<Person> entityManager = EntityManager.of(Person.class);
		Person linda = entityManager.find(Person.class, 1L);
		Person james = entityManager.find(Person.class, 2L);
		Person susan = entityManager.find(Person.class, 3L);
		Person john = entityManager.find(Person.class, 4L);

		System.out.println(linda);
		System.out.println(james);
		System.out.println(susan);
		System.out.println(john);
	}
}

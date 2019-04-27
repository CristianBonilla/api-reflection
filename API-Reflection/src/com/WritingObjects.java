package com;

import com.beanManager.BeanManager;
import com.injection.EntityManagerBase;
import com.injection.EntityManager;
import com.model.Person;
// import com.orm.EntityManagerBase;

public class WritingObjects {
	public static void main(String[] args) throws Exception {
		BeanManager beanManager = BeanManager.getInstance();
		EntityManagerBase<Person> entityManager = beanManager.getInstance(EntityManager.class);

		// EntityManagerBase<Person> entityManager = EntityManagerBase.of(Person.class);

		Person linda = new Person("Linda", 31);
		Person james = new Person("James", 24);
		Person susan = new Person("Susan", 34);
		Person john = new Person("John", 33);

		System.out.println(linda);
		System.out.println(james);
		System.out.println(susan);
		System.out.println(john);

		System.out.println("Writing to DB");

		entityManager.persist(linda);
		entityManager.persist(james);
		entityManager.persist(susan);
		entityManager.persist(john);

		System.out.println(linda);
		System.out.println(james);
		System.out.println(susan);
		System.out.println(john);
	}
}

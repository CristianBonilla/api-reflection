package com.orm;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface EntityManager<T> {
	static <T> EntityManager<T> of(Class<T> modelClass) {
		return new H2EntityManager<>();
	}

	void persist(T model) throws SQLException, IllegalAccessException;

	T find(Class<T> modelClass, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}

package com.dependenciesInjection;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface EntityManager<T> {
	void persist(T model) throws SQLException, IllegalAccessException;

	T find(Class<T> modelClass, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
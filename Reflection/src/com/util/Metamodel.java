package com.util;

import com.annotation.Column;
import com.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Metamodel<T> {
	private Class<T> modelClass;

	public Metamodel(Class<T> modelClass) {
		this.modelClass = modelClass;
	}

	public static <T> Metamodel<T> of(Class<T> modelClass) {
		return new Metamodel<>(modelClass);
	}

	public PrimaryKeyField getPrimaryKey() {
		Field[] fields = modelClass.getDeclaredFields();
		for (Field field : fields) {
			PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
			if (primaryKey != null) {
				PrimaryKeyField primaryKeyField = new PrimaryKeyField(field);
				return primaryKeyField;
			}
		}

		throw new IllegalArgumentException("No primary key found in class: " + modelClass.getSimpleName());
	}

	public List<ColumnField> getColumns() {
		List<ColumnField> columnFields = new ArrayList<>();
		Field[] fields = modelClass.getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				ColumnField columnField = new ColumnField(field);
				columnFields.add(columnField);
			}
		}

		return columnFields;
	}
}
